package ok2.ext.websocket;

import java.io.IOException;

import ok2.httplib.Request;

public interface WebsocketServer {
	public void websocket_invoke(Websocket ws, int opcode, WebsocketManager wm, Request req) throws IOException;
}
