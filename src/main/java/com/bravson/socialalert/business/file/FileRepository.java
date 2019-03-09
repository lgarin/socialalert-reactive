package com.bravson.socialalert.business.file;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@AllArgsConstructor
public class FileRepository {

	@NonNull
	MongoOperations operations;
	
	@NonNull
    ApplicationEventPublisher eventPublisher;

	public FileEntity storeMedia(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata, @NonNull UserAccess userAccess) {
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, userAccess);
		operations.insert(entity);
		eventPublisher.publishEvent(NewFileEvent.of(entity));
		return entity;
	}
	
	public Optional<FileEntity> findFile(@NonNull String fileUri) {
		return Optional.ofNullable(operations.findById(fileUri, FileEntity.class));
	}
	
	public List<FileEntity> findByIpAddressPattern(@NonNull String ipAddressPattern) {
		return operations.find(query(where("versionInfo.ipAddress").regex("^" + Pattern.quote(ipAddressPattern))), FileEntity.class);
	}
	
	public List<FileEntity> findByUserIdAndState(@NonNull String userId, @NonNull FileState state) {
		return operations.find(query(where("versionInfo.userId").is(userId).and("state").is(state)), FileEntity.class);
	}
}
