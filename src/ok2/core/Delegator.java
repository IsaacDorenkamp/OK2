package ok2.core;

import java.net.ServerSocket;
import java.net.Socket;

import ok2.httplib.*;

import java.io.*;

public class Delegator extends Thread{
	private ServerSocket ss = null;
	private OK2App app = null;
	@Override
	public void run(){
		if( ss == null ){
			return;
		}
		while(true){
			Socket cli = null;
			try{
				cli = ss.accept();
			}catch(IOException ioe){
				System.out.println("Error when accepting client.");
			}
			if( cli == null ){
				continue;
			}
			String line;
			String total = "";
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader(cli.getInputStream()) );
				while( (line = br.readLine()) != null ){
					total += line;
				}
				Request req = Request.parse(total); //Make sure to handle parse exceptions.
				Response res = new Response();
				app.invoke( req, res );
			} catch (IOException e) {
				System.out.println("Failed to communicate with client.");
			}
		}
	}
	
	public Delegator(OK2App app){
		try{
			ss = new ServerSocket();
			this.app = app;
		}catch(IOException ioe){
			System.out.println("Could not instantiate Delegator.");
		}
	}
	
	public void stop_server(){
		try{
			ss.close();
		}catch(IOException ioe){
			System.out.println("Could not stop server!");
		}
	}
}
