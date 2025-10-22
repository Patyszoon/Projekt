package pack;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.Scanner;


public class Main {
    private static Connection conn;



    public static void insertUser(Connection conn, Scanner sc) throws SQLException {
        Statement stmt = conn.createStatement();
//    Scanner sc = new Scanner(System.in);

        System.out.print("Podaj imie: ");
        String imie = sc.nextLine();
        System.out.print("Podaj nazwisko: ");
        String nazwisko = sc.nextLine();
        System.out.print("Podaj nr telefonu: ");
        String nr_tel = sc.nextLine();
        System.out.print("Podaj date urodzenia (rrrr-mm-dd):  ");
        String data_ur = sc.nextLine();

        String sql = "INSERT INTO uzytkownik (imie, nazwisko, nr_tel, data_ur) VALUES (?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, imie);
        pstmt.setString(2, nazwisko);
        pstmt.setString(3, nr_tel);
        pstmt.setString(4, data_ur);
        pstmt.executeUpdate();
//    pstmt.close();
    }
    public static void updateUser(Connection conn, Scanner sc) throws SQLException {
//    Scanner sc = new Scanner(System.in);
        System.out.print("Podaj id uzytkownika do edycji: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Podaj pole do zmiany (imie/nazwisko/nr_tel/data_ur): ");
        String pole = sc.nextLine();
        System.out.print("Podaj nowa wartosc: ");
        String wartosc = sc.nextLine();
        String sql = "UPDATE uzytkownik SET " + pole + " = ? WHERE id = ?"; // w prepared statement nie mozna wstawiwac nazw kolumn jako parametrow ?
        PreparedStatement pstmt = conn.prepareStatement(sql);

        if(pole.equals("data_ur")){
            pstmt.setDate(1,Date.valueOf(wartosc)); // java.sql.Date to klasa reprezentujaca date w jdbc, valueOf konwersja
        }else if(pole.equals("nr_tel")){
            pstmt.setInt(2,Integer.parseInt(wartosc));
        }else{
            pstmt.setString(1, wartosc);
        }
        pstmt.setInt(2, id);
        pstmt.executeUpdate();

    }
    public static void deleteUser(Connection conn, Scanner sc) throws SQLException {
//    Scanner sc = new Scanner(System.in);
        System.out.print("Podaj id uzytkownika do usuniecia: ");
        int id = Integer.parseInt(sc.nextLine());
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    public static void showUsers(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM uzytkownik");
        while (rs.next()) {
            int id = rs.getInt("id");
            String imie = rs.getString("imie");
            String nazwisko = rs.getString("nazwisko");
            BigDecimal nr_tel = rs.getBigDecimal("nr_tel");
            Date data_ur = rs.getDate("data_ur");
            System.out.println(id + " " + imie + " " + nazwisko + " " + nr_tel + " " + data_ur);
        }
    }

    public static void main(String[] arg) throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);

        Statement stmt = conn.createStatement();
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
                    insertUser(conn, sc);
                    break;
                case 2:
                    updateUser(conn, sc);
                    break;
                case 3:
                    deleteUser(conn, sc);
                    break;
                case 4:
                    showUsers(conn);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("brak takiego dzialania");
                    break;
            }
        }

//   String sql = "CREATE TABLE uzytkownik ("
//            + "id INT PRIMARY KEY AUTO_INCREMENT, "
//            + "imie VARCHAR(20) NOT NULL, "
//            + "nazwisko VARCHAR(30) NOT NULL, "
//            + "nr_tel INT(9) NOT NULL, "
//            + "data_ur DATE NOT NULL)";
//    stmt.executeUpdate(sql);

//        stmt.executeUpdate(
//                "INSERT INTO uzytkownik (imie, nazwisko, nr_tel, data_ur) VALUES " +
//                        "('Jan', 'Kowalski', '600123456', '1990-05-14'), " +
//                        "('Anna', 'Nowak', '501987654', '1985-11-02'), " +
//                        "('Piotr', 'Wisniewski', '733555111', '1993-03-28'), " +
//                        "('Tomasz', 'Lewandowski', '690777666', '1998-09-05'), " +
//                        "('Joanna', 'Kozlowska', '698888111', '2000-04-09')"
//        );

    }
}