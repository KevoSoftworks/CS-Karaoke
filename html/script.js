var lis = document.getElementsByTagName("li");
var activeSong = lis[0].textContent;
for (let i = 0; i < lis.length; i += 1) {
	lis[i].addEventListener("click", function() {
		ws.send("0" + lis[i].textContent);
		activeSong = lis[i].textContent;
		ui.start();
	});
};

let ui = {
	"start": function(){
		console.log("Starting Game");
		document.getElementById("main").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("game").classList.remove("slideup");
		game.init();
	},

	"main": function(){
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("main").classList.remove("slideup");
	},

	"settings": function(){
		document.getElementById("main").classList.add("slideup");
		document.getElementById("songList").classList.add("slideup");
		document.getElementById("settings").classList.remove("slideup");
	},

	"songList": function() {
		document.getElementById("main").classList.add("slideup");
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("songList").classList.remove("slideup");
	}
}

let game = {
	"score": 0,
	"init": function(){
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
		
		let add = 50;
		document.getElementById("gameWindow").innerHTML = `<span id='gameScore' style='font-size: 24px'>Score: ${game.score}</span><br/><div class='barOuter'><div id="barInner" class='barInner'>&nbsp;</div></div>`;
		document.getElementById("barInner").setAttribute("style", `width: ${add}%`);
	}
}


