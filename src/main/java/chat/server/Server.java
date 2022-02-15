package chat.server;

import chat.shared.Message;
import chat.shared.MessageDeserializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws IOException {
        MessageDeserializer deserializer = new MessageDeserializer();
        CommandProcessor processor = new CommandProcessor();

        var serverSocket = new ServerSocket(6666);
        boolean flag = true;
        while (flag) {
            var clientSocket = serverSocket.accept();
            var out = new PrintWriter(clientSocket.getOutputStream(), true);
            var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String command = in.readLine();
            Message message = deserializer.deserialize(command);
            String resultProcessing = processor.process(message);
            out.println(resultProcessing);
            in.close();
            out.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}