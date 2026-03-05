package com.ryuqq.marketplace.adapter.out.client.fileflow.mapper;

import com.ryuqq.fileflow.sdk.model.download.CreateDownloadTaskRequest;
import com.ryuqq.fileflow.sdk.model.download.DownloadTaskResponse;
import com.ryuqq.fileflow.sdk.model.session.CreateSingleUploadSessionRequest;
import com.ryuqq.fileflow.sdk.model.session.SingleUploadSessionResponse;
import com.ryuqq.marketplace.adapter.out.client.fileflow.config.FileFlowClientProperties;
import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import java.time.Instant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * FileFlow Storage Mapper.
 *
 * <p>Application DTO와 FileFlow SDK 객체 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "fileflow", name = "base-url")
public class FileFlowStorageMapper {

    private static final String ACCESS_TYPE_PUBLIC = "PUBLIC";
    private static final String SOURCE = "marketplace";

    private final FileFlowClientProperties properties;

    public FileFlowStorageMapper(FileFlowClientProperties properties) {
        this.properties = properties;
    }

    /**
     * Application 업로드 요청을 SDK 요청으로 변환합니다.
     *
     * @param request 프리사인드 업로드 URL 요청
     * @return SDK CreateSingleUploadSessionRequest
     */
    public CreateSingleUploadSessionRequest toCreateSingleUploadSessionRequest(
            PresignedUploadUrlRequest request) {
        return new CreateSingleUploadSessionRequest(
                request.filename(),
                request.contentType(),
                ACCESS_TYPE_PUBLIC,
                request.directory().value(),
                SOURCE);
    }

    /**
     * SDK 업로드 세션 응답을 Application 응답으로 변환합니다.
     *
     * @param response SDK SingleUploadSessionResponse
     * @return PresignedUrlResponse
     */
    public PresignedUrlResponse toPresignedUrlResponse(SingleUploadSessionResponse response) {
        Instant expiresAt = parseInstantOrNull(response.expiresAt());
        String accessUrl = buildAccessUrl(response.s3Key());
        return new PresignedUrlResponse(
                response.sessionId(),
                response.presignedUrl(),
                response.s3Key(),
                expiresAt,
                accessUrl);
    }

    /**
     * Application 다운로드 요청을 SDK 요청으로 변환합니다.
     *
     * @param request 외부 다운로드 요청
     * @return SDK CreateDownloadTaskRequest
     */
    public CreateDownloadTaskRequest toCreateDownloadTaskRequest(ExternalDownloadRequest request) {
        return new CreateDownloadTaskRequest(
                request.sourceUrl(),
                ACCESS_TYPE_PUBLIC,
                request.category(),
                SOURCE,
                request.callbackUrl());
    }

    /**
     * SDK 다운로드 태스크 응답을 Application 성공 응답으로 변환합니다.
     *
     * @param response SDK DownloadTaskResponse
     * @param sourceUrl 원본 URL
     * @return ExternalDownloadResponse
     */
    public ExternalDownloadResponse toExternalDownloadResponse(
            DownloadTaskResponse response, String sourceUrl) {
        String newCdnUrl = buildAccessUrl(response.s3Key());
        return ExternalDownloadResponse.success(sourceUrl, newCdnUrl, response.downloadTaskId());
    }

    private String buildAccessUrl(String s3Key) {
        String cdnDomain = properties.cdnDomain();
        if (cdnDomain == null || cdnDomain.isBlank() || s3Key == null) {
            return null;
        }
        return "https://" + cdnDomain + "/" + s3Key;
    }

    private Instant parseInstantOrNull(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(timestamp);
        } catch (Exception e) {
            return null;
        }
    }
}
