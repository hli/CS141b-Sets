package ps01;

public class ps01 {
	
	public static void main(String[] args) {
		int numClients = 10;
		int clientIterations = 5;
		
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
		(new Thread(server)).start();
		
		for (int i = 0; i < numClients; i++) {
			(new Thread(new Client(server, clientIterations))).start();
		}
	}
}
