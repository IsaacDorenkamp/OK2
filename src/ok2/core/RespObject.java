package ok2.core;

public class RespObject {
	private int status;
	private String output;
	
	public RespObject(String out, int status){
		this.status = status;
		this.output = out;
	}
	public RespObject(String out){
		this(out, 200);
	}
	public RespObject(int status){
		this("", status);
	}
	public RespObject(){
		this("");
	}
	
	public int getStatus(){
		return status;
	}
	public String getOutput(){
		return output;
	}
}
