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

import org.apache.commons.lang3.RandomStringUtils;

@WebServlet(urlPatterns="/non-blocking-io/RandomDataNbioServlet", asyncSupported=true)
public class RandomDataNbioServlet extends HttpServlet  {

	private static final long serialVersionUID = -6167956299941229517L;

	private static class NumberReadListener implements ReadListener {
		private StringBuilder buf = new StringBuilder();  // buffer for user's data
		private AsyncContext context;

		public NumberReadListener(AsyncContext context) {
			this.context = context;
		}

		public void onDataAvailable() throws IOException {
			// 3. Read in data from user, this should be a number representing the amount
			//    of random characters to write back to the user
			ServletInputStream input = context.getRequest().getInputStream();
			try {
	            byte[] b = new byte[8192];
	            int read = 0;
	            do {
	                read = input.read(b);
	                if (read == -1) {
	                    break;
	                }
	                buf.append(new String(b, 0, read));
	            } while (input.isReady());
	        } catch (Exception ex) {
	            ex.printStackTrace(System.err);
	            context.complete();
	        }
		}

		public void onAllDataRead() throws IOException {
			// 4. Try to convert the string data into a number.  Defaults to 512.  Max 1,000,000.
			int randomDataSize = 512;
			if (buf.length() > 0) {
				try {
					randomDataSize = Integer.parseInt(buf.toString());
				} catch (NumberFormatException ex) {
					System.err.println("Invalid size specified, using default size of 512 chars");
				}
			}
			randomDataSize = (randomDataSize < 0) ? 512 : randomDataSize;  // must be >= 0
			randomDataSize = (randomDataSize > 1_000_000) ? 1_000_000 : randomDataSize; // must be <= 1,000,000

			// 5. Configure a write listener to write the random characters
			context.getResponse().getOutputStream().setWriteListener(
								new RandomDataWriteListener(context, randomDataSize));
		}

		public void onError(Throwable throwable) {
			throwable.printStackTrace(System.err);
			context.complete();
		}
	}

	private static class RandomDataWriteListener implements WriteListener {
		private AsyncContext context;
		private int numChars;

		public RandomDataWriteListener(AsyncContext context, int numChars) {
			this.context = context;
			this.numChars = numChars;
		}

		public void onWritePossible() throws IOException {
			ServletOutputStream output = context.getResponse().getOutputStream();
			// 6. Write random characters
			if (output.isReady()) {
				output.write(RandomStringUtils.randomAlphanumeric(numChars).getBytes("utf-8"));
			}

			// 7. Call complete, to signal we are done
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
		final AsyncContext context = req.startAsync();
		resp.setCharacterEncoding("utf-8");

		// 2. Add Read Listener to get the number of characters to return
		req.getInputStream().setReadListener(new NumberReadListener(context));
	}

}
