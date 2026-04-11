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
        // STEP 0: Force database migration to ensure 'description' column is TEXT
        // This is a workaround for Render's free tier shell limitations
        try {
            jdbcTemplate.execute("ALTER TABLE property ALTER COLUMN description TYPE TEXT");
            System.out.println("Database migration: Property description changed to TEXT successfully.");
        } catch (Exception e) {
            System.out.println("Migration note: Column might already be TEXT or table doesn't exist yet. " + e.getMessage());
        }

        // STEP 1: Initialize default users if the database is empty
        if (userRepository.count() == 0) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@example.com", true, "ADMIN");
            User user1 = new User("john_doe", passwordEncoder.encode("pass123"), "john@example.com", true, "USER");
            User business = new User("biz_owner", passwordEncoder.encode("bizpass"), "biz@example.com", true, "BUSINESS_OWNER");
            userRepository.saveAll(List.of(admin, user1, business));

            // STEP 2: Initialize default properties if the database is empty
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
                    "https://picsum.photos/id/107/400/300", List.of("https://picsum.photos/id/107/800/600"), admin);
                
                propertyRepository.saveAll(List.of(p1, p2, p3));
            }
        }
    }
}