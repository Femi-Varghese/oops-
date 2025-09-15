package backend;

public class DatabaseInitializer {
    
    public static void main(String[] args) {
        System.out.println("Initializing Vehicle Rental Database...");
        
        try {
            // Initialize database schema
            DatabaseConfig.initializeDatabase();
            
            // Insert sample data
            DatabaseConfig.insertSampleData();
            
            System.out.println("Database initialization completed successfully!");
            System.out.println("Default users created:");
            System.out.println("Admin - Username: admin, Password: adminpass");
            System.out.println("User - Username: user1, Password: user1pass");
            
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeConnection();
        }
    }
    
    public static void initialize() {
        try {
            DatabaseConfig.initializeDatabase();
            DatabaseConfig.insertSampleData();
            System.out.println("Database initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
