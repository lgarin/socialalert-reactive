package com.bravson.socialalert.business.file.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.MediaSizeVariant;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Document(collection="File")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileEntity extends VersionedEntity {

	@Getter
	@NonNull
	@Indexed
	private FileState state;
	
	private List<FileMetadata> fileVariants;
		
	@Getter
	@NonNull
	private MediaMetadata mediaMetadata;

	public FileEntity(@NonNull String fileUri, @NonNull MediaMetadata mediaMetadata, @NonNull UserAccess userAccess) {
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.id = fileUri;
		this.mediaMetadata = mediaMetadata;
		this.state = FileState.UPLOADED;
	}
	
	@Builder
	protected FileEntity(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata, @NonNull UserAccess userAccess, FileState state, @Singular List<FileMetadata> fileVariants) {
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.id = fileMetadata.buildFileUri();
		this.mediaMetadata = mediaMetadata;
		this.state = state != null ? state : FileState.UPLOADED;
		addVariant(fileMetadata);
		if (fileVariants != null) {
			fileVariants.forEach(this::addVariant);
		}
	}
	
	private Optional<FileMetadata> findFileMetadata(MediaSizeVariant sizeVariant) {
		if (fileVariants == null) {
			return Optional.empty();
		}
		return fileVariants.stream().filter(v -> v.getSizeVariant() == sizeVariant).max(Comparator.comparing(FileMetadata::isVideo));
	}
	
	public Optional<MediaFileFormat> findVariantFormat(@NonNull MediaSizeVariant sizeVariant) {
		return findFileMetadata(sizeVariant).map(FileMetadata::getFileFormat);
	}

	void addVariant(@NonNull FileMetadata metadata) {
		if (fileVariants == null) {
			fileVariants = new ArrayList<>();
		}
		fileVariants.add(metadata);
		versionInfo.touch(versionInfo.getUserId(), versionInfo.getIpAddress());
	}

	public FileMetadata getFileMetadata() {
		return findFileMetadata(MediaSizeVariant.MEDIA).orElseThrow(IllegalStateException::new);
	}

	public boolean isTemporary(MediaFileFormat format) {
		return getFileMetadata().isVideo() && format == MediaFileFormat.PREVIEW_JPG;
	}

	public boolean isVideo() {
		return getFileMetadata().isVideo();
	}
	
	public boolean isPicture() {
		return getFileMetadata().isPicture();
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
	
	public FileInfo toFileInfo() {
		FileInfo info = new FileInfo();
		info.setFileUri(getId());
		info.setFileFormat(getFileMetadata().getFileFormat());
		info.setContentSize(getFileMetadata().getContentSize());
		info.setCreatorId(getUserId());
		info.setTimestamp(getFileMetadata().getTimestamp());
		info.setLatitude(getMediaMetadata().getLatitude());
		info.setLongitude(getMediaMetadata().getLongitude());
		info.setCameraMaker(getMediaMetadata().getCameraMaker());
		info.setCameraModel(getMediaMetadata().getCameraModel());
		info.setHeight(getMediaMetadata().getHeight());
		info.setWidth(getMediaMetadata().getWidth());
		info.setDuration(getMediaMetadata().getDuration());
		info.setCreation(getMediaMetadata().getTimestamp());
		findVariantFormat(MediaSizeVariant.PREVIEW).ifPresent(info::setPreviewFormat);
		return info;
	}

	boolean markClaimed(UserAccess userAccess) {
		if (state != FileState.UPLOADED) {
			return false;
		} else if (!getUserId().equals(userAccess.getUserId())) {
			return false;
		}
		changeState(FileState.CLAIMED);
		return true;
	}
	
	boolean markDeleted(UserAccess userAccess) {
		if (state != FileState.UPLOADED) {
			return false;
		} else if (!getUserId().equals(userAccess.getUserId())) {
			return false;
		}
		changeState(FileState.DELETED);
		return true;
	}
	
	boolean markUploaded(UserAccess userAccess) {
		if (state == FileState.UPLOADED && getUserId().equals(userAccess.getUserId())) {
			return true;
		} else if (state != FileState.DELETED) {
			return false;
		}
		changeState(FileState.UPLOADED);
		return true;
	}
	
	private void changeState(@NonNull FileState newState) {
		state = newState;
		versionInfo.touch(versionInfo.getUserId(), versionInfo.getIpAddress());
	}
	
	public boolean isUploaded() {
		return state == FileState.UPLOADED;
	}
	
	public boolean isNotDeleted() {
		return state != FileState.DELETED;
	}
}
