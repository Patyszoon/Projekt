import java.sql.*;

public void insertUser(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();
    Scanner sc = new Scanner(System.in);

    System.out.print("Podaj imie: ");
    String imie = sc.nextLine();
    System.out.print("Podaj nazwisko: ");
    String nazwisko = sc.nextLine();
    System.out.print("Podaj nr telefonu: ");
    String nr_tel = sc.nextLine();
    System.out.print("Podaj date urodzenia (rdrr-mm-dd):  ");
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
public void updateUser(Connection conn) throws SQLException {
    Scanner sc = new Scanner(System.in);
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
public void deleteUser(Connection conn) throws SQLException {
    Scanner sc = new Scanner(System.in);
    System.out.print("Podaj id uzytkownika do usuniecia: ");
    int id = Integer.parseInt(sc.nextLine());
    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
    pstmt.setInt(1, id);
    pstmt.executeUpdate();
}
public void showUsers(Connection conn) throws SQLException {
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

public void main() throws SQLException, IOException {
//    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:55555/samochody", "root", "");
    File configFile = new File("config.properties");
        if (!configFile.exists()) {
            Properties defaultProps = new Properties();
            defaultProps.setProperty("db.host", "localhost");
            defaultProps.setProperty("db.port","55555");
            defaultProps.setProperty("db.name","samochody");
            defaultProps.setProperty("db.user","root");
            defaultProps.setProperty("db.password", "");
            defaultProps.store(new FileOutputStream(configFile), "Domyslne ustawienia bazy danych");
        }

    Properties props = new Properties();
    props.load(new FileInputStream("config.properties"));
    String host = props.getProperty("db.host");
    String port = props.getProperty("db.port");
    String dbName = props.getProperty("db.name");
    String user = props.getProperty("db.user");
    String password = props.getProperty("db.password");

    // budowa connection string dynamicznie
    String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    // nawiazanie polaczenia
    Connection conn = DriverManager.getConnection(url, user, password);
//
//    Statement stmt = conn.createStatement();
////   String sql = "CREATE TABLE uzytkownik ("
////            + "id INT PRIMARY KEY AUTO_INCREMENT, "
////            + "imie VARCHAR(20) NOT NULL, "
////            + "nazwisko VARCHAR(30) NOT NULL, "
////            + "nr_tel INT(9) NOT NULL, "
////            + "data_ur DATE NOT NULL)";
////    stmt.executeUpdate(sql);
//
//    stmt.executeUpdate(
//            "INSERT INTO uzytkownik (imie, nazwisko, nr_tel, data_ur) VALUES " +
//                    "('Jan', 'Kowalski', '600123456', '1990-05-14'), " +
//                    "('Anna', 'Nowak', '501987654', '1985-11-02'), " +
//                    "('Piotr', 'Wisniewski', '733555111', '1993-03-28'), " +
//                    "('Tomasz', 'Lewandowski', '690777666', '1998-09-05'), " +
//                    "('Joanna', 'Kozlowska', '698888111', '2000-04-09')"
//            );
//
//    ResultSet rs = stmt.executeQuery(
//            "SELECT id, imie,nazwisko, nr_tel, data_ur FROM uzytkownik");
//    while (rs.next()) {
//        int id = rs.getInt("id");
//        String imie = rs.getString("imie");
//        String nazwisko = rs.getString("nazwisko");
//        BigDecimal nr_tel = rs.getBigDecimal("nr_tel");
//        Date data_ur = rs.getDate("data_ur");
//
//        System.out.println(id + " " + imie + " " + nazwisko + " " + nr_tel + " " + data_ur);
//    }
//    // UPDATE
//    PreparedStatement upd = conn.prepareStatement(
//            "UPDATE uzytkownik SET imie = ? WHERE id = ?");
//    upd.setString(1, new String("Maciej"));
//    upd.setInt(2, 1);
//    upd.executeUpdate();
//
//    //DELETE
//    PreparedStatement del = conn.prepareStatement("DELETE FROM uzytkownik WHERE id = ?");
//    del.setInt(1, 1);
//    del.executeUpdate();

//    MENU
}