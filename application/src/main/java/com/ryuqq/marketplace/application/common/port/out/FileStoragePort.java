package com.ryuqq.marketplace.application.common.port.out;

import java.time.Instant;
import java.util.List;

/**
 * File Storage Port
 *
 * <p>파일 스토리지 작업을 위한 아웃바운드 포트입니다.
 *
 * <p><strong>구현체:</strong>
 *
 * <ul>
 *   <li>FileflowStorageAdapter - FileFlow SDK 기반 파일 스토리지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface FileStoragePort {

    /**
     * 업로드용 프리사인드 URL을 발급합니다.
     *
     * @param request 프리사인드 URL 발급 요청
     * @return 프리사인드 URL 정보
     */
    PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request);

    /**
     * 다운로드용 프리사인드 URL을 발급합니다.
     *
     * @param fileAssetId 파일 애셋 ID
     * @param expirationMinutes 만료 시간(분)
     * @return 프리사인드 다운로드 URL
     */
    String generateDownloadUrl(String fileAssetId, int expirationMinutes);

    /**
     * 파일을 삭제합니다.
     *
     * @param fileAssetId 파일 애셋 ID
     */
    void deleteFile(String fileAssetId);

    /**
     * 여러 파일을 삭제합니다.
     *
     * @param fileAssetIds 파일 애셋 ID 목록
     */
    void deleteFiles(List<String> fileAssetIds);

    /**
     * 외부 URL에서 파일을 다운로드하여 새 CDN에 업로드합니다.
     *
     * @param request 외부 다운로드 요청
     * @return 새 CDN URL 정보
     */
    ExternalDownloadResponse downloadFromExternalUrl(ExternalDownloadRequest request);

    /**
     * 여러 외부 URL에서 파일을 배치로 다운로드합니다.
     *
     * @param requests 외부 다운로드 요청 목록
     * @return 새 CDN URL 정보 목록
     */
    List<ExternalDownloadResponse> downloadFromExternalUrls(List<ExternalDownloadRequest> requests);

    /**
     * 프리사인드 업로드 URL 요청 DTO
     *
     * @param directory 디렉토리/카테고리
     * @param filename 파일명
     * @param contentType MIME 타입
     * @param contentLength 파일 크기 (바이트)
     */
    record PresignedUploadUrlRequest(
            String directory, String filename, String contentType, long contentLength) {

        /**
         * 정적 팩토리 메서드
         *
         * @param directory 디렉토리/카테고리
         * @param filename 파일명
         * @param contentType MIME 타입
         * @param contentLength 파일 크기 (바이트)
         * @return PresignedUploadUrlRequest
         */
        public static PresignedUploadUrlRequest of(
                String directory, String filename, String contentType, long contentLength) {
            return new PresignedUploadUrlRequest(directory, filename, contentType, contentLength);
        }
    }

    /**
     * 프리사인드 URL 응답 DTO
     *
     * @param presignedUrl 프리사인드 URL
     * @param fileKey 파일 키 (S3 Object Key)
     * @param expiresAt 만료 시간
     * @param accessUrl 접근 URL
     */
    record PresignedUrlResponse(
            String presignedUrl, String fileKey, Instant expiresAt, String accessUrl) {}

    /**
     * 외부 다운로드 요청 DTO
     *
     * @param sourceUrl 원본 URL
     * @param category 카테고리
     * @param filename 저장할 파일명
     */
    record ExternalDownloadRequest(String sourceUrl, String category, String filename) {}

    /**
     * 외부 다운로드 응답 DTO
     *
     * @param sourceUrl 원본 URL
     * @param newCdnUrl 새 CDN URL
     * @param fileAssetId 파일 애셋 ID
     * @param success 성공 여부
     * @param errorMessage 에러 메시지 (실패 시)
     */
    record ExternalDownloadResponse(
            String sourceUrl,
            String newCdnUrl,
            String fileAssetId,
            boolean success,
            String errorMessage) {

        /**
         * 성공 응답 생성
         *
         * @param sourceUrl 원본 URL
         * @param newCdnUrl 새 CDN URL
         * @param fileAssetId 파일 애셋 ID
         * @return 성공 응답
         */
        public static ExternalDownloadResponse success(
                String sourceUrl, String newCdnUrl, String fileAssetId) {
            return new ExternalDownloadResponse(sourceUrl, newCdnUrl, fileAssetId, true, null);
        }

        /**
         * 실패 응답 생성
         *
         * @param sourceUrl 원본 URL
         * @param errorMessage 에러 메시지
         * @return 실패 응답
         */
        public static ExternalDownloadResponse failure(String sourceUrl, String errorMessage) {
            return new ExternalDownloadResponse(sourceUrl, null, null, false, errorMessage);
        }
    }
}
