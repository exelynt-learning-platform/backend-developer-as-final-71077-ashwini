package com.example.booking.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="reservations")
public class Reservation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="resource_id", nullable=false)
    private Resource resource;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="user_id", nullable=false)
    private AppUser user;

    @Column(nullable=false, precision=12, scale=2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(nullable=false)
    private LocalDateTime startTime;

    @Column(nullable=false)
    private LocalDateTime endTime;

    public Reservation(){}
    public Long getId(){return id;} public Resource getResource(){return resource;} public AppUser getUser(){return user;}
    public BigDecimal getPrice(){return price;} public ReservationStatus getStatus(){return status;}
    public LocalDateTime getStartTime(){return startTime;} public LocalDateTime getEndTime(){return endTime;}
    public void setId(Long v){id=v;} public void setResource(Resource v){resource=v;} public void setUser(AppUser v){user=v;}
    public void setPrice(BigDecimal v){price=v;} public void setStatus(ReservationStatus v){status=v;}
    public void setStartTime(LocalDateTime v){startTime=v;} public void setEndTime(LocalDateTime v){endTime=v;}
}
