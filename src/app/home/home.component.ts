import { Component } from '@angular/core';

import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private router: Router) {}
  
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

}
