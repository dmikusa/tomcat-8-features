package com.pivotal.demos.websockets;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

// Interface implemented is detected by the container and used to determine
//  which endpoints get created.  Because this class exist, only classes that
//  are returned from the two methods below will be setup as endpoints.
public class EventsServerApplicationConfig implements ServerApplicationConfig {

	// Return our list of interface based endpoints.  This also defines their locations
	@Override
	public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> scanned) {
		Set<ServerEndpointConfig> cfgs = new HashSet<>();
		// define out interface based endpoint and map it's location
		cfgs.add(ServerEndpointConfig.Builder
		            .create(EventsInterfaceEndpoint.class, "/websockets/events/interface")
		            .build());
		return cfgs;
	}

	// Return our annotation based endpoint, mapping is defined in its annotation
	//  NOTE - we must return this here because this class exists.  If there were no
	//         ServerApplicationConfig classes present then then the container would
	//         automatically locate annotation based endpoints
	@Override
	public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
		Set<Class<?>> cfgs = new HashSet<>();
		cfgs.add(EventsAnnotationEndpoint.class);
		return cfgs;
	}

}
