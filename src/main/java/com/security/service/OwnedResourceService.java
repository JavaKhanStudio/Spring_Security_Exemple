package com.security.service;

import com.security.dto.OwnedResourceRecordDTO;
import com.security.entity.OwnedResource;
import com.security.repository.OwnedResourceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OwnedResourceService {

    private final OwnedResourceRepository ownedResourceRepository;

    public OwnedResourceService(OwnedResourceRepository ownedResourceRepository) {
        this.ownedResourceRepository = ownedResourceRepository;
    }

    // Cree et persiste une nouvelle ressource a partir du DTO deja valide (@Valid).
    public OwnedResource create(OwnedResourceRecordDTO dto) {
        OwnedResource resource = new OwnedResource();
        resource.setOwner(dto.owner());
        resource.setContent(dto.content());
        return ownedResourceRepository.save(resource);
    }

    // Charge une ressource par id, ou 404 si absente (@PostAuthorize verifie ensuite le proprietaire).
    public OwnedResource findById(Long id) {
        return ownedResourceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OwnedResource " + id + " introuvable"));
    }
}
