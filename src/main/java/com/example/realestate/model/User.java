package com.example.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entity representing a system user, implementing Spring Security's UserDetails.
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @JsonIgnore // Ensures the password is never sent in API responses
    private String password;

    @Column(unique = true)
    private String email;

    private boolean emailConfirmed;
    private String role;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relationship linking a user to multiple properties they own
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonIgnore // Prevents circular reference during Property serialization
    private List<Property> properties;

    // Constructors
    public User() {}

    public User(String username, String password, String email, boolean emailConfirmed, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailConfirmed = emailConfirmed;
        this.role = role;
    }

    // Standard Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEmailConfirmed() { return emailConfirmed; }
    public void setEmailConfirmed(boolean emailConfirmed) { this.emailConfirmed = emailConfirmed; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Property> getProperties() { return properties; }
    public void setProperties(List<Property> properties) { this.properties = properties; }

    /**
     * Spring Security Methods
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Formats the role as a Spring Security authority (e.g., ROLE_ADMIN)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    
    @Override public boolean isEnabled() { return emailConfirmed; }
}




