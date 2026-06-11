package chat.client;

import chat.shared.Message;
import chat.shared.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MessageService {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6666;

    private final String serverHost;
    private final int serverPort;

    public MessageService() {
        this(SERVER_HOST, SERVER_PORT);
    }

    MessageService(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void saveToServer(Message message) throws IOException, MessageNotDeliveredException {
        String request = Protocol.CMD_MESSAGE + ";" + Protocol.serialize(message);
        String response = exchange(request).strip();
        if (!Protocol.ACK_DELIVERED.equals(response)) {
            throw new MessageNotDeliveredException("server replied: '" + response + "'");
        }
    }

    public List<Message> getAllMessages() throws IOException {
        String response = exchange(Protocol.CMD_FETCH_ALL);
        return Protocol.deserializeAll(response);
    }

    private String exchange(String request) throws IOException {
        try (var socket = new Socket(serverHost, serverPort);
             var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            out.println(request);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        }
    }
}
