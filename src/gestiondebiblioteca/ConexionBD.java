package gestiondebiblioteca;

    import io.github.cdimascio.dotenv.Dotenv;
    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;
    
    /**
 * Clase de utilidad para obtener conexiones a la base de datos MySQL.
 * <p>
 * Los datos de conexión (URL, usuario y contraseña) no están escritos en el
 * código: se leen del archivo {@code .env} mediante la librería dotenv, con
 * las variables {@code DB_URL}, {@code DB_USER} y {@code DB_PASSWORD}.
 */
    public class ConexionBD {


         /** Configuración cargada desde el archivo {@code .env}. */
        private static final Dotenv dotenv = Dotenv.load();


        /** URL de conexión a la base de datos (variable {@code DB_URL}). */
        private static final String URL = dotenv.get("DB_URL");
        /** Usuario de la base de datos (variable {@code DB_USER}). */
        private static final String USER = dotenv.get("DB_USER");
        /** Contraseña de la base de datos (variable {@code DB_PASSWORD}). */
        private static final String PASSWORD = dotenv.get("DB_PASSWORD");

        /**
     * Abre una nueva conexión a la base de datos con los datos del
     * archivo {@code .env}.
     * <p>
     * Quien llame a este método debe cerrar la conexión, por ejemplo
     * usando try-with-resources.
     *
     * @return una conexión abierta a la base de datos
     * @throws SQLException si no se puede establecer la conexión
     */
        public static Connection getConnection() throws SQLException {
            try {

                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.out.println("Error al conectar a la base de datos: " + e.getMessage());
                throw e;
            }
        }
    }