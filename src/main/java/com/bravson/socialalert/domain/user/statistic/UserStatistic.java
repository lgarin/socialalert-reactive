package com.bravson.socialalert.domain.user.statistic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStatistic {

	private int fileCount;
	
	private int pictureCount;
	
	private int videoCount;
	
	private int commentCount;
	
	private int hitCount;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private int followerCount;
}
