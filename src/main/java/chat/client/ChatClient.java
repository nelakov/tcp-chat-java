package chat.client;

import chat.shared.Message;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ChatClient {
    private static final long POLL_INTERVAL_MILLIS = 1000;

    static void main(String[] args) throws InterruptedException {
        String sessionId = UUID.randomUUID().toString();
        MessageService messageService = new MessageService();

        new Thread(new UserInputMessageProcessor(messageService, sessionId)).start();
        pollAndPrintForeignMessages(messageService, sessionId);
    }

    private static void pollAndPrintForeignMessages(MessageService messageService, String ownSessionId)
            throws InterruptedException {
        int printedCount = 0;
        while (true) {
            try {
                List<Message> allMessages = messageService.getAllMessages();
                printedCount = printNewForeignMessages(allMessages, printedCount, ownSessionId);
            } catch (IOException e) {
                System.err.println("polling failed, will retry: " + e.getMessage());
            }
            Thread.sleep(POLL_INTERVAL_MILLIS);
        }
    }

    private static int printNewForeignMessages(List<Message> allMessages, int alreadyPrinted, String ownSessionId) {
        for (int i = alreadyPrinted; i < allMessages.size(); i++) {
            Message message = allMessages.get(i);
            if (!message.sessionId().equals(ownSessionId)) {
                System.out.println(message.text());
            }
        }
        return allMessages.size();
    }
}
