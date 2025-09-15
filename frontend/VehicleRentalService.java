package frontend;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VehicleRentalService {
    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    private PricingDAO pricingDAO;
    private BookingDAO bookingDAO;
    
    public VehicleRentalService() {
        this.userDAO = new UserDAO();
        this.vehicleDAO = new VehicleDAO();
        this.pricingDAO = new PricingDAO();
        this.bookingDAO = new BookingDAO();
    }
    
    // User Management
    public User authenticateUser(String username, String password) {
        return userDAO.authenticateUser(username, password);
    }
    
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
    
    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }
    
    public boolean createUser(User user) {
        return userDAO.createUser(user);
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
    
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }
    
    // Vehicle Management
    public boolean createVehicle(Vehicle vehicle) {
        return vehicleDAO.createVehicle(vehicle);
    }
    
    public Vehicle getVehicleById(int id) {
        return vehicleDAO.getVehicleById(id);
    }
    
    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }
    
    public List<Vehicle> getAvailableVehicles() {
        return vehicleDAO.getAvailableVehicles();
    }
    
    public List<Vehicle> getVehiclesByType(String type) {
        return vehicleDAO.getVehiclesByType(type);
    }
    
    public boolean updateVehicle(Vehicle vehicle) {
        return vehicleDAO.updateVehicle(vehicle);
    }
    
    public boolean deleteVehicle(int id) {
        return vehicleDAO.deleteVehicle(id);
    }
    
    public boolean updateVehicleAvailability(int vehicleId, boolean isAvailable) {
        return vehicleDAO.updateVehicleAvailability(vehicleId, isAvailable);
    }
    
    // Pricing Management
    public boolean createPricing(Pricing pricing) {
        return pricingDAO.createPricing(pricing);
    }
    
    public Pricing getPricingByVehicleType(String vehicleType) {
        return pricingDAO.getPricingByVehicleType(vehicleType);
    }
    
    public List<Pricing> getAllPricing() {
        return pricingDAO.getAllPricing();
    }
    
    public Map<String, Pricing> getAllPricingAsMap() {
        return pricingDAO.getAllPricingAsMap();
    }
    
    public boolean updatePricing(Pricing pricing) {
        return pricingDAO.updatePricing(pricing);
    }
    
    public boolean updatePricingByType(String vehicleType, double perHour, double perDay, double perKm) {
        return pricingDAO.updatePricingByType(vehicleType, perHour, perDay, perKm);
    }
    
    public boolean deletePricing(int id) {
        return pricingDAO.deletePricing(id);
    }
    
    public boolean deletePricingByType(String vehicleType) {
        return pricingDAO.deletePricingByType(vehicleType);
    }
    
    // Booking Management
    public boolean createBooking(Booking booking) {
        // Check if vehicle is available
        Vehicle vehicle = vehicleDAO.getVehicleById(booking.getVehicleId());
        if (vehicle == null || !vehicle.isAvailable()) {
            return false;
        }
        
        // Create booking
        boolean success = bookingDAO.createBooking(booking);
        if (success) {
            // Mark vehicle as unavailable
            vehicleDAO.updateVehicleAvailability(booking.getVehicleId(), false);
        }
        return success;
    }
    
    public Booking getBookingById(int id) {
        return bookingDAO.getBookingById(id);
    }
    
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }
    
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.getBookingsByUserId(userId);
    }
    
    public List<Booking> getBookingsByStatus(String status) {
        return bookingDAO.getBookingsByStatus(status);
    }
    
    public List<Booking> getActiveBookings() {
        return bookingDAO.getActiveBookings();
    }
    
    public List<Booking> getCompletedBookings() {
        return bookingDAO.getCompletedBookings();
    }
    
    public List<Booking> getCancelledBookings() {
        return bookingDAO.getCancelledBookings();
    }
    
    public boolean updateBookingStatus(int bookingId, String status) {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            return false;
        }
        
        boolean success = bookingDAO.updateBookingStatus(bookingId, status);
        if (success) {
            // If booking is cancelled or completed, make vehicle available again
            if ("CANCELLED".equals(status) || "COMPLETED".equals(status)) {
                vehicleDAO.updateVehicleAvailability(booking.getVehicleId(), true);
            }
        }
        return success;
    }
    
    public boolean cancelBooking(int bookingId) {
        return updateBookingStatus(bookingId, "CANCELLED");
    }
    
    public boolean completeBooking(int bookingId) {
        return updateBookingStatus(bookingId, "COMPLETED");
    }
    
    public boolean updateBooking(Booking booking) {
        return bookingDAO.updateBooking(booking);
    }
    
    public boolean deleteBooking(int id) {
        Booking booking = bookingDAO.getBookingById(id);
        if (booking == null) {
            return false;
        }
        
        boolean success = bookingDAO.deleteBooking(id);
        if (success) {
            // Make vehicle available again
            vehicleDAO.updateVehicleAvailability(booking.getVehicleId(), true);
        }
        return success;
    }
    
    // Statistics
    public double getTotalRevenue() {
        return bookingDAO.getTotalRevenue();
    }
    
    public int getTotalBookings() {
        return bookingDAO.getTotalBookings();
    }
    
    public int getActiveBookingsCount() {
        return bookingDAO.getActiveBookingsCount();
    }
    
    // Business Logic Methods
    public double calculateBookingAmount(String vehicleType, String rateType, double value) {
        Pricing pricing = pricingDAO.getPricingByVehicleType(vehicleType);
        if (pricing == null) {
            return 0.0;
        }
        
        switch (rateType.toUpperCase()) {
            case "PER HOUR":
                return value * pricing.getPerHour();
            case "PER DAY":
                return value * pricing.getPerDay();
            case "PER KM":
                return value * pricing.getPerKm();
            default:
                return 0.0;
        }
    }
    
    public LocalDateTime calculateEndTime(String rateType, double value) {
        LocalDateTime now = LocalDateTime.now();
        switch (rateType.toUpperCase()) {
            case "PER HOUR":
                return now.plusHours((long) value);
            case "PER DAY":
                return now.plusDays((long) value);
            case "PER KM":
                return now.plusHours(1); // Default 1 hour for per km
            default:
                return now.plusHours(1);
        }
    }
    
    public boolean isVehicleAvailable(int vehicleId) {
        Vehicle vehicle = vehicleDAO.getVehicleById(vehicleId);
        return vehicle != null && vehicle.isAvailable();
    }
    
    public boolean checkVehicleAvailability(int vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        // Check if vehicle exists and is available
        if (!isVehicleAvailable(vehicleId)) {
            return false;
        }
        
        // Check for overlapping bookings
        List<Booking> activeBookings = bookingDAO.getActiveBookings();
        for (Booking booking : activeBookings) {
            if (booking.getVehicleId() == vehicleId) {
                // Check for time overlap
                if (startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void updateBookingStatuses() {
        // Update bookings that have passed their end time to completed
        List<Booking> activeBookings = bookingDAO.getActiveBookings();
        LocalDateTime now = LocalDateTime.now();
        
        for (Booking booking : activeBookings) {
            if (now.isAfter(booking.getEndTime())) {
                completeBooking(booking.getId());
            }
        }
    }
}
