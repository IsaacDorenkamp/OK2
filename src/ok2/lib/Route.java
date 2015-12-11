package ok2.lib;

import ok2.httplib.*;

public abstract class Route {
	public abstract void onroute( Request req, Response res );
}
