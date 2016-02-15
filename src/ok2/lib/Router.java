package ok2.lib;

import ok2.httplib.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.*;

public class Router {
	private HashMap<String,Route> routes = new HashMap<>();
	
	public Router(){}
	
	public void route(String path, Route op){
		routes.put( path, op );
	}
	public void handle(Request req, Response res){
		Set<String> keys = routes.keySet();
		Iterator<String> it = keys.iterator();
		String path = req.getPath();
		boolean routed = false;
		
		while( it.hasNext() ){
			String key = it.next();
			try{
				Pattern p = Pattern.compile(key);
				Matcher m = p.matcher( "^" + path);
				if( m.find() ){
					String g = m.group();
					if( g.equals("/") && !(path.equals("/")) ){ //Special routing case
						continue;
					}
					Route r = routes.get(g);
					if( r == null ){
						continue;
					}
					req.deroute(m.group());
					r.onroute( req, res );
					routed = true;
				}
			}catch(PatternSyntaxException psy){}
		}
		
		if( !routed ){
			res.setStatus(404);
		}
	}
}
