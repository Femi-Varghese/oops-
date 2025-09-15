package backend;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double amount;
    private String status;
    private LocalDateTime createdAt;
    
    // Additional fields for display purposes
    private String userName;
    private String vehicleName;
    
    public Booking() {}
    
    public Booking(int userId, int vehicleId, LocalDateTime startTime, LocalDateTime endTime, double amount) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.status = "UPCOMING";
    }
    
    public Booking(int id, int userId, int vehicleId, LocalDateTime startTime, LocalDateTime endTime, 
                   double amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    public boolean isUpcoming() {
        return "UPCOMING".equals(status);
    }
    
    @Override
    public String toString() {
        return "Booking{id=" + id + ", userId=" + userId + ", vehicleId=" + vehicleId + 
               ", startTime=" + startTime + ", endTime=" + endTime + ", amount=" + amount + 
               ", status='" + status + "'}";
    }
}
