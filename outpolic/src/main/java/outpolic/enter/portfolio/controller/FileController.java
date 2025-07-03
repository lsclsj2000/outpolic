package outpolic.enter.portfolio.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.portfolio.domain.FileMetaData;
import outpolic.enter.portfolio.mapper.FileMapper;
import outpolic.enter.portfolio.service.FileService;
import outpolic.enter.portfolio.util.FilesUtils;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;
    private final FileMapper fileMapper;
    private final FilesUtils filesUtils;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("clCd", "CL_TEMP_001");
        model.addAttribute("mbrCd", "MB_C0000001");
        return "enter/portfolio/fileUpload";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files,
                                   @RequestParam("clCd") String clCd,
                                   @RequestParam("mbrCd") String mbrCd) {
        try {
            fileService.addFiles(files, "general", clCd, mbrCd);
        } catch (IOException e) {
            log.error("File upload failed", e);
        }
        return "redirect:/file/list";
    }

    @GetMapping("/list")
    public String showFileList(Model model) {
        model.addAttribute("fileList", fileMapper.getFileList());
        return "enter/portfolio/fileDownloadView";
    }

    @GetMapping("/display/{featureName}/{date}/{type}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> display(@PathVariable String featureName,
                                            @PathVariable String date,
                                            @PathVariable String type,
                                            @PathVariable String filename) {
        String fullPath = filesUtils.getFileRealPath() + featureName + "/" + date + "/" + type + "/" + filename;
        
        Resource resource = new FileSystemResource(fullPath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(Paths.get(fullPath)));
            return ResponseEntity.ok().headers(header).body(resource);
        } catch (Exception e) {
            log.error("Error while probing content type", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> handleFileDownload(@RequestParam("fileIdx") String fileIdx) {
        FileMetaData fileMetaData = fileMapper.getFileInfoByIdx(fileIdx);
        if (fileMetaData == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = Paths.get(filesUtils.getFileRealPath() + fileMetaData.getFilePath());
            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() || resource.isReadable()) {
                String encodedFileName = URLEncoder.encode(fileMetaData.getFileOriginalName(), "UTF-8").replaceAll("\\+", "%20");
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("File download failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/delete")
    public String handleFileDelete(@RequestParam("fileIdx") String fileIdx) {
        FileMetaData fileMetaData = fileMapper.getFileInfoByIdx(fileIdx);
        if (fileMetaData != null) {
            fileService.deleteFile(fileMetaData);
        }
        return "redirect:/file/list";
    }
}