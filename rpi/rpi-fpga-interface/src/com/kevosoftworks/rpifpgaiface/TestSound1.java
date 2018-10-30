package com.kevosoftworks.rpifpgaiface;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class TestSound1 {
	public static void main(String[] args) {
		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

		try {
			TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(targetInfo);
			microphone.open(format);
			microphone.start();
			
			SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			speakers.open(format);
			speakers.start();
			
			int numBytesRead;
			byte[] targetData = new byte[microphone.getBufferSize() / 5];

			while (true) {
				numBytesRead = microphone.read(targetData, 0, targetData.length);
				
				for (byte b: targetData) {
					// System.out.printf("0x%02X", b);
				}
				// System.out.println(numBytesRead);
				if (numBytesRead == -1)	break;

				speakers.write(targetData, 0, numBytesRead);
				// System.out.println(speakers);
				
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
