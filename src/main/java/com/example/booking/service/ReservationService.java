package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.entity.*;
import com.example.booking.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository reservations;
    private final ResourceRepository resources;
    private final UserRepository users;

    public ReservationService(
            ReservationRepository r,
            ResourceRepository res,
            UserRepository u) {
        reservations = r;
        resources = res;
        users = u;
    }

    public ReservationResponse createForUser(
            ReservationRequest x,
            String email) {

        AppUser user = user(email);
        Resource resource = resource(x.resourceId());

        validateTimes(x.startTime(), x.endTime());
        validateResource(resource);
        validateOverlap(
                resource.getId(),
                x.startTime(),
                x.endTime(),
                null
        );

        Reservation r = new Reservation();

        r.setResource(resource);
        r.setUser(user);
        r.setPrice(x.price());
        r.setStatus(ReservationStatus.PENDING);
        r.setStartTime(x.startTime());
        r.setEndTime(x.endTime());

        return ReservationResponse.from(
                reservations.save(r)
        );
    }

    @Transactional(readOnly = true)
    public Page<ReservationResponse> list(
            String email,
            boolean admin,
            ReservationStatus status,
            BigDecimal min,
            BigDecimal max,
            int page,
            int size,
            String sort) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be 1..100"
            );
        }

        Sort s = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, s);

        Specification<Reservation> spec =
                Specification.where(null);

        if (!admin) {
            spec = spec.and(
                    (root, q, cb) ->
                            cb.equal(
                                    root.get("user").get("email"),
                                    email
                            )
            );
        }

        if (status != null) {
            spec = spec.and(
                    (root, q, cb) ->
                            cb.equal(
                                    root.get("status"),
                                    status
                            )
            );
        }

        if (min != null) {
            spec = spec.and(
                    (root, q, cb) ->
                            cb.greaterThanOrEqualTo(
                                    root.get("price"),
                                    min
                            )
            );
        }

        if (max != null) {
            spec = spec.and(
                    (root, q, cb) ->
                            cb.lessThanOrEqualTo(
                                    root.get("price"),
                                    max
                            )
            );
        }

        return reservations
                .findAll(spec, pageable)
                .map(ReservationResponse::from);
    }

    @Transactional(readOnly = true)
    public ReservationResponse get(
            Long id,
            String email,
            boolean admin) {

        Reservation r = find(id);

        ensureOwner(r, email, admin);

        return ReservationResponse.from(r);
    }

    public ReservationResponse adminCreate(
            AdminReservationRequest x) {

        validateTimes(
                x.startTime(),
                x.endTime()
        );

        Resource res = resource(x.resourceId());
        AppUser u = userById(x.userId());

        validateResource(res);

        validateOverlap(
                res.getId(),
                x.startTime(),
                x.endTime(),
                null
        );

        Reservation r = new Reservation();

        r.setResource(res);
        r.setUser(u);
        r.setPrice(x.price());
        r.setStatus(x.status());
        r.setStartTime(x.startTime());
        r.setEndTime(x.endTime());

        return ReservationResponse.from(
                reservations.save(r)
        );
    }

    public ReservationResponse adminUpdate(
            Long id,
            AdminReservationRequest x) {

        Reservation r = find(id);

        validateTimes(
                x.startTime(),
                x.endTime()
        );

        Resource res = resource(x.resourceId());
        AppUser u = userById(x.userId());

        validateResource(res);

        validateOverlap(
                res.getId(),
                x.startTime(),
                x.endTime(),
                id
        );

        r.setResource(res);
        r.setUser(u);
        r.setPrice(x.price());
        r.setStatus(x.status());
        r.setStartTime(x.startTime());
        r.setEndTime(x.endTime());

        return ReservationResponse.from(
                reservations.save(r)
        );
    }

    public void adminDelete(Long id) {
        reservations.delete(find(id));
    }

    private Sort parseSort(String sort) {

        if (sort == null || sort.isBlank()) {
            return Sort.by(
                    Sort.Direction.DESC,
                    "id"
            );
        }

        String[] p = sort.split(",", 2);
        String field = p[0].trim();

        Set<String> allowed = Set.of(
                "id",
                "price",
                "startTime",
                "endTime",
                "status"
        );

        if (!allowed.contains(field)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field"
            );
        }

        Sort.Direction d =
                (p.length > 1 &&
                 p[1].equalsIgnoreCase("asc"))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return Sort.by(d, field);
    }

    private void validateTimes(
            LocalDateTime start,
            LocalDateTime end) {

        if (!end.isAfter(start)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "endTime must be after startTime"
            );
        }
    }

    private void validateResource(Resource r) {

        if (!r.isAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Resource is not available"
            );
        }
    }

    private void validateOverlap(
            Long resourceId,
            LocalDateTime start,
            LocalDateTime end,
            Long ignoreId) {

        List<ReservationStatus> active =
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.CONFIRMED
                );

        Specification<Reservation> spec =
                (root, q, cb) -> cb.and(
                        cb.equal(
                                root.get("resource").get("id"),
                                resourceId
                        ),
                        root.get("status").in(active),
                        cb.lessThan(
                                root.get("startTime"),
                                end
                        ),
                        cb.greaterThan(
                                root.get("endTime"),
                                start
                        )
                );

        if (ignoreId != null) {
            spec = spec.and(
                    (root, q, cb) ->
                            cb.notEqual(
                                    root.get("id"),
                                    ignoreId
                            )
            );
        }

        if (reservations.count(spec) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Resource is already booked for the requested time"
            );
        }
    }

    private void ensureOwner(
            Reservation r,
            String email,
            boolean admin) {

        if (!admin &&
            !r.getUser()
              .getEmail()
              .equalsIgnoreCase(email)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can access only your own reservations"
            );
        }
    }

    private Reservation find(Long id) {

        return reservations
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Reservation not found"
                        )
                );
    }

    private Resource resource(Long id) {

        return resources
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Resource not found"
                        )
                );
    }

    private AppUser user(String email) {

        return users
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private AppUser userById(Long id) {

        return users
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }
}