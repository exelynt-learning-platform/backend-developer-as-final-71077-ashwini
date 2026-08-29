package com.example.booking.dto;
import com.example.booking.entity.Resource;
import java.math.BigDecimal;
public record ResourceResponse(Long id,String name,String type,String description,BigDecimal pricePerBooking,boolean available) {
    public static ResourceResponse from(Resource r){return new ResourceResponse(r.getId(),r.getName(),r.getType(),r.getDescription(),r.getPricePerBooking(),r.isAvailable());}
}
