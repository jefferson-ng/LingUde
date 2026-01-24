import {Component, signal} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';
import { CommonModule} from '@angular/common';
import { ShareButtonComponent } from '../share-button/share-button';

@Component({
  selector: 'app-friends-nav',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslocoDirective, CommonModule, ShareButtonComponent],
  templateUrl: './friends-nav.html',
  styleUrl: './friends-nav.css'
})
export class FriendsNav {

  requestCount = signal(0);
}
