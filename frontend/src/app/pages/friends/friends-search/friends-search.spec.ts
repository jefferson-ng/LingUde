import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsSearch } from './friends-search';

describe('FriendsSearch', () => {
  let component: FriendsSearch;
  let fixture: ComponentFixture<FriendsSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendsSearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendsSearch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
