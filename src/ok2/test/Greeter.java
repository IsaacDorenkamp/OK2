package ok2.test;

import java.io.IOException;

import ok2.core.*;
import ok2.httplib.OutputConfiguration;
import ok2.httplib.Request;
import ok2.httplib.Response;
import ok2.lib.*;
import ok2.ext.*;

public class Greeter extends OK2App{
	private Router router = new Router();
	private TemplateConfig tc = new TemplateConfig();
	public static final float VERSION = 1.0F;
	public Greeter(){
		FileRoute fr = new FileRoute("greeter");
		Route approute = new Route(){
			public void onroute(Request req, Response res){
				tc.var("version", String.valueOf(Greeter.VERSION));
				res.setOutputConfiguration( tc );
				String cookiestring = req.getHeader("Cookie");
				Cookie[] cookies = CookieUtil.parse(cookiestring);
				Cookie namecookie = CookieUtil.find( "name", cookies );
				if( namecookie == null ){
					res.header( "Content-type", "text/html" );
					res.send( "greeter/index.html" );
					return;
				}
				String name = namecookie.get();
				tc.var("name", Sanitizer.html(name));
				res.header( "Content-type", "text/html" );
				res.send("greeter/greeting.html");
			}
		};
		router.route("/static", fr);
		router.route("/", approute);
	}
	@Override
	public void invoke(Request req, Response res) throws IOException {
		router.handle(req, res);
	}

	public static void main(String[] args){
		Greeter app = new Greeter();
		OK2Server server = new OK2Server(app);
		server.server_start();
	}
}
