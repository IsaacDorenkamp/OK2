package ok2.ext;

import ok2.core.RespObject;
import ok2.httplib.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;

import java.util.HashMap;

public class TemplateConfig extends OutputConfiguration{
	private HashMap<String,String> scope = new HashMap<>();
	public static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{[A-Za-z1-9_]*\\}");
	public TemplateConfig(){
		super("Template");
	}
	
	public void var(String name, String val){
		scope.put( name, val );
	}
	public String get(String name){
		return scope.get(name);
	}
	
	
	@Override
	public RespObject process(String inp) {
		// TODO Auto-generated method stub
		String out;
		String cwd = System.getProperty("user.dir");
		String main = cwd.replace("..", "");
		try{
			BufferedReader fr = new BufferedReader(new FileReader(main + "/" + inp));
			String line;
			String total = "";
			while( ( line = fr.readLine() ) != null ){
				total += line;
			}
			System.out.println( "Success" );
			fr.close();
			out = total;
		}catch( FileNotFoundException fnfe ){
			System.out.println( "Not Found: " + main + "/" + inp );
			return new RespObject(404);
		}catch( IOException ioe ){
			return new RespObject(500);
		}
		
		if( out.isEmpty() ){
			return new RespObject(204);
		}
		
		Matcher m = TemplateConfig.VAR_PATTERN.matcher(out);
		while( m.find() ){
			String group = m.group();
			System.out.println(group);
			String name = group.substring(2, group.length()-1);
			String var = scope.get(name);
			if( var == null ) var = "";
			out = out.substring( 0, m.start() ) + var + out.substring( m.end(), out.length() );
			m = TemplateConfig.VAR_PATTERN.matcher(out);
		}
		return new RespObject( out );
	}
	
}
