package cm.bogne_stanley.money_flow.security;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import cm.bogne_stanley.money_flow.domain.model.RefreshTokenModel;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenUtils {
    private final static String SECRET = "stanley_bogne_money_flow_application_secret_key_2024";
    private final static long EXPIRATION_MINUTE = 60; // 24 hours
    private final static long REFRESH_EXPIRATION_DAY = 7; // 7 days
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(String email) {
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(new Date().toInstant().plus(Duration.ofMinutes(EXPIRATION_MINUTE))))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public RefreshTokenModel createRefreshToken() {
        return new RefreshTokenModel(
            UUID.randomUUID().toString(),
            Instant.now().plus(Duration.ofDays(REFRESH_EXPIRATION_DAY))
        );
    }

    public String getEmailFromToken(String token) {
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
