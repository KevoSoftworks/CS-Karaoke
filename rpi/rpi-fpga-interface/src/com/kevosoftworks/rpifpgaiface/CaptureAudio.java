package com.kevosoftworks.rpifpgaiface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class CaptureAudio {
	static boolean songIsPlaying; 
	
	public static void main(String[] args) {
		startCapture();
		
	}
	
	public static void startCapture() {
		long startTime = System.currentTimeMillis();
		songIsPlaying = true;
		float sampleRate = 8000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format =  new AudioFormat(sampleRate, 
		  sampleSizeInBits, channels, signed, bigEndian);
		int test = 0;

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();
			
			int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
			System.out.println("buffer size: " + bufferSize);
			System.out.println("frame size: " + format.getFrameSize());
			
			byte buffer[] = new byte[bufferSize/8];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while (songIsPlaying) {
				int count = line.read(buffer, 0, buffer.length);
				if (count > 0) {
					long time = System.currentTimeMillis() - startTime;
					//new Packet(buffer, true, false, time);
					System.out.println("new packet with: " + buffer.toString());
					System.out.println("time: " + time);
					test++;
					if(test == 20) {
					   stopCapture();
					}
					//out.write(buffer, 0, count);
				}
			}
			out.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stopCapture() {
		songIsPlaying = false;
		System.out.println("stopped");
	}
	
	
}
