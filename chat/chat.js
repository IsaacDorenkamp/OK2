var ws;
var box;
function Init(){
	ws = new WebSocket("ws://" + location.host);

	ws.onopen = function(){
		ws.send("I joined the server!");
	};

	ws.onmessage = function(evt){
		var data = evt.data;
		var elem = document.createElement('li');
		elem.innerHTML = data;
		document.getElementById("chat").appendChild(elem);
	}
	
	box = document.getElementById("chat-message");
}

function send_message(){
	ws.send(box.value);
	box.value = "";
}

function check_send(evt){
	if( evt.keyCode == 13 ){
		send_message();
	}
}

onload = Init;