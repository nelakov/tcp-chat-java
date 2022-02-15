package chat.server;

import chat.shared.ListSerializer;
import chat.shared.Message;

import java.util.List;

public class CommandProcessor {
    ArchiveMessage archive = ArchiveMessage.getArchiveMessage();

    public String process(Message command) {
        if (command.getText().equals("0")) {
            List<Message> getAll = archive.getAll();
            System.out.println("Command processed 0 has been done");
            return ListSerializer.serializer(getAll);
        } else {
            archive.add(command);
            System.out.println("Command processed saveToServer has been done");
            return "Доставлено";
        }
    }
}
