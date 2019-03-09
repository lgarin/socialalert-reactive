package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.picture.PictureFileProcessor;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.video.SnapshotVideoFileProcessor;
import com.bravson.socialalert.domain.media.MediaSizeVariant;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.TempFileFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(propagation=Propagation.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaFileStore {

	@Autowired
	@NonNull
	PictureFileProcessor pictureFileProcessor;
	
	@Autowired
	@NonNull
	SnapshotVideoFileProcessor videoFileProcessor;
	
	@Autowired
	@NonNull
	FileStore fileStore;
	
	public FileMetadata buildFileMetadata(@NonNull File file, @NonNull MediaFileFormat fileFormat) throws IOException {
		String md5 = fileStore.computeMd5Hex(file);
		return FileMetadata.builder()
			.md5(md5)
			.timestamp(Instant.now())
			.contentSize(file.length())
			.fileFormat(fileFormat)
			.build();
	}
	
	public MediaMetadata buildMediaMetadata(@NonNull File inputFile, @NonNull MediaFileFormat fileFormat) throws Exception {
		MediaFileProcessor processor =  MediaFileFormat.VIDEO_SET.contains(fileFormat) ? videoFileProcessor : pictureFileProcessor;
		return processor.parseMetadata(inputFile);
	}
	
	public FileMetadata storeVariant(@NonNull File inputFile, @NonNull FileMetadata fileMetadata, @NonNull MediaSizeVariant sizeVariant) throws IOException {
		switch (sizeVariant) {
		case MEDIA:
			return storeMedia(inputFile, fileMetadata);
		case PREVIEW:
		case THUMBNAIL:
			return storedDerivedMedia(inputFile, fileMetadata, sizeVariant);
		default:
			throw new IllegalArgumentException();
		}
	}

	private FileMetadata storedDerivedMedia(File inputFile, FileMetadata fileMetadata, MediaSizeVariant sizeVariant) throws IOException {
		MediaFileProcessor processor = fileMetadata.isVideo() ? videoFileProcessor : pictureFileProcessor;
		TempFileFormat tempFormat = new TempFileFormat(processor.getFormat(sizeVariant));
		File tempFile = fileStore.createEmptyFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), tempFormat);
		MediaFileFormat fileFormat = processor.createVariant(inputFile, tempFile, sizeVariant);
		File outputFile = fileStore.changeFileFormat(fileMetadata.getMd5(), fileMetadata.getTimestamp(), tempFormat, fileFormat);
		return buildFileMetadata(outputFile, fileFormat);
	}

	private FileMetadata storeMedia(File inputFile, FileMetadata fileMetadata) throws IOException {
		fileStore.storeFile(inputFile, fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat());
		return fileMetadata;
	}
}
