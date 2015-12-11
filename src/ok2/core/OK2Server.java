package ok2.core;

import java.io.IOException;
import java.util.Date;

import ok2.httplib.*;

public class OK2Server {
	private OK2App app;
	private Delegator del;
	
	public OK2Server(OK2App ok2app){
		app = ok2app;
		del = new Delegator(app);
	}
	
	public void server_start(){
		del.start();
	}
	public void server_stop(){
		del.stop_server();
	}
	
	public static void main(String[] args){
		OK2App app = new OK2App(){
			public void invoke( Request req, Response res ) throws IOException{
				System.out.println("Sending");
				res.header( "Content-type", "text/plain");
				Date d = new Date();
				res.send( d.toString() );
			}
		};
		OK2Server server = new OK2Server(app);
		server.server_start();
		System.out.println("Started OK2 Server");
	}
}
