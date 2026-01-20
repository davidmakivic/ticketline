package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepr.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.TicketGenerationService;
import at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class TicketGenerationServiceImpl implements TicketGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    public TicketGenerationServiceImpl(SeatRepository seatRepository, TicketRepository ticketRepository) {
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Transactional
    public void generateTicketsForPerformance(Performance performance) {
        LOGGER.info("Generating tickets for performance id={} in hall id={}",
            performance.getId(), performance.getHall().getId());

        List<Seat> seats = seatRepository.findByHallIdWithSector(performance.getHall().getId());

        for (Seat seat : seats) {
            Long priceInCents = calculatePrice(performance.getBasePriceCents(),
                seat.getSector().getPriceCategory().getPrice());

            Ticket ticket = new Ticket();
            ticket.setPerformance(performance);
            ticket.setSeat(seat);
            ticket.setPriceFinalCents(priceInCents);
            ticket.setStatus(TicketStatus.AVAILABLE);

            ticketRepository.save(ticket);
        }

        LOGGER.info("Created {} tickets for performance id={}", seats.size(), performance.getId());
    }

    private Long calculatePrice(Long basePriceCents, double categoryPriceMultiplier) {
        if (basePriceCents == null) {
            return 0L;
        }
        return (long) (basePriceCents * categoryPriceMultiplier);
    }
}
