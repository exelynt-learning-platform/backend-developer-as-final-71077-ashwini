package com.example.booking.dto;
import com.example.booking.entity.Reservation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record ReservationResponse(Long id,Long resourceId,String resourceName,Long userId,String userEmail,
                                   BigDecimal price,String status,LocalDateTime startTime,LocalDateTime endTime) {
    public static ReservationResponse from(Reservation r){
        return new ReservationResponse(r.getId(),r.getResource().getId(),r.getResource().getName(),
            r.getUser().getId(),r.getUser().getEmail(),r.getPrice(),r.getStatus().name(),r.getStartTime(),r.getEndTime());
    }
}
