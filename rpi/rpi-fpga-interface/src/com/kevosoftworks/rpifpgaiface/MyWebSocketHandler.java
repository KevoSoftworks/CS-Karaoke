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
	public static HashMap<String, String> songtofile = new HashMap<String, String>();
	public static String activeSong = "";
	private Session wsSession;
	
	Song s;

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
        	wsSession = session;
            session.getRemote().sendString("Hello Webbrowser");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //put all the known songs into the hashmap with their filenames
        songtofile.put("I Want It That Way - Backstreet Boys", "i-want-it-that-way.wav");
        songtofile.put("Never Gonna Give You Up - Rick Astley", "never-gonna-give-you-up.wav");
        songtofile.put("Don't Stop Believin' - Journey", "dont-stop-believin.wav");
        songtofile.put("Wonderwall - Oasis", "wonderwall.wav");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);

        if (message.charAt(0) == '0') {
        	// Update the active song.
        	System.out.println("Song update");
        	activeSong = songtofile.get(message.substring(1, message.length()));
        	System.out.println(activeSong);
        	s = new Song(Main.si, activeSong);
        	while(!s.isReady()){
        		//Do Nothing
        	}
        	try {
				wsSession.getRemote().sendString("SongReady");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (message.equals("Starting")) {
        	// Game has started
        	// Start recording should be here V
        	
        	System.out.println("Game has started");
	
        	// send random numbers for score.
            new Thread()
            {
            	//Just added to test the score, can be removed untill line 82
            	public void run() {
                   	try {
						wsSession.getRemote().sendString("Thread is created");
						System.out.println("Thread is createdd");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    while (true) {
                    	updateScore((int) (Math.random() * 100));
                    	
                    	try {
    						Thread.sleep(1000);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                    }
                }
            }.start();
            
            System.out.println("Start recording song");
        	s.startRecord();
            
        } else if (message.equals("Ending")) {
        	// Game has ended now.
        	System.out.println("Game has ended");
        	activeSong = "";
        }
    }

    // Updates the score for the client.
    public void updateScore(int score) {
    	try {
			wsSession.getRemote().sendString("Score so far: "+String.valueOf(score));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}