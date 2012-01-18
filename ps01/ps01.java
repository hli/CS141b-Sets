package ps01;

public class ps01 {
	public static final int NUM_CLIENTS = 10;
	public static final int CLIENT_ITERATIONS = 100;
	
	public static void main(String[] args) {

		Server server = new Server(NUM_CLIENTS);
		(new Thread(server)).start();
		
		for (int i = 0; i < NUM_CLIENTS; i++) {
			(new Thread(new Client(server, CLIENT_ITERATIONS))).start();
		}
	}
}
