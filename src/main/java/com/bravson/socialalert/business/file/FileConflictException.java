package com.bravson.socialalert.business.file;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.CONFLICT)
public class FileConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
