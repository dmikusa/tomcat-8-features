package com.pivotal.demos.nbio;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/non-blocking-io/HelloNbioServlet", asyncSupported=true)
public class HelloNbioServlet extends HttpServlet {
	
	private static final long serialVersionUID = -2388771176018937413L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. Start Async
		final AsyncContext asyncContext = req.startAsync();

		// 2. Add Write Listener - to say Hello
		ServletOutputStream outputStream = resp.getOutputStream();
		WriteListener listener = new WriteListener() {
			@Override
			public void onWritePossible() throws IOException {
				// 3. Write to output stream 
				ServletOutputStream output = asyncContext.getResponse().getOutputStream();
				if (output.isReady()) {
					output.print("Hello World!");
				}
				
				// 4. Call complete, to signal we are done
				asyncContext.complete();
			}
			
			@Override
			public void onError(Throwable throwable) {
				throwable.printStackTrace(System.err);
				asyncContext.complete();
			}
		};
		outputStream.setWriteListener(listener);
		listener.onWritePossible();   // Workaround for https://issues.apache.org/bugzilla/show_bug.cgi?id=55381
	}
}
