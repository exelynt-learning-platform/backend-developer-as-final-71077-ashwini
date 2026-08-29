package com.example.booking.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record ReservationRequest(
    @NotNull Long resourceId,
    @NotNull @DecimalMin(value="0.01") @Digits(integer=10,fraction=2) BigDecimal price,
    @NotNull @FutureOrPresent LocalDateTime startTime,
    @NotNull LocalDateTime endTime
) {}
