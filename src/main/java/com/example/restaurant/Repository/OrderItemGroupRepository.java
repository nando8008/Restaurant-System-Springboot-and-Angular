package com.example.restaurant.Repository;

import com.example.restaurant.Entity.OrderItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemGroupRepository extends JpaRepository<OrderItemGroup, Integer> {
    @Query(value = "SELECT COALESCE(MAX(serve_order), 0) FROM order_item_groups WHERE order_id = :orderId", nativeQuery = true)
    int findMaxServeOrderByOrderId(@Param("orderId") Integer orderId);

    @Modifying
    @Query("UPDATE OrderItemGroup g SET g.isReady = true, g.serveOrder = :serveOrder WHERE g.id = :groupId")
    void markGroupReady(@Param("groupId") Integer groupId, @Param("serveOrder") int serveOrder);

    @Query(value = """
    SELECT
        g.id as groupId,
        g.serve_order as serveOrder,
        g.course_type as courseType,
        t.table_number as tableNumber,
        f.name as foodName,
        i.quantity,
        i.special_instructions as specialInstructions
    FROM order_item_groups g
    JOIN orders o ON g.order_id = o.id
    JOIN tables t ON o.table_id = t.id
    JOIN order_items i ON g.id = i.group_id
    JOIN food f ON i.food_id = f.id
    WHERE g.is_ready = true
      AND g.delivered = false
      AND NOT EXISTS (
          SELECT 1 FROM order_item_groups g2
          WHERE g2.order_id = g.order_id
            AND g2.serve_order < g.serve_order
            AND g2.is_ready = false
      )
    ORDER BY g.serve_order ASC, g.id ASC""", nativeQuery = true)
    List<Object[]> getServeData();

    @Modifying
    @Query("UPDATE OrderItemGroup g SET g.delivered = true WHERE g.id = :groupId")
    void markGroupDelivered(@Param("groupId") Integer groupId);

    @Query("SELECT COUNT(g) FROM OrderItemGroup g WHERE g.order.id = :orderId AND g.delivered = false")
    int countUndeliveredGroups(@Param("orderId") Integer orderId);

    @Query(value = """
    SELECT
        g.id as groupId,
        g.serve_order as serveOrder,
        g.course_type as courseType,
        o.order_by_id as customerId,
        f.name as foodName,
        i.quantity,
        i.special_instructions,

        l.name as loc_name,
        l.phone,
        l.street,
        l.apartment,
        l.city,
        l.state,
        l.zip,
        l.delivery_instructions

    FROM order_item_groups g
    JOIN orders o ON g.order_id = o.id
    JOIN order_items i ON g.id = i.group_id
    JOIN food f ON i.food_id = f.id
    JOIN order_location l ON o.id = l.order_id

    WHERE g.is_ready = true
      AND g.delivered = false
      AND o.table_id IS NULL
      AND NOT EXISTS (
          SELECT 1 FROM order_item_groups g2
          WHERE g2.order_id = g.order_id
            AND g2.serve_order < g.serve_order
            AND g2.is_ready = false
      )

    ORDER BY g.serve_order ASC, g.id ASC
""", nativeQuery = true)
    List<Object[]> getDeliveryData();


}