package com.donggua.aiagent.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.donggua.aiagent.common.ResponseResult;
import com.donggua.aiagent.constant.FileConstant;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    private static final String UPLOAD_DIR = FileConstant.FILE_SAVE_DIR + "/upload";
    private static final String AGENT_FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";
    private static final String PDF_DIR = FileConstant.FILE_SAVE_DIR + "/pdf";
    private static final String DOWNLOAD_DIR = FileConstant.FILE_SAVE_DIR + "/download";
    private static final List<String> ALL_DIRS = Arrays.asList(UPLOAD_DIR, AGENT_FILE_DIR, PDF_DIR, DOWNLOAD_DIR);

    @PostMapping("/upload")
    public ResponseResult<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseResult.error("File is empty");
        try {
            FileUtil.mkdir(UPLOAD_DIR);
            String originalName = file.getOriginalFilename();
            String storedName = System.currentTimeMillis() + "_" + (originalName != null ? originalName : "unnamed");
            String filePath = UPLOAD_DIR + "/" + storedName;
            file.transferTo(new File(filePath));
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", storedName);
            data.put("originalName", originalName);
            data.put("size", file.getSize());
            data.put("path", filePath);
            log.info("File uploaded: {} ({} bytes)", originalName, file.getSize());
            return ResponseResult.success(data);
        } catch (IOException e) {
            log.error("Upload failed", e);
            return ResponseResult.error("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
        String filePath = findFile(fileName);
        if (filePath == null) { response.setStatus(404); return; }
        File file = new File(filePath);
        if (!file.exists()) { response.setStatus(404); return; }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8) + "\"");
        response.setContentLengthLong(file.length());
        try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
            IoUtil.copy(fis, os); os.flush();
        } catch (IOException e) {
            log.error("Download failed: {}", fileName, e);
        }
    }

    @DeleteMapping("/{fileName:.+}")
    public ResponseResult<Void> deleteFile(@PathVariable String fileName) {
        String filePath = findFile(fileName);
        if (filePath == null) return ResponseResult.error("File not found: " + fileName);
        try {
            if (FileUtil.del(filePath)) {
                log.info("File deleted: {}", filePath);
                return ResponseResult.success();
            }
            return ResponseResult.error("Delete failed");
        } catch (Exception e) {
            log.error("Delete exception: {}", filePath, e);
            return ResponseResult.error("Delete error: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseResult<Map<String, List<Map<String, Object>>>> listFiles() {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        result.put("agent_files", listDir(AGENT_FILE_DIR));
        result.put("pdf_files", listDir(PDF_DIR));
        result.put("downloads", listDir(DOWNLOAD_DIR));
        result.put("uploads", listDir(UPLOAD_DIR));
        return ResponseResult.success(result);
    }

    private String findFile(String fileName) {
        for (String dir : ALL_DIRS) {
            String path = dir + "/" + fileName;
            if (new File(path).exists()) return path;
        }
        return null;
    }

    private List<Map<String, Object>> listDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) return Collections.emptyList();
        File[] files = dir.listFiles();
        if (files == null) return Collections.emptyList();
        return Arrays.stream(files).filter(File::isFile).map(f -> {
            Map<String, Object> info = new HashMap<>();
            info.put("name", f.getName());
            info.put("size", f.length());
            info.put("sizeReadable", FileUtil.readableFileSize(f));
            info.put("lastModified", new Date(f.lastModified()));
            return info;
        }).sorted((a, b) -> ((Date) b.get("lastModified")).compareTo((Date) a.get("lastModified")))
          .collect(Collectors.toList());
    }
}
