package com.example.realestate.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class PropertyCreateDto {
    @NotBlank private String title;
    private String description;
    @NotBlank private String address;
    @NotBlank private String city;
    @NotBlank private String county;
    @Positive private double price;
    @Min(1) @Max(10) private int rooms;
    @Min(1) @Max(10) private int bathrooms;
    private String mainImageUrl;
    private List<String> imageUrls;

    // getters/setters
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
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}