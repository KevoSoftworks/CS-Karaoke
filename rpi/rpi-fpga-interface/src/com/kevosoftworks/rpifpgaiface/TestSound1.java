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
			TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();
			
			SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

			int numBytesRead;
			byte[] targetData = new byte[targetLine.getBufferSize() / 5];

			while (true) {
				numBytesRead = targetLine.read(targetData, 0, targetData.length);
				
				for (byte b: targetData) {
					System.out.printf("0x%02X", b);
				}
				// System.out.println(numBytesRead);
				if (numBytesRead == -1)	break;

				sourceLine.write(targetData, 0, numBytesRead);
				// System.out.println(sourceLine);
				
			}
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}
