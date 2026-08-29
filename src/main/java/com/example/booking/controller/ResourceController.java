package com.example.booking.controller;
import com.example.booking.dto.*;
import com.example.booking.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/resources")
public class ResourceController {
    private final ResourceService service; public ResourceController(ResourceService s){service=s;}
    @GetMapping public List<ResourceResponse> all(){return service.all();}
    @GetMapping("/{id}") public ResourceResponse get(@PathVariable Long id){return service.get(id);}
    @PostMapping public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest x){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(x));}
    @PutMapping("/{id}") public ResourceResponse update(@PathVariable Long id,@Valid @RequestBody ResourceRequest x){return service.update(id,x);}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
