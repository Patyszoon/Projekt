package pack;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class DialogManager {

    private UserCRUD userCRUD;
    private StatusBarManager statusBarManager;
    private TableManager tableManager;
    private MainWindow parentFrame;  // odniesienie do glownego okna

    public DialogManager(MainWindow parentFrame, UserCRUD userCRUD, StatusBarManager statusBarManager, TableManager tableManager) {
        this.parentFrame = parentFrame;
        this.userCRUD = userCRUD;
        this.statusBarManager = statusBarManager;
        this.tableManager = tableManager;
    }

    void showHelpDialog(){
        JOptionPane.showMessageDialog(parentFrame,
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
        JOptionPane.showMessageDialog(parentFrame,
                "Aplikacja do zarządzania użytkownikami.\n" +
                        "Wersja 1.0\n" +
                        "Autorzy: Patrycja Woźniak, Wiktoria Kowalczuk",
                "O nas", JOptionPane.INFORMATION_MESSAGE);
    }

    String selectTableDialog(){
        JDialog dialog = new JDialog(parentFrame, "Wybierz tabele",true);
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
                    // odśwież główną tabelę
                    parentFrame.refreshMainTable(selectedTable[0]);

                    JOptionPane.showMessageDialog(parentFrame,
                            "Wybrano tabelę: " + selectedTable[0],
                            "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    setStatusBar("Wybrano tabelę: " + selectedTable[0]);
                } catch (SQLException ex) {
                    parentFrame.updateUiState(MainWindow.appState.NO_CONNECTION);
                    throw new RuntimeException(ex);
                }
                dialog.dispose();
            }
        });
        dialog.add(panel);
        dialog.setVisible(true);
        return selectedTable[0];
    }

    void showAddUserDialog() {
        JDialog dialog = new JDialog(parentFrame, "Dodaj użytkownika", true);
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
                String imie = imieField.getText().trim();
                String nazwisko = nazwiskoField.getText().trim();
                String nrTel = nrTelField.getText().trim();
                String dataUrodzenia = dataUrField.getText().trim();

                // WALIDACJA
                if (Validator.isEmpty(imie, nazwisko, nrTel, dataUrodzenia)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Wszystkie pola muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Validator.isValidName(imie)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Imię może zawierać tylko litery, spacje i myślniki!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    imieField.requestFocus();
                    return;
                }

                if (!Validator.isValidName(nazwisko)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Nazwisko może zawierać tylko litery, spacje i myślniki!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    nazwiskoField.requestFocus();
                    return;
                }

                if (!Validator.isValidPhone(nrTel)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Numer telefonu musi składać się z 9 cyfr!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    nrTelField.requestFocus();
                    return;
                }

                if (!Validator.isValidDate(dataUrodzenia)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Data musi być w formacie RRRR-MM-DD (np. 1990-01-15)!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    dataUrField.requestFocus();
                    return;
                }

                try {
                    userCRUD.insertUser(imie, nazwisko, nrTel, dataUrodzenia);
                    JOptionPane.showMessageDialog(parentFrame, "Dodano użytkownika do bazy!");
                    setStatusBar("Dodano użytkownika: " + imie + " " + nazwisko);
                    tableManager.refreshTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(parentFrame,"BŁĄD! Nie dodano użytkownika!", "ERROR",JOptionPane.ERROR_MESSAGE);
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
    void showEditUserDialog(int prefilledId) {
        String poleSql[] = {"imie", "nazwisko", "nr_tel", "data_ur"};
        JDialog dialog = new JDialog(parentFrame, "Edytuj użytkownika", true);
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

        if (prefilledId > 0) {
            idField.setText(String.valueOf(prefilledId));
            idField.setEditable(false); // edycja zablokowana
        }
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
                String id = idField.getText().trim();
                String pole = poleField.getSelectedItem().toString().trim();
                String zmiana = zmianaField.getText().trim();

                // WALIDACJA
                if (Validator.isEmpty(id, zmiana)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "ID i nowa wartość muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Validator.isPositiveInteger(id)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "ID musi być dodatnią liczbą całkowitą!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    idField.requestFocus();
                    return;
                }

                // Walidacja w zaleznosci od poal
                switch(pole) {
                    case "imie":
                    case "nazwisko":
                        if (!Validator.isValidName(zmiana)) {
                            JOptionPane.showMessageDialog(parentFrame,
                                    pole + " może zawierać tylko litery, spacje i myślniki!", "Błąd", JOptionPane.ERROR_MESSAGE);
                            zmianaField.requestFocus();
                            return;
                        }
                        break;

                    case "nr_tel":
                        if (!Validator.isValidPhone(zmiana)) {
                            JOptionPane.showMessageDialog(parentFrame,
                                    "Numer telefonu musi składać się z 9 cyfr!", "Błąd", JOptionPane.ERROR_MESSAGE);
                            zmianaField.requestFocus();
                            return;
                        }
                        break;

                    case "data_ur":
                        if (!Validator.isValidDate(zmiana)) {
                            JOptionPane.showMessageDialog(parentFrame,
                                    "Data musi być w formacie RRRR-MM-DD!", "Błąd", JOptionPane.ERROR_MESSAGE);
                            zmianaField.requestFocus();
                            return;
                        }
                        break;
                }

                try {
                    userCRUD.updateUser(Integer.parseInt(id), pole, zmiana);
                    JOptionPane.showMessageDialog(parentFrame, "Edytowano użytkownika o polu id: " + id);
                    setStatusBar("Edytowano użytkownika o polu id: " + id);
                    tableManager.refreshTable();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(parentFrame,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
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

    void showEditUserDialog() {
        showEditUserDialog(0);
    }
    void showDelUserDialog(int prefilledId) {
        JDialog dialog = new JDialog(parentFrame, "Usuń użytkownika", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(500, 400);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setStatusBar("Usuwanie użytkownika...");

        JPanel addPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // bez tego nie widac
        addPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JTextField idField = new JTextField();

        if (prefilledId > 0) {
            idField.setText(String.valueOf(prefilledId));
            idField.setEditable(false); // edycja zablokowana
        }

        addPanel.add(new JLabel("Id:"));
        addPanel.add(idField);

        JPanel buttonPanel = new JPanel();
        JButton delButton = new JButton("Usuń");
        JButton cancelButton = new JButton("Anuluj");
        buttonPanel.add(delButton);
        buttonPanel.add(cancelButton);

        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                int result = JOptionPane.showConfirmDialog(
                        parentFrame,
                        "Czy na pewno chcesz usunąć użytkownika o ID " + id + "?\n" +
                                "Tej akcji nie można cofnąć.",
                        "Potwierdź usunięcie",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // WALIDACJA
                if (Validator.isEmpty(id)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Musisz podać ID użytkownika!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Validator.isPositiveInteger(id)) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "ID musi być dodatnią liczbą całkowitą!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    idField.requestFocus();
                    return;
                }

                // SPRAWDZ, czy wybrano tak
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        userCRUD.deleteUser(Integer.parseInt(id));
                        JOptionPane.showMessageDialog(parentFrame, "Usunięto użytkownika o polu id: " + id);
                        setStatusBar("Usunięto użytkownika o polu id: " + id);
                        tableManager.refreshTable();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(parentFrame,"BŁĄD!!!", "ERROR",JOptionPane.ERROR_MESSAGE);
                        setStatusBar("BŁĄD przy usuwaniu użytkownika o polu id: " + id);
                        throw new RuntimeException(ex);
                    }
                }else {
                    // wybrano nie
                    setStatusBar("Anulowano usuwanie użytkownika o ID: " + id);
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
    void showDelUserDialog(){
        showDelUserDialog(0);
    }
    void showUsersDialog() throws SQLException {
        JDialog dialog = new JDialog(parentFrame, "Pokaż użytkownika", false);
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

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        sorter.addRowSorterListener(e -> {
            if (e.getType() != javax.swing.event.RowSorterEvent.Type.SORT_ORDER_CHANGED) {return;}

            java.util.List<? extends javax.swing.RowSorter.SortKey> sortKeys = sorter.getSortKeys();
            if (sortKeys == null || sortKeys.isEmpty()) {
                setStatusBar("Sortowanie wyłączone");
                return;
            }

            javax.swing.RowSorter.SortKey key = sortKeys.get(0);
            int columnIndex = key.getColumn();
            javax.swing.SortOrder order = key.getSortOrder();

            String columnName = table.getColumnName(columnIndex);
            String direction;
            if (order == javax.swing.SortOrder.ASCENDING) {direction = "rosnąco";}
            else if (order == javax.swing.SortOrder.DESCENDING) {direction = "malejąco";}
            else {
                setStatusBar("Sortowanie wyłączone");
                return;
            }
            setStatusBar("Sortowanie po kolumnie: \"" + columnName + "\" (" + direction + ")");
        });

        JScrollPane scrollPane = new JScrollPane(table);
        dialog.getContentPane().add(scrollPane);
        dialog.pack();
        dialog.setVisible(true);
    }

    public void setStatusBar(String message) {
        statusBarManager.setStatus(message);
    }
}
