package com.bravson.socialalert.business.user.profile;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.NewFileEvent;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.UserInfo;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@AllArgsConstructor
public class UserProfileRepository {

	@NonNull
	MongoOperations operations;
	
	public Optional<UserProfileEntity> findByUserId(@NonNull String userId) {
		return Optional.ofNullable(operations.findById(userId, UserProfileEntity.class));
	}

	public UserProfileEntity createProfile(@NonNull UserInfo userInfo, @NonNull String ipAddress) {
		UserProfileEntity entity = new UserProfileEntity(userInfo.getUsername(), userInfo.getEmail(), UserAccess.of(userInfo.getId(), ipAddress));
		return operations.insert(entity);
	}
	
	@EventListener
	void handleNewFile(NewFileEvent event) {
		findByUserId(event.getNewFile().getUserId()).ifPresent(profile -> profile.addFile(event.getNewFile()));
	}
	/*
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMedia(media));
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		findByUserId(comment.getUserId()).ifPresent(profile -> profile.addComment(comment));
	}
	*/
}
