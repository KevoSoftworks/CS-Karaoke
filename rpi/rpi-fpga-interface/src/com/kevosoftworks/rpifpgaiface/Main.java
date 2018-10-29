package com.kevosoftworks.rpifpgaiface;

import java.io.IOException;

public class Main implements Runnable{
	
	static boolean running = false;
	Thread thread;	
	SPIInterface si;
	Song s;
	
	public Main(String file){
		si = new SPIInterface();
		s = new Song(si, file);
	}
	
	public Main(){
		si = new SPIInterface();
		s = new Song(si);
	}

	@Override
	public void run() {
		s.convertOriginal();
		try {
			s.sendOriginal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		if(args.length == 1){
			new Main(args[0]).start();
		} else {
			new Main().start();
		}
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
