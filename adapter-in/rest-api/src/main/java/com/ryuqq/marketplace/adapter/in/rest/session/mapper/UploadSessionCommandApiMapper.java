package com.ryuqq.marketplace.adapter.in.rest.session.mapper;

import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.CompleteUploadSessionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.GenerateUploadUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.GenerateUploadUrlApiResponse;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import com.ryuqq.marketplace.application.uploadsession.vo.UploadDirectory;
import org.springframework.stereotype.Component;

/**
 * UploadSessionCommandApiMapper - 업로드 세션 Command API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class UploadSessionCommandApiMapper {

    /**
     * GenerateUploadUrlApiRequest -> PresignedUploadUrlRequest 변환.
     *
     * @param request API 요청 DTO
     * @return Application DTO
     */
    public PresignedUploadUrlRequest toPresignedUploadUrlRequest(
            GenerateUploadUrlApiRequest request) {
        return PresignedUploadUrlRequest.of(
                UploadDirectory.from(request.directory()),
                request.filename(),
                request.contentType(),
                request.contentLength());
    }

    /**
     * CompleteUploadSessionApiRequest -> CompleteUploadSessionCommand 변환.
     *
     * @param sessionId 업로드 세션 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CompleteUploadSessionCommand toCompleteCommand(
            String sessionId, CompleteUploadSessionApiRequest request) {
        return new CompleteUploadSessionCommand(sessionId, request.fileSize(), request.etag());
    }

    /**
     * PresignedUrlResponse -> GenerateUploadUrlApiResponse 변환.
     *
     * @param response Application 응답 DTO
     * @return API 응답 DTO
     */
    public GenerateUploadUrlApiResponse toApiResponse(PresignedUrlResponse response) {
        return new GenerateUploadUrlApiResponse(
                response.sessionId(),
                response.presignedUrl(),
                response.fileKey(),
                response.expiresAt(),
                response.accessUrl());
    }
}
