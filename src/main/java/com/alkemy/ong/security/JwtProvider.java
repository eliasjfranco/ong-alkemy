package com.alkemy.ong.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import com.alkemy.ong.model.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

	private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);
	@Autowired
	private MessageSource messageSource;

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	@Value("${jwt.authorities.key}")
	private String AUTHORITIES_KEY;

	@Value("${jwt.expiration}")
	private int expiration;

	public String generatedToken(Authentication authentication) {
		final String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		return Jwts.builder().setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + expiration * 1000L))
				.compact();
	}

	public String getEmailFromToken(String token) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
	}

	public Boolean validateToken(String token) throws Exception {

		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
			return true;
		} catch (MalformedJwtException e) {
			logger.error(messageSource.getMessage("jwt.error.token.malformed", null, Locale.getDefault()));
		} catch (UnsupportedJwtException e) {
			logger.error(messageSource.getMessage("jwt.error.token.unsupported", null, Locale.getDefault()));
		} catch (ExpiredJwtException e) {
			logger.error(messageSource.getMessage("jwt.error.token.expired", null, Locale.getDefault()));
		} catch (IllegalArgumentException e) {
			logger.error(messageSource.getMessage("jwt.error.token.notFound", null, Locale.getDefault()));
		} catch (SignatureException e) {
			logger.error(messageSource.getMessage("jwt.error.token.failure", null, Locale.getDefault()));
		}
		return false;
	}

	public UsernamePasswordAuthenticationToken getAuthentication(final String token,
																 final UserDetails userDetails) {
		final JwtParser jwtParser = Jwts.parser().setSigningKey(SECRET_KEY);
		final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
		final Claims claims = claimsJws.getBody();
		final Collection<SimpleGrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
	}
}
