package com.kevosoftworks.rpifpgaiface;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class GetAudio {
	TargetDataLine microphone;
	SPIInterface spi = new SPIInterface();
	public GetAudio() {
		AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		try {
			microphone = (TargetDataLine) AudioSystem.getLine(targetInfo);
			microphone.open(format);
			microphone.start();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void start() {
		long startTime = System.currentTimeMillis();
		int numBytesRead;
		byte[] targetData = new byte[1020];
		System.out.println(microphone.getBufferSize());
		PlayAudio player = new PlayAudio();
		FileOutputStream stream = null;
		
		while (true) {
			numBytesRead = microphone.read(targetData, 0, targetData.length);
			player.playSound(targetData, numBytesRead);
			long time = System.currentTimeMillis() - startTime;
			try {
				Packet pack = new Packet(targetData, true, false, time);
				spi.readByte(pack.getPacket());
				System.out.println(pack.getPacket().length);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (numBytesRead == -1)	break;
		}
	}
	
	public static void main(String[] args) {
		new GetAudio().start();
	}
}
