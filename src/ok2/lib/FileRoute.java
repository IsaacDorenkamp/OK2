package ok2.lib;

import ok2.httplib.*;

public class FileRoute extends Route {
	private String dir;
	public FileRoute(String dir){
		this.dir = dir.replace("..","");
	}
	@Override
	public void onroute(Request req, Response res) {
		// TODO Auto-generated method stub
		String path = req.getDeroutedPath().replace("..","");
		String main = "";
		String all = dir + path;
		main = all;
		if( main.isEmpty() ){
			res.setStatus(404);
			return;
		}
		res.setOutputConfiguration(OutputConfiguration.FILE);
		res.send(main);
	}

}
