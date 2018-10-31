package com.kevosoftworks.rpifpgaiface;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class MyWebSocketHandler {
	HashMap<String, String> songtofile = new HashMap<String, String>();

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {
            session.getRemote().sendString("Hello Webbrowser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //put all the known songs into the hashmap with their filenames
        songtofile.put("I Want It That Way - Backstreet Boys", "i-want-it-that-way");
        songtofile.put("Never Gonna Give You Up - Rick Astley", "never-gonna-give-you-up");
        songtofile.put("Don't Stop Believin' - Journey", "dont-stop-believin");
        
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        if(message.charAt(0) == 0) {
        	//get the filename of the song the user selected
        	String songfile = songtofile.get(message.substring(1, message.length()-1));
            System.out.println(songfile);
        } else if(message.equals("Starting")) {
        	//the game is starting so the karaoke game should start
        	
        }    
    }
}