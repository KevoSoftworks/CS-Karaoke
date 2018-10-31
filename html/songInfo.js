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

    if (indexToCheck == song.length || currentIndex === 0) {
        return "";
    }

    console.log("currentTime: " + currentTime);
    console.log("song time: " + song[currentIndex][1]);

    if (Math.abs(currentTime - song[currentIndex][1]) <= 2 && Math.abs(currentTime - song[currentIndex][1]) != 0) {
        return song[currentIndex][0];
    }

    return "";
}

// Start a song with a particular songName
function startSong(songName) {
    let song = songs[songName];
    let waitTime = 1000;
    let timeSeconds = 0;
    let currentIndex = 0;
    let lyricsRuleHtml = document.getElementById("lyricsRule");
    let nextLyricsRule = document.getElementById("nextLyricsRule");
    let timeIndicator = document.getElementById("timeIndicator");

    var timeInterval = setInterval(function() {
        timeIndicator.textContent = secondsToTimeString(timeSeconds) + " / " + secondsToTimeString(song[song.length - 1][1]);
        nextLyricsRule.textContent = getNextSongText(song, currentIndex, timeSeconds);
        if (timeSeconds === song[currentIndex][1]) {
            lyricsRuleHtml.textContent = song[currentIndex][0];
            currentIndex += 1;
            if (currentIndex === song.length) {
                setInterval(function() {
                    lyricsRule.textContent = "Song has ended.";
                }, 3000);
                clearTimeout(timeInterval);
                return;
            }
        }
        timeSeconds += waitTime / 1000;
    }, waitTime);
}