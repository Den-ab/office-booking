package ru.otus.pw.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.otus.pw.config.jwt.JwtProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-that-is-long-enough-for-hs256-0123456789";

    private final JwtService jwtService = new JwtService(new JwtProperties(SECRET, 60_000));

    private final UserDetails alice = User.withUsername("alice@example.com").password("x").roles("USER").build();

    private final UserDetails bob = User.withUsername("bob@example.com").password("x").roles("USER").build();

    @Test
    @DisplayName("A generated token carries the user's email as subject")
    void generatedTokenContainsUsername() {
        String token = jwtService.generateToken(alice);

        assertThat(jwtService.extractUserName(token)).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("A token is valid only for the user it was issued to")
    void tokenIsValidOnlyForItsOwner() {
        String token = jwtService.generateToken(alice);

        assertThat(jwtService.isTokenValid(token, alice)).isTrue();
        assertThat(jwtService.isTokenValid(token, bob)).isFalse();
    }

    @Test
    @DisplayName("An expired token is rejected")
    void expiredTokenIsRejected() {
        JwtService expiredTokens = new JwtService(new JwtProperties(SECRET, -1_000));
        String token = expiredTokens.generateToken(alice);

        assertThatThrownBy(() -> expiredTokens.isTokenValid(token, alice))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("A token signed with another secret is rejected")
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtService otherIssuer = new JwtService(
            new JwtProperties("another-secret-that-is-also-long-enough-0123456789", 60_000)
        );
        String foreignToken = otherIssuer.generateToken(alice);

        assertThatThrownBy(() -> jwtService.extractUserName(foreignToken))
            .isInstanceOf(io.jsonwebtoken.security.SignatureException.class);
    }
}
