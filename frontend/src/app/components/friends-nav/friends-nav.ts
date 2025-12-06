import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-friends-nav',
  imports: [RouterLink, RouterLinkActive, TranslocoDirective],
  templateUrl: './friends-nav.html',
  styleUrl: './friends-nav.css'
})
export class FriendsNav {

}
