import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketCardComponent } from './ticket-card.component';
import { TicketStatus } from '../../../dtos/ticket';

describe('TicketCardComponent', () => {
  let component: TicketCardComponent;
  let fixture: ComponentFixture<TicketCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TicketCardComponent);
    component = fixture.componentInstance;

    component.ticket = {
      id: 1,
      performanceId: 10,
      seatId: 55,
      priceFinalCents: 2500,
      status: TicketStatus.RESERVED,
      version: 0
    };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
