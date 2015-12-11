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
				ioe.printStackTrace();
				System.out.println("Error when accepting client.");
				continue;
			}
			if( cli == null ){
				continue;
			}else{
				ClientThread ct = new ClientThread(cli, app);
				ct.start();
			}
		}
	}
	
	public Delegator(OK2App app, int port){
		try{
			ss = new ServerSocket(port);
			this.app = app;
		}catch(IOException ioe){
			System.out.println("Could not instantiate Delegator.");
		}
	}
	public Delegator(OK2App app){
		this( app, 8080 );
	}
	
	public void stop_server(){
		try{
			ss.close();
		}catch(IOException ioe){
			System.out.println("Could not stop server!");
		}
	}
}
