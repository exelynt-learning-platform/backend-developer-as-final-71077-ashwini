package com.example.booking.repository;
import com.example.booking.entity.Reservation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
public interface ReservationRepository extends JpaRepository<Reservation,Long>, JpaSpecificationExecutor<Reservation> {
    boolean existsByResourceIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
        Long resourceId, java.time.LocalDateTime endTime, java.time.LocalDateTime startTime,
        java.util.Collection<com.example.booking.entity.ReservationStatus> statuses);
}
