package com.kevosoftworks.rpifpgaiface;

import java.io.IOException;

public class Main implements Runnable{
	
	static boolean running = false;
	Thread thread;
	boolean fast = false;
	boolean sanic = false;
	long timeStart;
	int blen;
	
	int totalLoops = 2000;
	
	SPIInterface si;
	
	public Main(boolean fast, boolean sanic, int blen, int loops){
		this.fast = fast;
		this.sanic = sanic;
		this.blen = blen;
		this.totalLoops = loops;
		
		System.out.println("Lala");
		System.out.println(fast + "; " + sanic + blen + "; " + loops);
		si = new SPIInterface(totalLoops, blen);
		
	}

	@Override
	public void run() {
		timeStart = System.nanoTime();
		while(running){
			try {
				si.read();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(!sanic)Thread.sleep(fast ? 1 : 500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long timeStop = System.nanoTime();
		long tt = timeStop - timeStart;
		long tpp = tt / totalLoops;
		int bytesSent = blen * totalLoops;
		double kbps = ((bytesSent * 8/1000) / (double)((double)tt/1000000000d));
		System.out.println("Avg. time per packet: " + (tpp/1000) + "us; Total time: " + (tt/1000000) + "ms");
		System.out.println("Bits sent: " + bytesSent*8 + " (" + (bytesSent/1000) + "kb)");
		System.out.println("Transfer speed: " + kbps/8d + "kb/s (" + kbps + "kbps)");
	}
	
	public static void main(String[] args){
		boolean fast = false;
		boolean sanic = false;
		int blen = 1;
		int loops = 1;
		if(args.length >= 1){
			fast = true;
			if(args[0].equals("sanic")) sanic = true;
			if(args.length >= 2) blen = Integer.parseInt(args[1]);
			if(args.length >= 3) loops = Integer.parseInt(args[2]);
		}
		new Main(fast, sanic, blen, loops).start();
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
