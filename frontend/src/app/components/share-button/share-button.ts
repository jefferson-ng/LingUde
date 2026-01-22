import { Component, signal, HostListener, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslocoDirective } from '@jsverse/transloco';
import { LucideAngularModule, Share2, Copy, Check } from 'lucide-angular';
import { ReferralService } from '../../services/referral.service';

@Component({
  selector: 'app-share-button',
  imports: [CommonModule, TranslocoDirective, LucideAngularModule],
  templateUrl: './share-button.html',
  styleUrl: './share-button.css',
  standalone: true
})
export class ShareButtonComponent implements OnInit {
  private referralService = inject(ReferralService);

  // Lucide Icons
  readonly ShareIcon = Share2;
  readonly CopyIcon = Copy;
  readonly CheckIcon = Check;

  // State
  protected readonly isDropdownOpen = signal(false);
  protected readonly isLinkCopied = signal(false);
  protected readonly isLoading = signal(false);

  // Share configuration
  protected readonly shareUrl = signal('https://lingude.app');
  protected readonly shareTitle = signal('LingUDE');
  protected readonly shareText = signal('Join me on LingUDE and let\'s learn languages together!');

  ngOnInit(): void {
    this.loadReferralLink();
  }

  /**
   * Fetch the user's personalized referral link from backend
   */
  private loadReferralLink(): void {
    this.isLoading.set(true);
    this.referralService.getReferralLink().subscribe({
      next: (response) => {
        this.shareUrl.set(response.referralUrl);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load referral link:', error);
        this.isLoading.set(false);
        // Keep default URL as fallback
      }
    });
  }

  /**
   * Toggle dropdown visibility
   */
  toggleDropdown(): void {
    this.isDropdownOpen.set(!this.isDropdownOpen());
    this.isLinkCopied.set(false);
  }

  /**
   * Close dropdown
   */
  closeDropdown(): void {
    this.isDropdownOpen.set(false);
    this.isLinkCopied.set(false);
  }

  /**
   * Share via Web Share API or fallback to social link
   */
  async shareVia(platform: 'whatsapp' | 'facebook' | 'twitter' | 'instagram' | 'native'): Promise<void> {
    const url = this.shareUrl();
    const text = this.shareText();
    const title = this.shareTitle();

    if (platform === 'native' && navigator.share) {
      try {
        await navigator.share({ title, text, url });
        this.closeDropdown();
        return;
      } catch (err) {
        // User cancelled or share failed, continue with fallback
      }
    }

    // Fallback to social platform links
    let shareLink = '';
    const encodedUrl = encodeURIComponent(url);
    const encodedText = encodeURIComponent(text);

    switch (platform) {
      case 'whatsapp':
        shareLink = `https://wa.me/?text=${encodedText}%20${encodedUrl}`;
        break;
      case 'facebook':
        shareLink = `https://www.facebook.com/sharer/sharer.php?u=${encodedUrl}`;
        break;
      case 'twitter':
        shareLink = `https://twitter.com/intent/tweet?text=${encodedText}&url=${encodedUrl}`;
        break;
      case 'instagram':
        // Instagram doesn't have a direct share URL, copy link instead
        await this.copyShareLink();
        return;
    }

    if (shareLink) {
      window.open(shareLink, '_blank', 'noopener,noreferrer');
    }
  }

  /**
   * Copy share link to clipboard
   */
  async copyShareLink(): Promise<void> {
    try {
      await navigator.clipboard.writeText(this.shareUrl());
      this.isLinkCopied.set(true);
      setTimeout(() => this.isLinkCopied.set(false), 2000);
    } catch (err) {
      console.error('Failed to copy link:', err);
    }
  }

  /**
   * Close dropdown when clicking outside
   */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const clickedInside = target.closest('.share-button-container');
    if (!clickedInside && this.isDropdownOpen()) {
      this.closeDropdown();
    }
  }
}
