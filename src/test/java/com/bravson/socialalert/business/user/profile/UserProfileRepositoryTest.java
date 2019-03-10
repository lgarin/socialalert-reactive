package com.bravson.socialalert.business.user.profile;

import java.time.Instant;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.DatabaseConfiguration;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.event.NewFileEvent;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.UserInfo;

@RunWith(SpringRunner.class)
@DataMongoTest
@EnableAutoConfiguration
@ContextConfiguration(classes= {DatabaseConfiguration.class,UserProfileRepository.class})
@Transactional
public class UserProfileRepositoryTest extends Assertions {
    
	@Autowired
	UserProfileRepository repository;

	private UserInfo createTestUserInfo() {
		return UserInfo.builder()
				.id("test")
				.username("test")
				.email("test@test.com")
				.createdTimestamp(Instant.now())
				.online(false)
				.build();
	}
    
    @Test
    public void createNonExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	assertThat(entity).isNotNull();
    	assertThat(entity.getId()).isEqualTo("test");
    }
    
    @Test(expected=DuplicateKeyException.class)
    public void createExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	repository.createProfile(userInfo, "1.2.3.4");
    	repository.createProfile(userInfo, "1.2.3.4");
    }
    
    @Test
    public void findNonExistingProfile() {
    	Optional<UserProfileEntity> entity = repository.findByUserId("test");
    	assertThat(entity).isEmpty();
    }
    
    @Test
    public void findExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	Optional<UserProfileEntity> result = repository.findByUserId("test");
    	assertThat(result).isNotEmpty().hasValue(entity);
    }
    
    @Test
    public void handleNewFileEvent() {
    	UserProfileEntity profile = repository.createProfile(createTestUserInfo(), "1.2.3.4");
		MediaMetadata metadata = MediaMetadata.builder().width(100).height(100).build();
		FileEntity file = new FileEntity("testFile", metadata, UserAccess.of(profile.getId(), "1.2.3.4"));
    	repository.handleNewFile(NewFileEvent.of(file));
    	assertThat(repository.findByUserId(profile.getId()).map(p -> p.getStatistic().getFileCount())).hasValue(1);
    }
    
    @Test
    public void handleNewFileEventWithUnknownUser() {
		MediaMetadata metadata = MediaMetadata.builder().width(100).height(100).build();
		FileEntity file = new FileEntity("test", metadata, UserAccess.of("X", "1.2.3.4"));
    	repository.handleNewFile(NewFileEvent.of(file));
    }
}
