package backend;
import java.time.LocalDateTime;

public class Pricing {
    private int id;
    private String vehicleType;
    private double perHour;
    private double perDay;
    private double perKm;
    private LocalDateTime updatedAt;
    
    public Pricing() {}
    
    public Pricing(String vehicleType, double perHour, double perDay, double perKm) {
        this.vehicleType = vehicleType;
        this.perHour = perHour;
        this.perDay = perDay;
        this.perKm = perKm;
    }
    
    public Pricing(int id, String vehicleType, double perHour, double perDay, double perKm, LocalDateTime updatedAt) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.perHour = perHour;
        this.perDay = perDay;
        this.perKm = perKm;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public double getPerHour() { return perHour; }
    public void setPerHour(double perHour) { this.perHour = perHour; }
    
    public double getPerDay() { return perDay; }
    public void setPerDay(double perDay) { this.perDay = perDay; }
    
    public double getPerKm() { return perKm; }
    public void setPerKm(double perKm) { this.perKm = perKm; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Pricing{id=" + id + ", vehicleType='" + vehicleType + "', perHour=" + perHour + 
               ", perDay=" + perDay + ", perKm=" + perKm + "}";
    }
}
