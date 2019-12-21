package ru.kpfu.itis.game.network.messages;

import ru.kpfu.itis.game.network.GameClient;
import ru.kpfu.itis.game.network.GameServer;
//Continue later
public class Message04Put extends Message{
    public Message04Put(int messageId) {
        super(messageId);
    }

    @Override
    public void writeData(GameClient client) {

    }

    @Override
    public void writeData(GameServer server) {

    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
