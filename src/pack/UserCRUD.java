package pack;

import java.sql.*;

public class UserCRUD {
    private Connection conn;

    public UserCRUD(Connection conn) {
        this.conn = conn;
    }

    public void insertUser(String imie, String nazwisko, String nr_tel, String data_ur) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "INSERT INTO uzytkownik (imie, nazwisko, nr_tel, data_ur) VALUES (?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, imie);
        pstmt.setString(2, nazwisko);
        pstmt.setString(3, nr_tel);
        pstmt.setString(4, data_ur);
        pstmt.setDate(4, java.sql.Date.valueOf(data_ur));
        pstmt.executeUpdate();

        pstmt.close();
    }
    public void updateUser(int id, String pole, String wartosc) throws SQLException {


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
    public void deleteUser(int id) throws SQLException {

        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
    public void showUsers() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM uzytkownik");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("| %-5s |  %-10s |  %-20s |  %-10s | %-10s | \n", "id", "imie", "nazwisko", "nr_tel", "data_ur");
        System.out.println("--------------------------------------------------------------------------");
        while (rs.next()) {
            int id = rs.getInt("id");
            String imie = rs.getString("imie");
            String nazwisko = rs.getString("nazwisko");
            int nr_tel = rs.getInt("nr_tel");
            Date data_ur = rs.getDate("data_ur");
            System.out.printf("| %-5s |  %-10s |  %-20s |  %-10s | %-10s | \n", id, imie, nazwisko, nr_tel, data_ur);
        }
        System.out.println("--------------------------------------------------------------------------");
    }

}