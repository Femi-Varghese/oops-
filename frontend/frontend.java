package frontend;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;



class Vehicle {
    String name, type, fuel;

    Vehicle(String name, String type, String fuel) {
        this.name = name;
        this.type = type;
        this.fuel = fuel;
    }
}

class Pricing {
    double perHour, perDay, perKm;

    Pricing(double h, double d, double k) {
        this.perHour = h;
        this.perDay = d;
        this.perKm = k;
    }
}

class Booking {
    Vehicle vehicle;
    String userName;
    LocalDateTime from, to;
    double amount;
    String status;

    Booking(Vehicle vehicle, String userName, LocalDateTime from, LocalDateTime to, double amount) {
        this.vehicle = vehicle;
        this.userName = userName;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.status = LocalDateTime.now().isAfter(to) ? "Completed" : "Upcoming";
    }
}

public class VehicleRentalApp extends JFrame {
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    Map<String, Pricing> categoryPricing = new HashMap<>();

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "adminpass";
    static final String USER_USERNAME = "user1";
    static final String USER_PASSWORD = "user1pass";

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    DefaultTableModel vehicleModel = new DefaultTableModel(new String[]{"Name", "Type", "Fuel"}, 0);
    DefaultTableModel adminRentalModel = new DefaultTableModel(new String[]{"User", "Vehicle", "From", "To", "Amount"}, 0);
    DefaultTableModel userBookingModel = new DefaultTableModel(new String[]{"Vehicle", "From", "To", "Status", "Amount"}, 0);

    JTable userBookingTable = new JTable(userBookingModel);

    JLabel totalRevenueLabel = new JLabel("Total Revenue: ₹0");
    JLabel totalRentalsLabel = new JLabel("Total Rentals: 0");
    JLabel activeRentalsLabel = new JLabel("Active Rentals: 0");

    String loggedInUser = "";
    boolean isAdmin = false;

    public VehicleRentalApp() {
        setTitle("Vehicle Rental System");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        categoryPricing.put("Car", new Pricing(150, 1000, 12));
        categoryPricing.put("Bike", new Pricing(50, 400, 5));
        categoryPricing.put("SUV", new Pricing(200, 1500, 15));
        categoryPricing.put("Truck", new Pricing(300, 2000, 20));

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

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                isAdmin = true;
                loggedInUser = username;
                refreshAdminDashboard();
                cardLayout.show(mainPanel, "adminDashboard");
            } else if (username.equals(USER_USERNAME) && password.equals(USER_PASSWORD)) {
                isAdmin = false;
                loggedInUser = username;
                refreshUserDashboard(loggedInUser);
                cardLayout.show(mainPanel, "userDashboard");
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
        logoutBtn.addActionListener(e -> { loggedInUser = ""; isAdmin = false; cardLayout.show(mainPanel, "login"); });

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
        buttonsPanel.add(addBtn); buttonsPanel.add(updateBtn); buttonsPanel.add(deleteBtn);

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

        // Fill current pricing
        Pricing p;
        p = categoryPricing.get("Car"); carH.setText(""+(int)p.perHour); carD.setText(""+(int)p.perDay); carK.setText(""+(int)p.perKm);
        p = categoryPricing.get("Bike"); bikeH.setText(""+(int)p.perHour); bikeD.setText(""+(int)p.perDay); bikeK.setText(""+(int)p.perKm);
        p = categoryPricing.get("SUV"); suvH.setText(""+(int)p.perHour); suvD.setText(""+(int)p.perDay); suvK.setText(""+(int)p.perKm);
        p = categoryPricing.get("Truck"); truckH.setText(""+(int)p.perHour); truckD.setText(""+(int)p.perDay); truckK.setText(""+(int)p.perKm);

        JButton updatePricingBtn = new JButton("Update Pricing");
        JPanel pricingContainer = new JPanel(new BorderLayout());
        pricingContainer.add(pricingPanel, BorderLayout.CENTER);
        pricingContainer.add(updatePricingBtn, BorderLayout.SOUTH);
        panel.add(pricingContainer, BorderLayout.EAST);

        // ========== BUTTON ACTIONS ==========
        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(this,"Vehicle name required."); return; }
            Vehicle v = new Vehicle(name, (String)typeBox.getSelectedItem(), (String)fuelBox.getSelectedItem());
            vehicles.add(v); vehicleModel.addRow(new Object[]{v.name,v.type,v.fuel});
            nameF.setText("");
        });

        updateBtn.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row==-1){ JOptionPane.showMessageDialog(this,"Select vehicle to update."); return; }
            Vehicle v = vehicles.get(row);
            v.name = nameF.getText().trim();
            v.type = (String) typeBox.getSelectedItem();
            v.fuel = (String) fuelBox.getSelectedItem();
            vehicleModel.setValueAt(v.name,row,0);
            vehicleModel.setValueAt(v.type,row,1);
            vehicleModel.setValueAt(v.fuel,row,2);
        });

        deleteBtn.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row==-1) return;
            vehicles.remove(row);
            vehicleModel.removeRow(row);
        });

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if(row>=0){
                Vehicle v = vehicles.get(row);
                nameF.setText(v.name);
                typeBox.setSelectedItem(v.type);
                fuelBox.setSelectedItem(v.fuel);
            }
        });

        updatePricingBtn.addActionListener(e -> {
            try {
                categoryPricing.put("Car", new Pricing(Double.parseDouble(carH.getText()),Double.parseDouble(carD.getText()),Double.parseDouble(carK.getText())));
                categoryPricing.put("Bike", new Pricing(Double.parseDouble(bikeH.getText()),Double.parseDouble(bikeD.getText()),Double.parseDouble(bikeK.getText())));
                categoryPricing.put("SUV", new Pricing(Double.parseDouble(suvH.getText()),Double.parseDouble(suvD.getText()),Double.parseDouble(suvK.getText())));
                categoryPricing.put("Truck", new Pricing(Double.parseDouble(truckH.getText()),Double.parseDouble(truckD.getText()),Double.parseDouble(truckK.getText())));
                JOptionPane.showMessageDialog(this,"Category pricing updated!");
            } catch(Exception ex){ JOptionPane.showMessageDialog(this,"Invalid pricing values."); }
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
        double revenue = bookings.stream().filter(b -> !b.status.equals("Cancelled")).mapToDouble(b -> b.amount).sum();
        long totalRentals = bookings.stream().filter(b -> !b.status.equals("Cancelled")).count();
        long activeRentals = bookings.stream().filter(b -> b.status.equals("Upcoming")).count();

        totalRevenueLabel.setText("Total Revenue: ₹" + (int)revenue);
        totalRentalsLabel.setText("Total Rentals: " + totalRentals);
        activeRentalsLabel.setText("Active Rentals: " + activeRentals);

        adminRentalModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(Booking b : bookings){
            if(!b.status.equals("Cancelled")){
                adminRentalModel.addRow(new Object[]{
                    b.userName,b.vehicle.name,b.from.format(fmt),b.to.format(fmt),"₹"+(int)b.amount
                });
            }
        }
    }

    // ================= USER DASHBOARD =================
    JPanel userDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel();
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> { loggedInUser = ""; isAdmin=false; cardLayout.show(mainPanel,"login"); });
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
            LocalDateTime from = LocalDateTime.parse((String)userBookingModel.getValueAt(row,1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime to = LocalDateTime.parse((String)userBookingModel.getValueAt(row,2), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String vehicleName = (String)userBookingModel.getValueAt(row,0);

            for(Booking b : bookings){
                if(b.userName.equals(loggedInUser) && b.vehicle.name.equals(vehicleName)
                        && b.from.equals(from) && b.to.equals(to) && !b.status.equals("Cancelled")){
                    b.status="Cancelled"; break;
                }
            }
            refreshUserDashboard(loggedInUser); refreshAdminDashboard();
        });

        bookBtn.addActionListener(e -> showBookingDialog());
        panel.addHierarchyListener(e -> welcomeLabel.setText("Welcome, "+loggedInUser));
        return panel;
    }

    void refreshUserDashboard(String user){
        userBookingModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(Booking b : bookings){
            if(b.userName.equals(user)){
                userBookingModel.addRow(new Object[]{b.vehicle.name,b.from.format(fmt),b.to.format(fmt),b.status,"₹"+(int)b.amount});
            }
        }
    }

    // ================= BOOKING =================
    void showBookingDialog(){
        if(vehicles.isEmpty()){ JOptionPane.showMessageDialog(this,"No vehicles available."); return; }

        String[] names = vehicles.stream().map(v->v.name+" ("+v.type+")").toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this,"Select a vehicle:","Booking",
                JOptionPane.PLAIN_MESSAGE,null,names,names[0]);
        if(choice==null) return;

        Vehicle selected = vehicles.stream().filter(v->choice.startsWith(v.name)).findFirst().get();
        Pricing p = categoryPricing.get(selected.type);

        JPanel bookingPanel = new JPanel(new GridLayout(0,2,8,8));
        JComboBox<String> rateTypeBox = new JComboBox<>(new String[]{"Per Hour","Per Day","Per Km"});
        JTextField valueField = new JTextField();

        bookingPanel.add(new JLabel("Rate type:")); bookingPanel.add(rateTypeBox);
        bookingPanel.add(new JLabel("Enter value:")); bookingPanel.add(valueField);

        int result = JOptionPane.showConfirmDialog(this,bookingPanel,"Booking Options",JOptionPane.OK_CANCEL_OPTION);
        if(result!=JOptionPane.OK_OPTION) return;

        try{
            double value = Double.parseDouble(valueField.getText().trim());
            double total=0; LocalDateTime now=LocalDateTime.now(),end=now;
            String type=(String) rateTypeBox.getSelectedItem();
            if(type.equals("Per Hour")){ total=value*p.perHour; end=now.plusHours((long)value); }
            else if(type.equals("Per Day")){ total=value*p.perDay; end=now.plusDays((long)value); }
            else if(type.equals("Per Km")){ total=value*p.perKm; end=now.plusHours(1); }

            Booking booking = new Booking(selected,loggedInUser,now,end,total);
            bookings.add(booking);
            refreshUserDashboard(loggedInUser);
            refreshAdminDashboard();

            JOptionPane.showMessageDialog(this,"Booking successful!\nAmount: ₹"+(int)total);
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"Invalid input"); }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new VehicleRentalApp().setVisible(true));
    }
    
}
