let ui = {
	"start": function(){
		document.getElementById("main").classList.add("slideup");
		document.getElementById("game").classList.remove("slideup");
		game.init();
	},

	"main": function(){
		document.getElementById("settings").classList.add("slideup");
		document.getElementById("main").classList.remove("slideup");
	},
	
	"settings": function(){
		document.getElementById("main").classList.add("slideup");
		document.getElementById("settings").classList.remove("slideup");
	}
}

let game = {
	"score": 0,
	"init": function(){
		setTimeout(function(){
			document.getElementById("status").innerHTML = "Connected!";
			document.getElementById("gameWindow").innerHTML = "<span style='font-size: 32px'>Starting in 3...</span>";
			game.tick2();
		}, 1000);
	},
	"tick2": function(){
		setTimeout(function(){
			document.getElementById("gameWindow").innerHTML = "<span style='font-size: 32px'>Starting in 2...</span>";
			game.tick3();
		},1000);
	},
	"tick3": function(){
                setTimeout(function(){
                        document.getElementById("gameWindow").innerHTML = "<span style='font-size: 32px'>Starting in 1...</span>";
                        game.tick4();
                },1000);
	},
	"tick4": function(){
		setInterval(function(){
			let add = Math.round(Math.random() * 100);
			game.score += add;
			document.getElementById("gameWindow").innerHTML = `<span style='font-size: 24px'>Score: ${game.score}</span><br/><div class='barOuter'><div id="barInner" class='barInner'>&nbsp;</div></div>`;
			document.getElementById("barInner").setAttribute("style", `width: ${add}%`);
		}, 250);
	}
}
