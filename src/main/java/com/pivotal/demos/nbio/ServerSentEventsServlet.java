package com.pivotal.demos.nbio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/non-blocking-io/ServerSentEventsServlet", asyncSupported=true)
public class ServerSentEventsServlet extends HttpServlet {
	private static final long serialVersionUID = 7678519506620543431L;
	
	private List<EventPusherWriteListener> clients = new ArrayList<>();
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		// Setup a scheduled thread pool executor.  This has two purposes.  First, it is
		// 	  used to periodically create data.  Second, it is used to check if we have
		//    data that can be pushed to the client.
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(
				new TimeGenerator(clients), 0, 500, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void destroy() {
		// Clean up our thread pool
		scheduledThreadPoolExecutor.shutdown();
		try {
			if (! scheduledThreadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
				getServletContext().log("Threads still running after 5 seconds");
			}
		} catch (InterruptedException e) {
			getServletContext().log("Interrupted at shutdown", e);
		}
		super.destroy();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// We're using an async context to free up request processing threads.  We set
		//    the context to never timeout, this is because we'll be indefinitely producing 
		//    data for the client 
		final AsyncContext context = req.startAsync();
		context.setTimeout(-1);
		
		// Create our write listener and add it to the list of clients.  The list of
		//    clients is used by the data generator, so that it can send generated
		//    data to each connected client.
		EventPusherWriteListener wl = new EventPusherWriteListener(context);
		clients.add(wl);
		
		// Setup the request for Server-Sent Events.  This requires that we set the
		//   content type to "text/event-stream".  Setting the cache control to "no-cache"
		//   is not required, but recommended.  We also set our write listener.
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/event-stream");
		resp.setHeader("Cache-Control", "no-cache");
		resp.getOutputStream().setWriteListener(wl);
	}

	/**
	 * Pushes events to the client.  Uses Server Sent Events.
	 */
	private class EventPusherWriteListener implements WriteListener, Runnable {
		private AsyncContext context;
		private BlockingQueue<Long> data = new LinkedBlockingQueue<>();

		public EventPusherWriteListener(AsyncContext context) {
			this.context = context;
		}
		
		@Override
		public void onWritePossible() throws IOException {
			// Check if we can write data
			ServletOutputStream output = context.getResponse().getOutputStream();
			if (output.isReady()) {
				// Yes, we can.  Check if we have any items in the queue to write.
				try {
					Long item = data.poll(100, TimeUnit.MILLISECONDS);
					while (item != null && output.isReady()) {
						// Yes, we have an item.  Write it.  Notice the "data:" that is added
						//   to the beginning of the output.  This is part of the Server-Sent 
						//   events format.
						output.write(("data: " + item + "\n\n").getBytes());
						// Check if we have any more data to send
						item = data.poll(100, TimeUnit.MILLISECONDS);
					}
					// We also flush so that data is sent to the client right away.
					output.flush();	
					// If we're still able to send data (isRead() is true), but we do not 
					//    have any more data to send, it's our job to make sure onWritePossible 
					//    is called again.  We do this by scheduling this task to be called one
					//    second from now.
					// If isReady() is false then then the container will call onWritePossible so 
					//    we do not need to schedule it to be called.
					if (output.isReady()) {
						scheduledThreadPoolExecutor.schedule(this, 2, TimeUnit.SECONDS);
					}
				} catch (InterruptedException e) {
					// Interrupted, don't reschedule
					getServletContext().log("Interrupted waiting for data", e);
				}
			}
		}
		
		@Override
		public void onError(Throwable t) {
			// log the error and complete our work
			getServletContext().log("Async Error", t);
			context.complete();
		}

		@Override
		public void run() {
			// Called by the scheduled task executor.  This calls onWritePossible which checks
			//    if we have more data to send and if it can be sent.
			try {
				onWritePossible();
			} catch (Exception ex) {
				onError(ex);
			}
		}
		
		// Called by the data generator to add data to the queue
		public void publish(Long item) {
			data.add(item);
		}
	}
	
	/**
	 * Generates data.  Called periodically by the scheduled thread pool.
	 */
	public class TimeGenerator implements Runnable {
		
		List<EventPusherWriteListener> clients;
		
		public TimeGenerator(List<EventPusherWriteListener> clients) {
			this.clients = clients;
		}
		
		@Override
		public void run() {
			// Generates a piece of data, in this case the current time stamp, and sends it
			//    to each of the clients.
			Long item = System.currentTimeMillis();
			for (EventPusherWriteListener client : clients) {
				client.publish(item);
			}
		}
	}
	
}
