import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MenuServiceService, Food } from '../services/menu-service.service';
import { forkJoin } from 'rxjs';


@Component({
  standalone: true,
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
  imports: [CommonModule]
})
export class MenuComponent implements OnInit {
  categories = ['Starters', 'Pizzas', 'Burgers', 'Bread', 'Curries', 'Rice', 'Dessert', 'Beverages'];
  menuData: { name: string; items: Food[] }[] = [];
  quantityMap: { [foodId: number]: number } = {}; 

  constructor(private menuService: MenuServiceService) {}

  ngOnInit(): void {
    const observables = this.categories.map(category =>
      this.menuService.getFoodByCategory(category)
    );

    forkJoin(observables).subscribe({
      next: results => {
        this.menuData = this.categories.map((category, index) => ({
          name: category,
          items: results[index]
        }));
      },
      error: err => console.error('Failed to load menu:', err)
    });
  }
  getQuantity(id: number): number {
    return this.quantityMap[id] || 0;
  }

  addToCart(item: Food): void {
    this.quantityMap[item.id] = 1;
  }

  increaseQty(item: Food): void {
    this.quantityMap[item.id] = (this.quantityMap[item.id] || 0) + 1;
  }

  decreaseQty(item: Food): void {
    if (this.quantityMap[item.id] > 1) {
      this.quantityMap[item.id]--;
    } else {
      delete this.quantityMap[item.id];
    }
  }
  
}
