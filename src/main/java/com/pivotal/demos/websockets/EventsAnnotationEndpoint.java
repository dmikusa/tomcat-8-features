package com.pivotal.demos.websockets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

// marks the class as an endpoint for the container. Also, defines our endpoint
//  location, relative to the context path
@ServerEndpoint("/websockets/events/annotation")
public class EventsAnnotationEndpoint {

	// called when connection is opened, use blocking api to send a response message
	@OnOpen
	public void onOpen(Session session) {
		try {
			session.getBasicRemote().sendText("Welcome!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called on close, we do nothing.
	@OnClose
	public void onClose() {
		// Clean up resources here
	}

	// called when a full message is received, we echo back to client using blocking api
	@OnMessage
	public void onMessage(Session session, String message) {
		try {
			session.getBasicRemote().sendText("Echo [" + message + "]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
