package com.example.restaurant.Service;

import com.example.restaurant.Entity.Cart;
import com.example.restaurant.Entity.CartItem;
import com.example.restaurant.Entity.Food;
import com.example.restaurant.Repository.CartItemRepository;
import com.example.restaurant.Repository.CartRepository;
import com.example.restaurant.DTO.CartItemDto;
import com.example.restaurant.Repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private FoodRepository foodRepo;

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private CartItemRepository cartItemRepo;


    public Cart getOrCreateCart(String sessionId) {
        return cartRepo.findBySessionId(sessionId)
                .orElseGet(() -> cartRepo.save(new Cart(sessionId)));
    }

    public List<CartItemDto> getItemsDto(Long cartId) {
        return cartItemRepo.findCartItemDtosByCartId(cartId);
    }


//    public CartItem addItem(Integer cartId, CartItemDto dto) {
//        Cart cart = cartRepo.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart not found"));
//
//        Food food = foodRepo.findById(dto.getFoodId())
//                .orElseThrow(() -> new RuntimeException("Food not found"));
//
//
//        Optional<CartItem> existingOpt = cartItemRepo.findByCartIdAndFoodId(cartId, food.getId());
//
//        CartItem item = existingOpt.orElseGet(() -> new CartItem());
//        item.setCart(cart);
//        item.setFood(food);
//        item.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
//        item.setSpecialInstructions(dto.getSpecialInstructions());
//        item.setUnitPrice(dto.getUnitPrice());
//        item.setAddedAt(dto.getAddedAt() != null ? dto.getAddedAt() : java.time.LocalDateTime.now());
//
//        return cartItemRepo.save(item);
//    }


    public CartItem updateItemQuantity(Integer cartId, Integer foodId, int newQty) {
        CartItem item = cartItemRepo.findByCartIdAndFoodId(cartId, foodId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setQuantity(newQty);
        return cartItemRepo.save(item);
    }

    @Transactional
    public void removeItem(Integer cartId, Integer foodId) {
        cartItemRepo.deleteByCartIdAndFoodId(cartId, foodId);
    }
    @Transactional
    public void deleteCartAndItems(Integer cartId) {
        cartItemRepo.deleteByCartId(cartId);
        cartRepo.deleteById(cartId);
    }

    public CartItem addOrUpdateItem(String sessionId, CartItemDto itemDto) {
        Cart cart = getOrCreateCart(sessionId);

        Optional<CartItem> existingItemOpt = cartItemRepo.findByCartIdAndFoodId(cart.getId(), itemDto.getFoodId());

        if (itemDto.getQuantity() <= 0) {
            existingItemOpt.ifPresent(ci -> cartItemRepo.deleteById(ci.getId()));
            return null;
        }

        CartItem itemToSave = existingItemOpt.orElseGet(() -> {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setFood(foodRepo.findById(itemDto.getFoodId()).orElseThrow());
            newItem.setUnitPrice(itemDto.getUnitPrice());
            return newItem;
        });

        itemToSave.setQuantity(itemDto.getQuantity());
        itemToSave.setSpecialInstructions(itemDto.getSpecialInstructions());

        return cartItemRepo.save(itemToSave);
    }

    @Transactional
    public void updateAllItemFields(Integer cartId, List<CartItem> updatedItems) {
        for (CartItem updatedItem : updatedItems) {
            CartItem existingItem = cartItemRepo.findByCartIdAndFoodId(cartId, updatedItem.getFood().getId())
                    .orElseThrow(() -> new RuntimeException("Item not found for foodId: " + updatedItem.getFood().getId()));

            if (updatedItem.getQuantity() != null) {
                existingItem.setQuantity(updatedItem.getQuantity());
            }

            if (updatedItem.getSpecialInstructions() != null) {
                existingItem.setSpecialInstructions(updatedItem.getSpecialInstructions());
            }

            cartItemRepo.save(existingItem);
        }
    }
}
