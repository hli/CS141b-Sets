package ps01;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {

    private int clients;
    
    private BlockingQueue<Message> queue;
    private BlockingQueue<Message> token;
    
    public Server(int clients) {
        this.clients = clients;
        this.queue = new LinkedBlockingQueue<Message>();
        this.token = new LinkedBlockingQueue<Message>();
        
        // Construct token for server.
        Message t = new Message();
        t.type = Message.MessageType.TOKEN;
        try {
			this.token.put(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void run() {
        while (this.clients > 0) {
            try {
                
                Message m = this.queue.take();
                
                switch (m.type) {
	                case REQUEST:
	                	
	                	System.out.println(String.format("Server processing request from %d.", m.client.getId()));
	                    
	                	Message resp = this.token.take();
	                    m.client.message(resp);
	                    break;
	                    
	                case TERMINATE:
	                	
	                	System.out.println("Server processing terminate.");
	                    
	                    this.clients -= 1;
	                    break;
                }
                
            } catch (InterruptedException e) {
                
                // TODO Auto-generated catch block
                e.printStackTrace();
                
            }
        }
        System.out.println("Server shut down.");
    }
    
    public void message(Message message) {
        switch (message.type) {
            case REQUEST:
            case TERMINATE:    
                this.queue.add(message);
                break;
                
            case TOKEN:
            	
            	System.out.println("Server processing token.");
                
                this.token.add(message);
                break;
        }
    }
}
