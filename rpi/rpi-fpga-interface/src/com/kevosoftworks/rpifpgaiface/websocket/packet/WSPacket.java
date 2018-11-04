package com.kevosoftworks.rpifpgaiface.websocket.packet;

import org.json.JSONObject;

public class WSPacket {
	
	WSPacketType type;
	WSPacketMsgType msgType;
	WSPacketDataType dataType;
	String data;
	
	public WSPacket(JSONObject p) {
		this(
				WSPacketType.fromCode(p.getInt("type")),
				WSPacketMsgType.fromCode(p.getInt("msgType")),
				WSPacketDataType.fromCode(p.getInt("dataType")),
				p.getString("data")
			);
	}
	
	public WSPacket(WSPacketType type) {
		this(type, WSPacketMsgType.BLANK, WSPacketDataType.BLANK, "");
	}
	
	public WSPacket(WSPacketType type, WSPacketMsgType msgType) {
		this(type, msgType, WSPacketDataType.BLANK, "");
	}
	
	public WSPacket(WSPacketType type, WSPacketMsgType msgType, WSPacketDataType dataType, String data) {
		this.type = type;
		this.msgType = msgType;
		this.dataType = dataType;
		this.data = data;
	}
	
	public String getFinalisedPacket() {
		JSONObject p = new JSONObject();
		p.put("type", this.type.getValue());
		p.put("msgType", this.msgType.getCode());
		p.put("dataType", this.dataType.getCode());
		p.put("data", this.data);
		
		return p.toString();
	}
	
	public WSPacketType getType() {
		return this.type;
	}
	
	public WSPacketMsgType getMsgType() {
		return this.msgType;
	}
	
	public WSPacketDataType getDataType() {
		return this.dataType;
	}
	
	public String getData() {
		return this.data;
	}

}
