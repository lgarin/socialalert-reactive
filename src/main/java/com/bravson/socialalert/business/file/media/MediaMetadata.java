package com.bravson.socialalert.business.file.media;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class MediaMetadata {

	@NonNull
	private Integer width;
	@NonNull
	private Integer height;
	private Instant timestamp;
	private Duration duration;
	@Indexed
	private Point coordinates;
	private String cameraMaker;
	private String cameraModel;

	public boolean hasCoordinates() {
		return coordinates != null;
	}
	
	public Double getLongitude() {
		return coordinates != null ? coordinates.getX() : null;
	}
	
	public Double getLatitude() {
		return coordinates != null ? coordinates.getY() : null;
	}
}
