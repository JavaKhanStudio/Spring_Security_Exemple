package com.security.dto;

import jakarta.validation.constraints.NotBlank;

// Corps de la requete pour creer une OwnedResource (exemple @PostAuthorize).
public record OwnedResourceRecordDTO(
        @NotBlank(message = "owner must not be blank")
        String owner,

        @NotBlank(message = "content must not be blank")
        String content
) {
}
