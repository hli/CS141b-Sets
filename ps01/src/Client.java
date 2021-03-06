import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Runnable {

    private int id;
    private int iterations;
    private long thinktime;
    private long hungrytime;
    private long mealtime;
    private Server server;

    private BlockingQueue<Message> token;

    public Client(int id, Server server, int iterations) {
        this.id = id;
        this.server = server;
        this.iterations = iterations;
        this.thinktime = 0;
        this.hungrytime = 0;
        this.mealtime = 0;
        this.token = new LinkedBlockingQueue<Message>();
    }

    public void run() {
        Random randomGenerator = new Random();
        for (int i = 0; i < this.iterations; i++) {
            try {
                // Thinking: wait for some random time.
                long beforethink = System.currentTimeMillis();
                System.out.println(String
                        .format("Client %d thinking.", this.id));
                Thread.sleep(randomGenerator.nextInt(10) + 195);
                this.addtothinktime(System.currentTimeMillis() - beforethink);

                // Hungry
                long beforehungry = System.currentTimeMillis();
                System.out.println(String.format("Client %d hungry.", this.id));
                Message m = new Message();
                m.type = Message.MessageType.REQUEST;
                m.client = this;
                this.server.message(m);

                // When a message appears in the input queue.
                Message resp = this.token.take();
                System.out.println(String.format("Client %d has token.",
                        this.id));
                this.addtohungrytime(System.currentTimeMillis() - beforehungry);

                // Eating: wait for some random time.
                long beforemeal = System.currentTimeMillis();
                System.out.println(String.format("Client %d eating.", this.id));
                Thread.sleep(randomGenerator.nextInt(10) + 15);

                // Append token to server's queue.
                System.out.println(String.format(
                        "Client %d giving token back.", this.id));
                this.server.message(resp);
                this.addtomealtime(System.currentTimeMillis() - beforemeal);

            } catch (InterruptedException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }

        // Append terminate message to server's queue.
        Message term = new Message();
        term.type = Message.MessageType.TERMINATE;
        this.server.message(term);
        this.server.addtothinktime(this.getthinktime() / this.iterations);
        this.server.addtohungrytime(this.gethungrytime() / this.iterations);
        this.server.addtomealtime(this.getmealtime() / this.iterations);
    }

    public void message(Message message) {
        switch (message.type) {
        case TOKEN:

            this.token.add(message);
            break;
        }
    }

    public int getId() {
        return this.id;
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
