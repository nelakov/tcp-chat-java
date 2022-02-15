package chat.client;

import chat.shared.Message;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class UserInputMessageProcessor implements Runnable{
    @Override
    public void run() {
        MessageService messageService = new MessageService();
        List<Message> printedMessage = ChatClient.printedMessage;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputMessage = scanner.nextLine();
            Message message = new Message(inputMessage, ChatClient.sessionId);
            try {
                printedMessage.add(message);
                messageService.saveToServer(message);
            } catch (IOException | MessageNotDeliveredException e) {
                e.printStackTrace();
            }
        }
    }
}
