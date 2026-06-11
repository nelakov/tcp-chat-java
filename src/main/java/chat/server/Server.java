package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private static final int PORT = 6666;

    public static void main(String[] args) throws IOException {
        CommandProcessor commandProcessor = new CommandProcessor();
        try (var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                handleClient(serverSocket.accept(), commandProcessor);
            }
        }
    }

    private static void handleClient(Socket clientSocket, CommandProcessor commandProcessor) {
        try (clientSocket;
             var out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
             var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8))) {
            String request = in.readLine();
            if (request == null) {
                return;
            }
            out.println(commandProcessor.process(request));
        } catch (IOException | RuntimeException e) {
            System.err.println("connection handling failed: " + e);
        }
    }
}
