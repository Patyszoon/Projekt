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
                    System.out.print("Podaj imie: ");
                    String imie = sc.nextLine();
                    System.out.print("Podaj nazwisko: ");
                    String nazwisko = sc.nextLine();
                    System.out.print("Podaj nr telefonu: ");
                    String nr_tel = sc.nextLine();
                    System.out.print("Podaj date urodzenia (rrrr-mm-dd):  ");
                    String data_ur = sc.nextLine();
                    userCRUD.insertUser(imie, nazwisko, nr_tel, data_ur);
                    break;
                case 2:
                    System.out.print("Podaj id uzytkownika do edycji: ");
                    int id = Integer.parseInt(sc.nextLine());
                    System.out.print("Podaj pole do zmiany (imie/nazwisko/nr_tel/data_ur): ");
                    String pole = sc.nextLine();
                    System.out.print("Podaj nowa wartosc: ");
                    String wartosc = sc.nextLine();
                    userCRUD.updateUser(id, pole, wartosc);
                    break;
                case 3:

                    System.out.print("Podaj id uzytkownika do usuniecia: ");
                    int idDel = Integer.parseInt(sc.nextLine());
                    userCRUD.deleteUser(idDel);
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