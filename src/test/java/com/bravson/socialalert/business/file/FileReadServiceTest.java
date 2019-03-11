package com.bravson.socialalert.business.file;

import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.BaseServiceTest;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class FileReadServiceTest extends BaseServiceTest {

	@InjectMocks
	FileReadService fileService;
	
	@Mock
	FileRepository mediaRepository;

	@Mock
	FileStore fileStore;

	@Test
	public void downloadNonExistingFile() throws IOException {
		String fileUri = "abc";
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.empty());
		
		Optional<FileResponse> result = fileService.download(fileUri);
		assertThat(result).isEmpty();
		
		verifyZeroInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingFile() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.MEDIA_JPG;
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(format).build();
		FileEntity entity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of("test", "1.2.3.4")).build();
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.download(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
	
	@Test
	public void downloadMissingPreview() throws IOException {
		String fileUri = "abc";
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity entity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of("test", "1.2.3.4")).build();
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		
		Optional<FileResponse> result = fileService.preview(fileUri);
		assertThat(result).isEmpty();
		
		verifyZeroInteractions(fileStore);
	}
	
	@Test
	public void downloadExistingThumbnail() throws IOException {
		String fileUri = "abc";
		File outputFile = new File("outfile");
		MediaFileFormat format = MediaFileFormat.THUMBNAIL_JPG;
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileMetadata thumbnailMetadata = FileMetadata.builder().md5("456").timestamp(Instant.MAX).contentSize(1000L).fileFormat(format).build();
		FileEntity entity = FileEntity.builder().fileMetadata(fileMetadata).mediaMetadata(mediaMetadata).userAccess(UserAccess.of("test", "1.2.3.4")).fileVariant(thumbnailMetadata).build();
		
		when(mediaRepository.findFile(fileUri)).thenReturn(Optional.of(entity));
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), thumbnailMetadata.getFileFormat())).thenReturn(outputFile);
		
		Optional<FileResponse> result = fileService.thumbnail(fileUri);
		assertThat(result).hasValue(FileResponse.builder().file(outputFile).format(format).temporary(false).build());
	}
}
