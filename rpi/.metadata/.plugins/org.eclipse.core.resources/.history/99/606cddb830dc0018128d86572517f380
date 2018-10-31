package com.kevosoftworks.rpifpgaiface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class PlayAudio {
	
	
	public PlayAudio() {
		AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		
		for (Mixer.Info info : mixInfos) {
			System.out.println(info.getName() + "--------------" + info.getDescription());
		}
		
		// change the index in the mixinfos[] until it has the correct driver.
		Mixer mixer = AudioSystem.getMixer(mixInfos[2]);
		
		
//		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
//		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
		
	}
	
	public void playSound(byte[] bytes, int numBytesRead) {
		speakers.write(bytes, 0, numBytesRead);
	}
}
