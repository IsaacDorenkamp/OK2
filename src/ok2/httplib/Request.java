package ok2.httplib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import java.util.regex.*;

public class Request {
	private String path;
	private String derouted;
	private float version;
	private String method;
	private String body;
	private HashMap<String,String> headers = new HashMap<>();
	
	public Request( String method, String path, float version, String body ){
		this.path = path;
		this.derouted = path;
		this.version = version;
		this.method = method;
		this.body = body;
	}
	public Request( String method, String path, float version ){
		this( method, path, version, "" );
	}
	public Request( String method, String path ){
		this( method, path, 1.1F );
	}
	public Request( String method ){
		this( method, "/" );
	}
	public Request(){
		this( "GET" );
	}
	
	//Header functions.
	public void header( String name, String value ){
		headers.put( name, value );
	}
	public String getHeader( String name ){
		return headers.get( name );
	}
	public Iterator<String> getAllHeaders(){
		return headers.keySet().iterator();
	}
	
	//Basic Getters
	public String getPath(){
		return path;
	}
	public String getDeroutedPath(){
		return derouted;
	}
	public float getVersion(){
		return version;
	}
	public String getMethod(){
		return method;
	}
	public String getBody(){
		return body;
	}
	
	public void deroute(String der){
		Pattern p = Pattern.compile(der);
		Matcher m = p.matcher(path);
		
		if( m.find() ){
			String out = path.substring(0, m.start()) + path.substring(m.end(), path.length());
			derouted = out;
		}
	}
	
	@Override
	public String toString(){
		String out = method + " HTTP/" + String.valueOf(version) + " " + path + "\r\n";
		Set<String> keys = headers.keySet();
		Iterator<String> it = keys.iterator();
		while( it.hasNext() ){
			String name = it.next();
			String val  = headers.get(name);
			out += name + ": " + val + "\r\n";
		}
		if( !body.isEmpty() ){
			out += "\r\n" + body;
		}
		return out;
	}
	
	//Static parser method
	public static final Request parse(String value){
		System.out.println("INFO RECEIVED: " + value);
		try{
			String method;
			float version;
			String path;
			
			String heads = value.split("(\r\n\r\n|\n\n)")[0];
			String[] lines = heads.split("(\r\n|\n)");
			String data = lines[0];
			String[] headers = {};
			if( lines.length > 1 )
				headers = Arrays.copyOfRange( lines, 1, lines.length );
			
			String[] maindata = data.split(" ");
			method = maindata[0];
			String versiontext = maindata[2].split("/")[1];
			version = Float.parseFloat(versiontext);
			path = maindata[1];
			
			String[] delimmed = value.split("\r\n\r\n");
			String body = "";
			if( delimmed.length > 1 ){
				body = delimmed[1];
			}
			
			Request req = new Request( method, path, version, body );
			for( int i = 0; i < headers.length; i++ ){
				String[] headdata = headers[i].split(":");
				String headname = headdata[0];
				String headvalue = headdata[1];
				
				req.header( headname, headvalue.trim() );
			}
			return req;
		}catch( ArrayIndexOutOfBoundsException aioobe ){
			aioobe.printStackTrace();
			return null;
		}
	}
}
