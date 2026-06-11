package chat.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test of the accept loop: the server must survive any single
 * bad connection and keep serving the next one (the fail-soft guarantee).
 */
class ServerTest {
    private static ServerSocket serverSocket;
    private static int port;

    @BeforeAll
    static void startServerOnEphemeralPort() throws IOException {
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
        Thread serverThread = new Thread(() -> {
            try {
                Server.serve(serverSocket);
            } catch (IOException shutdown) {
                // closing the socket in @AfterAll ends the accept loop
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        serverSocket.close();
    }

    @Test
    void survivesConnectionClosedWithoutSendingAnything() throws IOException {
        new Socket("127.0.0.1", port).close();

        assertEquals("Доставлено", exchange("MSG;server-test-silent;after silent client"));
    }

    @Test
    void survivesGarbageRequestAndKeepsServing() throws IOException {
        String garbageResponse = exchange("complete garbage without separator");

        assertEquals("", garbageResponse);
        assertEquals("Доставлено", exchange("MSG;server-test-garbage;after garbage"));
    }

    @Test
    void storesMessageAndReturnsItOnFetch() throws IOException {
        assertEquals("Доставлено", exchange("MSG;server-test-store;hello;with;semicolons"));

        assertTrue(exchange("GET").contains("server-test-store;hello;with;semicolons"));
    }

    private String exchange(String request) throws IOException {
        try (var socket = new Socket("127.0.0.1", port);
             var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            out.println(request);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString().strip();
        }
    }
}
