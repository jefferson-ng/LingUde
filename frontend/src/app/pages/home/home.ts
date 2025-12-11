import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Footer } from '../../components/footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, Footer],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
