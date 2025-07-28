package com.example.restaurant.Service;

import com.example.restaurant.DTO.*;
import com.example.restaurant.Entity.*;
import com.example.restaurant.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemGroupRepository orderItemGroupRepository;

    @Autowired
    private KitchenRepository kitchenRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private TablesRepository tablesRepository;

    @Autowired
    private OrderLocationRepository orderLocationRepository;

    @Transactional
    public Order placeOrderFromCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 1. Create Order
        Order order = new Order();
        order.setOrderById(sessionId);
        order.setOrderByType("customer");
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PLACED");
        order = orderRepository.save(order);

        // 2. Create OrderItemGroup
        OrderItemGroup group = new OrderItemGroup();
        group.setOrder(order);
        group.setCourseType("Full");
        group.setServeOrder(1);
        group.setIsReady(false);
        group.setDelivered(false);
        group = orderItemGroupRepository.save(group);

        // 3. Move items from cart to order
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setGroup(group);
            orderItem.setFood(cartItem.getFood());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
            orderItem.setIsPrepared(false);
            orderItem = orderItemRepository.save(orderItem);

            // Add to kitchen
            Kitchen kitchenItem = new Kitchen();
            kitchenItem.setItem(orderItem);
            kitchenItem.setStatus("PENDING");
            kitchenItem.setStartedAt(null);
            kitchenItem.setCompletedAt(null);
            kitchenItem.setChefId(null);
            kitchenRepository.save(kitchenItem);

            // Add to subtotal
            BigDecimal itemTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        // 4. Billing
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05));
        BigDecimal service = subtotal.multiply(BigDecimal.valueOf(0));
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(service).subtract(discount);

        Billing bill = new Billing();
        bill.setOrder(order);
        bill.setPlacedById(sessionId);
        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setServiceCharge(service);
        bill.setDiscount(discount);
        bill.setTotal(total);
        bill.setPaymentStatus("PAID");
        bill.setPaidAt(LocalDateTime.now());
        billingRepository.save(bill);

        // 5. Clear Cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cartRepository.delete(cart);

        return order;
    }


    @Transactional
    public Order placeOrder(String orderById, OrderRequestDto dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item.");
        }

        // 1. Create Order
        Order order = new Order();
        order.setOrderById(orderById);
        order.setOrderByType(dto.getOrderByType());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("PLACED");

        if ("waiter".equalsIgnoreCase(dto.getOrderByType())) {
            if (dto.getTableId() == null) {
                throw new RuntimeException("Table ID is required for waiter orders.");
            }
            Tables table = tablesRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new RuntimeException("Table ID not found."));
            order.setTable(table);
        }

        order = orderRepository.save(order);

        // 2. Group items by courseType
        Map<String, List<OrderRequestDto.ItemDTO>> groupedItems = dto.getItems().stream()
                .collect(Collectors.groupingBy(item -> item.getCourseType() != null ? item.getCourseType() : "Full"));

        for (Map.Entry<String, List<OrderRequestDto.ItemDTO>> groupEntry : groupedItems.entrySet()) {
            String courseType = groupEntry.getKey();
            List<OrderRequestDto.ItemDTO> items = groupEntry.getValue();

            // 3. Create OrderItemGroup
            OrderItemGroup group = new OrderItemGroup();
            group.setOrder(order);
            group.setCourseType(courseType);
            group.setServeOrder(0);
            group.setIsReady(false);
            group.setDelivered(false);
            group = orderItemGroupRepository.save(group);

            // 4. Create OrderItems and Kitchen entries
            for (OrderRequestDto.ItemDTO dtoItem : items) {
                Food food = foodRepository.findById(dtoItem.getFoodId())
                        .orElseThrow(() -> new RuntimeException("Food item not found: ID " + dtoItem.getFoodId()));

                OrderItem orderItem = new OrderItem();
                orderItem.setGroup(group);
                orderItem.setFood(food);
                orderItem.setQuantity(dtoItem.getQuantity());
                orderItem.setSpecialInstructions(dtoItem.getSpecialInstructions());
                orderItem.setIsPrepared(false);
                orderItem = orderItemRepository.save(orderItem);

                Kitchen kitchen = new Kitchen();
                kitchen.setItem(orderItem);
                kitchen.setStatus("PENDING");
                kitchen.setStartedAt(null);
                kitchen.setCompletedAt(null);
                kitchen.setChefId(null);
                kitchenRepository.save(kitchen);
            }
        }

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderDetailsDto> getCustomerOrders(String orderById) {
        List<Order> orders = orderRepository.findByCustomerId(orderById);

        return orders.stream().map(order -> {
            OrderDetailsDto dto = new OrderDetailsDto();
            dto.setId(order.getId());
            dto.setOrderById(order.getOrderById());
            dto.setOrderByType(order.getOrderByType());
            dto.setTableId(order.getTable() != null ? order.getTable().getId() : null);
            dto.setOrderTime(order.getOrderTime());
            dto.setCompletionTime(order.getCompletionTime());
            dto.setStatus(order.getStatus());

            List<OrderItemGroupDto> groupDtos = order.getOrderItemGroups().stream().map(group -> {
                OrderItemGroupDto groupDto = new OrderItemGroupDto();
                groupDto.setId(group.getId());
                groupDto.setCourseType(group.getCourseType());
                groupDto.setServeOrder(group.getServeOrder());
                groupDto.setIsReady(group.getIsReady());
                groupDto.setDelivered(group.getDelivered());

                List<OrderItemDto> itemDtos = group.getOrderItems().stream().map(item -> {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setSpecialInstructions(item.getSpecialInstructions());
                    itemDto.setIsPrepared(item.getIsPrepared());

                    FoodDto foodDto = new FoodDto();
                    foodDto.setId(item.getFood().getId());
                    foodDto.setName(item.getFood().getName());
                    foodDto.setPrice(item.getFood().getPrice());
                    foodDto.setVeg(item.getFood().getVeg());

                    itemDto.setFood(foodDto);
                    return itemDto;
                }).collect(Collectors.toList());

                groupDto.setItems(itemDtos);
                return groupDto;
            }).collect(Collectors.toList());

            dto.setGroups(groupDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    public void saveLocationForOrder(OrderLocationDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderLocation loc = new OrderLocation();
        loc.setOrder(order);
        loc.setName(dto.getName());
        loc.setPhone(dto.getPhone());
        loc.setStreet(dto.getStreet());
        loc.setApartment(dto.getApartment());
        loc.setCity(dto.getCity());
        loc.setState(dto.getState());
        loc.setZip(dto.getZip());
        loc.setDeliveryInstructions(dto.getDeliveryInstructions());

        orderLocationRepository.save(loc);
    }



}
