
var ws = new WebSocket("ws://10.42.0.100:8080/");
    ws.onopen = function() {
        console.log("Opened!");
        ws.send("Hello Server");
    };

    ws.onmessage = function (evt) {
        console.log("Message: " + evt.data);
        let gamescore = document.getElementById("gameScore");
        if (gamescore) {
            gamescore.textContent = evt.data;
            document.getElementById("barInner").setAttribute("style", `width: ${evt.data}%`);
        } else {
            console.log("gameScore is not defined because of hacky code");
        }
    };

    ws.onclose = function() {
        console.log("Closed!");
    };

    ws.onerror = function(err) {
        console.log("Error: " + err);
    };