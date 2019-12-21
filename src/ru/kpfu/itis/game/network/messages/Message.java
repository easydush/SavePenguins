package ru.kpfu.itis.game.network.messages;

import ru.kpfu.itis.game.network.GameClient;
import ru.kpfu.itis.game.network.GameServer;

public abstract class Message {
    public static enum MessageTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), TAKE(03), PUT(04);

        private int messageId;

        private MessageTypes(int messageId) {
            this.messageId = messageId;
        }

        public int getId() {
            return messageId;
        }
    }

    public byte messageId;

    public Message(int messageId) {
        this.messageId = (byte) messageId;
    }

    public abstract void writeData(GameClient client);

    public abstract void writeData(GameServer server);

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static MessageTypes lookupMessage(String messageId) {
        try {
            return lookupMessage(Integer.parseInt(messageId));
        } catch (NumberFormatException e) {
            return MessageTypes.INVALID;
        }
    }

    public static MessageTypes lookupMessage(int id) {
        for (MessageTypes p : MessageTypes.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return MessageTypes.INVALID;
    }
}