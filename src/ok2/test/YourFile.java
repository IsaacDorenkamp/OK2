package ok2.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import ok2.core.*;
import ok2.httplib.OutputConfiguration;
import ok2.httplib.Request;
import ok2.httplib.Response;
import ok2.lib.*;
import ok2.ext.*;

public class YourFile extends OK2App{
	private Router router = new Router();
	public static final float VERSION = 1.0F;
	public YourFile(){
		FileRoute fr = new FileRoute("yourfile");
		Route approute = new Route(){
			public void onroute(Request req, Response res){
				res.header("Connection","keep-alive");
				TemplateConfig tc = new TemplateConfig();
				tc.var("version", String.valueOf(Greeter.VERSION));
				
				String contents = readFile();
				
				tc.var("contents", Sanitizer.html(contents));
				
				res.setOutputConfiguration( tc );
				res.header( "Content-type", "text/html" );
				res.send( "yourfile/index.html" );
				return;
			}
		};
		Route saveroute = new Route(){
			public void onroute(Request req, Response res){
				res.header("Connection", "keep-alive");
				if( req.getMethod().equals("POST") ){
					String urlenc = req.getBody();
					try{
						HashMap<String,String> qs = URLUtil.parseQueryString(urlenc);
						String content = qs.get("text");
						if( content == null || content.isEmpty() ){
							res.setOutputConfiguration(OutputConfiguration.TEXT);
							res.send("<!DOCTYPE html><html><head><title>Saved</title><link rel=\"stylesheet\" href=\"/static/style.css\" /></head><body><h1>Your file was empty!</h1><button onclick=\"location.assign('/')\">&lt;&dash;&nbsp;Go Back</button></body></html>");
						}
						FileWriter fw = new FileWriter("yourfile/file.txt");
						fw.write(content);
						fw.close();
						res.setStatus(302);
						res.header("Location", "/");
					}catch( UnsupportedEncodingException uee ){
						res.setStatus(500);
						res.setOutputConfiguration(OutputConfiguration.TEXT);
						res.send("<!DOCTYPE html><html><head><title>Internal Server Error</title></head><body><h1>500 Internal Server Error</h1><p>An error occurred on the server.</p></body></html>");
						return;
					}catch(IOException ioe){
						res.setStatus(500);
						res.setOutputConfiguration(OutputConfiguration.TEXT);
						res.send("<!DOCTYPE html><html><head><title>Internal Server Error</title></head><body><h1>500 Internal Server Error</h1><p>An error occurred on the server.</p></body></html>");
						return;
					}
					
				}
			}
		};
		router.route("/static", fr);
		router.route("/", approute);
		router.route("/save", saveroute);
	}
	public String readFile(){
		String out = "";
		try{
			BufferedReader fr = new BufferedReader(new FileReader("yourfile/file.txt"));
			String line;
			String total = "";
			boolean first = true;
			while( (line = fr.readLine()) != null ){
				if( first ){
					total += line;
					first = false;
				}
				else total += "\n" + line;
			}
			fr.close();
			out = total;
		}catch(IOException ioe){
			out = "";
		}
		return out;
	}
	@Override
	public void invoke(Request req, Response res) throws IOException {
		router.handle(req, res);
	}

	public static void main(String[] args){
		YourFile app = new YourFile();
		OK2Server server = new OK2Server(app);
		server.server_start();
	}
}
