package io.todo.api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Objects;

public class JwtUtils {


    /**
     * Util method to generate a email verification token
     * @param username
     * @return String
     */
    public static String generateAuthorizationToken(String username, JwtBean jwtBean) {
        Date iat = new Date();
        Date exp = new Date(System.currentTimeMillis() + jwtBean.getExpirationTimeInMillis());

        return Jwts.builder()
                    .setSubject(username)
                    .setIssuer(jwtBean.getIssuer())
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .signWith(SignatureAlgorithm.HS512, jwtBean.getKey())
                    .compact();
    }

    /**
     * Util method to generate a email verification token
     * @param username
     * @return String
     */
    public static String generateVerificationToken(String username, JwtBean jwtBean) {
        Date iat = new Date();
        Date exp = new Date(System.currentTimeMillis() + jwtBean.getVerificationExpirationTimeInMillis());

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtBean.getIssuer())
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, jwtBean.getKey())
                .compact();
    }

    /**
     * Method is to verify is the email token is valid
     * @param token
     * @param verificationToken
     * @param jwtBean
     * @return boolean
     */
    public static boolean validateToken(String token, String verificationToken, JwtBean jwtBean) {
        Objects.requireNonNull(token);
        if (!Objects.equals(token, verificationToken))
            return false;

        // If tokens are equal, it is a valid token
        // We check the expiration to check if the token is still valid in context of time
        Claims claims = getClaims(token, jwtBean);

        Date expiration = claims.getExpiration();
        return expiration.after(new Date());
    }

    /**
     * Method to valid Bearer Token for Auth
     * @param username
     * @param token
     * @param jwtBean
     * @return boolean
     */
    public static boolean validateBearerToken(String username, String token, JwtBean jwtBean) {
        Claims claims = getClaims(token, jwtBean);
        String subject = claims.getSubject();
        String issuer = claims.getIssuer();
        Date expiration = claims.getExpiration();

        if (Objects.equals(subject, username) && Objects.equals(issuer, jwtBean.getIssuer()) && expiration.after(new Date()))
            return true;

        return false;
    }


    private static Claims getClaims(String token, JwtBean jwtBean) {
        return Jwts.parser()
                   .setSigningKey(jwtBean.getKey())
                   .parseClaimsJws(token)
                   .getBody();
    }

}
