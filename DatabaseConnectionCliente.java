import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionCliente {
    private static final String URL = "jdbc:postgresql://34.57.136.213:5432/Usuarios"; // Reemplaza con tu base
    private static final String USER = "postgres"; // Ejemplo: postgres
    private static final String PASSWORD = "1907"; // Contraseña del usuario

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
