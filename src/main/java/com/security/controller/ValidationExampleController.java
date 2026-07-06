package com.security.controller;

import com.security.dto.ValidationExampleRecordDTO;
import com.security.entity.ValidationExample;
import com.security.service.ValidationExampleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validation-example")
public class ValidationExampleController {

    private final ValidationExampleService validationExampleService;

    public ValidationExampleController(ValidationExampleService validationExampleService) {
        this.validationExampleService = validationExampleService;
    }

    // @Valid declenche la validation du DTO ; en cas d'echec -> 400 via GlobalExceptionHandler.
    @PostMapping
    public ResponseEntity<ValidationExample> create(@Valid @RequestBody ValidationExampleRecordDTO dto) {
        ValidationExample saved = validationExampleService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
