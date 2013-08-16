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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 1. Start Async
		final AsyncContext asyncContext = req.startAsync();
		final ServletInputStream servletInputStream = asyncContext.getRequest().getInputStream();
		resp.setCharacterEncoding("utf-8");
		
		// 2. Add Read Listener to get user's input
		ReadListener listener = new ReadListener() {
			
			private StringBuilder sb = new StringBuilder();  // buffer user's request
			
			public void onDataAvailable() throws IOException {
				// 4. Read all the data that is available, may be called multiple times
				try {
		            byte[] b = new byte[8192];
		            int read = 0;
		            do {
		                read = servletInputStream.read(b);
		                if (read == -1) {
		                    break;
		                }
		                sb.append(new String(b, 0, read));
		            } while (servletInputStream.isReady());
		        } catch (Exception ex) {
		            ex.printStackTrace(System.err);
		            asyncContext.complete();
		        }
			}
			
			// 5. Called when all data has been read			
			public void onAllDataRead() throws IOException {
				System.out.println("onAllDataRead");
				final ServletOutputStream outputStream = asyncContext.getResponse().getOutputStream();
				
				int randomDataSize = 512;
				if (sb.length() > 0) {
					try {
						randomDataSize = Integer.parseInt(sb.toString());
					} catch (NumberFormatException ex) {
						// ignore, use default size
					}
				}
				randomDataSize = (randomDataSize > 1_000_000) ? 1_000_000 : randomDataSize;
				
				final String data = RandomStringUtils.random(randomDataSize);
				
				// 6. Configure a write listener to echo the response
				WriteListener listener = new WriteListener() {
					public void onWritePossible() throws IOException {
						// 7. Write output
						if (outputStream.isReady()) {
							outputStream.write(data.getBytes("utf-8"));
						}
						
						// 8. Call complete, to signal we are done
						asyncContext.complete();
					}
					
					public void onError(Throwable throwable) {
						throwable.printStackTrace(System.err);
						asyncContext.complete();
					}
				};
				outputStream.setWriteListener(listener);
			}
			
			public void onError(Throwable throwable) {
				throwable.printStackTrace(System.err);
				asyncContext.complete();
			}
		};
		
		// 3. Add listener, starts Non-blocking IO support
		servletInputStream.setReadListener(listener);
	}
	
}
