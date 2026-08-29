package com.example.booking.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ResourceRequest(
    @NotBlank @Size(max=120) String name,
    @NotBlank @Size(max=50) String type,
    @Size(max=500) String description,
    @NotNull @DecimalMin(value="0.01") @Digits(integer=10,fraction=2) BigDecimal pricePerBooking,
    boolean available
) {}
