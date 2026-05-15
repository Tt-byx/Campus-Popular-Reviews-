package com.meategg.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;


@Component
public class JwtUtils {
  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.expire}")
  private long expire;

  private SecretKey getSigningKey() {
    byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String createJwt(Long userId, String username, String role) {
    return Jwts.builder()
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expire * 1000))
        .setSubject(username)
        .claim("role", role != null ? role : "user")
        .claim("userId", userId)
        .signWith(getSigningKey())
        .compact();
  }
  
  public long getExpireInSeconds() {
    return expire;
  }

  public boolean isExpire(String jwt) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(jwt)
        .getBody()
        .getExpiration()
        .before(new Date());
  }
  public Claims parseJwt(String jwt) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(jwt)
            .getBody();
  }
  public String getUsername(String jwt) {
    return parseJwt(jwt).getSubject();
  }

  public String getRole(String jwt) {
    Object role = parseJwt(jwt).get("role");
    return role != null ? role.toString() : "user";
  }

  public Long getUserId(String jwt) {
    Object userId = parseJwt(jwt).get("userId");
    if (userId == null) return null;
    return Long.valueOf(userId.toString());
  }
}
//1
