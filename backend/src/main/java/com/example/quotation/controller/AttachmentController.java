package com.example.quotation.controller;

import com.example.quotation.common.ApiResponse;
import com.example.quotation.entity.Attachment;
import com.example.quotation.service.AttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/quotation/{quotationId}")
    public ApiResponse<List<Attachment>> getByQuotationId(@PathVariable Long quotationId) {
        return ApiResponse.ok(attachmentService.findByQuotationId(quotationId));
    }

    @PostMapping("/upload")
    public ApiResponse<Attachment> upload(@RequestParam("file") MultipartFile file,
                                          @RequestParam("quotationId") Long quotationId,
                                          @RequestParam(value = "uploadedBy", required = false) String uploadedBy) {
        if(file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if(originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 保存附件记录
            Attachment attachment = new Attachment();
            attachment.setQuotationId(quotationId);
            attachment.setFileName(uniqueFileName);
            attachment.setOriginalFileName(originalFileName);
            attachment.setFilePath(filePath.toString());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setUploadedBy(uploadedBy);
            attachment.setUploadedAt(LocalDateTime.now());

            attachment = attachmentService.save(attachment);

            return ApiResponse.ok(attachment);
        } catch(Exception e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Attachment attachment = attachmentService.findById(id);
        if(attachment == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(attachment.getFilePath());
        if(!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(attachment.getFileType());
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(attachment.getOriginalFileName(), "UTF-8") + "\"");
        response.setContentLengthLong(attachment.getFileSize());

        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Attachment attachment = attachmentService.findById(id);
        if(attachment != null) {
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            } catch(IOException e) {
                // 忽略文件删除错误
            }
        }
        attachmentService.delete(id);
        return ApiResponse.ok(null);
    }
}

