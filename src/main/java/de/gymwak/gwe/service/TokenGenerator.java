package de.gymwak.gwe.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenGenerator {
	private SecureRandom rnd;

	@Value("${gwe.token-length}")
	private int tokenLength;

	@Autowired
	public TokenGenerator(SecureRandom rnd) {
		this.rnd = rnd;
	}

	public String nextToken() {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokenLength; i++) {
			char c = chars[rnd.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}
}
