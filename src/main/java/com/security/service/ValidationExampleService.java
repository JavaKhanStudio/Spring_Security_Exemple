package com.security.service;

import com.security.dto.ValidationExampleRecordDTO;
import com.security.entity.ValidationExample;
import com.security.repository.ValidationExampleRepository;
import org.springframework.stereotype.Service;

@Service
public class ValidationExampleService {

    private final ValidationExampleRepository validationExampleRepository;

    public ValidationExampleService(ValidationExampleRepository validationExampleRepository) {
        this.validationExampleRepository = validationExampleRepository;
    }

    // Persiste un nouvel element a partir du DTO deja valide par le controller (@Valid).
    public ValidationExample save(ValidationExampleRecordDTO dto) {
        return validationExampleRepository.save(toEntity(dto));
    }

    // Mapping DTO -> entite. Garde le controller et le repository propres.
    private ValidationExample toEntity(ValidationExampleRecordDTO dto) {
        ValidationExample entity = new ValidationExample();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setUsername(dto.username());
        entity.setEmail(dto.email());
        entity.setAge(dto.age());
        entity.setPhoneNumber(dto.phoneNumber());
        entity.setAgreedToTerms(dto.agreedToTerms());
        entity.setRoles(dto.roles());
        entity.setBirthdate(dto.birthdate());
        return entity;
    }
}
