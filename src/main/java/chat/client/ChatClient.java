package chat.client;

import chat.shared.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatClient {
    public static List<Message> printedMessage = new ArrayList<>();
    public static String sessionId = UUID.randomUUID().toString();

    public static void main(String[] args) throws InterruptedException, IOException {
        MessageService messageService = new MessageService();
        Runnable runnable = new UserInputMessageProcessor();

        Thread thread = new Thread(runnable);
        thread.start();

        while (true) {
            List<Message> allMessages = messageService.getAllMessages();
            int n = printedMessage.size();
                for (int i = n; i < allMessages.size(); i++) {
                    if (!allMessages.get(i).getSessionId().equals(ChatClient.sessionId)) {
                        System.out.println(allMessages.get(i).getText());
                    }
               }
            printedMessage = allMessages;
            Thread.sleep(1000);
        }
    }
}
