package com.kevosoftworks.rpifpgaiface;

import com.pi4j.wiringpi.Spi;

public class SPIInterface {
	
	private static final int CHANNEL = 0;
	private static final int FREQ = 16000000;
	
	public SPIInterface(){
		if(!this.initialise()){
			System.out.println("Cannot init SPI interface!");
		}
	}
	
	public boolean initialise(){
		int fd = Spi.wiringPiSPISetup(CHANNEL, FREQ);
        if (fd <= -1) {
            System.out.println(" ==>> SPI SETUP FAILED");
            return false;
        }
        return true;
	}
	
	public byte[] readByte(byte[] b){
		byte[] ret = b.clone();
		Spi.wiringPiSPIDataRW(0, ret);
		return ret;
	}
	
	public String byteToString(byte b){
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}

}
