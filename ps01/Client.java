package ps01;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

	private int iterations;
	private Server server;
	
	private BlockingQueue<Message> token;
	
	public Client(Server server, int iterations) {
		this.server = server;
		this.iterations = iterations;
		this.token = new LinkedBlockingQueue<Message>();
	}
	
	public void run() {
		for (int i = 0; i < this.iterations; i++) {
			try {
				Random randomGenerator = new Random();
				
				// Thinking: wait for some random time.
				Thread.sleep(randomGenerator.nextInt(10) * 1000);
				
				// Hungry
				this.server.message(this, Message.REQUEST);
				
				// When a message appears in the input queue.
				this.token.take();
				
				// Eating: wait for some random time.
				Thread.sleep(randomGenerator.nextInt(10) * 1000);
				
				// Append token to server's queue.
				this.server.message(this, Message.TOKEN);
				
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		
		// Append terminate message to server's queue.
		this.server.message(this, Message.TERMINATE);
    }
	
	public void message(Message message) {
		switch (message) {
			case TOKEN:
				
				this.token.add(Message.TOKEN);
				break;
		}
	}
}
