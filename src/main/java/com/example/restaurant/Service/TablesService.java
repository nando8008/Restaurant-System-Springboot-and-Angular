package com.example.restaurant.Service;


import com.example.restaurant.Entity.Tables;
import com.example.restaurant.Repository.TablesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TablesService {
    @Autowired
    private TablesRepository tablesRepository;

    public List<Tables> getAllTables(){ return tablesRepository.findAll();}

    public Tables updateTableStatus(Integer id, Tables.Status status) {
        Tables table = tablesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        table.setStatus(status);
        return tablesRepository.save(table);
    }

}
