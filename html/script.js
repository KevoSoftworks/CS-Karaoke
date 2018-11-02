var lis = document.getElementsByTagName("li");
var activeSong = lis[0].textContent;
for (let i = 0; i < lis.length; i += 1) {
	lis[i].addEventListener("click", function() {
		//send the active song with a 0 at the start
		ws.send("0" + lis[i].textContent);
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
		game.init();
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
			document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 3...</span>";
			game.tick2();
		}, 1000);
	},
	"tick2": function(){
		setTimeout(function(){
			document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 2...</span>";
			game.tick3();
		},1000);
	},
	"tick3": function(){
		setTimeout(function(){
				document.getElementById("gameWindow").innerHTML = "<span id='gameScore' style='font-size: 32px'>Starting in 1...</span>";
				game.tick4();
		},1000);
	},
	"tick4": function(){
		ws.send("Starting");
		startSong(activeSong);
		
		document.getElementById("gameWindow").innerHTML = `<span id='gameScore' style='font-size: 24px'>Score: 0</span><br/><div class='barOuter'><div id="barInner" class='barInner'>&nbsp;</div></div>`;
		document.getElementById("barInner").setAttribute("style", `width: 0%`);
	}
}


