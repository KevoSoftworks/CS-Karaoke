package com.kevosoftworks.rpifpgaiface.websocket;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import com.kevosoftworks.rpifpgaiface.Main;
import com.kevosoftworks.rpifpgaiface.Song;
import com.kevosoftworks.rpifpgaiface.SongType;
import com.kevosoftworks.rpifpgaiface.websocket.packet.WSPacket;
import com.kevosoftworks.rpifpgaiface.websocket.packet.WSPacketDataType;
import com.kevosoftworks.rpifpgaiface.websocket.packet.WSPacketType;
import com.kevosoftworks.rpifpgaiface.websocket.packet.WSPacketMsgType;

@WebSocket
public class WSHandler {
	public HashMap<String, String> songtofile = new HashMap<String, String>();
	public String activeSong = "";
	private Session wsSession;
	
	Song s;
	SongType st = SongType.NOVOCAL;

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
            session.getRemote().sendString(
            		new WSPacket(WSPacketType.SERVER_OPEN).getFinalisedPacket()
            	);
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
    public void onMessage(String message) throws IOException {
    	System.out.println(message);
    	WSPacket pin = new WSPacket(new JSONObject(message));
    	if(pin.getType() != WSPacketType.CLIENT_MESSAGE) {
    		//Do something.
    	} else {
    		switch(pin.getMsgType()) {
    			case SONG_LOAD:
    				this.activeSong = songtofile.get(pin.getData());
    				this.s = new Song(Main.si, activeSong, st);
    				while(!s.isReady()) {} //Do Nothing
    				this.wsSession.getRemote().sendString(
    						new WSPacket(
    								WSPacketType.SERVER_MESSAGE,
    								WSPacketMsgType.SONG_READY
    							).getFinalisedPacket()
    					);
    				break;
    			case SONG_START:
    				new Thread(new Runnable(){
    					public void run(){
    						s.startRecord();
    					}
    				}).start();
    				//Maybe send something back to confirm.
    				break;
    			case SONG_BREAK:
    				s.stopRecord();
    				break;
    			case SCORE:
    				this.wsSession.getRemote().sendString(
    						new WSPacket(
    								WSPacketType.SERVER_MESSAGE,
    								WSPacketMsgType.SCORE,
    								WSPacketDataType.TEXT,
    								Integer.toString(s.getCurrentScore())
    							).getFinalisedPacket()
    					);
    				break;
    			case MODE_NOVOCAL:
    				st = SongType.NOVOCAL;
    				break;
    			case MODE_ORIGINVOCAL:
    				st = SongType.ORIGINVOCAL;
    				break;
    			case MODE_YOURVOCAL:
    				st = SongType.YOURVOCAL;
    				break;
    			case MODE_AUTOTUNE:
    				st = SongType.AUTOTUNE;
    				break;
    			default:
    				//Do something
    				break;
    		}
    	}
    }
}
