package com.bravson.socialalert.business.file;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.entity.FileState;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@AllArgsConstructor
public class FileSearchService {

	@NonNull
	UserInfoService userService;
	
	@NonNull
	FileRepository fileRepository;
	
	public List<FileInfo> findNewFilesByUserId(@NonNull String userId) {
		return userService.fillUserInfo(fileRepository.findByUserIdAndState(userId, FileState.UPLOADED).stream().map(FileEntity::toFileInfo).collect(Collectors.toList()));
	}
	
	public Optional<FileInfo> findFileByUri(@NonNull String fileUri) {
		return userService.fillUserInfo(fileRepository.findFile(fileUri).map(FileEntity::toFileInfo));
	}
}
