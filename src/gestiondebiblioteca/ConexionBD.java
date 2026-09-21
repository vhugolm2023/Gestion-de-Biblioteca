package gestiondebiblioteca;

    import io.github.cdimascio.dotenv.Dotenv;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

    public class ConexionBD {


        private static final Dotenv dotenv = Dotenv.load();


        private static final String URL = dotenv.get("DB_URL");
        private static final String USER = dotenv.get("DB_USER");
        private static final String PASSWORD = dotenv.get("DB_PASSWORD");

        public static Connection getConnection() throws SQLException {
            try {

                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.out.println("Error al conectar a la base de datos: " + e.getMessage());
                throw e;
            }
        }
    }