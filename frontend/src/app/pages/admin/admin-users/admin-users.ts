import { Component, OnInit, signal } from '@angular/core';
import { AdminService } from '../../../services/admin.service';
import { AdminUserSummary, AdminUserDetail, PageResponse } from '../../../models/admin.model';
import { TranslocoDirective } from '@jsverse/transloco';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [TranslocoDirective],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css'
})
export class AdminUsers implements OnInit {
  users = signal<AdminUserSummary[]>([]);
  isLoading = signal<boolean>(true);
  error = signal<string | null>(null);

  currentPage = signal<number>(0);
  pageSize = signal<number>(20);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);

  isModalOpen = signal<boolean>(false);
  selectedUser = signal<AdminUserDetail | null>(null);
  isLoadingDetails = signal<boolean>(false);

  showRoleConfirm = signal<boolean>(false);
  isUpdatingRole = signal<boolean>(false);
  roleUpdateError = signal<string | null>(null);

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.adminService.getUsers(this.currentPage(), this.pageSize()).subscribe({
      next: (response: PageResponse<AdminUserSummary>) => {
        this.users.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load users:', err);
        this.error.set('admin.users.errorLoad');
        this.isLoading.set(false);
      }
    });
  }

  onUserClick(userId: string): void {
    this.isModalOpen.set(true);
    this.isLoadingDetails.set(true);
    this.selectedUser.set(null);

    this.adminService.getUserDetails(userId).subscribe({
      next: (user: AdminUserDetail) => {
        this.selectedUser.set(user);
        this.isLoadingDetails.set(false);
      },
      error: (err) => {
        console.error('Failed to load user details:', err);
        this.isLoadingDetails.set(false);
        this.closeModal();
      }
    });
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.selectedUser.set(null);
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.closeModal();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadUsers();
    }
  }

  previousPage(): void {
    this.goToPage(this.currentPage() - 1);
  }

  nextPage(): void {
    this.goToPage(this.currentPage() + 1);
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString();
  }

  getShowingEnd(): number {
    return Math.min((this.currentPage() + 1) * this.pageSize(), this.totalElements());
  }

  getNewRole(): string {
    return this.selectedUser()?.role === 'ADMIN' ? 'USER' : 'ADMIN';
  }

  openRoleConfirm(): void {
    this.showRoleConfirm.set(true);
    this.roleUpdateError.set(null);
  }

  cancelRoleChange(): void {
    this.showRoleConfirm.set(false);
    this.roleUpdateError.set(null);
  }

  confirmRoleChange(): void {
    const user = this.selectedUser();
    if (!user) return;

    const newRole = this.getNewRole();
    this.isUpdatingRole.set(true);
    this.roleUpdateError.set(null);

    this.adminService.updateUserRole(user.userId, newRole).subscribe({
      next: () => {
        const updatedUser = { ...user, role: newRole };
        this.selectedUser.set(updatedUser);
        this.showRoleConfirm.set(false);
        this.isUpdatingRole.set(false);
        this.loadUsers();
      },
      error: (err) => {
        console.error('Failed to update user role:', err);
        const errorMsg = err?.error?.message || err?.message || 'admin.users.roleChange.error';
        this.roleUpdateError.set(errorMsg);
        this.isUpdatingRole.set(false);
      }
    });
  }
}