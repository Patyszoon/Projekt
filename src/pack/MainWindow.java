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
    // TODO: zmiana statusu aplikacji (NO_CONNECTION, READY itp) i dostosowanie UI do tego
    //  (jak jest NO_CONNECTION to po ruszaniu kursorem żeby nie znieniał się na "Gotowy")

    private TableManager tableManager;
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
        new Shortcuts(this);
    }


    private void setupWindow() throws SQLException{ //rzeczy w srdoku okna
        setTitle("Aplikacja do zarządzania bazą danych"); // tytuł okna
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zamykaj, gdy x
        setLocationRelativeTo(null);

        // status bar
        statusBarManager = new StatusBarManager();
        add(statusBarManager.getStatusBar(), BorderLayout.SOUTH);

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

        // inicjalizacja TableManager
        JTable mainTable = new JTable(); // zmienna lokalna
        tableManager = new TableManager(mainTable, userCRUD);

        JScrollPane tableScroll = new JScrollPane(tableManager.getTable());
        tableScroll.setPreferredSize(new Dimension(700, 300));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(tableScroll);

        newUserBtn = new JButton("Nowy użytkownik");
        showUsersBtn = new JButton("Pokaż użytkowników");
        editUserBtn = new JButton("Edytuj użytkownika");
        delUserBtn = new JButton("Usuń użytkownika");

        newUserBtn.addActionListener(e -> showAddUserDialog());
        showUsersBtn.addActionListener(e -> {
            try {
                showUsersDialog();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        editUserBtn.addActionListener(e -> showEditUserDialog());
        delUserBtn.addActionListener(e -> showDelUserDialog());

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
                statusBarManager.setStatus("Gotowy");
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
                statusBarManager.setStatusPermanent("Gotowy");
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
                showAddUserDialog();
            }
        });

        showUsersItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    showUsersDialog();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        editUserItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEditUserDialog();
            }
        });

        delUserItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDelUserDialog();
            }
        });

        selectTableItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectTableDialog();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelpDialog();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
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

    private void updateUiState(appState newState) {
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
        // pasek stanu
        switch (newState) {
            case READY:
                statusBarManager.setStatusPermanent("Gotowy");
                break;
            case NO_CONNECTION:
                statusBarManager.setStatusPermanent("Brak połączenia z bazą. Proszę wybrać tabelę z menu.");
                break;
        }
    }

    private void refreshMainTable(String tableName) throws SQLException {
        if (tableName == null) {
            updateUiState(appState.NO_CONNECTION);
            return;
        }

        tableManager.refreshTable(tableName);
        updateUiState(appState.READY);
    }

    void showHelpDialog(){
        JOptionPane.showMessageDialog(MainWindow.this,
                "Skróty klawiszowe:\n" +
                        "Ctrl+N - Nowy użytkownik\n" +
                        "Ctrl+P - Pokaż użytkowników\n" +
                        "Ctrl+E - Edytuj użytkownika\n" +
                        "Ctrl+D - Usuń użytkownika\n" +
                        "Ctrl+S - Wybierz tabele\n" +
                        "Ctrl+Q - Wyjście\n" +
                        "F1 - Pomoc\n" +
                        "F2 - O nas\n" +
                        "F5 - Odśwież tabelę",
                "Pomoc", JOptionPane.INFORMATION_MESSAGE);
    }

    void showAboutDialog(){
        JOptionPane.showMessageDialog(MainWindow.this,
                "Aplikacja do zarządzania użytkownikami.\n" +
                        "Wersja 1.0\n" +
                        "Autorzy: Patrycja Woźniak, Wiktoria Kowalczuk",
                "O nas", JOptionPane.INFORMATION_MESSAGE);
    }

    String selectTableDialog(){
        JDialog dialog = new JDialog(this, "Wybierz tabele",true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(300,200);
        dialog.setResizable(false);

        setStatusBar("Wybieranie tabeli...");

        JPanel panel = new JPanel(new BorderLayout());
        String[] tables = {"uzytkownik"}; // mozna dodac wiecej

        JComboBox<String> tableComboBox = new JComboBox<>(tables);
        panel.add(tableComboBox, BorderLayout.CENTER);

        JButton selectButton = new JButton("Wybierz");
        panel.add(selectButton, BorderLayout.SOUTH);

        final String[] selectedTable = {null};

        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedTable[0] = (String) tableComboBox.getSelectedItem();
                try {
                    tableManager.refreshTable(selectedTable[0]);
                    updateUiState(appState.READY);
                } catch (SQLException ex) {
                    updateUiState(appState.NO_CONNECTION);
                    throw new RuntimeException(ex);
                }

                JOptionPane.showMessageDialog(MainWindow.this,
                        "Wybrano tabelę: " + selectedTable[0],
                        "Informacja", JOptionPane.INFORMATION_MESSAGE);
                setStatusBar("Wybrano tabelę: " + selectedTable[0]);

                dialog.dispose();
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
        return selectedTable[0];
    }

    void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Dodaj użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setStatusBar("Dodawanie użytkownika...");

        JPanel addPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // bez tego nie widac
        addPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField imieField = new JTextField();
        JTextField nazwiskoField = new JTextField();
        JTextField nrTelField = new JTextField();
        JTextField dataUrField = new JTextField();

        addPanel.add(new JLabel("Imię:"));
        addPanel.add(imieField);
        addPanel.add(new JLabel("Nazwisko:"));
        addPanel.add(nazwiskoField);
        addPanel.add(new JLabel("Nr telefonu:"));
        addPanel.add(nrTelField);
        addPanel.add(new JLabel("Data urodzenia:"));
        addPanel.add(dataUrField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Zapisz");
        JButton cancelButton = new JButton("Anuluj");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String imie = imieField.getText();
                String nazwisko = nazwiskoField.getText();
                String nrTel = nrTelField.getText();
                String dataUrodzenia = dataUrField.getText();

                // TODO: dodac sprawdzanie poprawnosci pol + refresh tabeli
                try {
                    userCRUD.insertUser(imie, nazwisko, nrTel, dataUrodzenia);
                    JOptionPane.showMessageDialog(MainWindow.this, "Dodano użytkownika do bazy!");
                    setStatusBar("Dodano użytkownika: " + imie + " " + nazwisko);
                    tableManager.refreshTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD! Nie dodano użytkownika!", "ERROR",JOptionPane.ERROR_MESSAGE);
                    setStatusBar("BŁĄD przy dodawaniu użytkownika!");
                    throw new RuntimeException(ex);
                }
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(addPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    void showEditUserDialog() {
        String poleSql[] = {"imie", "nazwisko", "nr_tel", "data_ur"};
        JDialog dialog = new JDialog(this, "Edytuj użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setStatusBar("Edycja użytkownika...");

        JPanel addPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // bez tego nie widac
        addPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField idField = new JTextField();
        JComboBox poleField = new JComboBox(poleSql);
        JTextField zmianaField = new JTextField();

        addPanel.add(new JLabel("Id:"));
        addPanel.add(idField);
        addPanel.add(new JLabel("Pole:"));
        addPanel.add(poleField);
        addPanel.add(new JLabel("Zmiana:"));
        addPanel.add(zmianaField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Zapisz");
        JButton cancelButton = new JButton("Anuluj");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String pole = poleField.getSelectedItem().toString();
                String zmiana = zmianaField.getText();

                // TODO: dodac sprawdzanie poprawnosci pol + refresh tabeli
                try {
                    userCRUD.updateUser(Integer.parseInt(id), pole, zmiana);
                    JOptionPane.showMessageDialog(MainWindow.this, "Edytowano użytkownika o polu id: " + id);
                    setStatusBar("Edytowano użytkownika o polu id: " + id);
                    tableManager.refreshTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
                    setStatusBar("BŁĄD przy edycji użytkownika o polu id: " + id);
                    throw new RuntimeException(ex);
                }
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(addPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    void showDelUserDialog() {
        JDialog dialog = new JDialog(this, "Usuń użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setStatusBar("Usuwanie użytkownika...");

        JPanel addPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // bez tego nie widac
        addPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField idField = new JTextField();

        addPanel.add(new JLabel("Id:"));
        addPanel.add(idField);

        JPanel buttonPanel = new JPanel();
        JButton delButton = new JButton("Usuń");
        JButton cancelButton = new JButton("Anuluj");
        buttonPanel.add(delButton);
        buttonPanel.add(cancelButton);

        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                JOptionPane.showConfirmDialog( MainWindow.this,
                        "Czy napewno chcesz usunąć użytkownika?.\n"
                                + "Tej akcji nie można cofnąć.", "Potwierdź",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                // TODO: dodac sprawdzanie poprawnosci pol + refresh tabeli
                try {
                    userCRUD.deleteUser(Integer.parseInt(id));
                    JOptionPane.showMessageDialog(MainWindow.this, "Usunięto użytkownika o polu id: " + id);
                    setStatusBar("Usunięto użytkownika o polu id: " + id);
                    tableManager.refreshTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
                    setStatusBar("BŁĄD przy usuwaniu użytkownika o polu id: " + id);
                    throw new RuntimeException(ex);
                }
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(addPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    void showUsersDialog() throws SQLException {
        // TODO: zmiana pasku stanu przy zaznaczeniu filtru w tabeli (filtr uaktywnia sie po kliknieciu w nazwe kolumny)

        JDialog dialog = new JDialog(this, "Pokaż użytkownika", false);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setStatusBar("Wyświetlanie użytkowników...");

        List<Object[]> users = userCRUD.getUsers();
        String[] columnNames = {"ID", "Imię", "Nazwisko", "Nr_tel", "Data_ur"};

        Object[][] tableData = new Object[users.size()][];
        for (int i = 0; i < users.size(); i++) {
            tableData[i] = users.get(i);
        };

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //brak możliwości edycji komórek
            }
        };

        JTable table = new JTable(tableData, columnNames);
        table.setEditingColumn(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setModel(model);

        javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);

        java.util.Comparator<Object> idComparator = (o1, o2) -> {
            try {
                if (o1 instanceof Number && o2 instanceof Number) {
                    return Integer.compare(((Number)o1).intValue(), ((Number)o2).intValue());
                }
                int i1 = Integer.parseInt(o1.toString());
                int i2 = Integer.parseInt(o2.toString());
                return Integer.compare(i1, i2);
            } catch (Exception ex) {
                return o1.toString().compareTo(o2.toString());
            }
        };

        sorter.setComparator(0, idComparator);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setVisible(true);
    }

    public void setStatusBar(String message) {
        statusBarManager.setStatus(message);
    }
}