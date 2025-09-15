package backend;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PricingDAO {
    
    public boolean createPricing(Pricing pricing) {
        String sql = "INSERT INTO pricing (vehicle_type, per_hour, per_day, per_km) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, pricing.getVehicleType());
            stmt.setDouble(2, pricing.getPerHour());
            stmt.setDouble(3, pricing.getPerDay());
            stmt.setDouble(4, pricing.getPerKm());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pricing.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating pricing: " + e.getMessage());
        }
        return false;
    }
    
    public Pricing getPricingByVehicleType(String vehicleType) {
        String sql = "SELECT * FROM pricing WHERE vehicle_type = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Pricing(
                        rs.getInt("id"),
                        rs.getString("vehicle_type"),
                        rs.getDouble("per_hour"),
                        rs.getDouble("per_day"),
                        rs.getDouble("per_km"),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting pricing by vehicle type: " + e.getMessage());
        }
        return null;
    }
    
    public List<Pricing> getAllPricing() {
        List<Pricing> pricingList = new ArrayList<>();
        String sql = "SELECT * FROM pricing ORDER BY vehicle_type";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                pricingList.add(new Pricing(
                    rs.getInt("id"),
                    rs.getString("vehicle_type"),
                    rs.getDouble("per_hour"),
                    rs.getDouble("per_day"),
                    rs.getDouble("per_km"),
                    rs.getTimestamp("updated_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pricing: " + e.getMessage());
        }
        return pricingList;
    }
    
    public Map<String, Pricing> getAllPricingAsMap() {
        Map<String, Pricing> pricingMap = new HashMap<>();
        String sql = "SELECT * FROM pricing ORDER BY vehicle_type";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pricing pricing = new Pricing(
                    rs.getInt("id"),
                    rs.getString("vehicle_type"),
                    rs.getDouble("per_hour"),
                    rs.getDouble("per_day"),
                    rs.getDouble("per_km"),
                    rs.getTimestamp("updated_at").toLocalDateTime()
                );
                pricingMap.put(pricing.getVehicleType(), pricing);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pricing map: " + e.getMessage());
        }
        return pricingMap;
    }
    
    public boolean updatePricing(Pricing pricing) {
        String sql = "UPDATE pricing SET per_hour = ?, per_day = ?, per_km = ? WHERE vehicle_type = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, pricing.getPerHour());
            stmt.setDouble(2, pricing.getPerDay());
            stmt.setDouble(3, pricing.getPerKm());
            stmt.setString(4, pricing.getVehicleType());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pricing: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updatePricingByType(String vehicleType, double perHour, double perDay, double perKm) {
        String sql = "UPDATE pricing SET per_hour = ?, per_day = ?, per_km = ? WHERE vehicle_type = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, perHour);
            stmt.setDouble(2, perDay);
            stmt.setDouble(3, perKm);
            stmt.setString(4, vehicleType);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pricing by type: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deletePricing(int id) {
        String sql = "DELETE FROM pricing WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pricing: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deletePricingByType(String vehicleType) {
        String sql = "DELETE FROM pricing WHERE vehicle_type = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicleType);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pricing by type: " + e.getMessage());
        }
        return false;
    }
}
