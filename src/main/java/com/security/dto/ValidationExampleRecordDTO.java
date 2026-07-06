package com.security.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ValidationExampleRecordDTO(
        @NotNull(message = "id must not be null")
        Long id,

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Username must not be blank")
        @Size(min = 5, max = 10, message = "Username must be between 5 and 10 characters")
        String username,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email,

        @NotNull(message = "Age must not be null")
        @Min(value = 18, message = "Age must be at least 18")
        @Max(value = 100, message = "Age must be at most 100")
        Integer age,

        @NotBlank(message = "Phone number must not be blank")
        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number is invalid")
        String phoneNumber,

        @AssertTrue(message = "Must agree to terms")
        boolean agreedToTerms,

        @NotEmpty(message = "List of roles must not be empty")
        List<@NotBlank String> roles,

        @Past(message = "Birthdate must be in the past")
        LocalDate birthdate
) {
}
