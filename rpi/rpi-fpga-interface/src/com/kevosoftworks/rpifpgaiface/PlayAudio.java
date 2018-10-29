package com.kevosoftworks.rpifpgaiface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class PlayAudio {
	SourceDataLine speakers;
	
	public PlayAudio() {
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		
		// change the index in the mixinfos[] until it has the correct driver.
		Mixer mixer = AudioSystem.getMixer(mixInfos[0]);
		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
		
		try {
			speakers = (SourceDataLine) mixer.getLine(sourceInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		try {
			speakers = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			speakers.open(format);
			speakers.start();
		} catch (Exception e) {
			System.err.println(e);
		}	
		
//		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
//		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
		
	}
	
	public void playSound(byte[] bytes, int numBytesRead) {
		speakers.write(bytes, 0, numBytesRead);
	}
}
