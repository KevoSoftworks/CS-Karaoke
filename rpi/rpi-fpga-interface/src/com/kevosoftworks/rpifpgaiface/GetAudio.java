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
	public GetAudio() {
		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
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
		int numBytesRead;
		byte[] targetData = new byte[microphone.getBufferSize() / 5];
		PlayAudio player = new PlayAudio();
		
		FileOutputStream stream = null;
		
		/*
		try {
			stream = new FileOutputStream("/test.wav");	
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		while (true) {
			numBytesRead = microphone.read(targetData, 0, targetData.length);
			if (numBytesRead == -1)	break;
			
			/*
			try {
				stream.write(targetData);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
			
			player.playSound(targetData, numBytesRead);
		}
	}
	
	public static void main(String[] args) {
		new GetAudio().start();
	}
}
