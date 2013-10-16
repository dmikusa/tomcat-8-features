package com.pivotal.demos.websockets;

import java.util.Arrays;
import java.util.List;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CsvDecoder implements Decoder.Text<List> {

	@Override
	public void init(EndpointConfig endpointConfig) {
	}

	@Override
	public void destroy() {
	}

	// Must use List and not List<String>, workaround for Tomcat NPE
	@Override
	public List decode(String s) throws DecodeException {
		return Arrays.asList(s.split(","));
	}

	@Override
	public boolean willDecode(String s) {
		return (s.contains(","));
	}

}
