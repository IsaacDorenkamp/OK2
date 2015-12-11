package ok2.lib;

public class Cookie {
	private String name;
	private String value;
	public Cookie(String name, String val){
		this.name = name;
		this.value = val;
	}
	
	public String get(){
		return value;
	}
	public String name(){
		return name;
	}
}
