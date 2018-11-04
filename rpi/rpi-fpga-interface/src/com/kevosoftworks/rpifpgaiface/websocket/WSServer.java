package com.kevosoftworks.rpifpgaiface.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WSServer extends Thread {	
	public WSServer(){
		Server server = new org.eclipse.jetty.server.Server(8080);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WSHandler.class);
            }
        };
        
        server.setHandler(wsHandler);
        try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        try {
			server.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}