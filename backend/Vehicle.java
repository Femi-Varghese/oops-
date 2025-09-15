package backend;
import java.time.LocalDateTime;

public class Vehicle {
    private int id;
    private String name;
    private String type;
    private String fuelType;
    private boolean isAvailable;
    private LocalDateTime createdAt;
    
    public Vehicle() {}
    
    public Vehicle(String name, String type, String fuelType) {
        this.name = name;
        this.type = type;
        this.fuelType = fuelType;
        this.isAvailable = true;
    }
    
    public Vehicle(int id, String name, String type, String fuelType, boolean isAvailable, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.fuelType = fuelType;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", name='" + name + "', type='" + type + "', fuelType='" + fuelType + "', available=" + isAvailable + "}";
    }
}
