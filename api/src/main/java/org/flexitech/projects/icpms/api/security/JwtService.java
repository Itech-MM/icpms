package org.flexitech.projects.icpms.api.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

    private static final String ROLES_CLAIM = "roles";
    private static final String SESSION_TOKEN_CLAIM = "sessionToken";
    public static final String SCOPES_CLAIM = "scopes";
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${app.jwt.issuer}")
    private String jwtIssuer;

    public String generateToken(String username,
                                Collection<? extends GrantedAuthority> authorities,
                                Set<String> scopes,
                                String sessionToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
        claims.put(SESSION_TOKEN_CLAIM, sessionToken);
        claims.put(ROLES_CLAIM, authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put(SCOPES_CLAIM, String.join(",", scopes));
        return buildToken(claims, username, jwtExpirationMs);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
        return buildToken(claims, username, jwtExpirationMs);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH);
        return buildToken(claims, username, refreshExpirationMs);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        extraClaims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
        return buildToken(extraClaims, username, jwtExpirationMs);
    }

    public String generateToken(String username, String sessionToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS);
        claims.put(SESSION_TOKEN_CLAIM, sessionToken);
        return buildToken(claims, username, jwtExpirationMs);
    }

    private String buildToken(Map<String, Object> claims,
                              String subject,
                              long expiration) {
        return Jwts.builder()
                .setIssuer(jwtIssuer)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public long getExpirationMillisFromToken(String token) {
        return extractClaim(token, Claims::getExpiration).getTime();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername != null
                    && extractedUsername.equals(username)
                    && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            String type = extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
            return type == null || TOKEN_TYPE_ACCESS.equals(type);
        } catch (Exception e) {
            log.warn("Failed to extract token type: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
            return TOKEN_TYPE_REFRESH.equals(type);
        } catch (Exception e) {
            log.warn("Failed to extract token type: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get(ROLES_CLAIM, List.class));
    }

    public String extractScopes(String token) {
        return extractClaim(token, claims -> claims.get(SCOPES_CLAIM, String.class));
    }

    public String extractSessionToken(String token) {
        return extractClaim(token, claims -> claims.get(SESSION_TOKEN_CLAIM, String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}