package com.bravson.socialalert.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND)
public class MediaFileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
