package ok2.core;

import java.net.Socket;
import java.io.IOException;

public class TimerFactory {
	public static Thread createTimer(final int millis, final Socket s){
		return new Thread( new Runnable(){
			@Override
			public void run(){
				try{
					Thread.sleep(millis);
					System.out.println("Closing connection");
					s.close();
				}catch(InterruptedException ie){}
				catch(IOException ioe){}
			}
		} );
	}
}
