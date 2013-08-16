package com.pivotal.demos.nbio;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Reads and writes based on what is available.
 */
@WebServlet(urlPatterns="/non-blocking-io/EchoNbioServlet2", asyncSupported=true)
public class EchoNbioServlet2 extends HttpServlet  {
	
	private static final long serialVersionUID = -6167956299941229517L;

	private static class EchoReadListener implements ReadListener {
		private AsyncContext context;
		private LinkedBlockingQueue<String> data;
		
		public EchoReadListener(AsyncContext context, LinkedBlockingQueue<String> data) {
			this.context = context;
			this.data = data;
		}

		public void onDataAvailable() throws IOException {
			ServletInputStream input = context.getRequest().getInputStream();
			try {
	            byte[] b = new byte[8192];
	            int read = 0;
	            do {
	                read = input.read(b);
	                if (read == -1) {
	                    break;
	                }
	                String tmp = new String(b, 0, read, "utf-8");
	                data.add(tmp);
	                System.out.println("Read [" + tmp + "]");
	            } while (input.isReady());
	        } catch (Exception ex) {
	            ex.printStackTrace(System.err);
	            context.complete();
	        }
		}

		public void onAllDataRead() throws IOException {
			System.out.println("All data has been read");
			data.add("DONE");
		}

		public void onError(Throwable throwable) {
			throwable.printStackTrace(System.err);
			context.complete();
		}
	}

	private static class EchoWriteListener implements WriteListener {
		private AsyncContext context;
		private LinkedBlockingQueue<String> data;
		private ReadListener rlistener;

		public EchoWriteListener(AsyncContext context, LinkedBlockingQueue<String> data, ReadListener rlistener) {
			this.context = context;
			this.data = data;
			this.rlistener = rlistener;
		}

		public void onWritePossible() throws IOException {
			ServletOutputStream output = context.getResponse().getOutputStream();
			while (output.isReady()) {
				String str = data.poll();
				if (str != null) {
					System.out.println("Writing: [" + str + "]");
					if ("DONE".equals(str)) {
						// Call complete, to signal we are done
						context.complete();
						break;
					}
					output.write(str.getBytes("utf-8"));
				} else {
					// were out of data, but we can't exit until we've written everything
					//  or output.isReady() returns false.  This is because onWritePossible only
					//  gets called if we write until isReady is false
					System.out.println("Out of data, looking for more");
					rlistener.onDataAvailable();
					if (data.peek() == null) {
						System.out.println("Still no data, giving up");
						data.add("DONE");
					}
				}
			}
			System.out.println("Done writing for now");
		}

		public void onError(Throwable throwable) {
			throwable.printStackTrace(System.err);
			context.complete();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LinkedBlockingQueue<String> data = new LinkedBlockingQueue<>(10);

		// 1. Start Async
		final AsyncContext context = req.startAsync();

		// 2. Add Read Listener to get user's input
		ReadListener rlistener = new EchoReadListener(context, data);
		req.getInputStream().setReadListener(rlistener);
		System.out.println("ReadListener added");
		
		// 3. Add listener, starts Non-blocking IO support
		WriteListener wlistener = new EchoWriteListener(context, data, rlistener);
		resp.getOutputStream().setWriteListener(wlistener);
		System.out.println("WriteListener added");
	}
	
}

