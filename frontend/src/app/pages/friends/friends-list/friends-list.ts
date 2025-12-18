import { Component } from '@angular/core';
import { FriendsNav } from '../../../components/friends-nav/friends-nav';
import { FriendsList as FriendsListComponent } from '../../../components/friends-list/friends-list';

@Component({
  selector: 'app-friends-list-page',
  standalone: true,
  imports: [FriendsNav, FriendsListComponent],
  templateUrl: './friends-list.html',
  styleUrl: './friends-list.css'
})
export class FriendsListPage {

}
