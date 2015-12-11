package ok2.core;

import java.io.IOException;

import ok2.httplib.*;

public abstract class OK2App {
	public abstract void invoke( Request req, Response res ) throws IOException;
}
