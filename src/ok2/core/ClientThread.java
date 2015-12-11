package ok2.core;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import sun.misc.BASE64Encoder;

import ok2.httplib.Request;
import ok2.httplib.Response;

public class ClientThread extends Thread {
	private Socket cli;
	private OK2App app;
	private String protocol = "http";
	private MessageDigest sha1;
	public ClientThread(Socket accepted, OK2App app){
		cli = accepted;
		this.app = app;
		
		try{
			 sha1 = MessageDigest.getInstance("SHA-1");
		}catch( NoSuchAlgorithmException nsae ){
			System.out.println("SHA-1 Not Supported!");
			sha1 = null;
		}
	}
	public void http() throws IOException{
		boolean first = true;
		BufferedReader br = new BufferedReader( new InputStreamReader(cli.getInputStream()) );
		int cl = 0;
		String line;
		String total = "";
		while( (line = br.readLine()) != null ){
			if( line.length() == 0 ){
				total += "\r\n";
				break;
			}
			if( line.startsWith("Content-Length:") ){
				cl = Integer.parseInt(line.split(":")[1].trim());
			}
			if( first ){
				total += line;
				first = false;
			}
			else
				total += "\r\n" + line;
		}
		if( total.isEmpty() ){
			return;
		}
		if( ALIVE_TIMER != null ){
			ALIVE_TIMER.interrupt();
		}
		String method = total.split(" ")[0];
		//Read POST data
		if( method.equals("POST") ){
			total += "\r\n";
			for( int i = 0; i < cl; i++ ){
				char c = (char)br.read();
				total += c;
			}
		}
		Request req = Request.parse(total); //Make sure to handle parse exceptions.
		Response res = new Response( cli );
		if( req == null ){
			res.setStatus(400);
			res.send("Malformed Request.");
			res.complete();
			return;
		}else{
			System.out.println(req.getBody());
		}
		res.header("X-Powered-By","OK2 Java Web Framework");
		if( handle_headers(req,res) ){
			return;
		}
		app.invoke( req, res );
		res.complete();
	}
	private String getWebsocketAccept(String acc){
		if( sha1 == null ){
			System.out.println("SHA-1 Not Supported!");
			return "";
		}
		String tohash = acc + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		sha1.reset();
		sha1.update(tohash.getBytes());
		byte[] bout = sha1.digest();
		String out = new BASE64Encoder().encode(bout);
		return out;
	}
	
	private Thread ALIVE_TIMER;
	
	public boolean handle_headers(Request req, Response res) throws IOException{
		Iterator<String> keys = req.getAllHeaders();
		while( keys.hasNext() ){
			String head = keys.next();
			String val  = req.getHeader(head);
			System.out.println( head + ": " + val );
			switch(head){
			//Handle here
			case "Connection":
				switch(val){
				case "Upgrade":
					String upg = req.getHeader("Upgrade");
					if( upg == null ){
						res.setStatus(400);
						return true;
					}
					switch(upg){
					case "websocket":
						//Create websocket server
						res.setStatus(101);
						res.header("Upgrade","websocket");
						res.header("Connection","upgrade");
						
						System.out.println(res.toString());
						
						String accept = getWebsocketAccept(req.getHeader("Sec-WebSocket-Key"));
						res.header("Sec-WebSocket-Accept", accept);
						
						protocol = "websocket";
						
						res.complete();
						return true;
					}
					break;
				case "keep-alive":
					//Keep the socket alive! Don't do anything.
					if( ALIVE_TIMER != null ){
						ALIVE_TIMER.interrupt();
					}
					ALIVE_TIMER = TimerFactory.createTimer(75, cli);
					ALIVE_TIMER.start();
					break;
				case "close":
					res.end();
					return true;
				}
			}
		}
		
		return false;
	}
	public void websocket() throws IOException{
		InputStreamReader isr = new InputStreamReader( cli.getInputStream() );
		ArrayList<Integer> ali = new ArrayList<>();
		int c = 0;
		while( (c = isr.read()) != -1 ){
			ali.add(new Integer(c));
		}
		//Frame handling code
		isr.close();
	}
	@Override
	public void run(){
		while(true){
			try {
				switch(protocol){
				case "http":
					http();
					break;
				case "websocket":
					websocket();
					break;
				default:
					break;
				}
			} catch (IOException e) {
				return;
			}
		}
	}
}
