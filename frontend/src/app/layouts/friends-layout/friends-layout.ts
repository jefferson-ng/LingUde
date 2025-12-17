import { Component } from '@angular/core';
import {FriendsNav} from '../../components/friends-nav/friends-nav';
import {RouterOutlet} from '@angular/router';
import {TranslocoDirective} from '@jsverse/transloco';

@Component({
  imports: [
    FriendsNav,
    RouterOutlet,
    TranslocoDirective
  ],
  templateUrl: './friends-layout.html',
  styleUrl: './friends-layout.css'
})
export class FriendsLayout {

}
