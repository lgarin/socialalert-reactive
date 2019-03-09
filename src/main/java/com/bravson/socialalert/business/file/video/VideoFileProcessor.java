package com.bravson.socialalert.business.file.video;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bravson.socialalert.business.file.media.MediaConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import lombok.NonNull;
import lombok.SneakyThrows;

@Service
@Transactional(propagation=Propagation.SUPPORTS)
public class VideoFileProcessor extends BaseVideoFileProcessor {
	
	public VideoFileProcessor(@NonNull MediaConfiguration config) {
		super(config);
	}
	
	@SneakyThrows(InterruptedException.class)
	@Override
	public MediaFileFormat createPreview(@NonNull File sourceFile, @NonNull File outputFile) throws IOException {
		
		if (!sourceFile.canRead()) {
			throw new IOException("Cannot read file " + sourceFile);
		}
		
		//String filter = "thumbnail,scale=320:240:force_original_aspect_ratio=decrease,pad=320:240:(ow-iw)/2:(oh-ih)/2";
		String filter = String.format("[0:v] scale='%1$d:%2$d:force_original_aspect_ratio=decrease',pad='%1$d:%2$d:(ow-iw)/2:(oh-ih)/2' [video]; [1] format=yuva420p,lutrgb='a=128' [watermark]; [video][watermark] overlay='x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2'; [0:a] aformat='sample_fmts=s16:sample_rates=44100:channel_layouts=mono'", config.getPreviewWidth(), config.getPreviewHeight());
		
		ProcessBuilder builder = new ProcessBuilder(config.getEncodingProgram(), "-i", sourceFile.getAbsolutePath(), "-i", config.getWatermarkFile(), "-c:v", "libx264", "-preset", "fast", "-profile:v", "baseline", "-level", "3.0", "-movflags", "+faststart", "-c:a", "aac", "-b:a", "64k", "-ac", "1", "-filter_complex", filter, "-y", outputFile.getAbsolutePath());
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
//		System.out.println(builder.command().stream().collect(Collectors.joining(" ")));
//		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
//        String line;
//        while((line=br.readLine())!=null){
//           System.out.println(line);
//        }
//		 
		int result = process.waitFor();
		if (result != 0) {
			throw new IOException("Cannot process file " + outputFile);
		}

		return getPreviewFormat();
	}
	
	@Override
	public MediaFileFormat getPreviewFormat() {
		return MediaFileFormat.PREVIEW_MP4;
	}
}
