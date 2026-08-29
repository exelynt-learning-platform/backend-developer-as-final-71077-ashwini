package com.example.booking.exception;

import com.example.booking.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ErrorResponse body(HttpStatus s,String msg,HttpServletRequest r){return new ErrorResponse(LocalDateTime.now(),s.value(),s.getReasonPhrase(),msg,r.getRequestURI());}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e,HttpServletRequest r){
        String msg=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST,msg,r));
    }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorResponse> status(ResponseStatusException e,HttpServletRequest r){
        HttpStatus s=HttpStatus.valueOf(e.getStatusCode().value()); return ResponseEntity.status(s).body(body(s,e.getReason(),r));
    }
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException e,HttpServletRequest r){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(HttpStatus.UNAUTHORIZED,"Invalid email or password",r));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception e,HttpServletRequest r){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error",r));
    }
}
