package com.example.restaurant.Service;
import com.example.restaurant.DTO.KitchenDto;
import com.example.restaurant.Entity.Kitchen;
import com.example.restaurant.Entity.OrderItem;
import com.example.restaurant.Entity.OrderItemGroup;
import com.example.restaurant.Repository.KitchenRepository;
import com.example.restaurant.Repository.OrderItemRepository;
import com.example.restaurant.Repository.OrderItemGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KitchenService {

    @Autowired
    private KitchenRepository kitchenRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private OrderItemGroupRepository orderItemGroupRepo;

    public List<Kitchen> getAll() {
        return kitchenRepo.getKitchenData();
    }
    private int getServeOrderPriority(String courseType) {
        switch (courseType.toUpperCase()) {
            case "STARTERS":
            case "FULL":
                return 1;
            case "MAIN COURSE":
                return 2;
            case "DESSERT":
            case "BEVERAGES":
                return 3;
            default:
                return 99;
        }
    }

    public Kitchen updateStatus(Integer kitchenId, String status,String userId) {
        Kitchen kitchen = kitchenRepo.findById(kitchenId).orElseThrow();
        kitchen.setStatus(status);
        LocalDateTime now = LocalDateTime.now();

        if ("PREPARING".equalsIgnoreCase(status)) {
            kitchen.setStartedAt(now);
            kitchen.setChefId(userId);
        }

        if ("READY".equalsIgnoreCase(status)) {
            kitchen.setCompletedAt(now);

            OrderItem item = kitchen.getItem();
            item.setIsPrepared(true);
            orderItemRepo.save(item);

            Integer groupId = item.getGroup().getId();
            boolean allPrepared = orderItemRepo.findByGroupId(groupId)
                    .stream()
                    .allMatch(OrderItem::getIsPrepared);

            if (allPrepared) {
                OrderItemGroup group = item.getGroup();
                group.setIsReady(true);

                if (group.getServeOrder() == 0) {
                    int serveOrder = getServeOrderPriority(group.getCourseType());

                    group.setServeOrder(serveOrder);
                }

                orderItemGroupRepo.save(group);

                orderItemGroupRepo.save(group);
            }
        }

        return kitchenRepo.save(kitchen);
    }

    public List<KitchenDto> getAllDTO() {
        return kitchenRepo.getKitchenData().stream().map(k -> {
            KitchenDto dto = new KitchenDto();
            dto.setId(k.getId());
            dto.setStatus(k.getStatus());
            dto.setStartedAt(k.getStartedAt());
            dto.setCompletedAt(k.getCompletedAt());
            dto.setChefId(k.getChefId());

            OrderItem item = k.getItem();
            dto.setQuantity(item.getQuantity());
            dto.setSpecialInstructions(item.getSpecialInstructions());
            dto.setIsPrepared(item.getIsPrepared());
            dto.setFoodName(item.getFood().getName());

            OrderItemGroup group = item.getGroup();
            dto.setCourseType(group.getCourseType());
            dto.setServeOrder(group.getServeOrder());
            dto.setIsReady(group.getIsReady());
            dto.setDelivered(group.getDelivered());

            if (group.getOrder() != null && group.getOrder().getTable() != null) {
                dto.setTableNumber(group.getOrder().getTable().getTableNumber());
            }

            return dto;
        }).collect(Collectors.toList());
    }

}