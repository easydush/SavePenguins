package ru.kpfu.itis.game;

import ru.kpfu.itis.game.entities.Penguin;
import ru.kpfu.itis.game.entities.Player;
import ru.kpfu.itis.game.entities.PlayerMP;
import ru.kpfu.itis.game.gfx.Screen;
import ru.kpfu.itis.game.gfx.SpriteSheet;
import ru.kpfu.itis.game.level.Level;
import ru.kpfu.itis.game.network.GameClient;
import ru.kpfu.itis.game.network.GameServer;
import ru.kpfu.itis.game.network.messages.Message00Login;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 320;
    private static final int HEIGHT = (WIDTH / 16) * 9;
    private static final int SCALE = 2;
    static final String NAME = "Save penguins";
    static final Dimension DIMENSIONS = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
    public static Game game;

    public JFrame frame;

    private Thread thread;

    public boolean running = false;
    public int tickCount = 0;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private int[] colours = new int[6 * 6 * 6];

    private Screen screen;
    public InputHandler input;
    public WindowHandler windowHandler;
    public Level level;
    public Player player;
    public ArrayList<Penguin> penguinList;

    public GameClient socketClient;
    public GameServer socketServer;

    public boolean debug = true;
    public boolean isApplet = false;


    public void init() {
        game = this;
        int index = 0;
        for (int r = 0; r < 6; r++) {
            for (int g = 0; g < 6; g++) {
                for (int b = 0; b < 6; b++) {
                    int rr = (r * 255 / 5);
                    int gg = (g * 255 / 5);
                    int bb = (b * 255 / 5);

                    colours[index++] = rr << 16 | gg << 8 | bb;
                }
            }
        }
        screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
        input = new InputHandler(this);
        level = new Level("/levels/water_test_level.png");
        player = new PlayerMP(level, 100, 100, input, JOptionPane.showInputDialog(this, "Please enter a username"),
                null, -1);
        level.addEntity(player);
        if (!isApplet) {
            Message00Login loginMessage = new Message00Login(player.getUsername(), player.x, player.y);
            if (socketServer != null) {
                socketServer.addConnection((PlayerMP) player, loginMessage);
            }
            loginMessage.writeData(socketClient);
        }
    }

    public synchronized void start() {
        running = true;

        thread = new Thread(this, NAME + "_main");
        thread.start();
        if (!isApplet) {
            if (JOptionPane.showConfirmDialog(this, "Do you want to start the game?") == 0) {
                socketServer = new GameServer(this);
                socketServer.start();
            }

            socketClient = new GameClient(this, "localhost");
            socketClient.start();
            penguinList = socketServer.getAllPenguins();

        }
    }

    public synchronized void stop() {
        running = false;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        init();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                frames = 0;
                ticks = 0;
            }
            if(penguinList.size()<3) {
                for (int k=0; k<3; k++) {
                    try {
                        penguinList.add(new Penguin());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void tick() {
        tickCount++;
        level.tick();
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        int xOffset = player.x - (screen.width / 2);
        int yOffset = player.y - (screen.height / 2);

        level.renderTiles(screen, xOffset, yOffset);
        level.renderEntities(screen);

        for (int y = 0; y < screen.height; y++) {
            for (int x = 0; x < screen.width; x++) {
                int colourCode = screen.pixels[x + y * screen.width];
                if (colourCode < 255)
                    pixels[x + y * WIDTH] = colours[colourCode];
            }
        }

        Graphics graphics = bs.getDrawGraphics();
        graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        System.out.println(player.x+";"+player.y);
        for(Penguin peng: penguinList){
            if(peng.isVisible()){
                graphics.drawImage(peng.getImage(),peng.getX(),peng.getY(),16,16,null);
            }
        }
        graphics.dispose();
        bs.show();
    }

}