import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseService {
    private static Connection conn;
    private static final String dbUrl = System.getenv("OPENSHIFT_MYSQL_DB_URL") != null ?
            System.getenv("OPENSHIFT_MYSQL_DB_URL") : "mysql://localhost:3306/ratings?user=root&password=root";
    private static final Logger logger = LogManager.getLogger(DatabaseService.class);

    public static void initConnection() {
        if(conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:" + dbUrl);
            } catch (SQLException e) {
                // TODO return a message so the UI can display
                logger.error("Could not connect to database: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            logger.debug("Initializing database connection...");
            initConnection();
        }
        logger.debug("Connection initialized");
        return conn;
    }
}