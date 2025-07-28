import { Routes } from '@angular/router';
import { ConsumerComponent } from './consumer/consumer.component';
import { AppComponent } from './app.component';
import { ProducerComponent } from './producer/producer.component';

export const routes: Routes = [
    {path:'',component:ProducerComponent},
    {path:'consumer',component  :ConsumerComponent}
];
