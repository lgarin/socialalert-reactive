package com.bravson.socialalert.domain.user;

import java.time.Instant;
import java.time.LocalDate;

import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo {

	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@JsonFormat(shape = Shape.NUMBER_INT)
	@NonNull
	private Instant createdTimestamp;
	
	private boolean online;
	
	@JsonFormat(shape = Shape.NUMBER_INT)
	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String imageUri;
	
	private String biography;
	
	private UserStatistic statistic;
}
