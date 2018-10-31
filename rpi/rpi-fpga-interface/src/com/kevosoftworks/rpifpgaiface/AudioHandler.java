package com.kevosoftworks.rpifpgaiface;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioHandler {
	
	TargetDataLine microphone;
	SourceDataLine speakers;
	
	int bufferSize;
	
	public AudioHandler(int sampleRate, int bits, int channels, int bufferSize){
		this.bufferSize = bufferSize;
		
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, bits, channels, 2, sampleRate, false);
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();		
		// change the index in the mixinfos[] until it has the correct driver.
		// Index 2 works for the pi.
		Mixer mixer = AudioSystem.getMixer(mixInfos[2]);
		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			microphone = (TargetDataLine) mixer.getLine(targetInfo);
			microphone.open(format);
			microphone.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
		
		try {
			speakers = (SourceDataLine) mixer.getLine(sourceInfo);
			speakers.open(format);
			speakers.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] recordBuffer(){
		int numBytesRead;
		byte[] targetData = new byte[this.bufferSize];
		
		numBytesRead = microphone.read(targetData, 0, this.bufferSize);
		if(numBytesRead < 0){} //do something
		return targetData;
	}
	
	public void playBuffer(byte[] buf){
		speakers.write(buf, 0, buf.length);
	}
}
