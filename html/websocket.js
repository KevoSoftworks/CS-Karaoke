
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

        // Gamescore is added later to the DOM by javascript. So check if it exists.
        let gamescore = document.getElementById("gameScore");
        if (gamescore) {
            gamescore.textContent = evt.data;
            document.getElementById("barInner").setAttribute("style", `width: ${evt.data}%`);
        } else {
            console.log("gameScore has not been added to the DOM yet.");
        }
    };

    ws.onclose = function() {
        console.log("Closed!");
    };

    ws.onerror = function(err) {
        console.log("Error: " + err);
    };