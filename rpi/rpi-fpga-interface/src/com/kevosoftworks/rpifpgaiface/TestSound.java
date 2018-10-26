package com.kevosoftworks.rpifpgaiface;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

public class TestSound {
	public static Mixer mixer;
	public static Clip clip;
	
	public static void main(String[] args) {
		Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixInfos) {
			System.out.println(info.getName() + "--------------" + info.getDescription());
		}
		
		// 0 is default output (maybe input as well).
		mixer = AudioSystem.getMixer(mixInfos[0]);
		
		DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
		try {
			clip = (Clip) mixer.getLine(dataInfo);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		try {
			// finds resource relative to the class location
			TestSound c = new TestSound();
	      	Class cls = c.getClass();	
	      	URL soundURL = cls.getResource("audio.wav");

			System.out.println(soundURL);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
			clip.open(audioStream);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		clip.start();
		
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		} while(clip.isActive());
	}
}
