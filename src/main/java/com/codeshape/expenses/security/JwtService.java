package com.codeshape.expenses.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
/**
 * Service to generate and validate JWT tokens.
 */
@Service
public class JwtService {

    // Secret key (should be stored securely in env/config for production)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity (e.g., 24 hours)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Generate JWT for a given user email.
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)  // Add role claim here
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Extract email (subject) from the given JWT token.
     */
    public String extractEmail(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getSubject();
        } catch (Exception e) {
            return null; // Token is invalid or expired
        }
    }

    /**
     * Extract the user role from the given JWT token.
     */
    public String extractRole(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().get("role", String.class);
        } catch (Exception e) {
            return null; // Token is invalid or expired
        }
    }
}