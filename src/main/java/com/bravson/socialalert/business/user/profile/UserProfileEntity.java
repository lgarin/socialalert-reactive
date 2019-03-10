package com.bravson.socialalert.business.user.profile;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Document(collection="Profile")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserProfileEntity extends VersionedEntity {

	@Getter
	@Setter
	@NonNull
	@Indexed
	private String username;
	
	@Getter
	@Setter
	@NonNull
	@Indexed
	private String email;
	
	@Getter
	@Setter
	private LocalDate birthdate;
	
	@Getter
	@Setter
	private Gender gender;
	
	@Getter
	@Setter
	private String country;
	
	@Getter
	@Setter
	private String language;
	
	@Getter
	@Setter
	private String imageUri;
	
	@Getter
	@Setter
	private String biography;
	
	@Getter
	private UserStatistic statistic;

	public UserProfileEntity(@NonNull String username, @NonNull String email, @NonNull UserAccess userAccess) {
		this.id = userAccess.getUserId();
		this.username = username;
		this.email = email;
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.statistic = new UserStatistic();
	}
	
	public UserProfileEntity(@NonNull String id) {
		this.id = id;
	}
	
	public UserInfo toOnlineUserInfo() {
		return toUserInfo(true);
	}
	
	public UserInfo toOfflineUserInfo() {
		return toUserInfo(false);
	}
	
	private UserInfo toUserInfo(boolean online) {
		return UserInfo.builder()
				.id(id)
				.username(username)
				.email(email)
				.createdTimestamp(getCreation())
				.online(online)
				.biography(biography)
				.birthdate(birthdate)
				.country(country)
				.language(language)
				.imageUri(imageUri)
				.statistic(statistic)
				.build();
	}
	
	public Instant getCreation() {
		return versionInfo.getCreation();
	}
}
