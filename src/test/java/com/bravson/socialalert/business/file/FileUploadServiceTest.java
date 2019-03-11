package com.bravson.socialalert.business.file;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import com.bravson.socialalert.business.BaseServiceTest;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.entity.FileState;
import com.bravson.socialalert.business.file.event.AsyncVideoPreviewEvent;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.MediaSizeVariant;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.messaging.EventMessagingBridge;

public class FileUploadServiceTest extends BaseServiceTest {

	@InjectMocks
	FileUploadService fileUploadService;
	
	@Mock
	FileRepository mediaRepository;
	
	@Mock
	MediaFileStore mediaFileStore;
	
	@Mock
	EventMessagingBridge eventMessagingBridge;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	Logger logger;
	
	@Test
	public void uploadExistingPictureWithSameUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of("test2", "4.3.2.1")).build();
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		when(mediaRepository.markUploaded(fileEntity, UserAccess.of(userId, ipAddress))).thenReturn(true);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result).isEqualTo(fileEntity.toFileInfo());
		verifyZeroInteractions(eventMessagingBridge, logger);
	}
	
	@Test
	public void uploadExistingPictureWithDifferentUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of("test2", "4.3.2.1")).build();
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(FileConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		
		verifyZeroInteractions(eventMessagingBridge, logger);
	}
	
	@Test
	public void uploadExistingPictureWithClaimedState() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of(userId, ipAddress)).state(FileState.CLAIMED).build();
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(FileConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		
		verifyZeroInteractions(eventMessagingBridge, logger);
	}
	
	@Test
	public void uploadNewPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).timestamp(Instant.EPOCH).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		
		FileEntity fileEntity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of(userId, ipAddress)).build();
		when(mediaRepository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata, fileMetadata, fileMetadata), UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verifyZeroInteractions(eventMessagingBridge, logger);
	}
	
	@Test
	public void uploadInvalidPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenThrow(Exception.class);
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(UnsupportedFormatException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		verify(logger).info(eq("Cannot extract metadata"), any(Exception.class));
		verifyZeroInteractions(eventMessagingBridge, userService);
	}
	
	@Test
	public void uploadNewVideo() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0236.MOV");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_MOV;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).duration(Duration.ofHours(2L)).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		
		FileEntity fileEntity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of(userId, ipAddress)).build();
		when(mediaRepository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata, fileMetadata, fileMetadata), UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.MOV_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verify(eventMessagingBridge).sendMessage(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		verifyZeroInteractions(logger);
	}
}
