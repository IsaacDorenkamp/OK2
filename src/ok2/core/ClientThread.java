package ok2.core;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Base64;

import ok2.ext.websocket.*;
import ok2.httplib.Request;
import ok2.httplib.Response;

public class ClientThread extends Thread {
	private Socket cli;
	private OK2App app;
	private String protocol = "http";
	private MessageDigest sha1;
	private WebsocketManager wm;
	public ClientThread(Socket accepted, OK2App app, WebsocketManager wm){
		cli = accepted;
		this.app = app;
		this.wm = wm;
		
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
		Base64.Encoder b64 = Base64.getEncoder();
		return b64.encodeToString(bout);
	}
	
	private Thread ALIVE_TIMER;
	private Request ws_req;
	
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
						//Return "not implemented" if our app is not labeled
						//as a websocket server.
						if( !(app instanceof WebsocketServer) ){
							res.setStatus(501);
							res.complete();
							return true;
						}
						//Create websocket server
						wm.add(cli);
						ws_req = req;
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
		if( !(app instanceof WebsocketServer) ){
			return; //Ignore this. This should NEVER happen.
		}
		InputStream isr = cli.getInputStream();
		ArrayList<Byte> ali = new ArrayList<>();
		byte c = 0;
		while(!(isr.available() > 0));   //Wait for content
		while( isr.available() > 0 ){  //Get our content
			int temp = isr.read();
			if( temp == -1 ){
				break;
			}
			c = (byte)((temp) & 0xff);
			ali.add(c);
		}
		
		//Frame handling code
		try{
			int opcode = ali.get(0);
			int clen = ali.get(1) & 0b01111111; //Screw the masking bit
			
			int start_index = 2;
			
			if( clen == 126 ){
				start_index = 4;
			}else if( clen == 127 ){
				start_index = 10;
			}else;
			
			List<Byte> masks = ali.subList(start_index, start_index + 4);
			String decoded = "";
			for( int pointer = start_index + 4, offset = 0; pointer < ali.size(); pointer++, offset = (offset+1) % 4 ){
				char cv = (char)(ali.get(pointer) ^ masks.get(offset));
				decoded += cv;
			}
			System.out.println("Decoded: " + decoded);
			
			Websocket ws = new Websocket(decoded, cli);
			synchronized(wm){
				((WebsocketServer)app).websocket_invoke(ws, opcode, wm, ws_req);
			}
		}catch(IndexOutOfBoundsException ioobe){
			wm.remove(cli);
			cli.close(); //Malformed client.
		}
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
				if( wm.getSockets().contains(cli) ){
					wm.remove(cli);
				}
				return;
			}
		}
	}
}
