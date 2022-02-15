package chat.client;

import chat.shared.ListDeserializer;
import chat.shared.Message;
import chat.shared.MessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class MessageService {

    public void saveToServer(Message message) throws IOException, MessageNotDeliveredException {
        Socket clientSocket = new Socket("127.0.0.1", 6666);
        String outMessage = MessageSerializer.serializer(message);
        var out = new PrintWriter(clientSocket.getOutputStream(), true);
        var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println(outMessage);
        if (!in.readLine().equals("Доставлено")) {
            throw new MessageNotDeliveredException();
        }
        in.close();
        out.close();
        clientSocket.close();
    }

    public List<Message> getAllMessages() throws IOException {
        Socket clientSocket = new Socket("127.0.0.1", 6666);
        var out = new PrintWriter(clientSocket.getOutputStream(), true);
        var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String sessionId = ChatClient.sessionId;
        Message commandForGetAllMessage = new Message("0", sessionId);
        String outMessage = MessageSerializer.serializer(commandForGetAllMessage);
        out.println(outMessage);

        String line = "";
        String resultList = "";
        while ((line = in.readLine()) != null) {
            resultList += line + "\n";
        }
        ListDeserializer listDesirializer = new ListDeserializer();
        in.close();
        out.close();
        clientSocket.close();
        return listDesirializer.deserialize(resultList);
    }
}















