package com.pivotal.demos.nbio;

import java.io.IOException;

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
 * Reads in the complete request, buffering it, then writes it out
 */
@WebServlet(urlPatterns="/non-blocking-io/EchoNbioServlet", asyncSupported=true)
public class EchoNbioServlet extends HttpServlet  {

	private static final long serialVersionUID = -6167956299941229517L;

	private static class EchoReadListener implements ReadListener {
		private AsyncContext context;
		private StringBuilder data = new StringBuilder();

		public EchoReadListener(AsyncContext context) {
			this.context = context;
		}

		public void onDataAvailable() throws IOException {
			// 3. Read all the data that is available, may be called multiple times
			ServletInputStream input = context.getRequest().getInputStream();
			try {
	            byte[] b = new byte[8192];
	            int read = 0;
	            do {
	                read = input.read(b);
	                if (read == -1) {
	                    break;
	                }
	                data.append(new String(b, 0, read));
	                System.out.println("Buffer increased to [" + data.length() + "] characters");
	            } while (input.isReady());
	        } catch (Exception ex) {
	            ex.printStackTrace(System.err);
	            context.complete();
	        }
		}

		public void onAllDataRead() throws IOException {
			// 4. All Data Read, add WriteListener
			context.getResponse().getOutputStream().setWriteListener(
								new EchoWriteListener(context, data.toString()));
		}

		public void onError(Throwable ex) {
			ex.printStackTrace(System.err);
			context.complete();
		}

	}

	private static class EchoWriteListener implements WriteListener {
		private AsyncContext context;
		private String data;

		public EchoWriteListener(AsyncContext context, String data) {
			this.context = context;
			this.data = data;
		}

		public void onWritePossible() throws IOException {
			ServletOutputStream output = context.getResponse().getOutputStream();
			// 5. Write output
			if (output.isReady()) {
				output.write(data.getBytes("utf-8"));
			}

			// 6. Call complete, to signal we are done
			context.complete();
		}

		public void onError(Throwable throwable) {
			throwable.printStackTrace(System.err);
			context.complete();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. Start Async
		AsyncContext context = req.startAsync();

		// 2. Add Read Listener to get user's input
		context.getRequest().getInputStream().setReadListener(new EchoReadListener(context));
	}

}
