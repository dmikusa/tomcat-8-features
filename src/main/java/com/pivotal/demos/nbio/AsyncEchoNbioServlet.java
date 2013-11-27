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
 * Reads in the complete request, buffering it, then writes it out
 */
@WebServlet(urlPatterns = "/non-blocking-io/AsyncEchoNbioServlet", asyncSupported = true)
public class AsyncEchoNbioServlet extends HttpServlet {
	private static final long serialVersionUID = -3038617032944575769L;
	public static final int BUFFER_SIZE = 8 * 1024;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AsyncContext asyncContext = request.startAsync(request, response);
		asyncContext.setTimeout(0);
		Echoer echoer = new Echoer(asyncContext);
		response.getOutputStream().setWriteListener(echoer);
		request.getInputStream().setReadListener(echoer);
	}

	private class Echoer implements ReadListener, WriteListener {
		private byte[] buffer = new byte[BUFFER_SIZE];
		private long totalRead = 0;
		private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

		private AsyncContext asyncContext;
		private ServletInputStream input;
		private ServletOutputStream output;

		private Echoer(AsyncContext asyncContext) throws IOException {
			this.asyncContext = asyncContext;
			this.input = asyncContext.getRequest().getInputStream();
			this.output = asyncContext.getResponse().getOutputStream();
		}

		@Override
		public void onDataAvailable() throws IOException {
			while (input.isReady()) {
				int read = input.read(buffer);
				totalRead += read;
				getServletContext().log(
						"read and wrote [" + read + "] total [" + totalRead
								+ "]");

				if (output.isReady()) {
					output.write(buffer, 0, read);
				} else {
					queue.add(buffer);
					buffer = new byte[BUFFER_SIZE];
				}
			}
		}

		@Override
		public void onAllDataRead() throws IOException {
		}

		@Override
		public void onWritePossible() throws IOException {
			if (input.isFinished()) {
				if (!queue.isEmpty()) {
					while (output.isReady()) {
						output.write(queue.poll());
						if (queue.isEmpty()) {
							asyncContext.complete();
							return;
						}
					}
				} else {
					asyncContext.complete();
				}
			} else {
				onDataAvailable();
			}
		}

		@Override
		public void onError(Throwable failure) {
			getServletContext().log("echo failure", failure);
		}
	}
}
