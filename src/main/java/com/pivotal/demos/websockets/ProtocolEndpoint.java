package com.pivotal.demos.websockets;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

// marks the class as an endpoint for the container. Also, defines our endpoint
//  location, relative to the context path
@ServerEndpoint(value="/websockets/protocol", subprotocols={"stomp", "wamp"})
public class ProtocolEndpoint {

	// called when connection is opened, use blocking api to send a response message
	@OnOpen
	public void onOpen(Session session) {
		try {
			String protocol = session.getNegotiatedSubprotocol();
			if ("".equals(protocol)) {
				session.getBasicRemote().sendText("Welcome!");
			} else {
				session.getBasicRemote().sendText("Negotiated protocol [" + session.getNegotiatedSubprotocol() + "]" +
					  						  ", but protocol is not implemented yet.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called on close, we do nothing.
	@OnClose
	public void onClose() {
		// Clean up resources here
	}

	@OnMessage
	public void onMessage(Session session, String msg) {
		try {
			session.getBasicRemote().sendText("Echo [" + msg + "]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
