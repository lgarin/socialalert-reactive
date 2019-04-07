package com.bravson.socialalert.rest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.bravson.socialalert.business.file.FileReadService;
import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="file")
@RestController
@RequestMapping("/file")
public class FileFacade extends BaseFacade {
	
	@Value("${file.maxUploadSize}")
	long maxUploadSize;
	
	@Value("${file.maxCacheAge}")
	int maxCacheAge;
	
	@Autowired
	FileReadService fileReadService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	FileSearchService fileSearchService;
	
	private ResponseEntity<FileSystemResource> createStreamResponse(FileResponse fileResponse, WebRequest webRequest) {
		String entityTag = fileResponse.getFormat().name();
		if (webRequest.checkNotModified(entityTag)) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(fileResponse.getFormat().getContentType()));
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileResponse.getFile().getName()).build());
		headers.setContentLength(fileResponse.getFile().length());
		headers.setETag(entityTag);
		if (fileResponse.isTemporary()) {
			headers.setCacheControl(CacheControl.maxAge(maxCacheAge, TimeUnit.SECONDS));
		}
		
		FileSystemResource body = new FileSystemResource(fileResponse.getFile());
		return ResponseEntity.ok().headers(headers).body(body);
	}
	
	@RequestMapping(value = "/download/{fileUri:.+}", method = RequestMethod.GET)
	@Operation(summary="Download a file in the same format as it has been uploaded.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.")
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public ResponseEntity<FileSystemResource> download(
			@Parameter(description="The relative file uri.", required=true)
			@NotEmpty @PathVariable("fileUri") String fileUri,
			@Autowired WebRequest webRequest) throws IOException {
		return createStreamResponse(fileReadService.download(fileUri).orElseThrow(MediaFileNotFoundException::new), webRequest);
	}
	
	@PermitAll
	@RequestMapping(value = "/preview/{fileUri : .+}", method = RequestMethod.GET)
	@Operation(summary="Download a file in the preview format.",
			description="For video media, the preview is initialy a still picture and the video preview is only created after a delay.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.", content= {@Content(mediaType=MediaFileConstants.JPG_MEDIA_TYPE), @Content(mediaType=MediaFileConstants.MP4_MEDIA_TYPE)})
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public ResponseEntity<FileSystemResource> preview(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathVariable("fileUri") String fileUri,
			@Autowired WebRequest webRequest) throws IOException {
		return createStreamResponse(fileReadService.preview(fileUri).orElseThrow(MediaFileNotFoundException::new), webRequest);
	}
	
	@RequestMapping(value = "/metadata/{fileUri : .+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary="Access the file metadata.")
	@ApiResponse(responseCode = "200", description = "File metadata are available in the response.", content=@Content(schema=@Schema(implementation=FileInfo.class)))
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public FileInfo getMetadata(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathVariable("fileUri") String fileUri,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws IOException {
		return fileSearchService.findFileByUri(fileUri).orElseThrow(MediaFileNotFoundException::new);
	}
	
	@RequestMapping(value = "/list/new", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary="List the new files for the current user.")
	@ApiResponse(responseCode = "200", description = "List of file metadata is available in the response.", content=@Content(array=@ArraySchema(schema=@Schema(implementation=FileInfo.class))))
	public List<FileInfo> listNewFiles(
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws IOException {
		return fileSearchService.findNewFilesByUserId(getUserAccess().getUserId());
	}
	
	@PermitAll
	@RequestMapping(value = "/thumbnail/{fileUri : .+}", method = RequestMethod.GET, produces = MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Download a jpeg thumbnail of the media.")
	@ApiResponse(responseCode = "200", description = "File will be streamed.")
	@ApiResponse(responseCode = "404", description = "No media exists with this uri.")
	public ResponseEntity<FileSystemResource> thumbnail(
			@Parameter(description="The relative file uri.", required=true) @NotEmpty @PathVariable("fileUri") String fileUri,
			@Autowired WebRequest webRequest) throws IOException {
		return createStreamResponse(fileReadService.thumbnail(fileUri).orElseThrow(MediaFileNotFoundException::new), webRequest);
	}
	
	private ResponseEntity<?> createUploadResponse(FileInfo fileInfo) {
		URI fileUri = URI.create("download/" + fileInfo.getFileUri());
		return ResponseEntity.created(fileUri).build();
	}
	
	@RequestMapping(value = "/upload/picture", method = RequestMethod.POST, consumes = MediaFileConstants.JPG_MEDIA_TYPE)
	@Operation(summary="Upload a picture file.")
    @ApiResponse(responseCode = "201", description = "The picture is ready to be claimed.", 
    				headers = @Header(name = "Location", description = "The media url", schema = @Schema(type="string", format="uri")))
    @ApiResponse(responseCode = "413", description = "The file is too large.")
	@ApiResponse(responseCode = "415", description = "The media is not in the expected format.")
	public ResponseEntity<?> uploadPicture(
			@RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull HttpEntity<Resource> inputMessage,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@Parameter(description="The content type of this picture.", required=true) @NotEmpty @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) throws IOException, ServletException {
		try (FileUploadParameter parameter = createUploadParameter(inputMessage.getBody(), contentType)) {
			return createUploadResponse(fileUploadService.uploadMedia(parameter, getUserAccess()));
		}
	}
	
	@RequestMapping(value = "/upload/video", method = RequestMethod.POST, consumes = {MediaFileConstants.MOV_MEDIA_TYPE, MediaFileConstants.MP4_MEDIA_TYPE})
	@Operation(summary="Upload a video file.")
    @ApiResponse(responseCode = "201", description = "The video is ready to be claimed.", 
		                   headers = @Header(name = "Location", description = "The media url", schema = @Schema(type="string", format="uri")))
    @ApiResponse(responseCode = "413", description = "The file is too large.")
    @ApiResponse(responseCode = "415", description = "The media is not in the expected format.")
	public ResponseEntity<?> uploadVideo(
		    @RequestBody(description="The file content must be included in the body of the HTTP request.", required=true) @NotNull HttpEntity<Resource> inputMessage,
			@Parameter(description="The authorization token returned by the login function.", required=true) @NotEmpty @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
			@Parameter(description="The content type of this video.", required=true) @NotEmpty @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType) throws IOException, ServletException {
		try (FileUploadParameter parameter = createUploadParameter(inputMessage.getBody(), contentType)) {
			return createUploadResponse(fileUploadService.uploadMedia(parameter, getUserAccess()));
		}
	}

	private FileUploadParameter createUploadParameter(Resource input, String contentType) throws IOException {
		if (input.contentLength() > maxUploadSize) {
			throw new MediaFileTooLargeException();
		}
		File tempFile = createTempFile(input);
		return FileUploadParameter.builder().inputFile(tempFile).contentType(contentType).build();
	}

	private File createTempFile(Resource input) throws IOException {
		File tempFile = File.createTempFile("upload", ".tmp");
		try {
			Files.copy(input.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			tempFile.delete();
		}
		return tempFile;
	}
}
