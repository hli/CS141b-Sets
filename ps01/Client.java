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
                Message m = new Message();
                m.type = Message.MessageType.REQUEST;
                m.client = this;
                this.server.message(m);
                
                // When a message appears in the input queue.
                Message resp = this.token.take();
                
                // Eating: wait for some random time.
                Thread.sleep(randomGenerator.nextInt(10) * 1000);
                
                // Append token to server's queue.
                this.server.message(resp);
                
            } catch (InterruptedException e) {
                
                // TODO Auto-generated catch block
                e.printStackTrace();
                
            }
        }
        
        // Append terminate message to server's queue.
        Message term = new Message();
        term.type = Message.MessageType.TERMINATE;
        this.server.message(term);
    }
    
    public void message(Message message) {
        switch (message.type) {
            case TOKEN:
                
                this.token.add(message);
                break;
        }
    }
}
