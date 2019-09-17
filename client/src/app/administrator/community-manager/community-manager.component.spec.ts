import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CommunityManagerComponent } from './community-manager.component';

describe('CommunityManagerComponent', () => {
  let component: CommunityManagerComponent;
  let fixture: ComponentFixture<CommunityManagerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommunityManagerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommunityManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
