package pack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbConnect {
    private Connection conn;

//    public DbConnect() throws SQLException, IOException {
//
//    }

    public Connection getConnection() throws SQLException, IOException {
        File configFile = new File("config.properties");
        if (!configFile.exists()) {
            Properties defaultProps = new Properties();
            defaultProps.setProperty("db.host", "localhost");
            defaultProps.setProperty("db.port", "55555");
            defaultProps.setProperty("db.name", "samochody");
            defaultProps.setProperty("db.user", "root");
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
        conn = DriverManager.getConnection(url, user, password);

        return conn;
    }

    // metoda do zamkniecia polaczenia
    public void closeConnection() throws SQLException {
        if(conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}