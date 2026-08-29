package com.example.booking.dto;
import jakarta.validation.constraints.*;
import com.example.booking.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record AdminReservationRequest(
    @NotNull Long resourceId,
    @NotNull Long userId,
    @NotNull @DecimalMin(value="0.01") @Digits(integer=10,fraction=2) BigDecimal price,
    @NotNull ReservationStatus status,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime
) {}
