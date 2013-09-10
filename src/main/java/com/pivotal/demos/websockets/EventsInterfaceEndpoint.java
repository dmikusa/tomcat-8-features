package com.pivotal.demos.websockets;

import java.io.IOException;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

// By extending this interface, it is marked as a WebSocket endpoint
public class EventsInterfaceEndpoint extends Endpoint {

	// called when connection is opened.
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		// Basic == blocking API,  Async == non-blocking
		final RemoteEndpoint.Basic remote = session.getBasicRemote();

		// We send a response welcome message...
		try {
			remote.sendText("Welcome!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ... and setup a handler for full messages.
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				try {
					// echo's the user's message back
					remote.sendText("Echo [" + message + "]");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
