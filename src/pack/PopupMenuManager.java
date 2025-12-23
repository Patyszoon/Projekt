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
    private JMenuItem editUserItem;
    private JMenuItem delUserItem;
    private JMenuItem copyIdItem;

    public PopupMenuManager(MainWindow mainWindow, DialogManager dialogManager) {
        this.mainWindow = mainWindow;
        this.dialogManager = dialogManager;
        popupMenu = new JPopupMenu();
        table = mainWindow.getTableManager().getTable();

        createPopupMenu();
    }

    public void createPopupMenu(){
        editUserItem = new JMenuItem("Edytuj");
        delUserItem = new JMenuItem("Usuń");
        //copyIdItem = new JMenuItem("Kopiuj ID");

        popupMenu.add(editUserItem);
        popupMenu.add(delUserItem);
        //popupMenu.addSeparator();
        //popupMenu.add(copyIdItem);

        editUserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = getSelectedUserId();
                if (id > 0) { dialogManager.showEditUserDialog(id); }

            }
        });

        delUserItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = getSelectedUserId();
                if (id > 0) { dialogManager.showDelUserDialog(id); }
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
