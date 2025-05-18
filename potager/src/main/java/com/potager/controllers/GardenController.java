package com.potager.controllers;

import com.potager.customExceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GardenController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
    protected ResponseEntity<Void> ok() {
        return ResponseEntity.ok().build();
    }
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    protected ResponseEntity<Void> created() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    protected <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    protected ResponseEntity<ErrorResponse> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), message));
    }

    protected ResponseEntity<
            ErrorResponse> badRequest(String message) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }
}

