package de.gymwak.gwe.model;

import java.io.Serializable;
import java.security.SecureRandom;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

public class GWEUserResetKey implements Serializable {

	private static SecureRandom random = new SecureRandom();

	private long userId;

	@NotEmpty(message = "Valid reset-key is required.")
	private String resetKey;

	@Min(0)
	private long expireTimeInMillis;

	public GWEUserResetKey() {
	}

	public GWEUserResetKey(GWEUserResetKey userResetKey) {
		this.userId = userResetKey.userId;
		this.expireTimeInMillis = userResetKey.expireTimeInMillis;
	}

	public long getResetId() {
		return userId;
	}

	public void setResetId(long resetId) {
		this.userId = resetId;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public long getExpireTime() {
		return expireTimeInMillis;
	}

	public void setExpireTime(long expireTimeInMillis) {
		this.expireTimeInMillis = expireTimeInMillis;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > expireTimeInMillis;
	}

	private static final long serialVersionUID = 4889124107685539222L;

	public static long calculateExpireTime() {
		return System.currentTimeMillis() + 86400000 /* 24h x 60min x 60sek x 1000ms */;
	}
	
	public static String generateRandomString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
}
