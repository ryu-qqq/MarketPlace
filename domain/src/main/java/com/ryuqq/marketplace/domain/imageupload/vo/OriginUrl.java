package com.ryuqq.marketplace.domain.imageupload.vo;

import java.net.URI;
import java.util.Objects;

/**
 * 이미지 업로드 원본 URL VO.
 *
 * <p>외부에서 다운로드할 이미지의 원본 URL을 표현합니다.
 *
 * <p>URL에서 파일 확장자를 추출하는 도메인 로직을 캡슐화합니다.
 */
public record OriginUrl(String value) {

    private static final int MAX_EXTENSION_LENGTH = 5;

    public OriginUrl {
        Objects.requireNonNull(value, "originUrl은 필수입니다");
        if (value.isBlank()) {
            throw new IllegalArgumentException("originUrl은 빈 값일 수 없습니다");
        }
    }

    public static OriginUrl of(String url) {
        return new OriginUrl(url);
    }

    /**
     * URL에서 파일 확장자를 추출합니다.
     *
     * <p>쿼리 파라미터를 제거한 순수 경로에서 확장자를 추출하며, 유효하지 않으면 빈 문자열을 반환합니다.
     *
     * @return 파일 확장자 (예: ".jpg", ".png") 또는 빈 문자열
     */
    public String extension() {
        try {
            String path = URI.create(value).getPath();
            int dotIndex = path.lastIndexOf('.');
            if (dotIndex >= 0) {
                String ext = path.substring(dotIndex);
                if (ext.length() <= MAX_EXTENSION_LENGTH) {
                    return ext;
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
        return "";
    }

    @Override
    public String toString() {
        return value;
    }
}
