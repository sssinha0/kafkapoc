import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-consumer',
  imports: [CommonModule],
  templateUrl: './consumer.component.html',
  styleUrl: './consumer.component.css'
})
export class ConsumerComponent {
  messages: any[] |undefined= [];
  constructor(private http:HttpClient,private route:Router){
    console.log(route.url)
  }
  async ngOnInit() {
    await this.getMessages(sessionStorage.getItem("topic")??'new_test',sessionStorage.getItem("groupId")??'student')
    console.log(this.messages)
  }
  async getMessages(topic: string, groupId: string) {
    this.messages= [];
    try {
      console.log('Fetching messages...');
      const url = `http://localhost:8082/kafka/messages?topic=${topic}&groupId=${groupId}`;
      // Using toPromise() - note: deprecated in RxJS 7+
      const data = await this.http.get<any[]>(url).toPromise();

      console.log('Data received:', data);
      this.messages = data;
      console.log('Messages updated:', this.messages);
    } catch (error) {
      console.error('Error fetching messages:', error);
      // Handle error
    }
  }
}
