import {Component, inject, OnInit, signal} from '@angular/core';
import { forkJoin} from 'rxjs';
import {FriendshipService} from '../../services/friendship.service';
import {PendingRequest} from '../../models/friendship.model';
import {LucideAngularModule} from 'lucide-angular';
import {TranslocoDirective} from '@jsverse/transloco';

@Component({
  selector: 'app-friend-requests',
  standalone: true,
  imports: [
    LucideAngularModule,
    TranslocoDirective
  ],
  templateUrl: './friend-requests.html',
  styleUrl: './friend-requests.css'
})
export class FriendRequests implements OnInit {

  private friendshipService = inject(FriendshipService);

  // Separate signals for separate lists
  incomingRequests = signal<PendingRequest[]>([]);
  outgoingRequests = signal<PendingRequest[]>([]);

  // loading state
  isLoading = signal(true);
  error = signal<string | null>(null);

  ngOnInit() {
    this.loadRequests();
  }

  private loadRequests() {
    this.isLoading.set(true);
    this.error.set(null);

    forkJoin({
      incoming: this.friendshipService.getIncomingRequests(),
      outgoing: this.friendshipService.getOutgoingRequests()
    }).subscribe({
      next: result => {
        this.incomingRequests.set(result.incoming);
        this.outgoingRequests.set(result.outgoing);
        this.isLoading.set(false);
      },
      error: error => {
        // Either request failed
        this.error.set('friends.requests.errorLoad');
        console.error('Error loading friend requests: ', error);
        this.isLoading.set(false);
      }
    })
  }

  // Action methods - we'll implement these, step by step
  acceptRequest(friendshipId: string) {

    this.friendshipService.acceptRequest(friendshipId).subscribe({
      next: () => {
        this.incomingRequests.set(
          this.incomingRequests().filter(friendshipId => friendshipId !== friendshipId)
        );
      },
      error: error => {
        console.error('Error accepting friend requests: ', error);
        this.error.set('friends.requests.errorAccept');
      }
    })
  }

  rejectRequest(friendshipId: string) {
    this.friendshipService.rejectRequest(friendshipId).subscribe({
      next: () => {
        this.incomingRequests.set(
          this.incomingRequests().filter(friendshipId => friendshipId !== friendshipId)
        );
      },
      error: error => {
        console.error('Error rejecting friend requests: ', error);
        this.error.set('friends.requests.errorReject');
      }
    })
  }

  cancelRequest(friendshipId: string) {
    this.friendshipService.removeFriendship(friendshipId).subscribe({
      next: () => {
        this.outgoingRequests.set(
          this.outgoingRequests().filter(friendshipId => friendshipId !== friendshipId)
        );
      },
      error: error => {
        console.error('Error removing friend requests: ', error);
        this.error.set('friends.requests.errorCancel');
      }
    })
  }

  getInitial(username: string): string {
    return username.charAt(0).toUpperCase();
  }

}
