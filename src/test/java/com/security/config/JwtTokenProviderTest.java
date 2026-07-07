package com.security.config;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test unitaire pur (pas de contexte Spring) du fournisseur de token JWT.
 * On instancie {@link JwtTokenProvider} directement avec un secret >= 64 octets
 * (requis par HS512).
 */
class JwtTokenProviderTest {

    // 64+ octets : indispensable pour HS512, sinon Keys.hmacShaKeyFor leve une exception.
    private static final String SECRET =
            "test-only-secret-0123456789-abcdefghijklmnopqrstuvwxyz-0123456789";

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET);
    }

    @Test
    void generatedToken_isValid() {
        String token = provider.generateToken("alice", List.of("ROLE_USER"));

        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void getUsernameFromJWT_returnsSubject() {
        String token = provider.generateToken("bob", List.of("ROLE_ADMIN", "ROLE_USER"));

        assertThat(provider.getUsernameFromJWT(token)).isEqualTo("bob");
    }

    @Test
    void validateToken_returnsFalse_forGarbage() {
        assertThat(provider.validateToken("not-a-real-jwt")).isFalse();
    }

    // Autre secret, distinct et lui aussi >= 64 octets (impose par HS512).
    private static final String OTHER_SECRET =
            "another-secret-9876543210-zyxwvutsrqponmlkjihgfedcba-9876543210-xyz";

    @Test
    void validateToken_returnsFalse_forTamperedToken() {
        String token = provider.generateToken("carol", List.of("ROLE_USER"));

        // On altere le premier caractere du payload (2e segment) : la signature
        // ne couvre alors plus le contenu et la validation doit echouer.
        int payloadStart = token.indexOf('.') + 1;
        char original = token.charAt(payloadStart);
        char replacement = (original == 'A') ? 'B' : 'A';
        String tampered = token.substring(0, payloadStart)
                + replacement
                + token.substring(payloadStart + 1);

        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void validateToken_returnsFalse_forTokenSignedWithAnotherKey() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(OTHER_SECRET);
        String foreignToken = otherProvider.generateToken("dave", List.of("ROLE_USER"));

        assertThat(provider.validateToken(foreignToken)).isFalse();
    }

    @Test
    void getUsernameFromJWT_throws_forTokenSignedWithAnotherKey() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(OTHER_SECRET);
        String foreignToken = otherProvider.generateToken("erin", List.of("ROLE_USER"));

        // Parser un token dont la signature est invalide doit lever une exception.
        assertThatThrownBy(() -> provider.getUsernameFromJWT(foreignToken))
                .isInstanceOf(JwtException.class);
    }
}
