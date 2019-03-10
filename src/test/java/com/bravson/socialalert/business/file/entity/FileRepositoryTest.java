package com.bravson.socialalert.business.file.entity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.DatabaseConfiguration;
import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

@RunWith(SpringRunner.class)
@DataMongoTest
@EnableAutoConfiguration
@ContextConfiguration(classes= {DatabaseConfiguration.class,FileRepository.class})
@Transactional
public class FileRepositoryTest extends Assertions {
    
	@Autowired
    FileRepository repository;
	
    @Test
    public void findNonExistingFile() {
    	Optional<FileEntity> result = repository.findFile("xyz");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void persistValidFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity result = repository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata), userAccess);
    	assertThat(result.getId()).isEqualTo("19700101/xyz");
    }
    
    @Test
    public void findExistingFile() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	repository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata), userAccess);
    	Optional<FileEntity> result = repository.findFile("19700101/xyz");
    	assertThat(result).isNotEmpty();
    }
    
    @Test
    public void queryEmptyRepositoryByIpAddressPattern() {
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1.");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void queryByIpAddressPattern() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity entity = repository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata), userAccess);
    	
    	List<FileEntity> result = repository.findByIpAddressPattern("1.1.");
    	assertThat(result).containsOnly(entity);
    }
    
    @Test
    public void queryByUserIdAndState() {
    	FileMetadata fileMetadata = FileMetadata.builder().md5("xyz").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
    	MediaMetadata mediaMetadata = MediaMetadata.builder().width(1200).height(1600).build();
    	UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
    	FileEntity entity = repository.storeMedia(fileMetadata.buildFileUri(), mediaMetadata, List.of(fileMetadata), userAccess);
    	
    	List<FileEntity> result = repository.findByUserIdAndState("test", FileState.UPLOADED);
    	assertThat(result).containsOnly(entity);
    }
}
