var ws = new WebSocket("ws://127.0.0.1:8080/");
    ws.onopen = function() {
        console.log("Opened!");
        ws.send("Hello Server");
    };

    ws.onmessage = function (evt) {
        console.log("Message: " + evt.data);
    };

    ws.onclose = function() {
        console.log("Closed!");
    };

    ws.onerror = function(err) {
        console.log("Error: " + err);
    };