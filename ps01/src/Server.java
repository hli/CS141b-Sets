import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {

    private int clients;
    private int numclients;
    private long thinktime;
    private long hungrytime;
    private long mealtime;

    private BlockingQueue<Message> queue;
    private BlockingQueue<Message> token;

    public Server(int clients) {
        this.clients = clients;
        this.numclients = clients;
        this.queue = new LinkedBlockingQueue<Message>();
        this.token = new LinkedBlockingQueue<Message>();
        this.thinktime = 0;
        this.hungrytime = 0;
        this.mealtime = 0;

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

                    System.out.println(String.format(
                            "Server processing request from %d.",
                            m.client.getId()));

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
        System.out.println(String.format("Average time thinking: %d",
                this.getthinktime() / this.numclients));
        System.out.println(String.format("Average time hungry: %d",
                this.gethungrytime() / this.numclients));
        System.out.println(String.format("Average time eating: %d",
                this.getmealtime() / this.numclients));
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

    public void addtothinktime(long time) {
        this.thinktime += time;
    }

    public void addtohungrytime(long time) {
        this.hungrytime += time;
    }

    public void addtomealtime(long time) {
        this.mealtime += time;
    }

    public long getthinktime() {
        return this.thinktime;
    }

    public long gethungrytime() {
        return this.hungrytime;
    }

    public long getmealtime() {
        return this.mealtime;
    }
}
