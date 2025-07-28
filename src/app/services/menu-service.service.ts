import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Food {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  isAvailable: boolean;   
  veg: 'VEG' | 'NON-VEG'; 
  createdAt: string;  
}

@Injectable({
  providedIn: 'root'
})
export class MenuServiceService {
  private baseUrl='http://localhost:8080';
  private baseFoodUrl = this.baseUrl+'/food';

  constructor(private http: HttpClient) {}

  getFoodByCategory(category: string): Observable<Food[]> {
    return this.http.get<Food[]>(`${this.baseFoodUrl}/${category}`);
  }
}
