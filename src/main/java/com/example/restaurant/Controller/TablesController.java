package com.example.restaurant.Controller;


import com.example.restaurant.Entity.Tables;
import com.example.restaurant.Service.TablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/tables")
public class TablesController {
    @Autowired
    public TablesService tablesService;

    @GetMapping("/all")
    public List<Tables> getAllTables(){
        return tablesService.getAllTables();
    }

    @PutMapping("/update-status/{id}")
    public Tables updateTableStatus(@PathVariable Integer id, @RequestParam String status) {
        Tables.Status enumStatus;
        try {
            enumStatus = Tables.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status);
        }

        return tablesService.updateTableStatus(id, enumStatus);
    }
}
