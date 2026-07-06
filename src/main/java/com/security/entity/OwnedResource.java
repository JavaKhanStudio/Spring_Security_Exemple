package com.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Ressource appartenant a un utilisateur, utilisee pour l'exemple @PostAuthorize.
// SpEL "returnObject.owner" resout getOwner() (fourni par Lombok @Getter).
@Entity
@Getter
@Setter
@Table(name = "owned_resource")
public class OwnedResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom d'utilisateur du proprietaire (compare a authentication.name par @PostAuthorize).
    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "content")
    private String content;
}
