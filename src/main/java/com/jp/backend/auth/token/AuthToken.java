package com.jp.backend.auth.token;

import java.security.Key;
import java.util.Date;
import java.util.List;

import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthToken {
	@Getter
	private final String token;
	private final Key key;
	private static final String AUTHORITIES_KEY = "role";

	public AuthToken(String token, Key key) {
		this.key = key;
		this.token = token;
	}

	AuthToken(String id, Date expiry, Key key) {
		this.key = key;
		this.token = createAccessToken(id, expiry);
	}

	AuthToken(String id, String role, Date expiry, Key key) {
		this.key = key;
		this.token = createAccessToken(id, role, expiry);
	}

	AuthToken(String id, List<String> roles, Date expiry, Key key) {
		this.key = key;
		this.token = createAccessToken(id, roles, expiry);
	}

	private String createAccessToken(String id, Date expiry) {
		return Jwts.builder()
			.setSubject(id)
			.signWith(key,
				SignatureAlgorithm.HS256)
			.setExpiration(expiry)
			.compact();
	}

	private String createAccessToken(String id, String role, Date expiry) {
		return Jwts.builder()
			.setSubject(id)
			.claim(AUTHORITIES_KEY, role)
			.signWith(key, SignatureAlgorithm.HS256)
			.setExpiration(expiry)
			.compact();
	}

	private String createAccessToken(String id, List<String> roles, Date expiry) {
		return Jwts.builder()
			.setSubject(id)
			.claim(AUTHORITIES_KEY, roles)
			.signWith(key, SignatureAlgorithm.HS256)
			.setExpiration(expiry)
			.compact();
	}

	public boolean isTokenValid() {
		return getValidTokenClaims() != null;
	}

	public boolean isTokenExpired() {
		return getExpiredTokenClaims() != null;
	}

	public Claims getValidTokenClaims() {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
		} catch (io.jsonwebtoken.security.SignatureException e) {
			throw new CustomLogicException(ExceptionCode.TOKEN_INVALID);
		}
		return null;
	}

	public Claims getExpiredTokenClaims() {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			return e.getClaims();
		}
		return null;
	}
}
