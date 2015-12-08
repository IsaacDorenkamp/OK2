package ok2.httplib;

import java.util.Arrays;
import java.util.HashMap;

public class Request {
	private String path;
	private float version;
	private String method;
	private String body;
	private HashMap<String,String> headers = new HashMap<>();
	
	public Request( String method, String path, float version, String body ){
		this.path = path;
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
	
	//Basic Getters
	public String getPath(){
		return path;
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
	
	//Static parser method
	public static final Request parse(String value){
		String method;
		float version;
		String path;
		
		String heads = value.split("\r\n\r\n")[0];
		String[] lines = heads.split("\r\n");
		String data = lines[0];
		String[] headers = Arrays.copyOfRange( lines, 1, lines.length );
		
		String[] maindata = data.split(" ");
		method = maindata[0];
		version = Float.parseFloat(maindata[1].split("/")[1]);
		path = maindata[2];
		
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
			
			req.header( headname, headvalue );
		}
		return req;
	}
}
