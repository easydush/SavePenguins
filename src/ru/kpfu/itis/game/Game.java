package ru.kpfu.itis.game;

import ru.kpfu.itis.game.gfx.Screen;
import ru.kpfu.itis.game.gfx.SpriteSheet;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Game extends Canvas implements Runnable {
    public static final int WIDTH = 320 ;
    public static final int HEIGHT = WIDTH/12*9;
    public static final int SCALE = 3;
    public static final String NAME = "Save penguins";

    private JFrame frame;

    public boolean running = false;
    public int tickCount = 0;

    private BufferedImage image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    private Screen screen;
    private SpriteSheet spriteSheet = new SpriteSheet("res/sprite_sheet.png");
    public Game() {
        setMinimumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        setMaximumSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
        setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));

        frame = new JFrame(NAME);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(this,BorderLayout.CENTER);
        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000d/60d;
        int frames = 0;
        int ticks = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;
        while (running){
            long now = System.nanoTime();
            delta += (now-lastTime)/nsPerTick;
            lastTime = now;
            boolean shouldRender = true;
            while(delta >= 1){
                ticks++;
                tick();
                delta--;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(shouldRender) {
                frames++;
                render();
            }
            if(System.currentTimeMillis() - lastTimer >= 1000){
                lastTimer += 100;
                frames = 0;
                ticks = 0;
            }
        }
    }

    public synchronized void start() {
        running = true;
        new Thread(this).start();
    }

    public synchronized void stop() {
        running = false;
    }

    public void tick(){
        tickCount++;
        for(int i = 0;i < pixels.length; i++){
          pixels[i] = i +  tickCount;
    }}

    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image,0,0,getWidth(),getHeight(),null);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args){
        new Game().start();
    }

}
