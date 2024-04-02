package com.reuben.thejournalist.config;

import com.reuben.thejournalist.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String SECRET_KEY = "13885687424175862018959995878651138856874241758620189599958786511388568742417586201895999587865113885687424175862018959995878651"; // Secret key for signing JWTs

    private long jwtExpiration = 1000 * 60 * 60 * 24; // 24 hours
    private long refreshExpiration = 1000 * 60 * 60 * 24 * 30; // 30 days

    public String extractEmail(String jwt) {
        return extractClaim(jwt, Claims::getSubject); // Extracts the subject from the JWT
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken); // Extracts all claims from the JWT
        return claimsResolver.apply(claims); // Applies the claims resolver to the claims
    }

    public String generateToken(UserEntity userData,UserDetails userDetails) {
        return generateToken(userData);
    }

    public String generateToken(
            UserEntity userData
    ) {
        Map<String, Object> newClaim = new HashMap<>();
        // Assume your UserDetails implementation has a method to get additional data
        // For example, let's add roles and a custom user ID
//        newClaim.put("roles", userData.getRole().stream().map(Enum::name).collect(Collectors.toList()));
        newClaim.put("userId", userData.getIdAsString()); // Cast to your custom UserDetails implementation
        newClaim.put("sub", userData.getEmail());
        return buildToken(newClaim,  jwtExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), refreshExpiration);
    }


    // Builds a JWT with the given claims, user details, and expiration time
    public String buildToken(Map<String, Object> claims, long expiration) {

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String email = extractEmail(jwt); // Extracts the email from the JWT
        return (Objects.equals(email, userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration); // Extracts the expiration date from the JWT
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser() // Creates a new parser for parsing JWTs
                .setSigningKey(getSignInKey())   // Sets the signing key
                .build() // Builds the JWT
                .parseSignedClaims(jwt) // Parses the JWT
                .getPayload(); // Gets the payload
    }

    private Key getSignInKey() {
        byte[] secretBytes = SECRET_KEY.getBytes(); // Converts the secret key to bytes
        return Keys.hmacShaKeyFor(secretBytes); // Creates a new key from the secret key bytes
    }
}
