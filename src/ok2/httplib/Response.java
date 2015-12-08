package ok2.httplib;

import java.util.Arrays;
import java.util.HashMap;

import ok2.core.*;

public class Response {
	private float version;
	private String body;
	private int code = 200;
	private OutputConfiguration cfg = OutputConfiguration.TEXT;
	
	private HashMap<String,String> headers = new HashMap<>();
	
	public Response( float version, String body ){
		this.body = body;
		this.version = version;
	}
	public Response( float version ){
		this( version, "" );
	}
	public Response(){
		this( 1.1F );
	}
	
	public void setCode( int code ){
		this.code = code;
	}
	public void header( String name, String value ){
		headers.put( name, value );
	}
	
	//Getters
	public String getHeader( String name ){
		return headers.get( name );
	}
	public String getBody(){
		return body;
	}
	public int getCode(){
		return code;
	}
	public float getVersion(){
		return version;
	}
	
	//Response Interaction Methods
	public void send( String text ){
		RespObject respobj = cfg.process( text );
		body += respobj.getOutput();
		code = respobj.getStatus();
	}
	
	//Static parse method
	public static final Response parse(String value){
		float version;
		int status;
		
		String heads = value.split("\r\n\r\n")[0];
		String[] lines = heads.split("\r\n");
		String data = lines[0];
		String[] headers = Arrays.copyOfRange( lines, 1, lines.length );
		
		String[] maindata = data.split(" ");
		version = Float.parseFloat(maindata[0].split("/")[1]);
		status = Integer.parseInt(maindata[1]);
		
		Response res = new Response( version );
		res.setCode( status );
		for( int i = 0; i < headers.length; i++ ){
			String[] headdata = headers[i].split(":");
			String headname = headdata[0];
			String headvalue = headdata[1];
			
			res.header( headname, headvalue );
		}
		return res;
	}
}
