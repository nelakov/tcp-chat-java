package chat.client;

import chat.shared.Message;

import java.io.IOException;
import java.util.Scanner;

public class UserInputMessageProcessor implements Runnable {
    private final MessageService messageService;
    private final String sessionId;

    public UserInputMessageProcessor(MessageService messageService, String sessionId) {
        this.messageService = messageService;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputText = scanner.nextLine();
            sendToServer(new Message(inputText, sessionId));
        }
    }

    private void sendToServer(Message message) {
        try {
            messageService.saveToServer(message);
        } catch (IOException | MessageNotDeliveredException e) {
            System.err.println("message not delivered: '" + message.getText() + "' (" + e.getMessage() + ")");
        }
    }
}
