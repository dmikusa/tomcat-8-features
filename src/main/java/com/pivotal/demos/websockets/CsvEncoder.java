package com.pivotal.demos.websockets;

import java.util.List;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class CsvEncoder implements Encoder.Text<List> {

	@Override
	public void init(EndpointConfig endpointConfig) {
	}

	@Override
	public void destroy() {
	}

	// Must use List and not List<String>, workaround for Tomcat NPE
	@Override
	public String encode(List list) throws EncodeException {
		StringBuilder sb = new StringBuilder();
		for (Object item : list) {
			sb.append((String) item);
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}

}
