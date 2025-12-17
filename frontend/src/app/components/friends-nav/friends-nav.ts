import {Component, signal} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { CommonModule} from '@angular/common';

@Component({
  selector: 'app-friends-nav',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslocoDirective, CommonModule],
  templateUrl: './friends-nav.html',
  styleUrl: './friends-nav.css'
})
export class FriendsNav {

  requestCount = signal(0);
}
