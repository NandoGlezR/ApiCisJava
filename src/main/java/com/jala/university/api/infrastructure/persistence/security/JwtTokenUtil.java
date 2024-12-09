package com.jala.university.api.infrastructure.persistence.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the given JWT token using the provided resolver function.
     *
     * @param token the JWT token
     * @param claimsResolver the resolver function to extract the claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Gets all claims from  
     the given JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims getAllClaimsFromToken(String token) {
        Key key = JwtKeyUtil.getKeyFromSecret(secretKey);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Gets  
     the expiration date from the given JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}


