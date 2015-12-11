package ok2.httplib;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ok2.core.*;

public class Response {
	public static final int IGNORE_CODE = -1;
	
	private float version;
	private int code = 200;
	private Socket sock;
	boolean sent = false;
	boolean anysent = false;
	private OutputConfiguration cfg = OutputConfiguration.TEXT;
	private String body = "";
	
	private HashMap<String, String> heads = new HashMap<>();
	
	public Response( float version, Socket sock ){
		this.version = version;
		this.sock = sock;
	}
	public Response( Socket sock ){
		this( 1.1F, sock );
	}
	public void setStatus( int stat ){
		System.out.println("Set status to " + String.valueOf(stat));
		this.code = stat;
		System.out.println("The code is now " + this.code);
	}
	public void header( String name, String value ){
		heads.put(name, value);
	}
	
	//Getters
	public int getCode(){
		return code;
	}
	public float getVersion(){
		return version;
	}
	public String getBody(){
		return body;
	}
	
	//Response Interaction Methods
	public void setOutputConfiguration( OutputConfiguration oc ){
		cfg = oc;
	}
	public void send( String text ){
		RespObject respobj = cfg.process(text);
		body += respobj.getOutput();
		if( respobj.getStatus() != Response.IGNORE_CODE ) code = respobj.getStatus();
	}
	public void complete() throws IOException{
		OutputStream sos = sock.getOutputStream();
		String reqline = "HTTP/1.1 " + String.valueOf(code) + " " + ProtocolConstants.getCodeName(code) + "\r\n";
		String headertext = "";
		Set<String> keys = heads.keySet();
		Iterator<String> it = keys.iterator();
		while( it.hasNext() ){
			String key = it.next();
			String val = heads.get(key);
			headertext += key + ": " + val + "\r\n";
		}
		String total = reqline + headertext + "\r\n" + body;
		System.out.println("---SENT---");
		System.out.println(total);
		byte[] bytes = total.getBytes();
		sos.write(bytes);
		System.out.println("Completing request");
		sos.flush();
	}
	public String toString(){
		String reqline = "HTTP/1.1 " + String.valueOf(code) + " " + ProtocolConstants.getCodeName(code) + "\r\n";
		String headertext = "";
		Set<String> keys = heads.keySet();
		Iterator<String> it = keys.iterator();
		while( it.hasNext() ){
			String key = it.next();
			String val = heads.get(key);
			headertext += key + ": " + val + "\r\n";
		}
		String total = reqline + headertext + "\r\n" + body;
		return total;
	}
	public void end() throws IOException{
		OutputStream sos = sock.getOutputStream();
		sos.close();
	}
}
