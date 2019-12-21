package ru.kpfu.itis.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    public InputHandler(Game game) {
        game.addKeyListener(this);
    }

    public class Key {
        private int numTimesPressed = 0;
        private boolean isPressed = false;
        public boolean isPressed() {
            return isPressed;
        }

        public void on(boolean press) {
            isPressed = press;
            if (isPressed) numTimesPressed++;
        }
    }

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key take_put = new Key();

    public void keyPressed(KeyEvent e) {
        onKey(e.getKeyCode(), true);
    }

    public void keyReleased(KeyEvent e) {
        onKey(e.getKeyCode(), false);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void onKey(int keyCode, boolean isPressed) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            up.on(isPressed);
        }
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            down.on(isPressed);
        }
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            left.on(isPressed);
        }
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            right.on(isPressed);
        }
        if (keyCode == KeyEvent.VK_SPACE) {
            take_put.on(isPressed);
        }
    }
}