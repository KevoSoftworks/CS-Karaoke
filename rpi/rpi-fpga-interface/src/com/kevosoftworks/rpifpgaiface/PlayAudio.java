package com.kevosoftworks.rpifpgaiface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class PlayAudio {
	SourceDataLine speakers;
	
	public PlayAudio() {
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
		try {
			speakers = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			speakers.open(format);
			speakers.start();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void playSound(byte[] bytes, int numBytesRead) {
		speakers.write(bytes, 0, numBytesRead);
	}
}
