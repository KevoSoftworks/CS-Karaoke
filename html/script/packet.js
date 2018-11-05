/*
* WARNING! All declarations in this file MUST match the equivalent packet.php file on the server side!
* If you make any modification in this file, make the identical modification in the packet.php file
* server side. If you do not, discrepancies will occur!
*
* Note: this document will lack any documentation on the packets. Please see packet.php on the server side
* for documentation and some explainations on the packets and functions.
*/

var PacketType = {
	NONE: 0x00,
	SERVER_BROADCAST: 0x10,
	CLIENT_BROADCAST: 0x11,
	SERVER_MESSAGE: 0x20,
	CLIENT_MESSAGE: 0x21,
	SERVER_PING: 0x30,
	SERVER_PONG: 0x31,
	CLIENT_PING: 0x32,
	CLIENT_PONG: 0x33,
	SERVER_OPEN: 0x40,
	CLIENT_OPEN: 0x41,
	SERVER_CLOSE: 0x42,
	CLIENT_CLOSE: 0x43
};

var PacketMsgType = {
	BLANK: 0x00,
	REQUEST: 0x01,
	MIXED: 0x02,
	ERROR: 0x03,
	WARN: 0x04,
	VERSION: 0x05,
	SONG_LOAD: 0x10,
	SONG_READY: 0x11,
	SONG_START: 0x12,
	SONG_END: 0x13,
	SONG_BREAK: 0x14,
	SCORE: 0x20,
	MODE_NOVOCAL: 0x30,
	MODE_ORIGINVOCAL: 0x31,
	MODE_YOURVOCAL: 0x32,
	MODE_AUTOTUNE: 0x33
};

var PacketDataType = {
	BLANK: 0x00,
	NUMBER: 0x01,
	TEXT: 0x02,
	JSON: 0x03
};

/*
* We need a browser with ECMAScript2015/ES6 to support JS classes. There will not be a workaround for this, since
* that would mean that we have two seperate pieces of code doing the same thing.
*/
'use strict';
class Packet{
	constructor(packetType = PacketType.NONE, packetMsgType = PacketMsgType.BLANK, packetDataType = PacketDataType.BLANK, packetData = ""){
		this.packetType = packetType;
		/*switch(this.packetType){
			case PacketType.SERVER_BROADCAST:
			case PacketType.SERVER_MESSAGE:
			case PacketType.CLIENT_MESSAGE:
			case PacketType.CLIENT_BROADCAST:*/
				this.packetMsgType = packetMsgType;
				this.packetDataType = packetDataType;
				this.packetData = packetData;
				/*break;
			default:
				break;
		}*/
	}
	
	isMalformedServer(){
		switch(this.packetType){
			case PacketType.SERVER_BROADCAST:
			case PacketType.SERVER_MESSAGE:
			case PacketType.SERVER_PING:
			case PacketType.SERVER_PONG:
			case PacketType.SERVER_OPEN:
			case PacketType.SERVER_CLOSE:
				return false;
			default:
				return true;
		}
		return true;
	}
	
	isMalformedClient(){
		switch(this.packetType){
			case PacketType.CLIENT_BROADCAST:
			case PacketType.CLIENT_MESSAGE:
			case PacketType.CLIENT_PING:
			case PacketType.CLIENT_PONG:
			case PacketType.CLIENT_OPEN:
			case PacketType.CLIENT_CLOSE:
				return false;
			default:
				return true;
		}
		return true;
	}
	
	getFinalisedPacket(){
		var tmpPacket = {};
		tmpPacket.type = this.packetType;
		/*switch(this.packetType){
			case PacketType.SERVER_BROADCAST:
			case PacketType.SERVER_MESSAGE:
			case PacketType.CLIENT_MESSAGE:
			case PacketType.CLIENT_BROADCAST:*/
				tmpPacket.msgType = this.packetMsgType;
				tmpPacket.dataType = this.packetDataType;
				tmpPacket.data = this.packetData;
				/*break;
			default:
				break;
		}		*/
		return JSON.stringify(tmpPacket);
	}
	
	fromJson(json){
		var raw = JSON.parse(json);
		this.packetType = raw.type;
		switch(this.packetType){
			case PacketType.SERVER_BROADCAST:
			case PacketType.SERVER_MESSAGE:
			case PacketType.CLIENT_MESSAGE:
			case PacketType.CLIENT_BROADCAST:
				this.packetMsgType = raw.msgType;
				this.packetDataType = raw.dataType;
				this.packetData = raw.data;
				break;
			default:
				break;
		}
	}
	
	send(){
		console.log(this);
		ws.sock.send(this.getFinalisedPacket());
	}
}
