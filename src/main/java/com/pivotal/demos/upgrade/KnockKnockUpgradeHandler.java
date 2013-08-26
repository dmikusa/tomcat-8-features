package com.pivotal.demos.upgrade;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.WebConnection;

public class KnockKnockUpgradeHandler implements HttpUpgradeHandler {

	@Override
	public void init(WebConnection con) {
		// use input / output streams for communications (blocking or non-blocking api can be used)
		try (
			BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
			ServletOutputStream output = con.getOutputStream();
		) {
			// read the client's standard greeting
			String greeting = input.readLine();
			if ("knock knock".equals(greeting.toLowerCase())) {
				output.write("Who's there?\r\n".getBytes());
				output.flush();

				// read the joke name
				String jokeName = input.readLine();
				output.write((jokeName + " who?\r\n").getBytes());
				output.flush();

				// read the punch line
				String punchLine = input.readLine();
				System.out.println("Read punchline [" + punchLine + "]");

				// end the session
				output.write("Lol. Bye!\r\n".getBytes());
				output.flush();
			} else {
				output.write("Invalid greeting, please follow proper etiquette.".getBytes());
			}
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
	}

	@Override
	public void destroy() {
		// do nothing
	}

}
