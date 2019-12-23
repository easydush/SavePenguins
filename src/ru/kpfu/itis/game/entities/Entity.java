package ru.kpfu.itis.game.entities;

import ru.kpfu.itis.game.gfx.Screen;
import ru.kpfu.itis.game.level.Level;

public abstract class Entity {

    public int x, y;
    protected Level level;

    public Entity(Level level) {
        init(level);
    }

    public void init(Level level) {
        this.level = level;
    }

    public abstract void tick();

    public abstract void render(Screen screen);
}