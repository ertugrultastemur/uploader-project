package com.example.security.config;

import com.example.security.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  public String generateRefreshToken(
      UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  public String extractRoles(String token) {
    Claims claims = extractAllClaims(token);
    Object role = claims.get("role");

    if (role instanceof String) {
      return  role.toString();
    } else if (role instanceof List) {
      List<String> roleNames = ((List<?>) role).stream()
              .filter(r -> r instanceof Role)
              .map(r -> "ROLE_" + ((Role) r).name())
              .collect(Collectors.toList());

      return String.join(",", roleNames);
    }

    return ""; // Eğer roller yoksa veya uygun bir biçimde değilse boş bir String döndür
  }



  private List<GrantedAuthority> parseRolesClaim(String rolesClaim) {
    List<GrantedAuthority> authorities = new ArrayList<>();

    // rolesClaim içindeki rolleri ayrıştırın ve GrantedAuthority listesine ekleyin
    // Örneğin, rolleri "ROLE_" ile başlatarak SimpleGrantedAuthority nesneleri oluşturabilirsiniz

    // Örnek olarak:
     String[] roles = rolesClaim.split(",");
     for (String role : roles) {
         authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
     }

    return authorities;
  }

  private List<GrantedAuthority> extractAuthoritiesFromToken(String token) {

      // JWT token'dan rolleri çıkartın ve authorities listesine ekleyin
    // Örneğin, "roles" claim'ini kontrol ederek rolleri çıkartabilirsiniz

    // Örnek olarak:
     String rolesClaim = extractRoles(token);

      return new ArrayList<>(parseRolesClaim(rolesClaim));
  }

  public Authentication createAuthentication(String token, UserDetails userDetails) {
    // JWT token'dan rolleri çıkartın (örneğin, "roles" claim'ini kontrol ederek)
    List<GrantedAuthority> authorities = extractAuthoritiesFromToken(token);

    // Spring Security için Authentication oluşturun
    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
  }
  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    String role = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    extraClaims.put("role", role);
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
