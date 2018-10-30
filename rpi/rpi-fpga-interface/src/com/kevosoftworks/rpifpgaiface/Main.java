package com.kevosoftworks.rpifpgaiface;

public class Main implements Runnable{
	
	static boolean running = false;
	Thread thread;	
	SPIInterface si;
	Song s;
	GetAudio a;
	
	public Main(String file){
		si = new SPIInterface();
		s = new Song(si, file);
	}
	
	public Main(){
		si = new SPIInterface();
		s = new Song(si);
		a = new GetAudio();
	}

	@Override
	public void run() {
		a.start();
		s.convertOriginal();
		try {
			s.sendOriginal();
		} catch (Exception e) {
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
