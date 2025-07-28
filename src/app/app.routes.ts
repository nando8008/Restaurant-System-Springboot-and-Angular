import { Routes,RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { MenuComponent } from './menu/menu.component';
import { OrderComponent } from './order/order.component';
import { CartComponent } from './cart/cart.component';

export const routes: Routes = [
    { path:'home' , component:HomeComponent},
    { path:'menu' , component:MenuComponent},
    { path:'order' , component:OrderComponent},
    { path:'cart', component:CartComponent}
];
