package ru.otus.pw.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.otus.pw.config.jwt.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Сервис для работы с JWT.
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    //region Fields

    /**
     * Проперти.
     */
    private final JwtProperties jwtProperties;

    //endregion
    //region Public

    /**
     * Извлекает имя пользователя.
     *
     * @param token Токен.
     *
     * @return Имя пользователя.
     */
    public String extractUserName(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует JWT-токен из данных пользователя.
     *
     * @param userDetails Данные пользователя.
     *
     * @return JWT-токен.
     */
    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * Валидный ли токен?
     *
     * @param token Токен.
     * @param userDetails Данные пользователя.
     *
     * @return Да/нет.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUserName(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Извлекает и возвращает часть JWT-токена.
     *
     * @param token Токен.
     * @param claimsResolver Резолвер.
     *
     * @return Часть JWT-токена.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Проверяет, истек ли токен.
     *
     * @param token Токен.
     *
     * @return Да/нет.
     */
    private boolean isTokenExpired(String token) {

        return this.extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает и возвращает все части JWT-токена.
     *
     * @param token Токен.
     *
     * @return Части JWT-токена.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser().verifyWith(this.getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * Извлекает и возвращает дату истечения токена.
     *
     * @param token Токен.
     *
     * @return Дата истечения.
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Возвращает ключ подписи.
     *
     * @return Ключ подписи.
     */
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    //endregion
}
