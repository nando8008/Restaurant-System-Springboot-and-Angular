import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartServiceService, CartItem } from '../services/cart-service.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];

  constructor(private cartService: CartServiceService) {}

  ngOnInit(): void {
    this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });
  }

  updateSpecialInstructions(item: CartItem, value: string): void {
    item.special_instructions = value;
    // Optionally update backend here
    // this.cartService.updateItemInstructions(item);
  }

  getTotal(): number {
    return this.cartItems.reduce(
      (sum, item) => sum + item.unit_price * item.quantity,
      0
    );
  }

  trackByFoodId(index: number, item: CartItem): number {
    return item.food_id;
  }
}
