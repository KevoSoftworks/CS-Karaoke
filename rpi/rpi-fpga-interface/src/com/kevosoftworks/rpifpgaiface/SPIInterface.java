package com.kevosoftworks.rpifpgaiface;

import java.io.IOException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.util.Console;

public class SPIInterface {
	
	public SpiDevice spi;
	protected final Console console = new Console();
	GpioController gpio;
	GpioPinDigitalOutput pin;
	
	public SPIInterface(){
		gpio = GpioFactory.getInstance();
		pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
		console.title("Test connection");
		console.promptForExit();
		if(!this.initialise()){
			System.out.println("Cannot init SPI interface!");
		}
	}
	
	public boolean initialise(){
		try {
			spi = SpiFactory.getInstance(SpiChannel.CS0,
			        SpiDevice.DEFAULT_SPI_SPEED,
			        SpiDevice.DEFAULT_SPI_MODE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void read() throws IOException, InterruptedException {
        console.print(0xff);
        this.sendByte(0xff);
        pin.toggle();
        Thread.sleep(500);
    }
	
	public boolean sendData(byte[] data){
		boolean res = true;
		for(byte b:data){
			res = this.sendByte(b);
			if(!res){
				System.out.println("Error in sending data");
				return res;
			}
		}
		return res;
	}
	
	public boolean sendByte(int b){
		try {
			spi.write((byte)b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
