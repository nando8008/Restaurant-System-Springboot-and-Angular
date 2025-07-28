package com.example.restaurant.Service;

import com.example.restaurant.DTO.DeliveryGroupDto;
import com.example.restaurant.DTO.OrderLocationDto;
import com.example.restaurant.DTO.ServeGroupDto;
import com.example.restaurant.DTO.ServeItemDto;
import com.example.restaurant.Repository.OrderItemGroupRepository;
import com.example.restaurant.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class ServingService {

    @Autowired
    private OrderItemGroupRepository groupRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<ServeGroupDto> getPendingServeGroups() {
        List<Object[]> rows = groupRepository.getServeData();
        Map<Integer, ServeGroupDto> grouped = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Integer groupId = (Integer) row[0];

            ServeGroupDto group = grouped.computeIfAbsent(groupId, id -> {
                ServeGroupDto dto = new ServeGroupDto();
                dto.setGroupId(groupId);
                dto.setServeOrder((Integer) row[1]);
                dto.setCourseType((String) row[2]);
                dto.setTableNumber((Integer) row[3]);
                dto.setItems(new ArrayList<>());
                return dto;
            });

            ServeItemDto item = new ServeItemDto();
            item.setFoodName((String) row[4]);
            item.setQuantity(((Number) row[5]).intValue());
            item.setSpecialInstructions((String) row[6]);

            group.getItems().add(item);
        }

        return new ArrayList<>(grouped.values());
    }

    @Transactional
    public void markGroupAsDelivered(Integer groupId) {
        // 1. Mark group as delivered
        groupRepository.markGroupDelivered(groupId);

        // 2. Get order ID and check if all groups delivered
        Integer orderId = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"))
                .getOrder().getId();

        int pending = groupRepository.countUndeliveredGroups(orderId);
        if (pending == 0) {
            orderRepository.findById(orderId).ifPresent(order -> {
                order.setStatus("COMPLETED");
                order.setCompletionTime(LocalDateTime.now());
                orderRepository.save(order);
            });
        }
    }

    public List<DeliveryGroupDto> getPendingDeliveries() {
        List<Object[]> rows = groupRepository.getDeliveryData();
        Map<Integer, DeliveryGroupDto> grouped = new LinkedHashMap<>();

        for (Object[] row : rows) {
            Integer groupId = (Integer) row[0];

            DeliveryGroupDto group = grouped.computeIfAbsent(groupId, id -> {
                DeliveryGroupDto dto = new DeliveryGroupDto();
                dto.setGroupId(groupId);
                dto.setServeOrder((Integer) row[1]);
                dto.setCourseType((String) row[2]);
                dto.setCustomerId((String) row[3]);
                dto.setItems(new ArrayList<>());

                OrderLocationDto location = new OrderLocationDto();
                location.setName((String) row[7]);
                location.setPhone((String) row[8]);
                location.setStreet((String) row[9]);
                location.setApartment((String) row[10]);
                location.setCity((String) row[11]);
                location.setState((String) row[12]);
                location.setZip((String) row[13]);
                location.setDeliveryInstructions((String) row[14]);

                dto.setDeliveryLocation(location);

                return dto;
            });

            ServeItemDto item = new ServeItemDto();
            item.setFoodName((String) row[4]);
            item.setQuantity(((Number) row[5]).intValue());
            item.setSpecialInstructions((String) row[6]);

            group.getItems().add(item);
        }

        return new ArrayList<>(grouped.values());
    }

}
