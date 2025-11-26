package pack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MainWindow extends JFrame{

    private UserCRUD userCRUD;
    public MainWindow(UserCRUD userCRUD) {
        this.userCRUD = userCRUD;
        setupWindow();
        createMenu();
        setVisible(true); // pokaz okno

    }

    private void setupWindow() {
        setTitle("Tytul okna");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zamykaj, gdy x
        setLocationRelativeTo(null);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Plik");

        JMenuItem newUserItem = new JMenuItem("Nowy uzytkownik");
        JMenuItem showUsersItem = new JMenuItem("Pokaż uzytkowników");
        JMenuItem editUserItem = new JMenuItem("Edytuj uzytkownika");
        JMenuItem delUserItem = new JMenuItem("Usun uzytkownika");
        JMenuItem saveItem = new JMenuItem("Zapisz");
        JMenuItem exitItem = new JMenuItem("Wyjscie");

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

        setJMenuBar(menuBar);
    }

    private void showAddUserDialog() {
        // TODO: dodac okno dialogowe dodawania uzytkowanika

        JDialog dialog = new JDialog(this, "Dodaj użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD! Nie dodano użytkownika!", "ERROR",JOptionPane.ERROR_MESSAGE);
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
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
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
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
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
        // TODO: dodac wyswietlanie uzytkownikow
        JDialog dialog = new JDialog(this, "Pokaż użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

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