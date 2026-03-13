package com.ryuqq.marketplace.application.common.port.out;

/**
 * 이미지 URL이 내부 스토리지(CDN)에 이미 존재하는지 판별하는 포트.
 *
 * <p>내부 URL이면 ImageUploadOutbox를 통한 비동기 다운로드/업로드를 건너뛰고,
 * 즉시 uploadedUrl로 설정할 수 있습니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface InternalImageUrlChecker {

    /**
     * 주어진 URL이 내부 CDN 도메인인지 판별합니다.
     *
     * @param url 이미지 URL
     * @return 내부 CDN URL이면 true
     */
    boolean isInternal(String url);
}
