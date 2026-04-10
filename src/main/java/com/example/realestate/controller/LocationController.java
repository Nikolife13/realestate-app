package com.example.realestate.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    private static final Map<String, List<String>> CITIES_BY_COUNTY = new HashMap<>();
    static {
        CITIES_BY_COUNTY.put("Manhattan", List.of("New York", "Harlem", "SoHo"));
        CITIES_BY_COUNTY.put("LA County", List.of("Los Angeles", "Santa Monica", "Beverly Hills"));
        CITIES_BY_COUNTY.put("Miami-Dade", List.of("Miami", "Miami Beach", "Hialeah"));
        CITIES_BY_COUNTY.put("Cook County", List.of("Chicago", "Evanston", "Oak Park"));
    }

    @GetMapping("/counties")
    public List<String> getCounties() {
        return new ArrayList<>(CITIES_BY_COUNTY.keySet());
    }

    @GetMapping("/cities")
    public List<String> getCities(@RequestParam String county) {
        return CITIES_BY_COUNTY.getOrDefault(county, List.of());
    }
}