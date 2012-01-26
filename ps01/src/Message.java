public class Message {
    public enum MessageType {
        REQUEST, TERMINATE, TOKEN
    }

    public Client client = null;
    public MessageType type = null;
}