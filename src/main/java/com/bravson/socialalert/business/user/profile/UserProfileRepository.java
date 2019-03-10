package com.bravson.socialalert.business.user.profile;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.event.DeletedFileEvent;
import com.bravson.socialalert.business.file.event.NewFileEvent;
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
	
	private void incrementField(String userId, String fieldName, int delta) {
		operations.updateFirst(query(where("_id").is(userId)), new Update().inc(fieldName, delta), UserProfileEntity.class);
	}
	
	@EventListener
	void handleNewFile(NewFileEvent event) {
		incrementField(event.getFile().getUserId(), "statistic.fileCount", 1);
	}
	
	@EventListener
	void handleDeletedFile(DeletedFileEvent event) {
		incrementField(event.getFile().getUserId(), "statistic.fileCount", -1);
	}
}
