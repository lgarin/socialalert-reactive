package com.bravson.socialalert.domain.media.comment;

import java.time.Instant;

import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="The comment information.")
@Data
public class MediaCommentInfo implements UserContent {

	private String id;
	
	private String creatorId;
	
	@Schema(description="The media timestamp in milliseconds since the epoch.")
	@JsonFormat(shape = Shape.NUMBER_INT)
	private Instant creation;
	
	private String comment;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private UserInfo creator;
}
