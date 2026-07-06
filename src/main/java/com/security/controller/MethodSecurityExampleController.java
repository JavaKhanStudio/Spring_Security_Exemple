package com.security.controller;

import com.security.dto.OwnedResourceRecordDTO;
import com.security.entity.OwnedResource;
import com.security.service.OwnedResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exemples de securite au niveau methode. Chaque route illustre UNE seule annotation.
 *
 * Prerequis : @EnableMethodSecurity(securedEnabled = true) dans SecurityConfiguration.
 * Au niveau URL, /examples/** est en permitAll : c'est donc uniquement l'annotation
 * sur la methode qui autorise ou refuse l'acces (403 si le role manque).
 *
 * Difference cle :
 *  - @PreAuthorize prend une expression SpEL, ex. hasRole('ADMIN'), et le prefixe ROLE_ est implicite.
 *  - @Secured prend la liste brute des autorites, donc le prefixe ROLE_ doit etre ecrit en entier.
 */
@RestController
@RequestMapping("/examples")
public class MethodSecurityExampleController {

    private final OwnedResourceService ownedResourceService;

    public MethodSecurityExampleController(OwnedResourceService ownedResourceService) {
        this.ownedResourceService = ownedResourceService;
    }

    // --- @PreAuthorize : expression SpEL, prefixe ROLE_ implicite ---

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/preauthorize/admin")
    public String preAuthorizeAdmin() {
        return "@PreAuthorize hasRole('ADMIN') : acces autorise (ROLE_ADMIN).";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/preauthorize/user")
    public String preAuthorizeUser() {
        return "@PreAuthorize hasRole('USER') : acces autorise (ROLE_USER).";
    }

    // --- @Secured : liste d'autorites brutes, prefixe ROLE_ obligatoire ---

    @Secured("ROLE_ADMIN")
    @GetMapping("/secured/admin")
    public String securedAdmin() {
        return "@Secured(\"ROLE_ADMIN\") : acces autorise (ROLE_ADMIN).";
    }

    @Secured("ROLE_USER")
    @GetMapping("/secured/user")
    public String securedUser() {
        return "@Secured(\"ROLE_USER\") : acces autorise (ROLE_USER).";
    }

    // --- @PreAuthorize avec SpEL sur un argument : impossible avec @Secured ---
    // Autorise seulement si le {username} de l'URL est celui de l'utilisateur connecte.
    @PreAuthorize("#username == authentication.name")
    @GetMapping("/preauthorize/self/{username}")
    public String preAuthorizeSelf(@PathVariable String username) {
        return "@PreAuthorize #username == authentication.name : tu accedes bien a TES donnees (" + username + ").";
    }

    // --- Helper : cree une OwnedResource en base (pour alimenter l'exemple @PostAuthorize) ---
    // A appeler juste avant la route @PostAuthorize ci-dessous : la reponse contient l'id genere.
    @PostMapping("/owned-resource")
    public ResponseEntity<OwnedResource> createOwnedResource(@Valid @RequestBody OwnedResourceRecordDTO dto) {
        OwnedResource saved = ownedResourceService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // --- @PostAuthorize : la methode s'execute (lecture en base), PUIS on verifie le resultat ---
    // La ressource est renvoyee seulement si son 'owner' est l'utilisateur connecte, sinon 403.
    @PostAuthorize("returnObject.owner == authentication.name")
    @GetMapping("/owned-resource/{id}")
    public OwnedResource getOwnedResource(@PathVariable Long id) {
        return ownedResourceService.findById(id);
    }

    // --- @AuthenticationPrincipal : injecte l'utilisateur connecte (UserDetails) ---
    @GetMapping("/whoami")
    public String whoAmI(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return "Aucun utilisateur authentifie (pas de JWT valide).";
        }
        return "Connecte en tant que : " + principal.getUsername()
                + " | autorites : " + principal.getAuthorities();
    }
}
