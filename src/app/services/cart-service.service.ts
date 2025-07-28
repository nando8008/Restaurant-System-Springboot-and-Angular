import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface Cart {
  id: number;
  session_id: string;
  created_at: string;
  updated_at: string;
}

export interface CartItem {
  id?: number;
  food_id: number;
  name: string;
  quantity: number;
  unit_price: number;
  special_instructions?: string;
  added_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CartServiceService {
  private apiUrl = 'http://localhost:8080/cart'; // ✅ Change to your backend base URL if needed
  private sessionId = this.getOrCreateSessionId();

  private cart: Cart | null = null;
  private cartItemsSubject = new BehaviorSubject<CartItem[]>([]);
  cartItems$ = this.cartItemsSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadCart();
  }

  private getOrCreateSessionId(): string {
    let sessionId = localStorage.getItem('session_id');
    if (!sessionId) {
      sessionId = 'sess_' + Math.random().toString(36).substring(2, 15);
      localStorage.setItem('session_id', sessionId);
    }
    return sessionId;
  }

  private loadCart() {
    this.http.get<any>(`${this.apiUrl}/${this.sessionId}`).subscribe(res => {
      this.cart = res.cart;
      this.cartItemsSubject.next(res.items);
    });
  }

  addToCart(item: CartItem) {
    if (!this.cart) return;

    const payload = {
      food_id: item.food_id,
      quantity: 1,
      unit_price: item.unit_price,
      special_instructions: item.special_instructions || ''
    };

    this.http.post<CartItem>(`${this.apiUrl}/${this.cart.id}/items`, payload).subscribe(added => {
      const existing = this.cartItemsSubject.value.find(i => i.food_id === added.food_id);
      if (existing) {
        existing.quantity += 1;
      } else {
        added.name = item.name;
        this.cartItemsSubject.next([...this.cartItemsSubject.value, added]);
      }
    });
  }

  increaseQty(item: CartItem) {
    this.addToCart(item); // Add-to-cart merges quantity
  }

  decreaseQty(item: CartItem) {
    if (!this.cart) return;

    if (item.quantity <= 1) {
      const filtered = this.cartItemsSubject.value.filter(ci => ci.food_id !== item.food_id);
      this.cartItemsSubject.next(filtered);
      return;
    }

    const updated = { ...item, quantity: item.quantity - 1 };

    this.http.post<CartItem>(`${this.apiUrl}/${this.cart.id}/items`, updated).subscribe(() => {
      const updatedItems = this.cartItemsSubject.value.map(ci =>
        ci.food_id === item.food_id ? { ...ci, quantity: updated.quantity } : ci
      );
      this.cartItemsSubject.next(updatedItems);
    });
  }

  clearCart() {
    if (!this.cart) return;

    this.http.delete(`${this.apiUrl}/${this.cart.id}/items`).subscribe(() => {
      this.cartItemsSubject.next([]);
    });
  }

  getCartId(): number | null {
    return this.cart?.id ?? null;
  }
}
