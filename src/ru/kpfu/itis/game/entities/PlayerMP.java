package ru.kpfu.itis.game.entities;

import ru.kpfu.itis.game.InputHandler;
import ru.kpfu.itis.game.level.Level;

import java.net.InetAddress;
import java.util.ArrayList;

public class PlayerMP extends Player {

    public InetAddress ipAddress;
    public int port;
    public int takenPenguinsNum;
    public int savedPenguinsNum;
    public ArrayList<Penguin> takenPenguins;

    public PlayerMP(Level level, int x, int y, InputHandler input, String username, InetAddress ipAddress, int port) {
        super(level, x, y, input, username);
        this.ipAddress = ipAddress;
        this.port = port;
        this.takenPenguinsNum = 0;
        this.savedPenguinsNum = 0;
    }

    public PlayerMP(Level level, int x, int y, String username, InetAddress ipAddress, int port) {
        super(level, x, y, null, username);
        this.ipAddress = ipAddress;
        this.port = port;
        this.takenPenguinsNum = 0;
        this.savedPenguinsNum = 0;
    }
    public void savePenguin(int x, int y, Penguin penguin){
        if(x<64 && y <8){
            penguin.beSaved(x,y, true);
            takenPenguinsNum--;
            savedPenguinsNum++;
            takenPenguins.remove(penguin);
        }
    }
    public void takePenguin(int x, int y, Penguin penguin){
        penguin.beTaken();
        takenPenguinsNum++;
        takenPenguins.add(penguin);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
