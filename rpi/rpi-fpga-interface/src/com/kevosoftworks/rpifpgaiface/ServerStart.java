package com.kevosoftworks.rpifpgaiface;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ServerStart extends Thread {
	public static void main(String[] args) {
		new ServerStart().start();
	}
	
	@Override
	public void run() {
		go();
	}
	
	public void go() {
		Server server = new org.eclipse.jetty.server.Server(8080);
        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(MyWebSocketHandler.class);
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