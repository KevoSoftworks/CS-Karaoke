var lis = document.getElementsByTagName("li");
var activeSong = lis[0].textContent;
for (let i = 0; i < lis.length; i += 1) {
	lis[i].addEventListener("click", function() {
		//send the active song with a 0 at the start
		new Packet(PacketType.CLIENT_MESSAGE, PacketMsgType.SONG_LOAD, PacketDataType.TEXT, lis[i].textContent).send();
		activeSong = lis[i].textContent;
		//start the game
		ui.start();
	});
};
var scoreinterval;

let ui = {
	"start": function(){
		//hide all other pages, only show the game page
		document.getElementById("main").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("endscreen").classList.add("slideup");
		document.getElementById("game").classList.remove("slideup");
		/*
			TODO: Show a loading page
		*/
		document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Loading...</span><img src='justdoit.gif' height='42' width='42'>";
	},

	"main": function(){
		//hide all oteh rpages, only show the main menu page
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("endscreen").classList.add("slideup");
		document.getElementById("main").classList.remove("slideup");
	},

	"settings": function(){
		//hide all other pages, only show the settings page
		document.getElementById("main").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("endscreen").classList.add("slideup");
		document.getElementById("settings").classList.remove("slideup");
	},

	"songList": function() {
		//hide all other pages, only show the songlist page
		document.getElementById("main").classList.add("slideup");
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("endscreen").classList.add("slideup");
		document.getElementById("songList").classList.remove("slideup");
	},
	
	"end" : function() {
		//hide all other pages, only show the endscreen
		document.getElementById("main").classList.add("slideup");
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("endscreen").classList.remove("slideup");
		clearInterval(songinterval);
	}
}

let game = {
	"init": function(){
		game.score = 0;
		// set active song
		document.getElementById("songIndicator").innerHTML = activeSong;
		
		// set time
		let song = songs[activeSong];
		timeIndicator.textContent = secondsToTimeString(0) + " / " + secondsToTimeString(song[song.length - 1][1]);

		setTimeout(function(){
			document.getElementById("status").innerHTML = "Connected";
			document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 3...</span><img src='justdoit.gif' height='42' width='42'>";
			game.tick2();
		}, 1000);
	},
	"tick2": function(){
		setTimeout(function(){
			document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 2...</span><img src='justdoit.gif' height='42' width='42'>";
			game.tick3();
		},1000);
	},
	"tick3": function(){
		setTimeout(function(){
				document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 1...</span><img src='justdoit.gif' height='42' width='42'>";
				game.tick4();
		},1000);
	},
	"tick4": function(){
	  	setTimeout(function(){
			new Packet(PacketType.CLIENT_MESSAGE, PacketMsgType.SONG_START).send();
			startSong(activeSong);
			
			document.getElementById("gameWindow").innerHTML = `<span id='gameScore' style='font-size: 24px'>Score: 0</span><br/><div class='barOuter'><div id="barInner" class='barInner'>&nbsp;</div></div>`;
			document.getElementById("barInner").setAttribute("style", `width: 0%`);
		},1000);
	}
}


