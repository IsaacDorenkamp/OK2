package ok2.ext.websocket;

import java.net.Socket;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;

public class Websocket {
	private String data;
	private OutputStream os;
	public Websocket(String data, Socket cli) throws IOException{
		this.data = data;
		this.os = cli.getOutputStream();
	}
	
	public String getData(){
		return data;
	}
	
	//Send a little packet of data to the client. From yours truly.
	public void send(String data) throws IOException{
		ArrayList<Byte> bytes = new ArrayList<>();
		byte[] bdata = data.getBytes("UTF-8");
		
		bytes.add((byte)129);
		
		if(bdata.length <= 125 ){
			bytes.add((byte)bdata.length);
		}else if(bdata.length >= 126 && bdata.length <= 65535){
			bytes.add((byte)126);
			bytes.add((byte)(bdata.length >> 8 & 0xff));
			bytes.add((byte)(bdata.length & 0xff));
		}else{
			bytes.add((byte)127);
			bytes.add((byte)(bdata.length >> 56 & 0xff));
			bytes.add((byte)(bdata.length >> 48 & 0xff));
			bytes.add((byte)(bdata.length >> 40 & 0xff));
			bytes.add((byte)(bdata.length >> 32 & 0xff));
			bytes.add((byte)(bdata.length >> 24 & 0xff));
			bytes.add((byte)(bdata.length >> 16 & 0xff));
			bytes.add((byte)(bdata.length >> 8 & 0xff));
			bytes.add((byte)(bdata.length & 0xff));
		}
		
		for( int i = 0; i < bdata.length; i++ ){
			bytes.add(bdata[i]);
		}
		
		byte[] out = new byte[bytes.size()];
		for( int i = 0; i < bytes.size(); i++ ){
			out[i] = bytes.get(i);
		}
		os.write(out);
		os.flush();
	}
}
