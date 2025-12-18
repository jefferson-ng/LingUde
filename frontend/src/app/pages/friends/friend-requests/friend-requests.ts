import { Component } from '@angular/core';
import { FriendsNav } from '../../../components/friends-nav/friends-nav';
import { FriendRequests} from '../../../components/friend-requests/friend-requests';

@Component({
  selector: 'app-friend-requests-page',
  imports: [FriendsNav, FriendRequests],
  templateUrl: './friend-requests.html',
  styleUrl: './friend-requests.css'
})
export class FriendRequestsPage {

}
