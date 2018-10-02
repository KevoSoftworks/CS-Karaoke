package com.kevosoftworks.rpifpgaiface;

import java.io.IOException;

public class Main implements Runnable{
	
	boolean running = false;
	Thread thread;
	
	SPIInterface si;
	
	public Main(){
		System.out.println("Lala");
		si = new SPIInterface();
	}

	@Override
	public void run() {
		while(running){
			try {
				si.read();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
