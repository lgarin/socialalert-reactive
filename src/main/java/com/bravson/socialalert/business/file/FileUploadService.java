package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.event.AsyncVideoPreviewEvent;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.MediaSizeVariant;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.messaging.EventMessagingBridge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileUploadService {
	
	@Autowired
	FileRepository mediaRepository;
	
	@Autowired
	MediaFileStore mediaFileStore;
	
	@Autowired
	EventMessagingBridge messagingBridge;
	
	@Autowired
	UserInfoService userService;
	
	Logger logger = LoggerFactory.getLogger(getClass());

	private FileInfo handleExistingFile(FileEntity file, UserAccess userAccess) {
		if (!mediaRepository.markUploaded(file, userAccess)) {
			throw new FileConflictException();
		}
		return file.toFileInfo();
	}
	
	public FileInfo uploadMedia(@NonNull FileUploadParameter parameter, @NonNull UserAccess userAccess) throws IOException {
		MediaFileFormat fileFormat = MediaFileFormat.fromMediaContentType(parameter.getContentType()).orElseThrow(UnsupportedFormatException::new);
		MediaMetadata mediaMetadata = buildMediaMetadata(parameter.getInputFile(), fileFormat).orElseThrow(UnsupportedFormatException::new);
		
		FileMetadata fileMetadata = mediaFileStore.buildFileMetadata(parameter.getInputFile(), fileFormat);
		
		Optional<FileEntity> existingEntity = mediaRepository.findFile(fileMetadata.buildFileUri());
		if (existingEntity.isPresent()) {
			return userService.fillUserInfo(handleExistingFile(existingEntity.get(), userAccess));
		}
		
		FileEntity newEntity = storeNewFile(parameter.getInputFile(), fileMetadata, mediaMetadata, userAccess);
		return userService.fillUserInfo(newEntity.toFileInfo());
	}

	private FileEntity storeNewFile(File inputFile, FileMetadata fileMetadata, MediaMetadata mediaMetadata, UserAccess userAccess) throws IOException {
		List<FileMetadata> fileList = List.of(
				mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA),
				mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL),
				mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW));
		FileEntity fileEntity = mediaRepository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, fileList, userAccess);
		
		if (fileMetadata.isVideo()) {
			messagingBridge.sendMessage(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		}
		
		return fileEntity;
	}

	private Optional<MediaMetadata> buildMediaMetadata(File inputFile, MediaFileFormat fileFormat) throws IOException {
		try {
			return Optional.of(mediaFileStore.buildMediaMetadata(inputFile, fileFormat));
		} catch (Exception e) {
			logger.info("Cannot extract metadata", e);
			return Optional.empty();
		}
	}
}
