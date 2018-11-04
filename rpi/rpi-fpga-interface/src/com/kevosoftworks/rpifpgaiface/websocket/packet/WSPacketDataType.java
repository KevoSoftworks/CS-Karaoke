package com.kevosoftworks.rpifpgaiface.websocket.packet;

public enum WSPacketDataType {
	
	BLANK(0x00),
	NUMBER(0x01),
	TEXT(0x02),
	JSON(0x03);
	
	private final int code;
	
	WSPacketDataType(int code){
		this.code = code;
	}
	
	public static WSPacketDataType fromCode(int code) {
		for(WSPacketDataType t : values()) {
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
