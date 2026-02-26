package com.ryuqq.marketplace.application.common.manager;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import org.springframework.stereotype.Component;

/**
 * FileStorage Manager.
 *
 * <p>FileStorageClient를 래핑하여 파일 스토리지 작업을 위임합니다.
 */
@Component
public class FileStorageManager {

    private final FileStorageClient fileStorageClient;

    public FileStorageManager(FileStorageClient fileStorageClient) {
        this.fileStorageClient = fileStorageClient;
    }

    public PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request) {
        return fileStorageClient.generateUploadUrl(request);
    }

    public void completeUploadSession(String sessionId, long fileSize, String etag) {
        fileStorageClient.completeUploadSession(sessionId, fileSize, etag);
    }

    public ExternalDownloadResponse downloadFromExternalUrl(ExternalDownloadRequest request) {
        return fileStorageClient.downloadFromExternalUrl(request);
    }

    public String uploadHtmlContent(String htmlContent, String category, String filename) {
        return fileStorageClient.uploadHtmlContent(htmlContent, category, filename);
    }
}
