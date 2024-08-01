package com.csvmanager.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class JWTService {
  private static final String SECRET_KEY = "ZnhleHJ6OGR0amc5ZzA1Z3o3bDA4YWJuMnJldWJobXFyazR3dXc2ajEyamk2OTNyeWltNHkyMmZibXdyNW1pbXJxamQ3cmRpbXh3dnh2dGwyZzk4OWxmOTRxajBuaHNmZ2prbWRsZjZobDFrMnZraDhwZDQ0MzNudWQ3MXhyM3A=";

  public String extractUserEmail(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  public String generateToken(UserDetailsImpl userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetailsImpl userDetails) {
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetailsImpl userDetails){
    final String userName = extractUserEmail(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsTFunction){
    final Claims claims = extractAllClaims(jwtToken);
    return claimsTFunction.apply(claims);
  }

  private Claims extractAllClaims(String jwtToken) {
    return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(jwtToken)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
