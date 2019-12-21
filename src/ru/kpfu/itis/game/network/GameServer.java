package ru.kpfu.itis.game.network;

import ru.kpfu.itis.game.Game;
import ru.kpfu.itis.game.entities.Penguin;
import ru.kpfu.itis.game.entities.PlayerMP;
import ru.kpfu.itis.game.network.messages.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameServer extends Thread {

    private DatagramSocket socket;
    private Game game;
    private List<PlayerMP> connectedPlayers = new ArrayList<>();
    private ArrayList<Penguin> allPenguins = new ArrayList<>();

    public GameServer(Game game) {
        this.game = game;
        try {
            this.socket = new DatagramSocket(49150);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Random random = new Random();
        for (int i = 0; i < random.nextInt() % 10; i++) {
            try {
                allPenguins.add(new Penguin());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket message = new DatagramPacket(data, data.length);
            try {
                socket.receive(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.parseMessage(message.getData(), message.getAddress(), message.getPort());
        }
    }

    private void parseMessage(byte[] data, InetAddress address, int port) {
        String line = new String(data).trim();
        Message.MessageTypes type = Message.lookupMessage(line.substring(0, 2));
        Message message = null;
        switch (type) {

            default:
            case INVALID:
                break;
            case LOGIN:
                message = new Message00Login(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((Message00Login) message).getUsername() + " has just connected.");
                PlayerMP player = new PlayerMP(game.level, 100, 100, ((Message00Login) message).getUsername(), address, port);
                this.addConnection(player, (Message00Login) message);
                break;
            case DISCONNECT:
                message = new Message01Disconnect(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((Message01Disconnect) message).getUsername() + " has left us.");
                this.removeConnection((Message01Disconnect) message);
                break;

            case MOVE:
                message = new Message02Move(data);
                this.handleMove(((Message02Move) message));

            case TAKE:
                message = new Message03Take(data);
                int curX = ((Message03Take) message).getX();
                int curY = ((Message03Take) message).getY();
                Penguin peng = findPenguin(curX, curY);

                if (peng != null) {
                    PlayerMP curPlayer = connectedPlayers.get(getPlayerMPIndex(((Message03Take) message).getUsername()));
                    if (!peng.isTaken()) {
                        curPlayer.takePenguin(curX, curY, peng);
                    }
                }

            case PUT:
                message = new Message04Put(data);
                int currentX = ((Message04Put) message).getX();
                int currentY = ((Message04Put) message).getY();
                Penguin curpeng = findPenguin(currentX, currentY);

                if (curpeng != null) {
                    PlayerMP curPlayer = connectedPlayers.get(getPlayerMPIndex(((Message04Put) message).getUsername()));
                    if (curpeng.isTaken()) {
                        curPlayer.savePenguin(currentX, currentY, curpeng);
                        allPenguins.remove(curpeng);
                    }
                }
        }
    }

    public Penguin findPenguin(int x, int y) {
        Penguin penguin = null;
        for (Penguin pengui : allPenguins) {
            if (Math.abs(pengui.getX() - x) < 2) {
                if (Math.abs(pengui.getY() - y) < 2) {
                    penguin = pengui;
                }
            }
        }
        return penguin;
    }

    public void addConnection(PlayerMP player, Message00Login message) {
        boolean alreadyConnected = false;
        for (PlayerMP p : this.connectedPlayers) {
            if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
                if (p.ipAddress == null) {
                    p.ipAddress = player.ipAddress;
                }
                if (p.port == -1) {
                    p.port = player.port;
                }
                alreadyConnected = true;
            } else {
                // relay to the current connected player that there is a new player
                sendData(message.getData(), p.ipAddress, p.port);

                // relay to the new player that the currently connect player exists
                message = new Message00Login(p.getUsername(), p.x, p.y);
                sendData(message.getData(), player.ipAddress, player.port);
            }
        }
        if (!alreadyConnected) {
            this.connectedPlayers.add(player);
        }
    }

    public void removeConnection(Message01Disconnect message) {
        this.connectedPlayers.remove(getPlayerMPIndex(message.getUsername()));
        message.writeData(this);
    }

    public PlayerMP getPlayerMP(String username) {
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerMPIndex(String username) {
        int index = 0;
        for (PlayerMP player : this.connectedPlayers) {
            if (player.getUsername().equals(username)) {
                break;
            }
            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        if (!game.isApplet) {

            DatagramPacket message = new DatagramPacket(data, data.length, ipAddress, port);
            try {
                this.socket.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (PlayerMP p : connectedPlayers) {
            sendData(data, p.ipAddress, p.port);
        }
    }

    private void handleMove(Message02Move message) {
        if (getPlayerMP(message.getUsername()) != null) {
            int index = getPlayerMPIndex(message.getUsername());
            PlayerMP player = this.connectedPlayers.get(index);
            player.x = message.getX();
            player.y = message.getY();
            player.setMoving(message.isMoving());
            player.setMovingDir(message.getMovingDir());
            player.setNumSteps(message.getNumSteps());
            message.writeData(this);
        }
    }

    public ArrayList<Penguin> getAllPenguins() {
        return allPenguins;
    }
}