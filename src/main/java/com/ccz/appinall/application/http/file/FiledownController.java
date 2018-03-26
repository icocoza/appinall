package com.ccz.appinall.application.http.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ccz.appinall.common.config.ServicesConfig;
import com.ccz.appinall.common.rdb.DbAppManager;
import com.ccz.appinall.services.model.db.RecFile;

@Controller

public class FiledownController {
	@Autowired
    private ServletContext servletContext;
	@Autowired
	private ServicesConfig servicesConfig;
 
    // http://localhost:8080/download1?fileName=abc.zip
    // Using ResponseEntity<InputStreamResource>
    @RequestMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String scode, @RequestParam String fileid) throws IOException {
    		RecFile recFile = DbAppManager.getInst().getFileInfo(scode, fileid);
    		MediaTypeUtils mediaTypeUtils = new MediaTypeUtils();
        MediaType mediaType = mediaTypeUtils.getMediaTypeForFileName(this.servletContext, recFile.filename);
        
        System.out.println("fileName: " + recFile.filename);
        System.out.println("mediaType: " + mediaType);
 
        File file = new File(servicesConfig.getFileUploadDir() + "/" + scode + "/" + fileid);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
 
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + recFile.filename)
                // Content-Type
                .contentType(mediaType)
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }
    
    @RequestMapping("/thumb")
    public ResponseEntity<InputStreamResource> downloadThumb(@RequestParam String scode, @RequestParam String fileid) throws IOException {
    		RecFile recFile = DbAppManager.getInst().getFileInfo(scode, fileid);
    		MediaTypeUtils mediaTypeUtils = new MediaTypeUtils();
        MediaType mediaType = mediaTypeUtils.getMediaTypeForFileName(this.servletContext, recFile.filename);
        
        System.out.println("fileName: " + recFile.filename);
        System.out.println("mediaType: " + mediaType);
 
        File file = new File(servicesConfig.getFileUploadDir() + "/" + scode + "/thumb/" + recFile.thumbname);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
 
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + recFile.filename)
                // Content-Type
                .contentType(mediaType)
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }
    
    public class MediaTypeUtils {
        public MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
            String mineType = servletContext.getMimeType(fileName);
            try {
                MediaType mediaType = MediaType.parseMediaType(mineType);
                return mediaType;
            } catch (Exception e) {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
        }
         
    }
}
