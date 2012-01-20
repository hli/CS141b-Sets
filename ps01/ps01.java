// Hello world.

package ps01;

import java.util.LinkedList;
import java.util.Queue;

public class ps01 {
	
	public static void main(String[] args) {
		int numClients = 30;
		int clientIterations = 50;
		
		// Check that command-line arguments are valid.
        try {
            switch (args.length) {
	            case 0:
	                break;
            	
                // Parse the string arguments into integer values.
	            case 1:
		            numClients = Integer.parseInt(args[0]);
	                break;
                
	            case 2:
	            	numClients = Integer.parseInt(args[0]);
		            clientIterations = Integer.parseInt(args[1]);
		            break;
		            
	            default:
	            	throw new Exception(); 	
            }
            
            if ((numClients <= 0) || (clientIterations <= 0))
                throw new Exception();

        } catch (Exception e) {
            System.out.println("usage: java ps01.ps01 [number of clients] " +
            				   "[number of iterations per client]");
            System.exit(1);
        }
        
		Server server = new Server(numClients);
		Thread serverThread = new Thread(server);
		
		serverThread.start();
		Queue<Thread> clientThreads = new LinkedList<Thread>();
		
		for (int i = 0; i < numClients; i++) {
			Thread c = new Thread(new Client(i, server, clientIterations));
			clientThreads.add(c);
			c.start();
		}
		
		while (!clientThreads.isEmpty())
		{
			Thread t = clientThreads.remove();
			try {
				t.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			serverThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
