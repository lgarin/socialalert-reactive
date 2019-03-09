package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class VersionInfo {

	@Indexed
	private String userId;
	
	@Indexed
	private String ipAddress;
	
	@Indexed
	private Instant creation;
	
	@Indexed
	private Instant lastUpdate;
	
	public static VersionInfo of(String userId, String ipAddress) {
		VersionInfo result = new VersionInfo();
		result.touch(userId, ipAddress);
		return result;
	}
	
	public void touch(String userId, String ipAddress) {
		this.userId = userId;
		this.ipAddress = ipAddress;
		lastUpdate = Instant.now();
		if (creation == null) {
			this.creation = lastUpdate;
		}
	}
}
