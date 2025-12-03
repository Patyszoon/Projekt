package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainWindow extends JFrame{

    private UserCRUD userCRUD;
    private final JLabel statusBar = new JLabel("Gotowy");

    public MainWindow(UserCRUD userCRUD) {
        this.userCRUD = userCRUD;
        setupWindow();
        createMenu();
        setVisible(true); // pokaz okno
    }

    private void setupWindow() { //rzeczy w srdoku okna
        setTitle("Tytul okna");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zamykaj, gdy x
        setLocationRelativeTo(null);

        //pasek stanu na dole okna
       statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       statusBar.setOpaque(true);
       statusBar.setBackground(Color.PINK);
       add(statusBar, BorderLayout.SOUTH);


    }

    public void setStatusBar(String message) {
        statusBar.setText(message);
        Timer timer = new Timer(10000, e ->{
                statusBar.setText("Gotowy");
            });
        timer.setRepeats(false);
        timer.start();
    }

    private void createMenu() { //pasek menu u gory
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Plik");
        JMenu help = new JMenu("Pomoc");
        JMenu about = new JMenu("O nas");

        JMenuItem newUserItem = new JMenuItem("Nowy uzytkownik");
        JMenuItem showUsersItem = new JMenuItem("Pokaż uzytkowników");
        JMenuItem editUserItem = new JMenuItem("Edytuj uzytkownika");
        JMenuItem delUserItem = new JMenuItem("Usun uzytkownika");
        JMenuItem saveItem = new JMenuItem("Zapisz");
        JMenuItem exitItem = new JMenuItem("Wyjscie");

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Object src = e.getSource();
                if (src == menuBar)          setStatusBar("Menu");
                else if (src == fileMenu)    setStatusBar("Pliki");
                else if (src == help)        setStatusBar("Help");
                else if (src == about)       setStatusBar("About");
                else if (src == newUserItem) setStatusBar("Nowy uzytkownik");
                else if (src == showUsersItem) setStatusBar("Pokaż uzytkowników");
                else if (src == editUserItem)  setStatusBar("Edytuj uzytkownika");
                else if (src == delUserItem)   setStatusBar("Usun uzytkownika");
                else if (src == saveItem)      setStatusBar("Zapisz");
                else if (src == exitItem)      setStatusBar("Wyjscie");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                statusBar.setText("Gotowy");
            }
        };

        menuBar.addMouseListener(mouseAdapter);
        fileMenu.addMouseListener(mouseAdapter);
        help.addMouseListener(mouseAdapter);
        about.addMouseListener(mouseAdapter);
        newUserItem.addMouseListener(mouseAdapter);
        showUsersItem.addMouseListener(mouseAdapter);
        editUserItem.addMouseListener(mouseAdapter);
        delUserItem.addMouseListener(mouseAdapter);
        saveItem.addMouseListener(mouseAdapter);
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

        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this, "Zapisywanie...");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // dodac itemy do tego file menu
        fileMenu.add(newUserItem);
        fileMenu.add(showUsersItem);
        fileMenu.add(editUserItem);
        fileMenu.add(delUserItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        menuBar.add(help);
        menuBar.add(about);

        setJMenuBar(menuBar);
    }

    private void showAddUserDialog() {
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

                // TODO: dodac sprawdzanie poprawnosci pol
                try {
                    userCRUD.insertUser(imie, nazwisko, nrTel, dataUrodzenia);
                    JOptionPane.showMessageDialog(MainWindow.this, "Dodano użytkownika do bazy!");
                    setStatusBar("Dodano użytkownika: " + imie + " " + nazwisko);
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
    private void showEditUserDialog() {
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

                // TODO: dodac sprawdzanie poprawnosci pol
                try {
                    userCRUD.updateUser(Integer.parseInt(id), pole, zmiana);
                    JOptionPane.showMessageDialog(MainWindow.this, "Edytowano użytkownika o polu id: " + id);
                    setStatusBar("Edytowano użytkownika o polu id: " + id);
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
    private void showDelUserDialog() {
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
                // TODO: dodac sprawdzanie poprawnosci pol
                try {
                    userCRUD.deleteUser(Integer.parseInt(id));
                    JOptionPane.showMessageDialog(MainWindow.this, "Usunięto użytkownika o polu id: " + id);
                    setStatusBar("Usunięto użytkownika o polu id: " + id);
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
    private void showUsersDialog() throws SQLException {
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
        }

        JTable table = new JTable(tableData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.pack();

        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setVisible(true);
    }
}
