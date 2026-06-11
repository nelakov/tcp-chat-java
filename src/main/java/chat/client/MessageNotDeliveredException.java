package chat.client;

public class MessageNotDeliveredException extends Exception {

    public MessageNotDeliveredException(String reason) {
        super(reason);
    }
}
