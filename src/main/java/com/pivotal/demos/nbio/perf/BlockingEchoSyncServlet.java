package com.pivotal.demos.nbio.perf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a basic synchronous servlet that echo's the request data to the
 * response.  This is used to show the difference between the sync and nbio-async.
 */
@WebServlet(urlPatterns="/non-blocking-io/BlockingEchoSyncServlet")
public class BlockingEchoSyncServlet extends HttpServlet
{
	private static final long serialVersionUID = -1442726028876944347L;
	public static final int BUFFER_SIZE = 8 * 1024;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true)
        {
            int read = request.getInputStream().read(buffer, 0, BUFFER_SIZE);
            System.out.println("Read [" + read + "]");
            if (read < 0)
                break;
            response.getOutputStream().write(buffer, 0, read);
        }
    }
}
