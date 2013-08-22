package com.pivotal.demos.upgrade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class KnockKnockClient {
	public static void main(String[] args) {
		new KnockKnockClient().run();
	}

	public void run() {
		try (
			Socket socket = new Socket("localhost", 8080);
			BufferedReader resp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter req = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		) {
			// sent HTTP request w/upgrade
			System.out.print("Sending upgrade request...");
			req.print("GET /tomcat-8-demos/protocol-upgrade/KnockKnockServlet HTTP/1.1\r\n");
			req.print("Host: localhost\r\n");
			req.print("Upgrade: knock-knock\r\n");
			req.print("Connection: Upgrade\r\n");
			req.print("\r\n");
			req.flush();
			System.out.println(" done.");

			// read response
			System.out.println("Response from server:");
			while (true) {
				String line = resp.readLine();
				System.out.println(line);
				if ("".equals(line)) {
					break;
				}
			}

			// interact with upgrade
			System.out.println("Initiating knock-knock protocol...");
			req.println("Knock Knock");
			req.flush();
			System.out.println(resp.readLine());
			req.println("Madam");
			req.flush();
			System.out.println(resp.readLine());
			req.println("Madam foot got caught in the door!");
			req.flush();
			System.out.println(resp.readLine());
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
