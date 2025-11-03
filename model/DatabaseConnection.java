package pos_tienda_abarrotes.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String SERVER = "localhost"; // o tu servidor
    private static final String DATABASE = "pos_tienda_abarrotes";
    private static final String PORT = "1433"; // Puerto por defecto de SQL Server
    
    private static final String URL = "jdbc:sqlserver://" + SERVER + ":" + PORT + 
                                      ";databaseName=" + DATABASE + 
                                      ";integratedSecurity=true;" +
                                      "encrypt=false;" +
                                      "trustServerCertificate=true;";
    
    private static Connection connection = null;
    
    private DatabaseConnection() {
    }
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                connection = DriverManager.getConnection(URL);
                
                System.out.println("✓ Conexión exitosa a la base de datos");
            }
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Driver JDBC no encontrado");
            System.err.println("Asegúrate de que el archivo mssql-jdbc.jar esté agregado al proyecto");
            e.printStackTrace();
            return null;
            
        } catch (SQLException e) {
            System.err.println("✗ Error al conectar con la base de datos");
            System.err.println("Verifica que:");
            System.err.println("  - SQL Server esté ejecutándose");
            System.err.println("  - La base de datos 'POS_TiendaAbarrotes' exista");
            System.err.println("  - La autenticación de Windows esté habilitada");
            System.err.println("  - El servicio SQL Server Browser esté activo");
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
    
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Prueba de Conexión a Base de Datos ===");
        System.out.println("Servidor: " + SERVER);
        System.out.println("Base de datos: " + DATABASE);
        System.out.println("Puerto: " + PORT);
        System.out.println("Autenticación: Windows");
        System.out.println("==========================================\n");
        
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("\n¡Conexión establecida correctamente!");
            
            try {
                System.out.println("\nInformación de la conexión:");
                System.out.println("  - URL: " + conn.getMetaData().getURL());
                System.out.println("  - Usuario: " + conn.getMetaData().getUserName());
                System.out.println("  - Driver: " + conn.getMetaData().getDriverName());
                System.out.println("  - Versión del driver: " + conn.getMetaData().getDriverVersion());
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            DatabaseConnection.closeConnection();
            
        } else {
            System.out.println("\n✗ No se pudo establecer la conexión");
            System.out.println("\nPosibles soluciones:");
            System.out.println("1. Verifica el nombre del servidor (puede ser: localhost, ., 127.0.0.1, o NOMBRE_EQUIPO\\SQLEXPRESS)");
            System.out.println("2. Asegúrate de que SQL Server esté ejecutándose");
            System.out.println("3. Verifica que TCP/IP esté habilitado en SQL Server Configuration Manager");
            System.out.println("4. Confirma que la base de datos 'POS_TiendaAbarrotes' existe");
            System.out.println("5. Verifica que el firewall permita conexiones en el puerto 1433");
        }
    }
}