package com.ryuqq.marketplace.domain.productgroup.vo;

/**
 * 상세설명 CDN 퍼블리시 상태.
 *
 * <p>이미지 업로드 완료 여부에 따라 CDN 퍼블리시 진행 상태를 관리한다.
 *
 * <ul>
 *   <li>PENDING: 이미지 업로드 미완료 (초기 상태)
 *   <li>PUBLISH_READY: 모든 이미지 업로드 완료, CDN 퍼블리시 대기
 *   <li>PUBLISHED: CDN 퍼블리시 완료
 * </ul>
 */
public enum DescriptionPublishStatus {
    PENDING("대기"),
    PUBLISH_READY("퍼블리시 대기"),
    PUBLISHED("퍼블리시 완료");

    private final String displayName;

    DescriptionPublishStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isPublishReady() {
        return this == PUBLISH_READY;
    }

    public boolean isPublished() {
        return this == PUBLISHED;
    }
}
