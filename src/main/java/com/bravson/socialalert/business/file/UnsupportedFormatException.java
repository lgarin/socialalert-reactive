package com.bravson.socialalert.business.file;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
