package ru.kpfu.itis.game.entities;

import ru.kpfu.itis.game.Game;
import ru.kpfu.itis.game.InputHandler;
import ru.kpfu.itis.game.gfx.Colours;
import ru.kpfu.itis.game.gfx.Font;
import ru.kpfu.itis.game.gfx.Screen;
import ru.kpfu.itis.game.level.Level;
import ru.kpfu.itis.game.network.GameServer;
import ru.kpfu.itis.game.network.messages.Message02Move;
import ru.kpfu.itis.game.network.messages.Message03Take;
import ru.kpfu.itis.game.network.messages.Message04Put;

import java.util.ArrayList;

public class Player extends Mob {

    private InputHandler input;
    private int colour = Colours.get(-1, 111, 145, 543);
    private int scale = 1;
    protected boolean isSwimming = false;
    private int tickCount = 0;
    private String username;
    public int takenPenguinsNum;
  /*  public int savedPenguinsNum;*/
    //public ArrayList<Penguin> takenPenguins;


    public Player(Level level, int x, int y, InputHandler input, String username) {
        super(level, "Player", x, y, 1);
        this.input = input;
        this.username = username;
        this.takenPenguinsNum = 0;
        /*this.savedPenguinsNum = 0;*/
    }

   /* public void savePenguin(int x, int y, Penguin penguin){
        if(x<64 && y <8){
            penguin.beSaved(x,y, true);
            takenPenguinsNum--;
            savedPenguinsNum++;
            takenPenguins.remove(penguin);
            Message04Put message04Put = new Message04Put(getUsername(),x,y,true);
            message04Put.writeData(Game.game.socketClient);

        }
    }*/
    public void takePenguin(int x, int y){
        Penguin penguin = findPenguin(x,y);
        if(penguin!=null) {
            System.out.println("found:" + penguin.hashCode());
            if (!penguin.isTaken()) {
                penguin.beTaken();
                takenPenguinsNum++;
                Message03Take message03Take = new Message03Take(getUsername(),x,y,true);
                message03Take.writeData(Game.game.socketClient);
                Game.game.penguinList.remove(penguin);
            } /*else savePenguin(x, y, penguin);*/
        }
    }
    public Penguin findPenguin(int x, int y) {
        Penguin penguin = null;
        for (Penguin pengui : Game.game.penguinList) {
            if (Math.abs(pengui.getX() - x) < 32) {
                if (Math.abs(pengui.getY() - y) < 32) {
                    penguin = pengui;
                }
            }
        }
        return penguin;
    }

    public void tick() {
        int xa = 0;
        int ya = 0;
        if (input != null) {
            if(input.take_put.isPressed()){
                takePenguin(this.x,this.y);
            }
            if (input.up.isPressed()) {
                ya--;
            }
            if (input.down.isPressed()) {
                ya++;
            }
            if (input.left.isPressed()) {
                xa--;
            }
            if (input.right.isPressed()) {
                xa++;
            }
        }
        if (xa != 0 || ya != 0) {
            move(xa, ya);
            isMoving = true;

            Message02Move message = new Message02Move(this.getUsername(), this.x, this.y, this.numSteps, this.isMoving,
                    this.movingDir);
            message.writeData(Game.game.socketClient);
        } else {
            isMoving = false;
        }
        if (level.getTile(this.x >> 3, this.y >> 3).getId() == 3) {
            isSwimming = true;
        }
        if (isSwimming && level.getTile(this.x >> 3, this.y >> 3).getId() != 3) {
            isSwimming = false;
        }
        tickCount++;
    }

    public void render(Screen screen) {
        int xTile = 0;
        int yTile = 28;
        int walkingSpeed = 4;
        int flipTop = (numSteps >> walkingSpeed) & 1;
        int flipBottom = (numSteps >> walkingSpeed) & 1;

        if (movingDir == 1) {
            xTile += 2;
        } else if (movingDir > 1) {
            xTile += 4 + ((numSteps >> walkingSpeed) & 1) * 2;
            flipTop = (movingDir - 1) % 2;
        }

        int modifier = 8 * scale;
        int xOffset = x - modifier / 2;
        int yOffset = y - modifier / 2 - 4;
        if (isSwimming) {
            int waterColour = 0;
            yOffset += 4;
            if (tickCount % 60 < 15) {
                waterColour = Colours.get(-1, -1, 225, -1);
            } else if (15 <= tickCount % 60 && tickCount % 60 < 30) {
                yOffset -= 1;
                waterColour = Colours.get(-1, 225, 115, -1);
            } else if (30 <= tickCount % 60 && tickCount % 60 < 45) {
                waterColour = Colours.get(-1, 115, -1, 225);
            } else {
                yOffset -= 1;
                waterColour = Colours.get(-1, 225, 115, -1);
            }
            screen.render(xOffset, yOffset + 3, 0 + 27 * 32, waterColour, 0x00, 1);
            screen.render(xOffset + 8, yOffset + 3, 0 + 27 * 32, waterColour, 0x01, 1);
        }
        screen.render(xOffset + (modifier * flipTop), yOffset, xTile + yTile * 32, colour, flipTop, scale);
        screen.render(xOffset + modifier - (modifier * flipTop), yOffset, (xTile + 1) + yTile * 32, colour, flipTop,
                scale);

        if (!isSwimming) {
            screen.render(xOffset + (modifier * flipBottom), yOffset + modifier, xTile + (yTile + 1) * 32, colour,
                    flipBottom, scale);
            screen.render(xOffset + modifier - (modifier * flipBottom), yOffset + modifier, (xTile + 1) + (yTile + 1)
                    * 32, colour, flipBottom, scale);
        }
        if (username != null) {
            Font.render(username, screen, xOffset - ((username.length() - 1) / 2 * 8), yOffset - 10,
                    Colours.get(-1, -1, -1, 555), 1);
        }
    }

    public boolean hasCollided(int xa, int ya) {
        int xMin = 0;
        int xMax = 7;
        int yMin = 3;
        int yMax = 7;
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMin)) {
                return true;
            }
        }
        for (int x = xMin; x < xMax; x++) {
            if (isSolidTile(xa, ya, x, yMax)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMin, y)) {
                return true;
            }
        }
        for (int y = yMin; y < yMax; y++) {
            if (isSolidTile(xa, ya, xMax, y)) {
                return true;
            }
        }
        return false;
    }

    public String getUsername() {
        return this.username;
    }
}