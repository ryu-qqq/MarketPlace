package com.ryuqq.marketplace.application.common.port.out.client;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import java.util.List;

/**
 * File Storage Client.
 *
 * <p>파일 스토리지 작업을 위한 아웃바운드 클라이언트 인터페이스입니다.
 *
 * <p><strong>구현체:</strong>
 *
 * <ul>
 *   <li>FileFlowStorageAdapter - FileFlow SDK 기반 파일 스토리지
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface FileStorageClient {

    PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request);

    String generateDownloadUrl(String fileAssetId, int expirationMinutes);

    void deleteFile(String fileAssetId);

    void deleteFiles(List<String> fileAssetIds);

    ExternalDownloadResponse downloadFromExternalUrl(ExternalDownloadRequest request);

    List<ExternalDownloadResponse> downloadFromExternalUrls(List<ExternalDownloadRequest> requests);

    /**
     * 업로드 세션 완료 처리를 합니다.
     *
     * @param sessionId 업로드 세션 ID
     * @param fileSize 업로드된 파일 크기 (바이트)
     * @param etag S3 ETag (nullable, CORS 제한으로 클라이언트가 못 받을 수 있음)
     */
    void completeUploadSession(String sessionId, long fileSize, String etag);

    /**
     * HTML 콘텐츠를 CDN에 업로드합니다.
     *
     * @param htmlContent 업로드할 HTML 콘텐츠
     * @param category 파일 카테고리 (예: "description")
     * @param filename 파일명 (예: "123.html")
     * @return 업로드된 CDN URL
     */
    String uploadHtmlContent(String htmlContent, String category, String filename);
}
