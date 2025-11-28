
package com.example.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Attachment entity class
 * Represents file attachments associated with quotations
 */
@TableName("attachments")
@Data
public class Attachment {
    /**
     * Primary key - attachment ID
     */
    @TableId
    private Long id;
    
    /**
     * Foreign key - associated quotation ID
     */
    private Long quotationId;
    
    /**
     * Stored file name (usually generated)
     */
    private String fileName;
    
    /**
     * Original file name uploaded by user
     */
    private String originalFileName;
    
    /**
     * File storage path on server
     */
    private String filePath;
    
    /**
     * File MIME type (e.g., application/pdf, image/png)
     */
    private String fileType;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * Username or ID of the person who uploaded the file
     */
    private String uploadedBy;
    
    /**
     * Timestamp when the file was uploaded
     */
    private LocalDateTime uploadedAt;
}

