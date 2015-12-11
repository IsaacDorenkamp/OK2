package ok2.httplib;

import java.io.*;

import ok2.core.RespObject;

public abstract class OutputConfiguration {
	private String name;
	public OutputConfiguration(String name){
		this.name = name;
	}
	public OutputConfiguration(){
		this.name= "<Generic>";
	}
	@Override
	public String toString(){
		return name;
	}
	public abstract RespObject process( String inp );
	
	public static final OutputConfiguration TEXT = new OutputConfiguration("Plaintext"){
		public RespObject process( String inp ){
			return new RespObject(inp, -1);
		}
	};
	public static final OutputConfiguration FILE = new OutputConfiguration("File"){
		public RespObject process( String inp ){
			String cwd = System.getProperty("user.dir");
			String main = cwd.replace("..", "");
			try{
				BufferedReader fr = new BufferedReader(new FileReader(main + "/" + inp));
				String line;
				String total = "";
				while( ( line = fr.readLine() ) != null ){
					total += line;
				}
				fr.close();
				return new RespObject(total);
			}catch( FileNotFoundException fnfe ){
				System.out.println( "Not Found: " + main + "/" + inp );
				return new RespObject(404);
			}catch( IOException ioe ){
				return new RespObject(500);
			}
		}
	};
}
