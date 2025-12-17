import { Component } from '@angular/core';
import { FriendsNav } from '../../../components/friends-nav/friends-nav';
import { FriendSearch} from '../../../components/friend-search/friend-search';

@Component({
  selector: 'app-friends-search-page',
  imports: [FriendsNav, FriendSearch],
  templateUrl: './friends-search.html',
  styleUrl: './friends-search.css'
})
export class FriendsSearch {

}
