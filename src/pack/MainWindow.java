package pack;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainWindow extends JFrame{

    public enum appState {
        READY,
        NO_CONNECTION,
    }
    private appState currentState = appState.READY;
    private UserCRUD userCRUD;
    private StatusBarManager statusBarManager;

    private TableManager tableManager;
    private DialogManager dialogManager;
    private PopupMenuManager popupMenuManager;
    private JTextPane infoPane;

    // przyciski
    private JButton newUserBtn;
    private JButton showUsersBtn;
    private JButton editUserBtn;
    private JButton delUserBtn;

    // pozycje menu
    private JMenuItem newUserItem;
    private JMenuItem showUsersItem;
    private JMenuItem editUserItem;
    private JMenuItem delUserItem;
    private JMenuItem selectTableItem;

    public UserCRUD getUserCRUD() {
        return userCRUD;
    }
    public appState getCurrentState() {
        return currentState;
    }

    public MainWindow(UserCRUD userCRUD) throws SQLException{
        this.userCRUD = userCRUD;
        setupWindow();
        createMenu();
        updateUiState(appState.NO_CONNECTION);
        setVisible(true); // pokaz okno
        new Shortcuts(this, dialogManager);
    }

    private void setupWindow() throws SQLException{ //rzeczy w srdoku okna
        setTitle("Aplikacja do zarządzania bazą danych"); // tytuł okna
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zamykaj, gdy x
        setLocationRelativeTo(null);

        // status bar
        statusBarManager = new StatusBarManager();
        add(statusBarManager.getStatusBar(), BorderLayout.SOUTH);

        // inicjalizacja TableManager
        JTable mainTable = new JTable(); // zmienna lokalna
        tableManager = new TableManager(mainTable, userCRUD);

        dialogManager = new DialogManager(this, userCRUD, statusBarManager, tableManager);

        popupMenuManager = new PopupMenuManager(this, dialogManager);
        mainTable.setComponentPopupMenu(popupMenuManager.getPopupMenu());

        // menu główne
        add(createMainMenuPanel(), BorderLayout.CENTER);
    }

    private JPanel createMainMenuPanel() throws SQLException {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        infoPane = new JTextPane();
        infoPane.setEditable(false);
        infoPane.setOpaque(false);
        infoPane.setBackground(new Color(0,0,0,0));
        infoPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoPane.setCaret(null);
        infoPane.setPreferredSize(new Dimension(200, 100));
        infoPane.setText("Brak wybranej tabeli.\n" + "Wybierz tabelę z menu 'Plik -> Wybierz tabele' lub użyj skrótu Ctrl+S");
        panel.add(infoPane);

        JScrollPane tableScroll = new JScrollPane(tableManager.getTable());
        tableScroll.setPreferredSize(new Dimension(700, 300));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(tableScroll);

        newUserBtn = new JButton("Nowy użytkownik");
        showUsersBtn = new JButton("Pokaż użytkowników");
        editUserBtn = new JButton("Edytuj użytkownika");
        delUserBtn = new JButton("Usuń użytkownika");

        newUserBtn.addActionListener(e -> dialogManager.showAddUserDialog());
        showUsersBtn.addActionListener(e -> {
            try {
                dialogManager.showUsersDialog();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        editUserBtn.addActionListener(e -> dialogManager.showEditUserDialog());
        delUserBtn.addActionListener(e -> dialogManager.showDelUserDialog());

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Object src = e.getSource();
                if (src == newUserBtn)        setStatusBar("Nowy użytkownik");
                else if (src == showUsersBtn) setStatusBar("Pokaż użytkowników");
                else if (src == editUserBtn)  setStatusBar("Edytuj użytkownika");
                else if (src == delUserBtn)   setStatusBar("Usuń użytkownika");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                restoreStatusForCurrentState();
            }
        };

        newUserBtn.addMouseListener(mouseAdapter);
        showUsersBtn.addMouseListener(mouseAdapter);
        editUserBtn.addMouseListener(mouseAdapter);
        delUserBtn.addMouseListener(mouseAdapter);

        panel.add(newUserBtn);
        panel.add(showUsersBtn);
        panel.add(editUserBtn);
        panel.add(delUserBtn);

        return panel;
    }

    private void createMenu() { //pasek menu u gory
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Plik");
        JMenu helpMenu = new JMenu("Pomoc");

        newUserItem = new JMenuItem("Nowy uzytkownik");
        newUserItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newUserItem.setToolTipText("Ctrl+N - dodaj nowego użytkownika");

        showUsersItem = new JMenuItem("Pokaż uzytkowników");
        showUsersItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        showUsersItem.setToolTipText("Ctrl+P - pokaż użytkowników");

        editUserItem = new JMenuItem("Edytuj uzytkownika");
        editUserItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        editUserItem.setToolTipText("Ctrl+E - edytuj użytkownika");

        delUserItem = new JMenuItem("Usun uzytkownika");
        delUserItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        delUserItem.setToolTipText("Ctrl+D - usun uzytkownika");

        selectTableItem = new JMenuItem("Wybierz tabele");
        selectTableItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        selectTableItem.setToolTipText("Ctrl+S - wybierz tabele");

        JMenuItem exitItem = new JMenuItem("Wyjscie");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        JMenuItem aboutItem = new JMenuItem("O nas");
        JMenuItem helpItem = new JMenuItem("Pomoc");

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Object src = e.getSource();
                if (src == menuBar)          setStatusBar("Menu");
                else if (src == fileMenu)    setStatusBar("Pliki");
                else if (src == helpMenu)        setStatusBar("Pomoc");
                else if (src == aboutItem)       setStatusBar("O nas");
                else if (src == helpItem)   setStatusBar("Pomoc");
                else if (src == newUserItem) setStatusBar("Nowy uzytkownik");
                else if (src == showUsersItem) setStatusBar("Pokaż uzytkowników");
                else if (src == editUserItem)  setStatusBar("Edytuj uzytkownika");
                else if (src == delUserItem)   setStatusBar("Usun uzytkownika");
                else if (src == selectTableItem)      setStatusBar("Wybierz tabele");
                else if (src == exitItem)      setStatusBar("Wyjscie");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                restoreStatusForCurrentState();
            }
        };

        menuBar.addMouseListener(mouseAdapter);
        fileMenu.addMouseListener(mouseAdapter);
        helpMenu.addMouseListener(mouseAdapter);
        aboutItem.addMouseListener(mouseAdapter);
        helpItem.addMouseListener(mouseAdapter);
        newUserItem.addMouseListener(mouseAdapter);
        showUsersItem.addMouseListener(mouseAdapter);
        editUserItem.addMouseListener(mouseAdapter);
        delUserItem.addMouseListener(mouseAdapter);
        selectTableItem.addMouseListener(mouseAdapter);
        exitItem.addMouseListener(mouseAdapter);

        newUserItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.showAddUserDialog();
            }
        });

        showUsersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    dialogManager.showUsersDialog();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        editUserItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.showEditUserDialog();
            }
        });

        delUserItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.showDelUserDialog();
            }
        });

        selectTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.selectTableDialog();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.showHelpDialog();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialogManager.showAboutDialog();
            }
        });

        // dodac itemy do tego file menu
        fileMenu.add(newUserItem);
        fileMenu.add(showUsersItem);
        fileMenu.add(editUserItem);
        fileMenu.add(delUserItem);
        fileMenu.add(selectTableItem);
        fileMenu.add(exitItem);
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    // pasek stanu
    private void restoreStatusForCurrentState() {
        statusBarManager.disableAutoReset();
        switch (currentState) {
            case READY:
                statusBarManager.setStatusPermanent("Gotowy");
                break;
            case NO_CONNECTION:
                statusBarManager.setStatusPermanent("Brak połączenia z bazą. Proszę wybrać tabelę z menu.");
                break;
        }
    }

    public void updateUiState(appState newState) {
        this.currentState = newState;

        boolean enabled = (newState == appState.READY);

        // przyciski
        if (newUserBtn != null)      newUserBtn.setEnabled(enabled);
        if (showUsersBtn != null)    showUsersBtn.setEnabled(enabled);
        if (editUserBtn != null)     editUserBtn.setEnabled(enabled);
        if (delUserBtn != null)      delUserBtn.setEnabled(enabled);

        // pozycje menu
        if (newUserItem != null)     newUserItem.setEnabled(enabled);
        if (showUsersItem != null)   showUsersItem.setEnabled(enabled);
        if (editUserItem != null)    editUserItem.setEnabled(enabled);
        if (delUserItem != null)     delUserItem.setEnabled(enabled);

        // Info nad tabelką
        if(infoPane !=null){
            switch (newState){
                case READY:
                    infoPane.setText("Połączono z bazą danych!" + "\nWybierz opcję z poniższych przycisków lub z menu.");
                    break;
                case NO_CONNECTION:
                    infoPane.setText("Brak wybranej tabeli.\n" + "Wybierz tabelę z menu 'Plik -> Wybierz tabele' lub użyj skrótu Ctrl+S");
                    break;
            }
        }
        restoreStatusForCurrentState();
    }

    private void refreshMainTable(String tableName) throws SQLException {
        if (tableName == null) {
            updateUiState(appState.NO_CONNECTION);
            return;
        }

        tableManager.refreshTable(tableName);
        updateUiState(appState.READY);
    }

    public void setStatusBar(String message) {
        statusBarManager.setStatus(message);
    }

    public TableManager getTableManager() {
        return tableManager;
    }

    public DialogManager getDialogManager() {
        return dialogManager;
    }

    public JTextPane getInfoPane() {
        return infoPane;
    }

    public StatusBarManager getStatusBarManager() {
        return statusBarManager;
    }

    public void setUserCRUD(UserCRUD userCRUD) {
        this.userCRUD = userCRUD;
    }

    public void setStatusBarManager(StatusBarManager statusBarManager) {
        this.statusBarManager = statusBarManager;
    }

    public void setTableManager(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void setDialogManager(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }
}