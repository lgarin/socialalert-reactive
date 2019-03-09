package com.bravson.socialalert.business.file.video;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper=false)
public class AsyncVideoPreviewEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 1L;

	private final String fileUri;
	
	private AsyncVideoPreviewEvent(@NonNull String fileUri) {
		super(fileUri);
		this.fileUri = fileUri;
	}
	
	public static AsyncVideoPreviewEvent of(@NonNull String fileUri) {
		return new AsyncVideoPreviewEvent(fileUri);
	}
}
