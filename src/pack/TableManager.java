package pack;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.sql.SQLException;
import java.util.List;
import java.util.Comparator;

public class TableManager {
    private final JTable table;
    private final UserCRUD userCRUD;
    private String currentTableName;

    public TableManager(JTable table, UserCRUD userCRUD) {
        this.table = table;
        this.userCRUD = userCRUD;
        configureTable();
    }

    private void configureTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public JTable getTable() {
        return table;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String tableName) {
        this.currentTableName = tableName;
    }

    public void refreshTable(String tableName) throws SQLException {
        setCurrentTableName(tableName);
        internalRefreshTable();
    }

    public void refreshTable() throws SQLException {
        if (currentTableName == null) {
            throw new SQLException("Nie wybrano tabeli");
        }
        internalRefreshTable();
    }

    private void internalRefreshTable() throws SQLException{
        if(currentTableName == null){
            clearTable();
            return;
        }

        List<Object[]> rows;
        String[] columnNames;

        switch(currentTableName) {
            case "uzytkownik":
                rows = userCRUD.getUsers();
                columnNames = new String[]{"ID", "Imię", "Nazwisko", "Adres", "Miejscowość", "Nr_tel",
                        "Email", "Nr_dowodu", "Data_ur", "Czy_zweryfikowany"};
                break;
            // case inna tabela:
                // rows = ...
                // columnNames = ...
                // break;
            default:
                clearTable();
                return;
        }

        Object[][] tableData = new Object[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            tableData[i] = rows.get(i);
        }

        DefaultTableModel model = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        table.setModel(model);
        configureTableSorter(model);

        // opcjonalnie: włączenie wizualnego sortowania nagłówka pozostaje domyślne
    }

    private void configureTableSorter(DefaultTableModel model) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        // TableRowSorter z komparatorem dla kolumny ID
        Comparator<Object> idComparator = (o1, o2) -> {
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

    }

    public void clearTable() {
        table.setModel(new DefaultTableModel());
    }

    public boolean isTableSelected() {
        return currentTableName != null && !currentTableName.isEmpty();
    }

}
