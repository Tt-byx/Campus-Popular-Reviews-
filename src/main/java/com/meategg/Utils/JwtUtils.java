package com.meategg.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtils {
  private final String key = "123654789";
  private final long expire = 60 * 60 * 24;

  public String createJwt(String username) {
    return Jwts.builder()
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expire * 1000))
        .setSubject(username)
        .signWith(SignatureAlgorithm.HS256, key)
        .compact();
  }
  public String getUsername(String jwt) {
    return Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(jwt)
        .getBody()
        .getSubject();
  }
  public boolean isExpire(String jwt) {
    return Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(jwt)
        .getBody()
        .getExpiration()
        .before(new Date());
  }



}
