// This file contains the sentences to be displayed with the time amount 
// that it should be displayed on the screen for every song.
/* 
    Example:
    "I want it that way": [
        ["Sentence 1", 0.5],
        ["Sentence 2", 3],
        ["Sentence 3", 2],
    ]
*/

songs = {
    "I Want It That Way - Backstreet Boys": [
        ["Yeah-eh-heah", 0],
        ["You are my fire", 10],
        ["The one desire", 15],
        ["Believe when I say", 19],
        ["I want it that way", 23],
        ["But we are two worlds apart", 28],
        ["Can't reach to your heart", 35],
        ["When you say", 40],
        ["That I want it that way", 43],
        ["Tell me why", 47],
        ["Ain't nothin' but a heartache", 48],
        ["Tell me why", 53],
        ["Ain't nothin' but a mistake", 54],
        ["Tell me why", 57],
        ["I never want to hear you say", 59],
        ["I want it that way", 62],
        ["Am I your fire?", 67],
        ["Your one desire", 72],
        ["Yes I know it's too late", 76],
        ["But I want it that way", 82],
        ["Tell me why", 87],
        ["Ain't nothin' but a heartache", 88],
        ["Tell me why", 90],
        ["Ain't nothin' but a mistake", 92],
        ["Tell me why", 96],
        ["I never want to hear you say", 97],
        ["I want it that way", 101],
        ["Now I can see that we've fallen apart", 106],
        ["From the way that it used to be Yeah", 111],
        ["No matter the distance", 116],
        ["I want you to know", 118],
        ["That deep down inside of me", 121],
        ["You are my fire", 125],
        ["The one desire", 130],
        ["You are (you are you are you are)", 135],
        ["Don't want to hear you say", 143],
        ["Ain't nothin' but a heartache", 145],
        ["Ain't nothin' but a mistake \n (Don't want to hear you say)", 150],
        ["I never want to hear you say Oh-yeah", 155],
        ["I want it that way", 160],
        ["Tell me why", 164],
        ["Ain't nothin' but a heartache", 165],
        ["Tell me why", 167],
        ["Ain't nothin' but a mistake", 170],
        ["Tell me why", 174],
        ["I never want to hear you say", 175],
        ["I want it that way", 179],
        ["Tell me why", 183],
        ["Ain't nothin' but a heartache", 184],
        ["Ain't nothin' but a mistake", 189],
        ["Tell me why", 192],
        ["I never want to hear you say \n (Never want to hear you say it) ", 194],
        ["I want it that way", 198],
        ["'Cause I want it that way", 203],
    ],
    "Don't Stop Believin' - Journey": [
        ["Just a small town girl", 16],
        ["Livin' in a lonely world", 21],
        ["She took the midnight train goin' anywhere", 25],
        ["Just a city boy", 33],
        ["Born and raised in south Detroit", 37],
        ["He took the midnight train goin' anywhere", 41],
        ["A singer in a smoky room", 66],
        ["A smell of wine and cheap perfume", 69],
        ["For a smile they can share the night", 73],
        ["It goes on and on, and on, and on", 76],
        ["Strangers waiting", 81],
        ["Up and down the boulevard", 86],
        ["Their shadows searching in the night", 89],
        ["Streetlights, people", 97],
        ["Living just to find emotion", 103],
        ["Hiding somewhere in the night", 105],
        ["Working hard to get my fill", 121],
        ["Everybody wants a thrill", 126],
        ["Payin' anything to roll the dice", 129],
        ["Just one more time", 132],
        ["Some will win, some will lose", 137],
        ["Some were born to sing the blues", 142],
        ["Oh, the movie never ends", 145],
        ["It goes on and on, and on, and on", 147],
        ["Strangers waiting", 153],
        ["Up and down the boulevard", 157],
        ["Their shadows searching in the night", 160],
        ["Streetlights, people", 169],
        ["Living just to find emotion", 173],
        ["Hiding somewhere in the night", 177],
        ["Don't stop believin'", 200],
        ["Hold on to the feelin'", 205],
        ["Streetlights, people", 208],
        ["Don't stop believin'", 215],
        ["Hold on", 220],
        ["Streetlights, people", 223],
        ["Don't stop believin'", 233],
        ["Hold on to the feelin'", 236],
        ["Streetlights, people", 240],
    ],
    "test": [
        ["test1'", 2],
        ["test2", 6],
    ]     
}

// Converts seconds to a time string. For example: 84 returns 01:24.
function secondsToTimeString(seconds) {
    minutes = Math.floor(seconds / 60);
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    seconds = seconds % 60;
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return minutes + ":" + seconds;
}

function getNextSongText(song, currentIndex, currentTime) {
    indexToCheck = currentIndex + 1;

    if (indexToCheck === song.length) {
        return "";
    }

    // If the current time is not equal to the next song text time then display the next song text.
    // otherwise you have that the next songtext is the current song text for 1 sec.
    if (currentTime - song[currentIndex][1] !== 0) {    
        return song[currentIndex][0];
    }

    return "";
}

var activesong;

// Start a song with a particular songName
function startSong(songName) {
    // initialize variables.
	activesong = songName;
    let song = songs[songName];
    let waitTime = 1000;
    let timeSeconds = 0;
    let currentIndex = 0;
    let lyricsRuleHtml = document.getElementById("lyricsRule");
    let nextLyricsRule = document.getElementById("nextLyricsRule");
    let timeIndicator = document.getElementById("timeIndicator");
    let myBar = document.getElementById("myBar");

    // every second.
    var timeInterval = setInterval(function() {
        // update timeIndicator and next lyrics rule
        timeIndicator.textContent = secondsToTimeString(timeSeconds) + " / " + secondsToTimeString(song[song.length - 1][1]);
        nextLyricsRule.textContent = getNextSongText(song, currentIndex, timeSeconds);
        
        // update progress bar.
        myBar.style.width = (timeSeconds / song[song.length - 1][1]) * 100 + '%';

        // if the current time is the same as the time when a new song line should be displayed
        if (timeSeconds === song[currentIndex][1]) {
            // update the song line.
            lyricsRuleHtml.textContent = song[currentIndex][0];
            currentIndex += 1;

            // if the song has ended.
            if (currentIndex === song.length) {
            	var score = document.getElementById("gameScore").innerHTML;
            	console.log(score);
            	ws.send("Ending");
                //go to the endscreen
                document.getElementById("main").classList.add("slideup");
            	document.getElementById("settings").classList.add("slideup");
            	document.getElementById("songList").classList.add("slideup");
            	document.getElementById("game").classList.add("slideup");
            	document.getElementById("endscreen").classList.remove("slideup");
            	//show the active song at the top of the page
            	document.getElementById("songIndicatorDone").innerHTML = activesong;
            	//show the final score
            	document.getElementById("score").innerHTML = score;
                clearInterval(timeInterval);
                return;
            }
        }

        timeSeconds += waitTime / 1000;
    }, waitTime);
}