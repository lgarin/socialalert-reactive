package com.bravson.socialalert.business.file;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.user.UserAccess;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@AllArgsConstructor
public class FileDeleteService {

	@NonNull
	FileRepository fileRepository;
	
	@NonNull
	FileStore fileStore;
	
	public boolean deleteFile(String fileUri, UserAccess userAccess) {
		return fileRepository.findFile(fileUri).map(f -> f.markDelete(userAccess)).orElse(false);
	}

	//@Schedule(minute="*/5", hour="*")
    public void automaticTimeout() {
        System.out.println("Cleanup file timer");
        // TODO find old files which have not been claimed
        // TODO delete files
    }
}
