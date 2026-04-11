package com.example.realestate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing a real estate property listing.
 */
@Entity
public class Property {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String address;
    private String city;
    private String county;
    private double price;
    private int rooms;
    private int bathrooms;
    private int rating;
    private String mainImageUrl;

    // Collection of additional gallery image URLs stored in a separate table
    @ElementCollection
    private List<String> imageUrls;

    // Relationship linking the property to its owner (User)
    @ManyToOne
    @JoinColumn(name = "owner_id")
    // Prevents recursion and hides sensitive fields when serializing User data
    @JsonIgnoreProperties({"properties", "password", "email", "emailConfirmed"}) 
    private User owner;

    // Constructors
    public Property() {}

    public Property(String title, String description, String address, String city, String county,
                    double price, int rooms, int bathrooms, String mainImageUrl, List<String> imageUrls, User owner) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.city = city;
        this.county = county;
        this.price = price;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.mainImageUrl = mainImageUrl;
        this.imageUrls = imageUrls;
        this.owner = owner;
        this.rating = 0; // Default rating starts at 0
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCounty() { return county; }
    public void setCounty(String county) { this.county = county; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }

    public int getBathrooms() { return bathrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}