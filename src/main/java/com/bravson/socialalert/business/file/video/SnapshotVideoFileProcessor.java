package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import lombok.NonNull;

@Service
@Transactional(propagation=Propagation.SUPPORTS)
public class SnapshotVideoFileProcessor extends BaseVideoFileProcessor {

	public SnapshotVideoFileProcessor(@NonNull MediaConfiguration config) {
		super(config);
	}
	
	@Override
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		takeSnapshot(sourceFile, outputFile, config.getPreviewWidth(), config.getPreviewHeight());
		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
