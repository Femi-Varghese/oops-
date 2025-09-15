package backend;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import backend.Booking;
import backend.DatabaseInitializer;
import backend.Pricing;
import backend.Vehicle;
import backend.VehicleRentalService;
import backend.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class VehicleRentalAppWithDB extends JFrame {
    private VehicleRentalService rentalService;
    private User currentUser;
    private boolean isAdmin = false;

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    DefaultTableModel vehicleModel = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Fuel", "Available"}, 0);
    DefaultTableModel adminRentalModel = new DefaultTableModel(new String[]{"ID", "User", "Vehicle", "From", "To", "Amount", "Status"}, 0);
    DefaultTableModel userBookingModel = new DefaultTableModel(new String[]{"ID", "Vehicle", "From", "To", "Status", "Amount"}, 0);

    JTable userBookingTable = new JTable(userBookingModel);

    JLabel totalRevenueLabel = new JLabel("Total Revenue: ₹0");
    JLabel totalRentalsLabel = new JLabel("Total Rentals: 0");
    JLabel activeRentalsLabel = new JLabel("Active Rentals: 0");

    public VehicleRentalAppWithDB() {
        setTitle("Vehicle Rental System - Database Version");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize database and service
        try {
            DatabaseInitializer.initialize();
            rentalService = new VehicleRentalService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error initializing database: " + e.getMessage());
            System.exit(1);
        }

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(userDashboardPanel(), "userDashboard");
        mainPanel.add(adminDashboardPanel(), "adminDashboard");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    // ================= LOGIN =================
    JPanel loginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Vehicle Rental Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('*');
            }
        });

        JButton loginBtn = new JButton("Login");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);
        gbc.gridx = 1; panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(showPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            currentUser = rentalService.authenticateUser(username, password);
            if (currentUser != null) {
                isAdmin = currentUser.isAdmin();
                if (isAdmin) {
                    refreshAdminDashboard();
                    cardLayout.show(mainPanel, "adminDashboard");
                } else {
                    refreshUserDashboard();
                    cardLayout.show(mainPanel, "userDashboard");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
            userField.setText("");
            passField.setText("");
        });
        return panel;
    }

    // ================= ADMIN DASHBOARD =================
    JPanel adminDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> { 
            currentUser = null; 
            isAdmin = false; 
            cardLayout.show(mainPanel, "login"); 
        });

        topPanel.add(new JLabel("Admin Dashboard", SwingConstants.CENTER), BorderLayout.CENTER);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manage Vehicles & Pricing", adminManageVehiclesPanel());
        tabs.add("Bookings & Stats", adminStatsPanel());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    JPanel adminManageVehiclesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JTable vehicleTable = new JTable(vehicleModel);
        panel.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // Vehicle form
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField nameF = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Car", "Bike", "SUV", "Truck"});
        JComboBox<String> fuelBox = new JComboBox<>(new String[]{"Fuel", "Electric"});
        form.add(new JLabel("Name:")); form.add(nameF);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Fuel:")); form.add(fuelBox);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Vehicle");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");
        buttonsPanel.add(addBtn); buttonsPanel.add(updateBtn); buttonsPanel.add(deleteBtn); buttonsPanel.add(refreshBtn);

        panel.add(form, BorderLayout.SOUTH);
        panel.add(buttonsPanel, BorderLayout.NORTH);

        // ===== CATEGORY PRICING SECTION =====
        JPanel pricingPanel = new JPanel(new GridLayout(5,4,5,5));
        JTextField carH = new JTextField(); JTextField carD = new JTextField(); JTextField carK = new JTextField();
        JTextField bikeH = new JTextField(); JTextField bikeD = new JTextField(); JTextField bikeK = new JTextField();
        JTextField suvH = new JTextField(); JTextField suvD = new JTextField(); JTextField suvK = new JTextField();
        JTextField truckH = new JTextField(); JTextField truckD = new JTextField(); JTextField truckK = new JTextField();
        pricingPanel.add(new JLabel("Category")); pricingPanel.add(new JLabel("Per Hour")); pricingPanel.add(new JLabel("Per Day")); pricingPanel.add(new JLabel("Per Km"));
        pricingPanel.add(new JLabel("Car")); pricingPanel.add(carH); pricingPanel.add(carD); pricingPanel.add(carK);
        pricingPanel.add(new JLabel("Bike")); pricingPanel.add(bikeH); pricingPanel.add(bikeD); pricingPanel.add(bikeK);
        pricingPanel.add(new JLabel("SUV")); pricingPanel.add(suvH); pricingPanel.add(suvD); pricingPanel.add(suvK);
        pricingPanel.add(new JLabel("Truck")); pricingPanel.add(truckH); pricingPanel.add(truckD); pricingPanel.add(truckK);

        // Load current pricing
        loadPricingData(carH, carD, carK, bikeH, bikeD, bikeK, suvH, suvD, suvK, truckH, truckD, truckK);

        JButton updatePricingBtn = new JButton("Update Pricing");
        JPanel pricingContainer = new JPanel(new BorderLayout());
        pricingContainer.add(pricingPanel, BorderLayout.CENTER);
        pricingContainer.add(updatePricingBtn, BorderLayout.SOUTH);
        panel.add(pricingContainer, BorderLayout.EAST);

        // ========== BUTTON ACTIONS ==========
        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { 
                JOptionPane.showMessageDialog(this,"Vehicle name required."); 
                return; 
            }
            Vehicle v = new Vehicle(name, (String)typeBox.getSelectedItem(), (String)fuelBox.getSelectedItem());
            if (rentalService.createVehicle(v)) {
                refreshVehicleTable();
                nameF.setText("");
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error adding vehicle.");
            }
        });

        updateBtn.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row==-1){ 
                JOptionPane.showMessageDialog(this,"Select vehicle to update."); 
                return; 
            }
            int vehicleId = (Integer)vehicleModel.getValueAt(row, 0);
            Vehicle v = rentalService.getVehicleById(vehicleId);
            if (v != null) {
                v.setName(nameF.getText().trim());
                v.setType((String) typeBox.getSelectedItem());
                v.setFuelType((String) fuelBox.getSelectedItem());
                if (rentalService.updateVehicle(v)) {
                    refreshVehicleTable();
                    JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating vehicle.");
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row==-1) return;
            int vehicleId = (Integer)vehicleModel.getValueAt(row, 0);
            if (rentalService.deleteVehicle(vehicleId)) {
                refreshVehicleTable();
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting vehicle.");
            }
        });

        refreshBtn.addActionListener(e -> {
            refreshVehicleTable();
            loadPricingData(carH, carD, carK, bikeH, bikeD, bikeK, suvH, suvD, suvK, truckH, truckD, truckK);
        });

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row>=0){
                int vehicleId = (Integer)vehicleModel.getValueAt(row, 0);
                Vehicle v = rentalService.getVehicleById(vehicleId);
                if (v != null) {
                    nameF.setText(v.getName());
                    typeBox.setSelectedItem(v.getType());
                    fuelBox.setSelectedItem(v.getFuelType());
                }
            }
        });

        updatePricingBtn.addActionListener(e -> {
            try {
                boolean success = true;
                success &= rentalService.updatePricingByType("Car", Double.parseDouble(carH.getText()), Double.parseDouble(carD.getText()), Double.parseDouble(carK.getText()));
                success &= rentalService.updatePricingByType("Bike", Double.parseDouble(bikeH.getText()), Double.parseDouble(bikeD.getText()), Double.parseDouble(bikeK.getText()));
                success &= rentalService.updatePricingByType("SUV", Double.parseDouble(suvH.getText()), Double.parseDouble(suvD.getText()), Double.parseDouble(suvK.getText()));
                success &= rentalService.updatePricingByType("Truck", Double.parseDouble(truckH.getText()), Double.parseDouble(truckD.getText()), Double.parseDouble(truckK.getText()));
                
                if (success) {
                    JOptionPane.showMessageDialog(this,"Category pricing updated!");
                } else {
                    JOptionPane.showMessageDialog(this,"Error updating pricing.");
                }
            } catch(Exception ex){ 
                JOptionPane.showMessageDialog(this,"Invalid pricing values."); 
            }
        });

        return panel;
    }

    JPanel adminStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        JTable bookingTable = new JTable(adminRentalModel);
        panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(3,1));
        statsPanel.add(totalRevenueLabel);
        statsPanel.add(totalRentalsLabel);
        statsPanel.add(activeRentalsLabel);
        panel.add(statsPanel, BorderLayout.NORTH);

        return panel;
    }

    void refreshAdminDashboard() {
        double revenue = rentalService.getTotalRevenue();
        int totalRentals = rentalService.getTotalBookings();
        int activeRentals = rentalService.getActiveBookingsCount();

        totalRevenueLabel.setText("Total Revenue: ₹" + (int)revenue);
        totalRentalsLabel.setText("Total Rentals: " + totalRentals);
        activeRentalsLabel.setText("Active Rentals: " + activeRentals);

        adminRentalModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Booking> bookings = rentalService.getAllBookings();
        for(Booking b : bookings){
            if(!b.isCancelled()){
                adminRentalModel.addRow(new Object[]{
                    b.getId(), b.getUserName(), b.getVehicleName(), 
                    b.getStartTime().format(fmt), b.getEndTime().format(fmt), 
                    "₹"+(int)b.getAmount(), b.getStatus()
                });
            }
        }
    }

    void refreshVehicleTable() {
        vehicleModel.setRowCount(0);
        List<Vehicle> vehicles = rentalService.getAllVehicles();
        for(Vehicle v : vehicles){
            vehicleModel.addRow(new Object[]{v.getId(), v.getName(), v.getType(), v.getFuelType(), v.isAvailable()});
        }
    }

    void loadPricingData(JTextField carH, JTextField carD, JTextField carK, 
                        JTextField bikeH, JTextField bikeD, JTextField bikeK,
                        JTextField suvH, JTextField suvD, JTextField suvK,
                        JTextField truckH, JTextField truckD, JTextField truckK) {
        Map<String, Pricing> pricingMap = rentalService.getAllPricingAsMap();
        
        Pricing p = pricingMap.get("Car");
        if (p != null) { carH.setText(""+(int)p.getPerHour()); carD.setText(""+(int)p.getPerDay()); carK.setText(""+(int)p.getPerKm()); }
        
        p = pricingMap.get("Bike");
        if (p != null) { bikeH.setText(""+(int)p.getPerHour()); bikeD.setText(""+(int)p.getPerDay()); bikeK.setText(""+(int)p.getPerKm()); }
        
        p = pricingMap.get("SUV");
        if (p != null) { suvH.setText(""+(int)p.getPerHour()); suvD.setText(""+(int)p.getPerDay()); suvK.setText(""+(int)p.getPerKm()); }
        
        p = pricingMap.get("Truck");
        if (p != null) { truckH.setText(""+(int)p.getPerHour()); truckD.setText(""+(int)p.getPerDay()); truckK.setText(""+(int)p.getPerKm()); }
    }

    // ================= USER DASHBOARD =================
    JPanel userDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel();
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> { 
            currentUser = null; 
            isAdmin=false; 
            cardLayout.show(mainPanel,"login"); 
        });
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        panel.add(new JScrollPane(userBookingTable), BorderLayout.CENTER);

        JButton bookBtn = new JButton("Book Vehicle");
        JButton cancelBtn = new JButton("Cancel Booking");
        JPanel btnPanel = new JPanel(); btnPanel.add(bookBtn); btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> {
            int row = userBookingTable.getSelectedRow();
            if(row==-1) return;
            int bookingId = (Integer)userBookingModel.getValueAt(row, 0);
            if (rentalService.cancelBooking(bookingId)) {
                refreshUserDashboard();
                refreshAdminDashboard();
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error cancelling booking.");
            }
        });

        bookBtn.addActionListener(e -> showBookingDialog());
        panel.addHierarchyListener(e -> welcomeLabel.setText("Welcome, "+currentUser.getUsername()));
        return panel;
    }

    void refreshUserDashboard(){
        userBookingModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<Booking> bookings = rentalService.getBookingsByUserId(currentUser.getId());
        for(Booking b : bookings){
            userBookingModel.addRow(new Object[]{b.getId(), b.getVehicleName(), b.getStartTime().format(fmt), b.getEndTime().format(fmt), b.getStatus(), "₹"+(int)b.getAmount()});
        }
    }

    // ================= BOOKING =================
    void showBookingDialog(){
        List<Vehicle> vehicles = rentalService.getAvailableVehicles();
        if(vehicles.isEmpty()){ 
            JOptionPane.showMessageDialog(this,"No vehicles available."); 
            return; 
        }

        String[] names = vehicles.stream().map(v->v.getName()+" ("+v.getType()+")").toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this,"Select a vehicle:","Booking",
                JOptionPane.PLAIN_MESSAGE,null,names,names[0]);
        if(choice==null) return;

        Vehicle selected = vehicles.stream().filter(v->choice.startsWith(v.getName())).findFirst().orElse(null);
        if (selected == null) return;

        Pricing p = rentalService.getPricingByVehicleType(selected.getType());
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Pricing not found for vehicle type: " + selected.getType());
            return;
        }

        JPanel bookingPanel = new JPanel(new GridLayout(0,2,8,8));
        JComboBox<String> rateTypeBox = new JComboBox<>(new String[]{"Per Hour","Per Day","Per Km"});
        JTextField valueField = new JTextField();

        bookingPanel.add(new JLabel("Rate type:")); bookingPanel.add(rateTypeBox);
        bookingPanel.add(new JLabel("Enter value:")); bookingPanel.add(valueField);

        int result = JOptionPane.showConfirmDialog(this,bookingPanel,"Booking Options",JOptionPane.OK_CANCEL_OPTION);
        if(result!=JOptionPane.OK_OPTION) return;

        try{
            double value = Double.parseDouble(valueField.getText().trim());
            String type = (String) rateTypeBox.getSelectedItem();
            
            double total = rentalService.calculateBookingAmount(selected.getType(), type, value);
            LocalDateTime endTime = rentalService.calculateEndTime(type, value);
            LocalDateTime startTime = LocalDateTime.now();

            Booking booking = new Booking(currentUser.getId(), selected.getId(), startTime, endTime, total);
            if (rentalService.createBooking(booking)) {
                refreshUserDashboard();
                refreshAdminDashboard();
                JOptionPane.showMessageDialog(this,"Booking successful!\nAmount: ₹"+(int)total);
            } else {
                JOptionPane.showMessageDialog(this, "Error creating booking. Vehicle may not be available.");
            }
        }catch(Exception ex){ 
            JOptionPane.showMessageDialog(this,"Invalid input"); 
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new VehicleRentalAppWithDB().setVisible(true));
    }
}
