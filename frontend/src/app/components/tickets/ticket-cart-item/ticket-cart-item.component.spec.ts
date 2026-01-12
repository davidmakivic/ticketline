import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TicketCartItemComponent } from './ticket-cart-item.component';
import { TicketStatus } from '../../../dtos/ticket';

describe('TicketCartItemComponent', () => {
  let component: TicketCartItemComponent;
  let fixture: ComponentFixture<TicketCartItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketCartItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TicketCartItemComponent);
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
