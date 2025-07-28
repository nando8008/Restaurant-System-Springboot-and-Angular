package com.example.restaurant.Controller;

import com.example.restaurant.Entity.Cart;
import com.example.restaurant.Entity.CartItem;
import com.example.restaurant.Service.CartService;
import com.example.restaurant.DTO.CartItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getCart(@PathVariable String sessionId) {
        Cart cart = cartService.getOrCreateCart(sessionId);
        List<CartItemDto> items = cartService.getItemsDto(cart.getId().longValue());
        return ResponseEntity.ok(Map.of("cart", cart, "items", items));
    }

    //@PostMapping("/{cartId}/items")
    //public ResponseEntity<CartItem> addItem(@PathVariable Integer cartId, @RequestBody CartItemDto itemDto) {
    //    CartItem saved = cartService.addItem(cartId, itemDto);
    //    return ResponseEntity.ok(saved);
    //}

    @PutMapping("/{cartId}/items/{foodId}")
    public ResponseEntity<CartItem> updateItemQuantity(
            @PathVariable Integer cartId,
            @PathVariable Integer foodId,
            @RequestBody Integer newQty) {
        return ResponseEntity.ok(cartService.updateItemQuantity(cartId, foodId, newQty));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Integer cartId) {
        cartService.deleteCartAndItems(cartId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cartId}/items/{foodId}")
    public ResponseEntity<?> removeItem(
            @PathVariable Integer cartId,
            @PathVariable Integer foodId) {
        cartService.removeItem(cartId, foodId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/item")
    public ResponseEntity<?> addOrUpdateItem(
            @PathVariable String sessionId,
            @RequestBody CartItemDto itemDto) {

        CartItem updatedItem = cartService.addOrUpdateItem(sessionId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @PutMapping("/{cartId}/items")
    public ResponseEntity<?> updateAllItemFields(
            @PathVariable Integer cartId,
            @RequestBody List<CartItem> updatedItems) {

        cartService.updateAllItemFields(cartId, updatedItems);
        return ResponseEntity.ok().build();
    }

}
