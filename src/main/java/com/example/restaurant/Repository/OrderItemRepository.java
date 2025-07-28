package com.example.restaurant.Repository;
import com.example.restaurant.Entity.Order;
import com.example.restaurant.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByGroupId(Integer groupId);
    @Query("SELECT i FROM OrderItem i WHERE i.group.order = :order")
    List<OrderItem> findByOrder(@Param("order") Order order);

    @Modifying
    @Query("UPDATE OrderItem i SET i.isPrepared = true WHERE i.id = :itemId")
    void markPrepared(@Param("itemId") Integer itemId);

    @Query("SELECT COUNT(i) FROM OrderItem i WHERE i.group.id = :groupId AND i.isPrepared = false")
    int countUnpreparedItemsInGroup(@Param("groupId") Integer groupId);
}