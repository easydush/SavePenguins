package ru.kpfu.itis.game.entities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Penguin {
    private int x,y;
    private boolean isTaken;
    private boolean isSaved;
    private boolean isVisible;
    private BufferedImage image;

    public Penguin() throws IOException {
        this.image = ImageIO.read(Penguin.class.getResourceAsStream("/levels/penguin.png"));
        this.isTaken = false;
        Random random =new Random();
        this.x = random.nextInt()%64;
        this.y = random.nextInt()%64;
        this.isVisible = true;
    }

    public void beSaved(int x, int y, boolean isTaken){
        if(isTaken){
            setSaved(true);
            setX(x);
            setY(y);
            setVisible(true);
        }

    }

    public void beTaken(){
        if(!isTaken){
            setTaken(true);
            setVisible(false);
        }

    }

    public void render(){

    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
