package pack;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;


public class Main {
    public static void main(String[] arg) {
        DbConnect dbConnect = new DbConnect();
        try {
            Scanner sc = new Scanner(System.in);
            Connection conn = dbConnect.getConnection();
            UserCRUD userCRUD = new UserCRUD(conn);
            Menu menu = new Menu(sc, userCRUD);

            menu.showMenu();
            menu.close();
        } catch (SQLException e) {
            System.out.println("BLAD BAZY DANYCH" + e.getMessage());
        } catch (IOException e) {
            System.out.println("BLAD I/O" + e.getMessage());
        } finally {
            try {
                dbConnect.closeConnection();
            } catch (SQLException e) {
                System.out.println("BLAD BAZY DANYCH" + e.getMessage());
            }
        }

    }
}