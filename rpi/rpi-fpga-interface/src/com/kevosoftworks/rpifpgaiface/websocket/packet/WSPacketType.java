package com.kevosoftworks.rpifpgaiface.websocket.packet;

public enum WSPacketType {
	
	NONE(0x00),
	SERVER_BROADCAST(0x10),
	CLIENT_BROADCAST(0x11),
	SERVER_MESSAGE(0x20),
	CLIENT_MESSAGE(0x21),
	SERVER_PING(0x30),
	SERVER_PONG(0x31),
	CLIENT_PING(0x32),
	CLIENT_PONG(0x33),
	SERVER_OPEN(0x40),
	CLIENT_OPEN(0x41),
	SERVER_CLOSE(0x42),
	CLIENT_CLOSE(0x43);
	
	private final int code;
	
	WSPacketType(int code){
		this.code = code;
	}
	
	public static WSPacketType fromCode(int code) {
		for(WSPacketType t : values()) {
			if(t.getCode() == code) {
				return t;
			}
		}
		return null;
	}
	
	public int getValue() {
		return this.code;
	}
	
	public int getCode() {
		return this.getValue();
	}

}
