package ok2.test;

import java.io.IOException;

import ok2.core.*;
import ok2.ext.TemplateConfig;
import ok2.ext.websocket.Websocket;
import ok2.ext.websocket.WebsocketManager;
import ok2.ext.websocket.WebsocketServer;
import ok2.httplib.OutputConfiguration;
import ok2.httplib.Request;
import ok2.httplib.Response;
import ok2.lib.*;

public class Chat extends OK2App implements WebsocketServer{
	private Router r = new Router();
	private FileRoute fr = new FileRoute("chat");
	private TemplateConfig tc = new TemplateConfig();
	private Route approute = new Route(){
		@Override
		public void onroute(Request req, Response res) {
			String cstring = req.getHeader("Cookie");
			Cookie[] cs = CookieUtil.parse(cstring);
			Cookie ncookie = CookieUtil.find("name", cs);
			if( ncookie == null ){
				res.setOutputConfiguration(tc);
				res.header("Content-type", "text/html");
				res.send("chat/index.html");
				return;
			}
			String name = ncookie.get();
			tc.var("name", name);
			res.setOutputConfiguration(tc);
			res.header("Content-type", "text/html");
			res.send("chat/chat.html");
		}
	};
	public Chat(){
		r.route("/static", fr);
		r.route("/", approute);
	}

	@Override
	public void invoke(Request req, Response res) throws IOException {
		// TODO Auto-generated method stub
		r.handle(req, res);
	}
	
	public String getName(Request req){
		String cstring = req.getHeader("Cookie");
		Cookie[] cs = CookieUtil.parse(cstring);
		Cookie ncookie = CookieUtil.find("name", cs);
		if( ncookie == null ){
			return null;
		}
		return ncookie.get();
	}

	@Override
	public void websocket_invoke(Websocket ws, int opcode, WebsocketManager wm, Request ws_req) throws IOException {
		String data = Sanitizer.html(ws.getData());
		String name = getName(ws_req);
		
		if( name == null ){
			return;
		}
		
		wm.broadcast(name + ": " + data);
	}
	
	public static void main(String[] args){
		Chat c = new Chat();
		OK2Server ok2s = new OK2Server(c);
		ok2s.server_start();
	}
}
