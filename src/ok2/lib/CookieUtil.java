package ok2.lib;

public class CookieUtil {
	public static Cookie[] parse(String cookie){
		if( cookie == null ) return null;
		String[] cookies = cookie.split(";");
		Cookie[] output = new Cookie[cookies.length];
		for( int i = 0; i < cookies.length; i++ ){
			String[] parts = cookies[i].split("=");
			String name = parts[0].trim();
			String val = "";
			if( parts.length < 2 ){
				output[i] = null;
				continue;
			}else{
				val  = parts[1].trim();
			}
			output[i] = new Cookie(name,val);
		}
		return output;
	}
	public static Cookie find(String cookie, Cookie[] cookies){
		if( cookies == null ) return null;
		for( int i = 0; i < cookies.length; i++ ){
			Cookie c = cookies[i];
			if( c != null ){
				if( c.name().equals(cookie) ){
					return c;
				}
			}
		}
		return null;
	}
	public static void dump(Cookie[] cookies){
		for( int i = 0; i < cookies.length; i++ ){
			Cookie c = cookies[i];
			System.out.println( c.name() + ": " + c.get() );
		}
	}
}
