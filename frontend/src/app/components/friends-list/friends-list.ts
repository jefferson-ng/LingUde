import {Component, OnInit, signal} from '@angular/core';
import {FriendshipService} from '../../services/friendship.service';
import {Friend, UserSummary} from '../../models/friendship.model';
import {LucideAngularModule} from 'lucide-angular';
import {AuthService} from '../../services/auth.service';
import {TranslocoDirective} from '@jsverse/transloco';

@Component({
  selector: 'app-friends-list',
  standalone: true,
  imports: [
    LucideAngularModule,
    TranslocoDirective
  ],
  templateUrl: './friends-list.html',
  styleUrl: './friends-list.css'
})
export class FriendsList implements  OnInit{

  currentUserId = signal<string>('');
  // signal to hold our friend list
  friends = signal<Friend[]>([]);

  // signals for loading and error states
  isLoading = signal<boolean>(true);
  error = signal< string | null>(null);

  // Injecting the friendship and authentication service
  constructor(
    private friendshipService: FriendshipService,
    private authService: AuthService
    ) {}

  //called when the component initializes
  ngOnInit() {
    console.log('friend list component initialized');
    this.authService.user$.subscribe({
      next: data => {
        console.log('user data received', data);
        if(data !== null){
          this.currentUserId.set(data.id);
          console.log('set currentUserId to: ', this.currentUserId());
        }
      },
      error: error => {
        console.log('error while fetching user info', error);
      }
    })
    this.loadFriends();
  }

  // Fetching the friends from backend
  loadFriends():void {
    this.isLoading.set(true);
    this.error.set(null);

    this.friendshipService.getFriendsList().subscribe({
      next: (data) => {
        this.friends.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load friends:', err);
        this.error.set('friends.list.errorLoad');
        this.isLoading.set(false);
      }
    });

  }

  //Helper method to get the friend info
  getFriendInfo(friendship: Friend): UserSummary{

    // If the current user is the requester, show receiver otherwise show the requester
    return friendship.requester.id === this.currentUserId()
      ? friendship.receiver
      : friendship.requester;
  }

  //Method to handle unfriend action

  onUnfriend(friendshipId: string): void{
    console.log('Unfriend clicked for: ', friendshipId);
    this.friendshipService.removeFriendship(friendshipId).subscribe({
      next: (data) => {
        this.loadFriends();
        console.log('Friend remove successfully');
      },
      error: (err) => {
        console.error('Failed to remove friendship:', err);
        this.error.set('friends.list.errorRemove');
      }
    });
  }
}
