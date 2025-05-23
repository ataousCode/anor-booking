package com.tadalatestudio.jwt;


import com.tadalatestudio.config.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final SecurityProperties securityProperties;
    private Key key;

    @PostConstruct
    public void init() {
        if (securityProperties.getJwt().getSecret() != null && !securityProperties.getJwt().getSecret().isEmpty()) {
            // For backward compatibility, use the configured secret if it's strong enough
            byte[] keyBytes = securityProperties.getJwt().getSecret().getBytes();
            if (keyBytes.length * 8 >= 512) {
                this.key = Keys.hmacShaKeyFor(keyBytes);
                log.info("Using configured JWT secret key");
            } else {
                // Generate a secure key for HS512 as recommended in the error message
                this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
                log.warn("Configured JWT secret key is too weak for HS512. Generated a secure key instead.");
            }
        } else {
            // Generate a secure key for HS512
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            log.info("Generated secure JWT key for HS512");
        }
    }

    public String generateToken(Authentication authentication) {
        String username;
        Collection<? extends GrantedAuthority> authorities;

        // Handle both String and User principals
        if (authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            username = userDetails.getUsername();
            authorities = userDetails.getAuthorities();
        } else {
            // When the principal is a String (e.g., during token refresh)
            username = authentication.getPrincipal().toString();
            authorities = authentication.getAuthorities();
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + securityProperties.getJwt().getExpirationMs());

        String authoritiesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authoritiesString)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + securityProperties.getJwt().getRefreshExpirationMs());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .filter(auth -> !auth.trim().isEmpty())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            throw new SecurityException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new SecurityException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
            throw new SecurityException("JWT token is expired");
        } catch (UnsupportedJwtException ex) {
            log.error("JWT token is unsupported: {}", ex.getMessage());
            throw new SecurityException("JWT token is unsupported");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new SecurityException("JWT claims string is empty");
        }
    }

    public long getExpirationFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().getTime();
    }

}
