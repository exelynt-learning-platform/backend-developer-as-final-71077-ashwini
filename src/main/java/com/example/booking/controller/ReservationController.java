package com.example.booking.controller;
import com.example.booking.dto.*;
import com.example.booking.entity.*;
import com.example.booking.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController @RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService service; public ReservationController(ReservationService s){service=s;}

    @PostMapping public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationRequest x,Authentication auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createForUser(x,auth.getName()));
    }

    @GetMapping public Page<ReservationResponse> list(
        @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,
        @RequestParam(required=false) ReservationStatus status,@RequestParam(required=false) BigDecimal minPrice,
        @RequestParam(required=false) BigDecimal maxPrice,@RequestParam(required=false) String sort,Authentication auth){
        boolean admin=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        return service.list(auth.getName(),admin,status,minPrice,maxPrice,page,size,sort);
    }

    @GetMapping("/{id}") public ReservationResponse get(@PathVariable Long id,Authentication auth){
        boolean admin=auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        return service.get(id,auth.getName(),admin);
    }

    @PostMapping("/admin") public ResponseEntity<ReservationResponse> adminCreate(@Valid @RequestBody AdminReservationRequest x){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adminCreate(x));
    }
    @PutMapping("/{id}") public ReservationResponse adminUpdate(@PathVariable Long id,@Valid @RequestBody AdminReservationRequest x){return service.adminUpdate(id,x);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> adminDelete(@PathVariable Long id){service.adminDelete(id);return ResponseEntity.noContent().build();}
}
