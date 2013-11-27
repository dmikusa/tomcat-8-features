package com.pivotal.demos.websockets;

import java.util.Arrays;
import java.util.List;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class CsvDecoder implements Decoder.Text<List<String>> {

	@Override
	public void init(EndpointConfig endpointConfig) {
	}

	@Override
	public void destroy() {
	}

	@Override
	public List<String> decode(String s) throws DecodeException {
		return Arrays.asList(s.split(","));
	}

	@Override
	public boolean willDecode(String s) {
		return (s.contains(","));
	}

}
