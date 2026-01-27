package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Ticket t
            set t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.RESERVED,
                t.reservedUntil = :reservedUntil,
                t.reservedByUserId = :userId
            where t.id = :ticketId
                and (
                    t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.AVAILABLE
                    or (
                        t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.RESERVED
                        and t.reservedUntil is not null
                        and t.reservedUntil <= :now
                    )
                    or (
                        t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.RESERVED
                        and t.reservedByUserId = :userId
                        and t.reservedUntil is not null
                        and t.reservedUntil > :now
                    )
                )
        """)
    int holdAtomically(@Param("ticketId") Long ticketId,
                       @Param("userId") Long userId,
                       @Param("reservedUntil") Instant reservedUntil,
                       @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Ticket t
            set t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.AVAILABLE,
                t.reservedUntil = null,
                t.reservedByUserId = null
            where t.id = :ticketId
                and t.status = at.ac.tuwien.sepr.groupphase.backend.type.TicketStatus.RESERVED
                and t.reservedByUserId = :userId
                and t.reservedUntil is not null
                and t.reservedUntil > :now
        """)
    int releaseHoldOwned(@Param("ticketId") Long ticketId,
                         @Param("userId") Long userId,
                         @Param("now") Instant now);


    @Query("""
         select t from Ticket t
         where t.performance.id = :performanceId
        """)
    List<Ticket> findByPerformanceId(@Param("performanceId") Long performanceId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tickets set order_id = null where ticket_id in (:ids)", nativeQuery = true)
    void detachFromOrder(@Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update tickets set reservation_id = null where ticket_id in (:ids)", nativeQuery = true)
    void detachFromReservation(@Param("ids") List<Long> ids);

    List<Ticket> findByReservedByUserId(Long reservedByUserId);

    boolean existsByPerformanceId(Long performanceId);

    @Modifying
    @Query(value = """
        INSERT INTO tickets (
                 performance_id,
                 seat_id,
                 price_final_cents,
                 status,
                 reserved_until,
                 reserved_by_user_id,
                 version )
                 SELECT :performanceId,
                 s.seat_id,
                 CAST(:basePriceCents * pc.price AS BIGINT),
                 'AVAILABLE',
                 NULL,
                 NULL,
                0 FROM seats s JOIN sectors sec ON s.sector_id = sec.sector_id JOIN price_categories pc ON sec.price_category_id = pc.price_category_id WHERE sec.sector_id = :sectorId """, nativeQuery = true)
    int bulkInsertForPerformanceAndSector(@Param("performanceId") Long performanceId,
                                          @Param("sectorId") Long sectorId,
                                          @Param("basePriceCents") Long basePriceCents);

}