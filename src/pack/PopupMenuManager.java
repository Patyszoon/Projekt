package pack;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupMenuManager {
    private JPopupMenu popupMenu;
    private MainWindow mainWindow;
    private JTable table;
    private DialogManager dialogManager;

    // elementy menu kontekstowego
    private JMenuItem editItem;
    private JMenuItem delItem;
    private JMenuItem addItem;

    public PopupMenuManager(MainWindow mainWindow, DialogManager dialogManager) {
        this.mainWindow = mainWindow;
        this.dialogManager = dialogManager;
        popupMenu = new JPopupMenu();
        table = mainWindow.getTableManager().getTable();

        createPopupMenu();
    }

    public void createPopupMenu(){
        editItem = new JMenuItem("Edytuj rekord");
        delItem = new JMenuItem("Usuń rekod");
        addItem = new JMenuItem(("Dodaj rekord"));

        popupMenu.add(editItem);
        popupMenu.add(delItem);
        popupMenu.add(addItem);

        editItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = getSelectedUserId();
                if (id > 0) { dialogManager.showEditUserDialog(id); }

            }
        });

        delItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = getSelectedUserId();
                if (id > 0) { dialogManager.showDelUserDialog(id); }
            }
        });

        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogManager.showAddUserDialog();
            }
        });
    }

    private int getSelectedUserId(){
        int row = table.getSelectedRow();
        Object selectedId = (table.getModel().getValueAt(row, 0));
        if(selectedId instanceof Number){
            return ((Number) selectedId).intValue();
        }

        return -1;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
}
