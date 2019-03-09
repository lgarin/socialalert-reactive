package com.bravson.socialalert.business.file.picture;

import static com.bravson.socialalert.domain.media.format.MediaFileConstants.JPG_EXTENSION;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.business.file.media.MediaFileProcessor;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaUtil;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;

import lombok.NonNull;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Service
@Transactional(propagation=Propagation.SUPPORTS)
public class PictureFileProcessor implements MediaFileProcessor {
	
	private MediaConfiguration config;
	
	private BufferedImage watermarkImage;
	
	public PictureFileProcessor(@NonNull MediaConfiguration config) {
		this.config = config;
		watermarkImage = MediaUtil.readImage(config.getWatermarkFile());
	}

	@Override
	public MediaMetadata parseMetadata(@NonNull File sourceFile) throws JpegProcessingException, IOException {
		Metadata metadata = JpegMetadataReader.readMetadata(sourceFile);
		
		if (metadata.hasErrors()) {
			ArrayList<String> errorList = new ArrayList<>();
			for (Directory directory : metadata.getDirectories()) {
			   for (String error : directory.getErrors()) {
				   errorList.add(error);
			   }
	        }
			throw new JpegProcessingException(errorList.stream().collect(Collectors.joining("; ")));
		}
		
		MediaMetadata.MediaMetadataBuilder builder = MediaMetadata.builder();
		
		ExifIFD0Directory exifTags = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (exifTags != null) {
			Date dateTime = exifTags.getDate(ExifIFD0Directory.TAG_DATETIME);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
			builder.cameraMaker(exifTags.getString(ExifIFD0Directory.TAG_MAKE));
			builder.cameraModel(exifTags.getString(ExifIFD0Directory.TAG_MODEL));
			builder.height(exifTags.getInteger(ExifIFD0Directory.TAG_Y_RESOLUTION));
			builder.width(exifTags.getInteger(ExifIFD0Directory.TAG_X_RESOLUTION));
		}
		
		ExifSubIFDDirectory exifSubTags = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		if (exifSubTags != null) {
			Date dateTime = exifSubTags.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			if (dateTime != null) {
				builder.timestamp(dateTime.toInstant());
			}
		}
		
		JpegDirectory jpegTags = metadata.getFirstDirectoryOfType(JpegDirectory.class);
		if (jpegTags != null) {
			builder.height(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT));
			builder.width(jpegTags.getInteger(JpegDirectory.TAG_IMAGE_WIDTH));
		}
		
		GpsDirectory gpsTags = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsTags != null) {
			GeoLocation location = gpsTags.getGeoLocation();
			if (location != null) {
				builder.coordinates(new Point(location.getLongitude(), location.getLatitude()));
			}
		}
		
		return builder.build();
	}
	
	@Override
	public MediaFileFormat createThumbnail(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getThumbnailWidth(), config.getThumbnailHeight())
			.crop(Positions.CENTER)
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
		return getThumbnailFormat();
	}
	
	@Override
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		Thumbnails
			.of(sourceFile)
			.watermark(Positions.CENTER, watermarkImage, 0.25f)
			.size(config.getPreviewWidth(), config.getPreviewHeight())
			.outputFormat(JPG_EXTENSION)
			.toFile(outputFile);
		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_JPG;
	}
}
