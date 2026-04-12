package com.example.realestate.config;

import com.example.realestate.model.Property;
import com.example.realestate.model.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // STEP 0: Force database migration
        try {
            jdbcTemplate.execute("ALTER TABLE property ALTER COLUMN description TYPE TEXT");
            System.out.println("Database migration: Property description changed to TEXT successfully.");
        } catch (Exception e) {
            System.out.println("Migration note: " + e.getMessage());
        }

        // STEP 1: Handle Admin Password via Environment Variables
        String adminPass = System.getenv("ADMIN_PASSWORD");
        if (adminPass == null || adminPass.isEmpty()) {
            adminPass = "admin123"; // Fallback default password
        }

        // Check if admin already exists
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        
        if (existingAdmin.isPresent()) {
            // Update existing admin password to match Environment Variable
            User admin = existingAdmin.get();
            admin.setPassword(passwordEncoder.encode(adminPass));
            userRepository.save(admin);
            System.out.println("Admin password updated from Environment Variables.");
        } else {
            // Create admin if not present
            User admin = new User("admin", passwordEncoder.encode(adminPass), "admin@example.com", true, "ADMIN");
            userRepository.save(admin);
            System.out.println("Admin created with password from Environment Variables.");
        }

        // STEP 2: Initialize other default users if they don't exist
        if (userRepository.count() <= 1) { // Only if we only have the admin we just handled
            User user1 = new User("john_doe", passwordEncoder.encode("pass123"), "john@example.com", true, "USER");
            User business = new User("biz_owner", passwordEncoder.encode("bizpass"), "biz@example.com", true, "BUSINESS_OWNER");
            userRepository.saveAll(List.of(user1, business));

            // STEP 3: Initialize default properties
            if (propertyRepository.count() == 0) {
                Property p1 = new Property("Modern Downtown Loft", 
                    "Spacious loft with amazing views of the city skyline. Perfect for young professionals who love urban living.", 
                    "123 Main St", "New York", "Manhattan", 850000, 2, 2,
                    "https://picsum.photos/id/104/400/300", List.of("https://picsum.photos/id/104/800/600"), user1);
                
                Property p2 = new Property("Cozy Family House", 
                    "Quiet neighborhood with great schools nearby. Includes a large backyard, a renovated kitchen, and plenty of natural light.", 
                    "45 Oak Ave", "Los Angeles", "LA County", 620000, 3, 2,
                    "https://picsum.photos/id/106/400/300", List.of("https://picsum.photos/id/106/800/600"), user1);
                
                Property p3 = new Property("Luxury Villa", 
                    "Beachfront paradise with private pool and direct access to the sea. Modern architecture with high-end finishes throughout.", 
                    "12 Seaview Blvd", "Miami", "Miami-Dade", 1950000, 5, 4,
                    "https://picsum.photos/id/107/400/300", List.of("https://picsum.photos/id/107/800/600"), existingAdmin.orElse(null));
                
                propertyRepository.saveAll(List.of(p1, p2, p3));
            }
        }
    }
}