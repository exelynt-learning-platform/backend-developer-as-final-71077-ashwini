package com.example.booking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="resources")
public class Resource {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=120) private String name;
    @Column(nullable=false, length=50) private String type;
    @Column(length=500) private String description;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal pricePerBooking;
    @Column(nullable=false) private boolean available = true;

    public Resource() {}
    public Resource(String name,String type,String description,BigDecimal pricePerBooking,boolean available){
        this.name=name;this.type=type;this.description=description;this.pricePerBooking=pricePerBooking;this.available=available;
    }
    public Long getId(){return id;} public String getName(){return name;} public String getType(){return type;}
    public String getDescription(){return description;} public BigDecimal getPricePerBooking(){return pricePerBooking;}
    public boolean isAvailable(){return available;}
    public void setId(Long id){this.id=id;} public void setName(String v){name=v;} public void setType(String v){type=v;}
    public void setDescription(String v){description=v;} public void setPricePerBooking(BigDecimal v){pricePerBooking=v;}
    public void setAvailable(boolean v){available=v;}
}
