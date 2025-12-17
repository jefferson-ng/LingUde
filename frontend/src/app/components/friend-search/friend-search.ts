import {Component, inject, OnInit} from '@angular/core';
import {FriendshipService} from '../../services/friendship.service';
import {FormsModule} from '@angular/forms';
import {LucideAngularModule} from 'lucide-angular';
import {TranslocoDirective, TranslocoService} from '@jsverse/transloco';

@Component({
  selector: 'app-friend-search',
  standalone: true,
  imports: [
    FormsModule,
    LucideAngularModule,
    TranslocoDirective
  ],
  templateUrl: './friend-search.html',
  styleUrl: './friend-search.css'
})

export class FriendSearch  {

  searchInput = '';
  isLoading = false;
  message: string | null = null;
  messageType: 'success' | 'error'| null = null;

  private friendshipService = inject(FriendshipService);
  private translocoService = inject(TranslocoService);

  sendFriendRequest() {

    if (this.searchInput.trim().length > 0) {
      this.isLoading = true;
      this.friendshipService.sendFriendRequest(this.searchInput).subscribe({
        next: result => {
          this.isLoading = false;
          this.messageType = 'success';
          this.message = this.translocoService.translate('friends.search.successMessage');
          this.searchInput = '';
        },
        error: error => {
          this.isLoading = false;
          this.messageType = 'error';
          this.message = error.error?.message || this.translocoService.translate('friends.search.errorMessage') + error;
        }
      });
    }
  }

}
