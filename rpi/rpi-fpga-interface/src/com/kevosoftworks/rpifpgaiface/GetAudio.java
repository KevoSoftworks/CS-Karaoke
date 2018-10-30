package com.kevosoftworks.rpifpgaiface;

import java.io.FileOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class GetAudio {
	TargetDataLine microphone;
	SourceDataLine speakers;
	SPIInterface spi = new SPIInterface();
	
	public GetAudio() {
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		
		for (Mixer.Info info : mixInfos) {
			System.out.println(info.getName() + "--------------" + info.getDescription());
		}
		
		// change the index in the mixinfos[] until it has the correct driver.
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
	
	public void start() {
		long startTime = System.currentTimeMillis();
		int numBytesRead;
		byte[] targetData = new byte[1020];
		System.out.println(microphone.getBufferSize());
		FileOutputStream stream = null;
		
		while (true) {
			numBytesRead = microphone.read(targetData, 0, targetData.length);
			speakers.write(targetData.clone(), 0, targetData.length);
			long time = System.currentTimeMillis() - startTime;
			
			try {
				Packet pack = new Packet(targetData, true, false, time);
				byte[] res = spi.readByte(pack.getPacket());
				StringBuilder sb = new StringBuilder();
			    for (int i = 0; i < 4; i++) {
			        sb.append(String.format("%02X ", res[i]));
			    }
			    System.out.println(sb.toString());
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
