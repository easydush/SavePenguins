package ru.kpfu.itis.game.network;

import ru.kpfu.itis.game.Game;
import ru.kpfu.itis.game.entities.Penguin;
import ru.kpfu.itis.game.entities.PlayerMP;
import ru.kpfu.itis.game.network.messages.*;

import java.io.IOException;
import java.net.*;

public class GameClient extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private Game game;

    public GameClient(Game game, String ipAddress) {
        this.game = game;
        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
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
        String messageS = new String(data).trim();
        Message.MessageTypes type = Message.lookupMessage(messageS.substring(0, 2));
        Message message = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                message = new Message00Login(data);
                handleLogin((Message00Login) message, address, port);
                break;
            case DISCONNECT:
                message = new Message01Disconnect(data);
                System.out.println("[" + address.getHostAddress() + ":" + port + "] "
                        + ((Message01Disconnect) message).getUsername() + " has left us.");
                game.level.removePlayerMP(((Message01Disconnect) message).getUsername());
                break;
            case MOVE:
                message = new Message02Move(data);
                handleMove((Message02Move) message);

           /* case PUT:
                message = new Message04Put(data);
                System.out.println("["+address.getHostAddress()+":"+port+"]"+
                        ((Message04Put) message).getUsername()+"has put the penguin");
                game.player.takePenguin(((Message04Put) message).getX(),((Message04Put) message).getY());
*/
            case TAKE:
                message = new Message03Take(data);
                System.out.println("["+address.getHostAddress()+":"+port+"]"+
                        ((Message03Take) message).getUsername()+"has taken the penguin");
                game.player.takePenguin(((Message03Take) message).getX(),((Message03Take) message).getY());
        }
    }

    public void sendData(byte[] data) {
        if (!game.isApplet) {
            DatagramPacket message = new DatagramPacket(data, data.length, ipAddress, 1331);
            try {
                socket.send(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLogin(Message00Login Message, InetAddress address, int port) {
        System.out.println("[" + address.getHostAddress() + ":" + port + "] " + Message.getUsername()
                + " has joined the game.");
        PlayerMP player = new PlayerMP(game.level, Message.getX(), Message.getY(), Message.getUsername(), address, port);
        game.level.addEntity(player);
    }

    private void handleMove(Message02Move Message) {
        this.game.level.movePlayer(Message.getUsername(), Message.getX(), Message.getY(), Message.getNumSteps(),
                Message.isMoving(), Message.getMovingDir());
    }
}