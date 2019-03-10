package com.bravson.socialalert.business.file.event;

import org.springframework.context.ApplicationEvent;

import com.bravson.socialalert.business.file.entity.FileEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper=false)
public class NewFileEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 1L;

	@NonNull
	private final FileEntity file;
	
	private NewFileEvent(@NonNull FileEntity file) {
		super(file);
		this.file = file;
	}
	
	public static NewFileEvent of(@NonNull FileEntity file) {
		return new NewFileEvent(file);
	}
}
