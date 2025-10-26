package pack;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private Scanner sc;
    private UserCRUD userCRUD;

    public Menu(Scanner sc, UserCRUD userCRUD) {
        this.sc = sc;
        this.userCRUD = userCRUD;
    }

    public void showMenu() throws SQLException {
        // MENU
        boolean running = true;
        while (running) {
            System.out.println("-----MENU-----\n" +
                    "Co chcesz zrobic?\n" +
                    "1. Dodaj uzytkownika\n" +
                    "2. Edytuj uzytkownika\n" +
                    "3. Usun uzytkownika\n" +
                    "4. Wyswietl uzytkownikow\n" +
                    "0. Wyjscie\n");

            int wybor = Integer.parseInt(sc.nextLine());

            switch (wybor) {
                case 1:
                    userCRUD.insertUser(sc);
                    break;
                case 2:
                    userCRUD.updateUser(sc);
                    break;
                case 3:
                    userCRUD.deleteUser(sc);
                    break;
                case 4:
                    userCRUD.showUsers();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("brak takiego dzialania");
                    break;
            }
        }
    }

    public void close() {
        sc.close();
    }
}