package com.bravson.socialalert.business.file.media;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties("media")
@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PUBLIC)
public class MediaConfiguration {

	long snapshotDelay;
	
	int thumbnailHeight;
	
	int thumbnailWidth;
	
	int previewHeight;
	
	int previewWidth;
	
	String watermarkFile;
	
	String encodingProgram;
}
