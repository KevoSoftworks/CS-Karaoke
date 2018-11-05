var ws = {
	port: 8080,
	ip: "10.42.0.100",
	protocol: "ws",
	sock: new WebSocket("ws://10.42.0.100:8080"),
	tries: 0,
	ready: false,
	func: {
		handleServerMessagePacket: function(pin){
			switch(pin.packetMsgType){
				case PacketMsgType.SONG_READY:
					game.init();
					break;
				case PacketMsgType.SCORE:
					document.getElementById("gameScore").innerHTML = ws.func.getDataInProperFormat(pin);
					break;
				default:
					//Ignore all other stuff
					break;
			}
		},
		
		getDataInProperFormat: function(pin){
			switch(pin.packetDataType){
				case PacketDataType.BLANK:
					return null;
				case PacketDataType.NUMBER:
				case PacketDataType.TEXT:
					return pin.packetData;
				case PacketDataType.JSON:
					return JSON.parse(pin.packetData);
			}
		}
	}
};

ws.sock.onopen = function(e){
	ui.hideConnectionStatus();
}

ws.sock.onmessage = function(e){
	console.log(e.data);
	var p = new Packet();
	p.fromJson(e.data);
	console.log(p);
	switch(p.packetType){
		case PacketType.SERVER_OPEN:
			ws.ready = true;
			new Packet(PacketType.CLIENT_OPEN).send();
			break;
		case PacketType.SERVER_MESSAGE:
			ws.func.handleServerMessagePacket(p);
			break;
	}
}

ws.sock.onerror = function(e){
	
}

ws.sock.onclose = function(e){
	ws.ready = false;
	recoverable = false;
	refreshable = true;
	
	switch(e.code){
		case 1001:
			err = "WSErr: Endpoint verdwijnt door serverfout of door pagina-navigatie. Code: 1001";
			break;
		case 1002:
			err = "WSErr: Endpoint beëindigd door een protocolfout. Code: 1002";
			break;
		case 1003:
			err = "WSErr: Verbinding verbroken door foutieve data. Code: 1003";
			break;
		case 1005:
			err = "WSErr: Verbinding verbroken, geen statuscode ontvangen waar deze verwacht werd. Code: 1005";
			break;
		case 1006:
			err = "WSErr: Verbinding onverwacht verbroken. De server is down of er is geen internetverbinding. Code: 1006";
			break;
		case 1007:
			err = "WSErr: Endpoint beëindigd door inconsistente data. Code: 1007";
			break;
		case 1008:
			err = "WSErr: Endpoint beëindigd door een policy violation. Code: 1008";
			break;
		case 1009:
			err = "WSErr: Endpoint beëindigd door een overgroot dataframe. Code: 1009";
			break;
		case 1010:
			err = "WSErr: Client beëindigd door missende server-extensies. Code: 1010";
			break;
		case 1011:
			err = "WSErr: Interne serverfout. Code: 1011";
			break;
		case 1012:
			err = "WSErr: De server herstart. Code: 1012";
			break;
		case 1013:
			err = "WSErr: Endpoint beëindigd door een tijdelijke fout. Code: 1013";
			break;
		case 1015:
			err = "WSErr: Verbinding verbroken door een probleem met de TLS Handshake. Code: 1015";
			break;
		default:
			err = "WS: Verbinding verbroken, onbekende code";
			break;
	}
	
	alert(err);
}
