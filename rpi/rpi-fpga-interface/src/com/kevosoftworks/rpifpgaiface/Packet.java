package com.kevosoftworks.rpifpgaiface;

public class Packet {
	
	/*
	 * Header layout:
	 * 
	 * xy....zz zzzzzzzz	x: boolean isRec
	 * tttttttt tttttttt	y: boolean sendScore
	 * 						z: total packet length (max 2^10)
	 * 						t: time frame (in 0.1s)
	 * 						.: unused
	 * 
	 */
	
	byte[] packet;
	
	public Packet(byte[] data, boolean isRec, boolean sendScore, long time, int zeroCross){
		byte[] p = new byte[6];
		
		p[0] = (byte)((data.length + 5) & 0xFF);
		p[1] = (byte)(((data.length + 5) >> 8) & 0xFF);
		p[2] = (byte)(time & 0xFF);
		p[3] = (byte)((time >> 8) & 0xFF);
		p[4] = (byte)(zeroCross & 0xFF);
		p[5] = (byte)((zeroCross >> 8) & 0xFF);
		if(isRec) p[1] |= 0b10000000;
		if(sendScore) p[1] |= 0b01000000;
		
		this.packet = new byte[p.length + data.length];
		System.arraycopy(p, 0, this.packet, 0, p.length);
		System.arraycopy(data, 0, this.packet, p.length, data.length);

	}
	
	public byte[] getPacket(){
		return this.packet;
	}

}
