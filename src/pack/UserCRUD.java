package pack;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class UserCRUD {
    private Connection conn;

    public UserCRUD(Connection conn) {
        this.conn = conn;
    }

    public void insertUser(Scanner sc) throws SQLException {
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
        //pstmt.setString(4, data_ur);
        pstmt.setDate(4, java.sql.Date.valueOf(data_ur));
        pstmt.executeUpdate();
//    pstmt.close();
    }
    public void updateUser(Scanner sc) throws SQLException {
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
            pstmt.setDate(1, Date.valueOf(wartosc)); // java.sql.Date to klasa reprezentujaca date w jdbc, valueOf konwersja
        }else if(pole.equals("nr_tel")){
            pstmt.setInt(1,Integer.parseInt(wartosc));
        }else{
            pstmt.setString(1, wartosc);
        }
        pstmt.setInt(2, id);
        pstmt.executeUpdate();

    }
    public void deleteUser(Scanner sc) throws SQLException {
//    Scanner sc = new Scanner(System.in);
        System.out.print("Podaj id uzytkownika do usuniecia: ");
        int id = Integer.parseInt(sc.nextLine());
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    public void showUsers() throws SQLException {
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

}