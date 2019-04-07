package com.bravson.socialalert.business.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public final class FileUploadParameter implements Closeable {

	@NonNull 
	private File inputFile;
	
	@NonNull 
	private String contentType;
	
	@Override
	public void close() throws IOException {
		inputFile.delete();
	}
}
