package ru.kpfu.itis.game.network.messages;

import ru.kpfu.itis.game.network.GameClient;
import ru.kpfu.itis.game.network.GameServer;

public class Message01Disconnect extends Message{
    private String username;

    public Message01Disconnect(byte[] data) {
        super(01);
        this.username = readData(data);
    }

    public Message01Disconnect(String username) {
        super(01);
        this.username = username;
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("01" + this.username).getBytes();
    }

    public String getUsername() {
        return username;
    }

}