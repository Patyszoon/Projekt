package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.*;

public class StatusBarManager {
    private JLabel statusBar;
    private Timer statusTimer;
    //private boolean allowAutoReset = true;

    public StatusBarManager() {
        this.statusBar = new JLabel("Gotowy");
        configureStatusBar();
    }

    void configureStatusBar() {
        //pasek stanu na dole okna
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBar.setOpaque(true);
        statusBar.setBackground(Color.PINK);
    }

    public JLabel getStatusBar() {
        return statusBar;
    }

    public void setStatus(String message) {
        setStatus(message, 10000); // 10 sek
    }

    public void setStatus(String message, int durationMs) {
        statusBar.setText(message);

        // zatrzymaj poprzedni timer, jesli istnieje
        if (statusTimer != null && statusTimer.isRunning()) {
            statusTimer.stop();
        }

        if (durationMs > 0) {
            statusTimer = new Timer(durationMs, e ->{
                statusBar.setText("Gotowy");
            });
            statusTimer.setRepeats(false);
            statusTimer.start();
        }
    }

    public void setStatusPermanent(String message) {
        setStatus(message, 0); // bez timera
    }

    public void addMouseListener(java.awt.event.MouseListener listener) {
        statusBar.addMouseListener(listener);
    }

}
