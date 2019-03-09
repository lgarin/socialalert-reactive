package com.bravson.socialalert.business.file;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper=false)
public class NewFileEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 1L;

	@NonNull
	private final FileEntity newFile;
	
	private NewFileEvent(@NonNull FileEntity newFile) {
		super(newFile);
		this.newFile = newFile;
	}
	
	public static NewFileEvent of(@NonNull FileEntity newFile) {
		return new NewFileEvent(newFile);
	}
}
