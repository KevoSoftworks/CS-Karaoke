package com.kevosoftworks.rpifpgaiface;

import com.kevosoftworks.rpifpgaiface.websocket.WSServer;

public class Main implements Runnable{
	
	public final static boolean ISPI = true;
	
	static boolean running = false;
	Thread thread;	
	public static SPIInterface si = null;
	WSServer server;
	
	public Main(){
		if(ISPI) si = new SPIInterface();
		server = new WSServer();
	}

	@Override
	public void run() {
		//WSServer handles this
	}
	
	public static void main(String[] args){
		new Main().start();
	}
	
	public synchronized void start(){
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop(){
		if(!running) return;
		running = false;
		try{
			thread.join();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
