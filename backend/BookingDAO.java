package backend;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import backend.Booking;

public class BookingDAO {
    
    public boolean createBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, vehicle_id, start_time, end_time, amount, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getVehicleId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getAmount());
            stmt.setString(6, booking.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
        }
        return false;
    }
    
    public Booking getBookingById(int id) {
        String sql = """
            SELECT b.*, u.username, v.name as vehicle_name 
            FROM bookings b 
            JOIN users u ON b.user_id = u.id 
            JOIN vehicles v ON b.vehicle_id = v.id 
            WHERE b.id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("vehicle_id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    booking.setUserName(rs.getString("username"));
                    booking.setVehicleName(rs.getString("vehicle_name"));
                    return booking;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting booking by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.username, v.name as vehicle_name 
            FROM bookings b 
            JOIN users u ON b.user_id = u.id 
            JOIN vehicles v ON b.vehicle_id = v.id 
            ORDER BY b.created_at DESC
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("vehicle_id"),
                    rs.getTimestamp("start_time").toLocalDateTime(),
                    rs.getTimestamp("end_time").toLocalDateTime(),
                    rs.getDouble("amount"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                booking.setUserName(rs.getString("username"));
                booking.setVehicleName(rs.getString("vehicle_name"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bookings: " + e.getMessage());
        }
        return bookings;
    }
    
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.username, v.name as vehicle_name 
            FROM bookings b 
            JOIN users u ON b.user_id = u.id 
            JOIN vehicles v ON b.vehicle_id = v.id 
            WHERE b.user_id = ? 
            ORDER BY b.created_at DESC
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("vehicle_id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    booking.setUserName(rs.getString("username"));
                    booking.setVehicleName(rs.getString("vehicle_name"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings by user ID: " + e.getMessage());
        }
        return bookings;
    }
    
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT b.*, u.username, v.name as vehicle_name 
            FROM bookings b 
            JOIN users u ON b.user_id = u.id 
            JOIN vehicles v ON b.vehicle_id = v.id 
            WHERE b.status = ? 
            ORDER BY b.created_at DESC
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("vehicle_id"),
                        rs.getTimestamp("start_time").toLocalDateTime(),
                        rs.getTimestamp("end_time").toLocalDateTime(),
                        rs.getDouble("amount"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    booking.setUserName(rs.getString("username"));
                    booking.setVehicleName(rs.getString("vehicle_name"));
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings by status: " + e.getMessage());
        }
        return bookings;
    }
    
    public List<Booking> getActiveBookings() {
        return getBookingsByStatus("UPCOMING");
    }
    
    public List<Booking> getCompletedBookings() {
        return getBookingsByStatus("COMPLETED");
    }
    
    public List<Booking> getCancelledBookings() {
        return getBookingsByStatus("CANCELLED");
    }
    
    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
        }
        return false;
    }
    
    public boolean cancelBooking(int bookingId) {
        return updateBookingStatus(bookingId, "CANCELLED");
    }
    
    public boolean completeBooking(int bookingId) {
        return updateBookingStatus(bookingId, "COMPLETED");
    }
    
    public boolean updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET user_id = ?, vehicle_id = ?, start_time = ?, end_time = ?, amount = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getVehicleId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            stmt.setDouble(5, booking.getAmount());
            stmt.setString(6, booking.getStatus());
            stmt.setInt(7, booking.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
        }
        return false;
    }
    
    public double getTotalRevenue() {
        String sql = "SELECT SUM(amount) as total FROM bookings WHERE status != 'CANCELLED'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }
    
    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) as total FROM bookings WHERE status != 'CANCELLED'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total bookings: " + e.getMessage());
        }
        return 0;
    }
    
    public int getActiveBookingsCount() {
        String sql = "SELECT COUNT(*) as total FROM bookings WHERE status = 'UPCOMING'";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active bookings count: " + e.getMessage());
        }
        return 0;
    }
}
