package com.example.restaurant.Controller;

import com.example.restaurant.DTO.KitchenDto;
import com.example.restaurant.Entity.Kitchen;
import com.example.restaurant.Service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/kitchen")
public class KitchenController {
    @Autowired
    private KitchenService kitchenService;

    @GetMapping("/all")
    public ResponseEntity<List<KitchenDto>>getAllKitchenItems() {
        List<KitchenDto> items = kitchenService.getAllDTO();
        return ResponseEntity.ok(items);
    }


    @PutMapping("/update-status/{id}")
    public ResponseEntity<Kitchen> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam String userId) {

        Kitchen updated = kitchenService.updateStatus(id, status, userId);
        return ResponseEntity.ok(updated);
    }
}
