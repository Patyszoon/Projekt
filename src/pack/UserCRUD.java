package pack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCRUD {
    private Connection conn;

    public UserCRUD(Connection conn) {
        this.conn = conn;
    }

    public void insertUser(String imie, String nazwisko, String adres, String miejscowosc, String nr_tel, String email,
                           String nr_dowodu, String data_ur, String czy_zweryfikowany) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "INSERT INTO uzytkownik (imie, nazwisko, adres, miejscowosc, nr_tel, email, nr_dowodu, " +
                "data_ur, czy_zweryfikowany) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, imie);
        pstmt.setString(2, nazwisko);
        pstmt.setString(3, adres);
        pstmt.setString(4, miejscowosc);
        pstmt.setInt(5, Integer.parseInt(nr_tel));
        pstmt.setString(6, email);
        pstmt.setString(7, nr_dowodu);
        pstmt.setDate(8, Date.valueOf(data_ur));
        pstmt.setString(9, czy_zweryfikowany);
        pstmt.executeUpdate();

        pstmt.close();
    }
    public void updateUser(int id, String pole, String wartosc) throws SQLException {


        String sql = "UPDATE uzytkownik SET " + pole + " = ? WHERE id = ?"; // w prepared statement nie mozna wstawiwac nazw kolumn jako parametrow ?
        PreparedStatement pstmt = conn.prepareStatement(sql);

        switch(pole) {
            case "data_ur":
                pstmt.setDate(1, Date.valueOf(wartosc));
                break;
            case "nr_tel":
                pstmt.setInt(1, Integer.parseInt(wartosc));
                break;
            case "czy_zweryfikowany":
                pstmt.setString(1, wartosc);
                break;
            default:
                pstmt.setString(1, wartosc);
                break;
        }
        pstmt.setInt(2, id);
        pstmt.executeUpdate();

    }
    public void deleteUser(int id) throws SQLException {

        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    // do konsoli
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

    // do gui
    public List<Object[]> getUsers() throws SQLException {
        List<Object[]> data = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM uzytkownik");

        while (rs.next()) {
            data.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("imie"),
                    rs.getString("nazwisko"),
                    rs.getString("adres"),
                    rs.getString("miejscowosc"),
                    rs.getInt("nr_tel"),
                    rs.getString("email"),
                    rs.getString("nr_dowodu"),
                    rs.getDate("data_ur"),
                    rs.getString("czy_zweryfikowany")
            });
        }
        return data;
    }

}