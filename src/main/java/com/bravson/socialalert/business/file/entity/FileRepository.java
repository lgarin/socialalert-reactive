package com.bravson.socialalert.business.file.entity;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.event.NewFileEvent;
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

	public FileEntity storeMedia(@NonNull String fileUri, @NonNull MediaMetadata mediaMetadata, List<FileMetadata> fileList, @NonNull UserAccess userAccess) {
		FileEntity entity = new FileEntity(fileUri, mediaMetadata, userAccess);
		fileList.forEach(entity::addVariant);
		operations.insert(entity);
		eventPublisher.publishEvent(NewFileEvent.of(entity));
		return entity;
	}
	
	public FileEntity addVariant(@NonNull FileEntity entity, @NonNull FileMetadata metadata) {
		entity.addVariant(metadata);
		return operations.save(entity);
	}
	
	public boolean markUploaded(@NonNull FileEntity entity, UserAccess userAccess) {
		if (entity.markUploaded(userAccess)) {
			operations.save(entity);
			return true;
		}
		return false;
	}
	
	public boolean markDeleted(@NonNull FileEntity entity, UserAccess userAccess) {
		if (entity.markDeleted(userAccess)) {
			operations.save(entity);
			return true;
		}
		return false;
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
