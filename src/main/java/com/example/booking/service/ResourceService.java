package com.example.booking.service;
import com.example.booking.dto.*;
import com.example.booking.entity.Resource;
import com.example.booking.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResourceService {
    private final ResourceRepository repo;
    public ResourceService(ResourceRepository repo){this.repo=repo;}
    public List<ResourceResponse> all(){return repo.findAll().stream().map(ResourceResponse::from).toList();}
    public ResourceResponse get(Long id){return ResourceResponse.from(find(id));}
    public ResourceResponse create(ResourceRequest x){
        Resource r=new Resource(x.name(),x.type(),x.description(),x.pricePerBooking(),x.available());
        return ResourceResponse.from(repo.save(r));
    }
    public ResourceResponse update(Long id,ResourceRequest x){
        Resource r=find(id); r.setName(x.name());r.setType(x.type());r.setDescription(x.description());
        r.setPricePerBooking(x.pricePerBooking());r.setAvailable(x.available()); return ResourceResponse.from(repo.save(r));
    }
    public void delete(Long id){repo.delete(find(id));}
    public Resource find(Long id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource not found"));}
}
