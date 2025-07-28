import { Component } from '@angular/core';
import { ActivatedRoute, Route, Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { HttpClient } from '@angular/common/http';
@Component({
  selector: 'app-producer',
  imports: [RouterOutlet,RouterLink, FormsModule, MatInputModule, CommonModule, MatFormFieldModule, MatCheckboxModule, MatButtonModule],
  templateUrl: './producer.component.html',
  styleUrl: './producer.component.css'
})
export class ProducerComponent {
  inputs = {
    json: {
      selected: false,   // checkbox bound to this controls visibility and selection
      data: null as any  // will hold parsed JSON content after file upload
    },
    excel: {
      selected: false,
      data: null as any  // will hold parsed Excel data after file upload
    },
    sql: {
      selected: false,
      query: '',
      host: '',
      port: '',
      db: '',
      user: '',
      password: ''
    },
    kafka: {
      topic: '',
      bootstrapServers: '',
      apiKey: '',
      apiSecret: '',
      groupName:'',
      useSSL: false,
      additionalConfig: ''
    }
  };
  isProducer:boolean = true;
  constructor(private http: HttpClient,private route:Router) { 
    console.log(this.route.url);
    this.isProducer = route.url.toString() != '/consumer'
  }

  handleFileUpload(event: Event, target: 'json' | 'excel') {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;

    // Store file object
    this.inputs[target].data = file;
  }

  onContinue() {
    const formData = new FormData();
if (this.inputs.json.data) formData.append('jsonFile', this.inputs.json.data);
if (this.inputs.excel.data) formData.append('excelFile', this.inputs.excel.data);
if(this.inputs.sql.selected) formData.append('sql', new Blob([JSON.stringify(this.inputs.sql)], { type: 'application/json' }));
formData.append('kafkaConfig', new Blob([JSON.stringify(this.inputs.kafka)], { type: 'application/json' }));
    // Send payload to your backend API for Kafka processing
    console.log('Sending payload to Kafka Producer:', formData);
    sessionStorage.setItem("topic",this.inputs.kafka.topic);
    sessionStorage.setItem("groupId",this.inputs.kafka.groupName);

    this.http.post('http://localhost:8082/kafka/send-to-kafka', formData).subscribe((data:any)=>{
      console.log("success post the data")
    },(error:any)=>{
      console.log("failed to send the data")
    });
  }
}
