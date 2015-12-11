package ok2.ext.websocket;

import java.util.HashMap;

import ok2.httplib.*;

public class WebsocketMiddleware {
	private HashMap<String,Websocket> sockets = new HashMap<>();
	public WebsocketMiddleware(){}
	
	public void check(Request req, Response res){
		String header = req.getHeader("Upgrade");
		if( header != null && header.equals("websocket") ){
			
		}
	}
}
