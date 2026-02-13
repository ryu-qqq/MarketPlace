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
}
