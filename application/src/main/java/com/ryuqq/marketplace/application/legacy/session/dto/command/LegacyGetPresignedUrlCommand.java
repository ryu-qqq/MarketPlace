package com.ryuqq.marketplace.application.legacy.session.dto.command;

/**
 * 레거시 Presigned URL 발급 커맨드.
 *
 * <p>세토프 어드민의 PreSignedUrlRequest와 호환됩니다.
 *
 * @param fileName 파일명
 * @param imagePath 이미지 경로 구분 (PRODUCT, DESCRIPTION, QNA, CONTENT, IMAGE_COMPONENT, BANNER)
 * @param fileSize 파일 크기 (bytes, null이면 기본 10MB)
 */
public record LegacyGetPresignedUrlCommand(String fileName, String imagePath, Long fileSize) {

    private static final long DEFAULT_FILE_SIZE = 10L * 1024 * 1024;

    public long fileSizeOrDefault() {
        return fileSize != null ? fileSize : DEFAULT_FILE_SIZE;
    }
}
