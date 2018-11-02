package com.kevosoftworks.rpifpgaiface;

public class Main implements Runnable{
	
	static boolean running = false;
	Thread thread;	
	public static SPIInterface si;
	ServerStart serverStart;
	
	public Main(){
		si = new SPIInterface();
		serverStart = new ServerStart();
		serverStart.start();
	}

	@Override
	public void run() {
		
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
