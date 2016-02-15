package ok2.ext.websocket;

import java.util.ArrayList;
import java.util.Iterator;
import java.net.Socket;
import java.io.*;

public class WebsocketManager {
	ArrayList<Socket> socks = new ArrayList<>();
	public WebsocketManager(){}
	
	public synchronized void add(Socket s){
		socks.add(s);
	}
	public synchronized void remove(Socket s){
		socks.remove(s);
	}
	
	public synchronized Iterator<Socket> getIterator(){
		ArrayList<Socket> als = new ArrayList<>(socks);
		return als.iterator();
	}
	
	public synchronized ArrayList<Socket> getSockets(){
		ArrayList<Socket> als = new ArrayList<>();
		return als;
	}
	
	public synchronized void broadcast(String message) throws IOException{
		for( Socket s : socks ){
			Websocket ws = new Websocket("",s);
			System.out.println("broadcasting to " + s.toString());
			ws.send(message);
		}
	}
}
