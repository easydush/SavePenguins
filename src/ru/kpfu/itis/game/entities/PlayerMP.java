package ru.kpfu.itis.game.entities;

import ru.kpfu.itis.game.InputHandler;
import ru.kpfu.itis.game.level.Level;

import java.net.InetAddress;
import java.util.ArrayList;

public class PlayerMP extends Player {

    public InetAddress ipAddress;
    public int port;

    public PlayerMP(Level level, int x, int y, InputHandler input, String username, InetAddress ipAddress, int port) {
        super(level, x, y, input, username);
        this.ipAddress = ipAddress;
        this.port = port;

    }

    public PlayerMP(Level level, int x, int y, String username, InetAddress ipAddress, int port) {
        super(level, x, y, null, username);
        this.ipAddress = ipAddress;
        this.port = port;

    }


    @Override
    public void tick() {
        super.tick();
    }
}
