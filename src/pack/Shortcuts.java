package pack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class Shortcuts {
    private final MainWindow mainWindow;
    private DialogManager dialogManager;

    public Shortcuts(MainWindow mainWindow, DialogManager dialogManager) {
        this.mainWindow = mainWindow;
        this.dialogManager = dialogManager;
        initShortcuts(mainWindow.getRootPane());
    }

    private boolean isReady(){
        return mainWindow.getCurrentState() == MainWindow.appState.READY;
    }
    private void initShortcuts(JRootPane rootPane) {
        // TODO: dodać skróty klawiszowe do akcji (w menu opis jaki skrót do czego)
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();

        // Ctrl+S to select
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "select");
        am.put("select", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogManager.selectTableDialog();
            }
        });
        // Ctrl+N to new user
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newUser");
        am.put("newUser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isReady()) return;
                dialogManager.showAddUserDialog();
            }
        });
        // Ctrl+E to edit user
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "editUser");
        am.put("editUser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isReady()) return;
                dialogManager.showEditUserDialog();
            }
        });
        // Ctrl+D to delete user
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "delUser");
        am.put("delUser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isReady()) return;
                dialogManager.showDelUserDialog();
            }
        });
        // Ctrl+P to show users
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "showUsers");
        am.put("showUsers", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            if(!isReady()) return;
                try {
                    dialogManager.showUsersDialog();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Ctrl+Q to quit
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "quit");
        am.put("quit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // F1 for help
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help");
        am.put("help", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogManager.showHelpDialog();
            }
        });
        // F2 for about
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "about");
        am.put("about", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogManager.showAboutDialog();
            }
        });
        // F5 to refresh
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        am.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.dispose();
                try {
                    new MainWindow(mainWindow.getUserCRUD());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
