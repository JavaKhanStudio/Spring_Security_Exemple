package com.security.repository;

import com.security.entity.OwnedResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedResourceRepository extends JpaRepository<OwnedResource, Long> {
}
