package com.pivotal.demos.nbio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

	private List<EventPusherWriteListener> clients = Collections.synchronizedList(new ArrayList<EventPusherWriteListener>());
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	@Override
	public void init() throws ServletException {
		super.init();

		// Setup a scheduled thread pool executor.  This has two purposes.  First, it is
		// 	  used to periodically create data.  Second, it is used to check if we have
		//    data that can be pushed to the client.
		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4);
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(
				new TimeGenerator(), 0, 500, TimeUnit.MILLISECONDS);
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
		private Queue<Long> data = new ConcurrentLinkedQueue<>();
		private AtomicBoolean manualUpdateRequired = new AtomicBoolean(false);
		private int count = 0;

		public EventPusherWriteListener(AsyncContext context) {
			this.context = context;
		}

		@Override
		public void onWritePossible() throws IOException {
			ServletOutputStream output = context.getResponse().getOutputStream();
			// Check if we have data to write & that we can write
			try {
				while (data.peek() != null && output.isReady()) {
					// We have data and we can write, so let's do it!
					//   Notice the "data:" that is added to the beginning of the
					//   output.  This is part of the Server-Sent events format.
					output.write(("data: " + data.poll() + "\n\n").getBytes());
					count += 1;
				}
				// Flush so that data is sent to the client right away.  Technically we
				//   shouldn't call flush without first checking output.isReady() since it's
				//   a separate write operation.  We're not doing that here cause our
				//   messages are small so it doesn't matter in this example.
				output.flush();
			} catch (IOException ex) {
				// There's no way to know when a client disconnects other than to just handle the exceptions.  Here we're checking
				//   for "Broken pipe" or "Connection reset by peer" IO Exceptions.  If these occur during a write operation, it likely
				//   means the user disconnected.
				if ("Broken pipe".equals(ex.getCause().getMessage()) || "Connection reset by peer".equals(ex.getCause().getMessage())) {
					// client disconnected
					getServletContext().log("Client processed by [" + Thread.currentThread().getName() + "] " +
											"has disconnected.  Wrote [" + count + "] messages");
					clients.remove(this);
					return;
				} else {
					throw ex;  // some other IOException
				}
			}
			// Check if we're done.  We limit to 20 messages per connection.  This is
			//    just an arbitrary value.  You could have any limit or no limit.  Having a
			//    limit doesn't break the demo because the browser's Server Sent Events support
			//    will automatically reconnect after we close the connection.  Be careful if
			//    you do not use a limit as a connection could technically last forever.
			if (count >= 20) {
				getServletContext().log("Client processes by [" + Thread.currentThread().getName() + "] is done.");
				onError(null);  // shortcut to clean up and close the context
				return;
			}
			// If we're still able to send data (isReady() is true), but we do not
			//    have any more data to send, it's our job to make sure onWritePossible
			//    is called again.  We do this by setting the manualUpdateRequired flag
			//    to true.
			// If isReady() is false then then the container will call onWritePossible so
			//    we do not need to manually call it.
			if (output.isReady()) {
				manualUpdateRequired.set(true);
			}
			// Log the thread which called this method, just as an FYI
			//getServletContext().log("onWritePossible called by [" + Thread.currentThread().getName() + "]");
		}

		@Override
		public void onError(Throwable t) {
			if (t != null) {
				// log the error
				getServletContext().log("Async Error", t);
			}
			// remove connection from clients list
			clients.remove(this);
			// complete our work
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

		// Called by a data generator to add data to the queue.  Also checks to see
		//   if we need to manually call onWritePossible, using the manualUpdateRequired flag.
		//   If we need to call it, we schedule the task so the call to onWritePossible does
		//   not happen in the data generator thread.
		public void publish(Long item) {
			data.add(item);
			if (manualUpdateRequired.get()) {
				manualUpdateRequired.set(false);
				scheduledThreadPoolExecutor.execute(this);
			}
		}

	}

	/**
	 * Generates data.  Called periodically by the scheduled thread pool.
	 */
	public class TimeGenerator implements Runnable {

		private int count = 0;
		private final long firstRun = System.currentTimeMillis();

		public TimeGenerator() {
		}

		@Override
		public void run() {
			// Don't do anything unless we have clients connected
			if (clients.size() > 0) {
				// Generates a piece of data, in this case the current time stamp, and sends it
				//    to each of the clients.
				Long item = System.currentTimeMillis();
				synchronized (clients) {
					for (EventPusherWriteListener client : clients) {
						client.publish(item);
					}
				}
				// Keep some stats on how much data we generate and our clients
				count++;
				if (count % 10 == 9) {
					getServletContext().log("Generated [" + count + "] messages at a rate of [" +
							(count / ((System.currentTimeMillis() - firstRun) / 1000.0)) + "] messages per second.");
					getServletContext().log("Client count [" + clients.size() + "]");
				}
			}
		}
	}

}
