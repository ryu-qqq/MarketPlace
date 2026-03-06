package com.ryuqq.marketplace.adapter.in.rest.imageupload.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * FileFlow 이미지 업로드 콜백 요청 DTO.
 *
 * <p>FileFlow가 다운로드 태스크 완료 시 전송하는 콜백 본문.
 *
 * @param downloadTaskId 다운로드 태스크 ID
 * @param assetId FileFlow 에셋 ID (COMPLETED 시)
 * @param status 태스크 상태 (COMPLETED, FAILED)
 * @param sourceUrl 원본 다운로드 URL
 * @param s3Key S3 저장 경로 (COMPLETED 시)
 * @param bucket S3 버킷 (COMPLETED 시)
 * @param fileName 파일명 (COMPLETED 시)
 * @param contentType 콘텐츠 타입 (nullable)
 * @param fileSize 파일 크기 (바이트)
 * @param errorMessage 에러 메시지 (FAILED 시)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageUploadCallbackApiRequest(
        String downloadTaskId,
        String assetId,
        String status,
        String sourceUrl,
        String s3Key,
        String bucket,
        String fileName,
        String contentType,
        long fileSize,
        String errorMessage) {}
