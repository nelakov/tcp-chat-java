package chat.client;

import chat.shared.Message;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageServiceTest {

    @Test
    void deliveredAckCompletesNormally() throws IOException {
        try (ServerSocket fakeServer = fakeServerReplying("Доставлено")) {
            MessageService service = serviceFor(fakeServer);

            assertDoesNotThrow(() -> service.saveToServer(new Message("hello", "session")));
        }
    }

    @Test
    void unexpectedReplyMeansNotDelivered() throws IOException {
        try (ServerSocket fakeServer = fakeServerReplying("что-то не то")) {
            MessageService service = serviceFor(fakeServer);

            MessageNotDeliveredException e = assertThrows(MessageNotDeliveredException.class,
                    () -> service.saveToServer(new Message("hello", "session")));
            assertTrue(e.getMessage().contains("что-то не то"));
        }
    }

    @Test
    void silentConnectionCloseMeansNotDelivered() throws IOException {
        try (ServerSocket fakeServer = fakeServerReplying(null)) {
            MessageService service = serviceFor(fakeServer);

            assertThrows(MessageNotDeliveredException.class,
                    () -> service.saveToServer(new Message("hello", "session")));
        }
    }

    @Test
    void getAllMessagesParsesServerResponse() throws IOException {
        try (ServerSocket fakeServer = fakeServerReplying("some-session;hi;with;semicolons")) {
            MessageService service = serviceFor(fakeServer);

            List<Message> messages = service.getAllMessages();

            assertEquals(List.of(new Message("hi;with;semicolons", "some-session")), messages);
        }
    }

    private MessageService serviceFor(ServerSocket fakeServer) {
        return new MessageService("127.0.0.1", fakeServer.getLocalPort());
    }

    /** Accepts one connection, reads one line, optionally replies, closes. */
    private ServerSocket fakeServerReplying(String reply) throws IOException {
        ServerSocket fakeServer = new ServerSocket(0);
        Thread handler = new Thread(() -> {
            try (Socket socket = fakeServer.accept();
                 var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                in.readLine();
                if (reply != null) {
                    out.println(reply);
                }
            } catch (IOException ignored) {
            }
        });
        handler.setDaemon(true);
        handler.start();
        return fakeServer;
    }
}
