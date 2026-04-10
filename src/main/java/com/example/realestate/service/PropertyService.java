package com.example.realestate.service;

import com.example.realestate.dto.PropertyCreateDto;
import com.example.realestate.model.Property;
import com.example.realestate.model.User;
import com.example.realestate.repository.PropertyRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> search(Integer rooms, Double minPrice, Double maxPrice, String city, String county) {
        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (rooms != null) predicates.add(cb.equal(root.get("rooms"), rooms));
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            if (city != null && !city.isEmpty()) predicates.add(cb.equal(root.get("city"), city));
            if (county != null && !county.isEmpty()) predicates.add(cb.equal(root.get("county"), county));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return propertyRepository.findAll(spec);
    }

    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public Property create(PropertyCreateDto dto, User owner) {
        Property prop = new Property();
        prop.setTitle(dto.getTitle());
        prop.setDescription(dto.getDescription());
        prop.setAddress(dto.getAddress());
        prop.setCity(dto.getCity());
        prop.setCounty(dto.getCounty());
        prop.setPrice(dto.getPrice());
        prop.setRooms(dto.getRooms());
        prop.setBathrooms(dto.getBathrooms());
        prop.setMainImageUrl(dto.getMainImageUrl());
        prop.setImageUrls(dto.getImageUrls());
        prop.setOwner(owner);
        prop.setRating(0);
        return propertyRepository.save(prop);
    }

    public Property update(Property prop, PropertyCreateDto dto) {
        prop.setTitle(dto.getTitle());
        prop.setDescription(dto.getDescription());
        prop.setAddress(dto.getAddress());
        prop.setCity(dto.getCity());
        prop.setCounty(dto.getCounty());
        prop.setPrice(dto.getPrice());
        prop.setRooms(dto.getRooms());
        prop.setBathrooms(dto.getBathrooms());
        prop.setMainImageUrl(dto.getMainImageUrl());
        prop.setImageUrls(dto.getImageUrls());
        return propertyRepository.save(prop);
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }
}