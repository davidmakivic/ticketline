import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformancesListComponent } from './performances-list.component';

describe('PerformancesListComponent', () => {
  let component: PerformancesListComponent;
  let fixture: ComponentFixture<PerformancesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformancesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PerformancesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
