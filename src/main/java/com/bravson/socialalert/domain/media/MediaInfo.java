package com.bravson.socialalert.domain.media;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description="The meta information for the media.")
@Data
public class MediaInfo implements UserContent {

	private String mediaUri;
    
    private MediaKind kind;
    
    private String creatorId;

    private String title;
    
    private String description;
	
    @Schema(description="The media timestamp in milliseconds since the epoch.")
    @JsonFormat(shape = Shape.NUMBER_INT)
	private Instant creation;
	 
    @Schema(description="The upload timestamp in milliseconds since the epoch.")
    @JsonFormat(shape = Shape.NUMBER_INT)
	private Instant timestamp;
	
	private MediaFileFormat fileFormat;
	
	private MediaFileFormat previewFormat;
	
	@Schema(description="The duration of the video in milliseconds.", implementation=Long.class)
	@JsonFormat(shape = Shape.NUMBER_INT)
	private Duration duration;

	private Double longitude;
	
	private Double latitude;
	
	private String locality;
	
	private String country;
	
	private String cameraMaker;
	
	private String cameraModel;
	
	private int hitCount;
	
	private int likeCount;
	
	private int dislikeCount;
	
	private int commentCount;
	
	private List<String> categories;
	
	private List<String> tags;
	
	private UserInfo creator;

	@JsonIgnore
	public boolean isVideo() {
		return MediaFileFormat.VIDEO_SET.contains(fileFormat);
	}
	
	@JsonIgnore
	public boolean isPicture() {
		return MediaFileFormat.PICTURE_SET.contains(fileFormat);
	}
	
	public boolean hasVideoPreview() {
		return MediaFileFormat.VIDEO_SET.contains(previewFormat);
	}

	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}
}
