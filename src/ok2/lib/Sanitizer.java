package ok2.lib;

public class Sanitizer {
	public static String html(String inp){
		return inp.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
	}
}
