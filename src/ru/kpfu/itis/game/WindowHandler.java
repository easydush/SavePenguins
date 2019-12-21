package ru.kpfu.itis.game;

import ru.kpfu.itis.game.network.messages.Message01Disconnect;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class WindowHandler implements WindowListener {

    private final Game game;

    public WindowHandler(Game game) {
        this.game = game;
        this.game.frame.addWindowListener(this);
    }

    @Override
    public void windowActivated(WindowEvent event) {
    }

    @Override
    public void windowClosed(WindowEvent event) {
        windowClosing(event);
    }

    @Override
    public void windowClosing(WindowEvent event) {
        Message01Disconnect message = new Message01Disconnect(this.game.player.getUsername());
        message.writeData(this.game.socketClient);
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
    }

    @Override
    public void windowIconified(WindowEvent event) {
    }

    @Override
    public void windowOpened(WindowEvent event) {
    }

}