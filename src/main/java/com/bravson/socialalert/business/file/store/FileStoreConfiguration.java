package com.bravson.socialalert.business.file.store;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties("store")
@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PUBLIC)
public class FileStoreConfiguration {

	String baseDirectory;
	
	public File getBaseDirectory() {
		return new File(baseDirectory);
	}
}
