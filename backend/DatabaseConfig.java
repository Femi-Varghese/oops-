package backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_rental_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Ria@martin_03";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    
    private static Connection connection = null;
    
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS vehicle_rental_db");
            stmt.executeUpdate("USE vehicle_rental_db");
            
            // Create users table
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    role ENUM('ADMIN', 'USER') NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createUsersTable);
            
            // Create vehicles table
            String createVehiclesTable = """
                CREATE TABLE IF NOT EXISTS vehicles (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    fuel_type VARCHAR(20) NOT NULL,
                    is_available BOOLEAN DEFAULT TRUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createVehiclesTable);
            
            // Create pricing table
            String createPricingTable = """
                CREATE TABLE IF NOT EXISTS pricing (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    vehicle_type VARCHAR(20) UNIQUE NOT NULL,
                    per_hour DECIMAL(10,2) NOT NULL,
                    per_day DECIMAL(10,2) NOT NULL,
                    per_km DECIMAL(10,2) NOT NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createPricingTable);
            
            // Create bookings table
            String createBookingsTable = """
                CREATE TABLE IF NOT EXISTS bookings (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    vehicle_id INT NOT NULL,
                    start_time TIMESTAMP NOT NULL,
                    end_time TIMESTAMP NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    status ENUM('UPCOMING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
                )
            """;
            stmt.executeUpdate(createBookingsTable);
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    public static void insertSampleData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Insert default users
            String insertUsers = """
                INSERT IGNORE INTO users (username, password, role) VALUES
                ('admin', 'adminpass', 'ADMIN'),
                ('user1', 'user1pass', 'USER')
            """;
            stmt.executeUpdate(insertUsers);
            
            // Insert default pricing
            String insertPricing = """
                INSERT IGNORE INTO pricing (vehicle_type, per_hour, per_day, per_km) VALUES
                ('Car', 150.00, 1000.00, 12.00),
                ('Bike', 50.00, 400.00, 5.00),
                ('SUV', 200.00, 1500.00, 15.00),
                ('Truck', 300.00, 2000.00, 20.00)
            """;
            stmt.executeUpdate(insertPricing);
            
            // Insert sample vehicles
            String insertVehicles = """
                INSERT IGNORE INTO vehicles (name, type, fuel_type) VALUES
                ('Honda City', 'Car', 'Fuel'),
                ('Toyota Innova', 'SUV', 'Fuel'),
                ('Bajaj Pulsar', 'Bike', 'Fuel'),
                ('Tesla Model 3', 'Car', 'Electric'),
                ('Mahindra Bolero', 'Truck', 'Fuel')
            """;
            stmt.executeUpdate(insertVehicles);
            
            System.out.println("Sample data inserted successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }
}
