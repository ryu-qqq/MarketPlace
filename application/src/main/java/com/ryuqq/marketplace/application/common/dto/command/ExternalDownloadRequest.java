package com.ryuqq.marketplace.application.common.dto.command;

/**
 * 외부 URL 다운로드 요청 DTO.
 *
 * @param sourceUrl 다운로드할 URL
 * @param category 파일카테고리
 * @param filename 저장할 파일이름
 * @param callbackUrl 다운로드 완료 시 콜백 URL (nullable)
 */
public record ExternalDownloadRequest(
        String sourceUrl, String category, String filename, String callbackUrl) {

    public ExternalDownloadRequest(String sourceUrl, String category, String filename) {
        this(sourceUrl, category, filename, null);
    }
}
