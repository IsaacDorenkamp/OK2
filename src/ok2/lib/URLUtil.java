package ok2.lib;

import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class URLUtil {
	public static HashMap<String,String> parseQueryString(String url) throws UnsupportedEncodingException{
		try{
			HashMap<String,String> out = new HashMap<>();
			String[] pieces = url.split("&");
			for( int i = 0; i < pieces.length; i++ ){
				String piece = pieces[i];
				String[] vals = piece.split("=");
				String name = URLDecoder.decode(vals[0],"UTF-8");
				String val  = URLDecoder.decode(vals[1],"UTF-8");
				out.put(name,val);
			}
			return out;
		}catch(ArrayIndexOutOfBoundsException aioobe){
			return null;
		}
	}
}
