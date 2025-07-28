package com.example.restaurant.Controller;


import com.example.restaurant.DTO.DeliveryGroupDto;
import com.example.restaurant.DTO.ServeGroupDto;
import com.example.restaurant.Service.ServingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/serve")
public class ServingController {

    @Autowired
    private ServingService servingService;

    @GetMapping("/pending")
    public List<ServeGroupDto> getPendingServeGroups() {
        return servingService.getPendingServeGroups();
    }

    @GetMapping("/deliveries")
    public List<DeliveryGroupDto> getPendingDeliveries() {
        return servingService.getPendingDeliveries();
    }

    @PostMapping("/deliver/{groupId}")
    public ResponseEntity<Map<String, String>> markAsDelivered(@PathVariable Integer groupId) {
        servingService.markGroupAsDelivered(groupId);
        return ResponseEntity.ok(Map.of("message", "Group marked as delivered"));
    }
}
