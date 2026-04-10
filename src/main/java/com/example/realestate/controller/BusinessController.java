package com.example.realestate.controller;

import com.example.realestate.dto.MonthlyRegistrationDto;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/business")
@PreAuthorize("hasRole('BUSINESS_OWNER')")
@CrossOrigin(origins = "*")
public class BusinessController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/registrations-per-month")
    public List<MonthlyRegistrationDto> getRegistrationsPerMonth(@RequestParam int year) {
        List<Object[]> results = userRepository.getRegistrationsByMonth(year);
        List<MonthlyRegistrationDto> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) list.add(new MonthlyRegistrationDto(i, 0L));
        for (Object[] row : results) {
            int month = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            list.get(month - 1).setCount(count);
        }
        return list;
    }
}