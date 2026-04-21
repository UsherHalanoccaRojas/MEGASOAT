package Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getDatabaseUrl());
        config.setUsername(ConfigLoader.getDatabaseUser());
        config.setPassword(ConfigLoader.getDatabasePassword());
        config.setDriverClassName(ConfigLoader.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));

        // Opciones de rendimiento
        config.setMaximumPoolSize(10);   // máximo de conexiones simultáneas
        config.setMinimumIdle(2);        // conexiones mínimas en reposo
        config.setIdleTimeout(30000);    // tiempo de espera en ms
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            Connection conn = dataSource.getConnection();
            ConnectionStatus.setEstado(ConnectionStatus.CONECTADO);
            return conn;
        } catch (SQLException e) {
            ConnectionStatus.setEstado(ConnectionStatus.SIN_CONEXION);
            return null; // evita romper la UI
        }
    }


    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
