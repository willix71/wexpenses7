package w.wexpense.server;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class StandaloneServer {

	private Server server;
	
	public StandaloneServer(int port) {
		server= new Server(port);
	}
	
	public void startServer() throws Exception {
		LoginService loginService = new HashLoginService("hasCode.com Secured REST Service","src/test/resources/realm.properties");
        server.addBean(loginService);
      
        WebAppContext webapp = new WebAppContext("rest/target/wexpenses-rest-2.4.0-SNAPSHOT.war", "/rest-expenses");        
        server.setHandler(webapp);

        server.start();

        // do not join else we block here and the tests are never run 
        // server.join(); 
	}
	
	public void stopServer() throws Exception {
		server.stop();
	}
	
	public void joinServer() throws Exception {
		server.join();
	}
	
	public static void main(String[] args) throws Exception {
		int port = args.length==0?9191:Integer.parseInt(args[0]);
		
		StandaloneServer ss = new StandaloneServer(port);
		ss.startServer();
		ss.joinServer();
	}
}
