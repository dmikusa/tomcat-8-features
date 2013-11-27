package com.pivotal.demos.websockets;

import java.io.IOException;
import java.util.List;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

// marks the class as an endpoint for the container. Also, defines our endpoint
//  location, relative to the context path
@ServerEndpoint(value="/websockets/csv", encoders={CsvEncoder.class}, decoders={CsvDecoder.class})
public class CsvEndpoint {

	// called when connection is opened, use blocking api to send a response message
	@OnOpen
	public void onOpen(Session session) {
		try {
			session.getBasicRemote().sendText("Welcome!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnMessage
	public void onMessage(Session session, List<String> messageItems) {
		try {
			session.getBasicRemote().sendText("Echo List " + messageItems);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
