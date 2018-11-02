
/* 
    Web socket defines the websocket that connects the website with the Java server.
*/

var ws = new WebSocket("ws://10.42.0.100:8080/");
    ws.onopen = function() {
        console.log("Opened!");
        ws.send("Hello Server");
    };

    // On a message received.
    ws.onmessage = function (evt) {
        console.log("Message: " + evt.data);
        if (evt.data=="SongReady"){
          game.tick4();
        } 
        let gamescore = document.getElementById("gameScore");
              if(evt.data.length>10 && gamescore!=null){
               // if(evt.data.substring(0, 13) == "Score so far: "){
           console.log("if-loop-of-score has been called");
                  gamescore.innerHTML = evt.data.substring(14, evt.data.length);
                  document.getElementById("barInner").setAttribute("style", `width: ${evt.data}%`);
               // }
                
              }
        
        /*// Gamescore is added later to the DOM by javascript. So check if it exists.
        let gamescore = document.getElementById("gameScore");
        if (gamescore) {
            gamescore.textContent = evt.data;
            document.getElementById("barInner").setAttribute("style", `width: ${evt.data}%`);
        } else {
            console.log("gameScore has not been added to the DOM yet.");
        }
        */
    };

    ws.onclose = function() {
        console.log("Closed!");
    };

    ws.onerror = function(err) {
        console.log("Error: " + err);
    };