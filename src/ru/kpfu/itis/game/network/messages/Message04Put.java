package ru.kpfu.itis.game.network.messages;

import ru.kpfu.itis.game.network.GameClient;
import ru.kpfu.itis.game.network.GameServer;

public class Message04Put extends Message{
    public Message04Put(int messageId) {
        super(messageId);
    }
    private String username;
    private int x, y;
    private boolean takePenguin;

    public Message04Put(byte[] data) {
        super(04);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.x = Integer.parseInt(dataArray[1]);
        this.y = Integer.parseInt(dataArray[2]);

    }

    public Message04Put(String username, int x, int y, boolean takePenguin) {
        super(04);
        this.username = username;
        this.x = x;
        this.y = y;
        this.takePenguin = takePenguin;

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
        return ("04" + this.username + "," + this.x + "," + this.y + "," + (takePenguin ? 1 : 0)).getBytes();

    }

    public String getUsername() {
        return username;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
