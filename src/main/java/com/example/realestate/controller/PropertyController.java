package com.example.realestate.controller;

import com.example.realestate.dto.PropertyCreateDto;
import com.example.realestate.model.Property;
import com.example.realestate.model.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PropertyRepository propertyRepository;

    @GetMapping
    public List<Property> searchProperties(
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String county) {
        return propertyService.search(rooms, minPrice, maxPrice, city, county);
    }

    @GetMapping("/{id}")
    public Property getProperty(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Property createProperty(@Valid @RequestBody PropertyCreateDto dto, @AuthenticationPrincipal User currentUser) {
        return propertyService.create(dto, currentUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Property updateProperty(@PathVariable Long id, @Valid @RequestBody PropertyCreateDto dto, @AuthenticationPrincipal User currentUser) {
        Property prop = propertyService.findById(id);
        if (!currentUser.getRole().equals("ADMIN") && !prop.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not your property");
        }
        return propertyService.update(prop, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void deleteProperty(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Property prop = propertyService.findById(id);
        if (!currentUser.getRole().equals("ADMIN") && !prop.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Not your property");
        }
        propertyService.delete(id);
    }

    @PostMapping("/{id}/boost")
    @PreAuthorize("hasRole('ADMIN')")
    public Property boostProperty(@PathVariable Long id) {
        Property prop = propertyService.findById(id);
        prop.setRating(prop.getRating() + 1);
        return propertyRepository.save(prop);
    }
}