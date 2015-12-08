package ok2.httplib;

import java.io.*;

import ok2.core.RespObject;

public abstract class OutputConfiguration {
	public abstract RespObject process( String inp );
	
	public static final OutputConfiguration TEXT = new OutputConfiguration(){
		public RespObject process( String inp ){
			return new RespObject(inp);
		}
	};
	public static final OutputConfiguration FILE = new OutputConfiguration(){
		public RespObject process( String inp ){
			try{
				BufferedReader fr = new BufferedReader(new FileReader( inp ));
				String line;
				String total = "";
				while( ( line = fr.readLine() ) != null ){
					total += line;
				}
				fr.close();
				return new RespObject(total);
			}catch( FileNotFoundException fnfe ){
				return new RespObject(404);
			}catch( IOException ioe ){
				return new RespObject(500);
			}
		}
	};
}
