import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsNav } from './friends-nav';

describe('FriendsNav', () => {
  let component: FriendsNav;
  let fixture: ComponentFixture<FriendsNav>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendsNav]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendsNav);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
