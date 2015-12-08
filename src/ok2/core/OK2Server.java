package ok2.core;

public class OK2Server {
	private OK2App app;
	private Delegator del;
	
	public OK2Server(OK2App ok2app){
		app = ok2app;
		del = new Delegator(app);
	}
	
	public void server_start(){
		del.start();
	}
	public void server_stop(){
		del.stop_server();
	}
}
