import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendarPostComponent } from './calendar-post.component';

describe('CalendarPostComponent', () => {
  let component: CalendarPostComponent;
  let fixture: ComponentFixture<CalendarPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalendarPostComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalendarPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
