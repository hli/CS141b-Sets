package ps01;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {

	private int clients;
	
	private BlockingQueue<Client> queue;
	private BlockingQueue<Message> token;
	
	public Server(int clients) {
		this.clients = clients;
		this.queue = new LinkedBlockingQueue<Client>();
		this.token = new LinkedBlockingQueue<Message>();
	}
	
	public void run() {
		while (this.clients > 0) {
			try {
				
				Client c = this.queue.take();			
				c.message(this.token.take());
				
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			
		}
    }
	
	public void message(Client client, Message message) {
		switch (message) {
			case REQUEST:
				
				this.queue.add(client);
				break;
				
			case TERMINATE:
				
				this.clients -= 1;
				break;
				
			case TOKEN:
				
				this.token.add(Message.TOKEN);
				break;
		}
	}
}
