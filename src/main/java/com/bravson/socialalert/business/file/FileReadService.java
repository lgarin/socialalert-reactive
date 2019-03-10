package com.bravson.socialalert.business.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.domain.media.MediaSizeVariant;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@AllArgsConstructor
public class FileReadService {

	@NonNull
	FileRepository mediaRepository;

	@NonNull
	FileStore fileStore;
	
	private Optional<FileResponse> createFileResponse(String fileUri, MediaSizeVariant sizeVariant) throws IOException {
		FileEntity fileEntity = mediaRepository.findFile(fileUri).filter(FileEntity::isNotDeleted).orElse(null);
		if (fileEntity == null) {
			return Optional.empty();
		}
        
		MediaFileFormat fileFormat = fileEntity.findVariantFormat(sizeVariant).orElse(null);
		if (fileFormat == null) {
			return Optional.empty();
		}
		FileMetadata fileMetadata = fileEntity.getFileMetadata();
		File file = fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getTimestamp(), fileFormat);
		return Optional.of(new FileResponse(file, fileFormat, fileEntity.isTemporary(fileFormat)));
	}

	public Optional<FileResponse> download(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.MEDIA);
	}
	
	public Optional<FileResponse> preview(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.PREVIEW);
	}
	
	public Optional<FileResponse> thumbnail(@NonNull String fileUri) throws IOException {
		return createFileResponse(fileUri, MediaSizeVariant.THUMBNAIL);
	}
}
