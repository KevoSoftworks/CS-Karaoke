package com.kevosoftworks.rpifpgaiface.websocket.packet;

public enum WSPacketMsgType {
	
	BLANK(0x00),
	REQUEST(0x01),
	MIXED(0x02),
	ERROR(0x03),
	WARN(0x04),
	VERSION(0x05),
	
	SONG_LOAD(0x10),
	SONG_READY(0x11),
	SONG_START(0x12),
	SONG_END(0x13),
	SONG_BREAK(0x14),
	
	SCORE(0x20),
	
	MODE_NOVOCAL(0x30),
	MODE_ORIGINVOCAL(0x31),
	MODE_YOURVOCAL(0x32),
	MODE_AUTOTUNE(0x33);
	
	private final int code;
	
	WSPacketMsgType(int code){
		this.code = code;
	}
	
	public static WSPacketMsgType fromCode(int code) {
		for(WSPacketMsgType t : values()) {
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
