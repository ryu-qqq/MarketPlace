package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto;

import java.util.List;

/**
 * ProductGroupDescriptionCompositeDto 테스트 Fixtures.
 *
 * <p>Composite Query 테스트용 DTO 생성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ProductGroupDescriptionCompositeDtoFixtures {

    private ProductGroupDescriptionCompositeDtoFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_DESCRIPTION_ID = 100L;
    public static final Long DEFAULT_IMAGE_ID_1 = 200L;
    public static final Long DEFAULT_IMAGE_ID_2 = 201L;
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/desc-image.jpg";
    public static final String DEFAULT_UPLOADED_URL = "https://s3.example.com/desc-uploaded.jpg";
    public static final String DEFAULT_CDN_PATH = "https://cdn.example.com/descriptions/pg1/";
    public static final String DEFAULT_PUBLISH_STATUS_PENDING = "PENDING";
    public static final String DEFAULT_PUBLISH_STATUS_PUBLISHED = "PUBLISHED";

    // ========================================================================
    // DescriptionProjectionDto Fixtures
    // ========================================================================

    /** PENDING 상태의 상세설명 프로젝션 DTO 생성 (cdnPath null). */
    public static DescriptionProjectionDto pendingDescriptionProjectionDto() {
        return new DescriptionProjectionDto(
                DEFAULT_DESCRIPTION_ID, DEFAULT_PUBLISH_STATUS_PENDING, null);
    }

    /** PENDING 상태의 상세설명 프로젝션 DTO 생성 (descriptionId 지정). */
    public static DescriptionProjectionDto pendingDescriptionProjectionDto(Long descriptionId) {
        return new DescriptionProjectionDto(descriptionId, DEFAULT_PUBLISH_STATUS_PENDING, null);
    }

    /** PUBLISHED 상태의 상세설명 프로젝션 DTO 생성 (cdnPath 포함). */
    public static DescriptionProjectionDto publishedDescriptionProjectionDto() {
        return new DescriptionProjectionDto(
                DEFAULT_DESCRIPTION_ID, DEFAULT_PUBLISH_STATUS_PUBLISHED, DEFAULT_CDN_PATH);
    }

    /** PUBLISHED 상태의 상세설명 프로젝션 DTO 생성 (descriptionId 지정). */
    public static DescriptionProjectionDto publishedDescriptionProjectionDto(Long descriptionId) {
        return new DescriptionProjectionDto(
                descriptionId, DEFAULT_PUBLISH_STATUS_PUBLISHED, DEFAULT_CDN_PATH);
    }

    // ========================================================================
    // DescriptionImageProjectionDto Fixtures
    // ========================================================================

    /** 업로드 완료된 이미지 프로젝션 DTO 생성. */
    public static DescriptionImageProjectionDto completedImageProjectionDto(Long imageId) {
        return new DescriptionImageProjectionDto(imageId, DEFAULT_ORIGIN_URL, DEFAULT_UPLOADED_URL);
    }

    /** 업로드 대기 중인 이미지 프로젝션 DTO 생성 (uploadedUrl null). */
    public static DescriptionImageProjectionDto pendingImageProjectionDto(Long imageId) {
        return new DescriptionImageProjectionDto(imageId, DEFAULT_ORIGIN_URL, null);
    }

    /** URL을 지정한 이미지 프로젝션 DTO 생성. */
    public static DescriptionImageProjectionDto imageProjectionDto(
            Long imageId, String originUrl, String uploadedUrl) {
        return new DescriptionImageProjectionDto(imageId, originUrl, uploadedUrl);
    }

    // ========================================================================
    // DescriptionImageOutboxProjectionDto Fixtures
    // ========================================================================

    /** COMPLETED 상태의 아웃박스 프로젝션 DTO 생성. */
    public static DescriptionImageOutboxProjectionDto completedOutboxProjectionDto(Long sourceId) {
        return new DescriptionImageOutboxProjectionDto(sourceId, "COMPLETED", 0, null);
    }

    /** PENDING 상태의 아웃박스 프로젝션 DTO 생성. */
    public static DescriptionImageOutboxProjectionDto pendingOutboxProjectionDto(Long sourceId) {
        return new DescriptionImageOutboxProjectionDto(sourceId, "PENDING", 0, null);
    }

    /** FAILED 상태의 아웃박스 프로젝션 DTO 생성. */
    public static DescriptionImageOutboxProjectionDto failedOutboxProjectionDto(Long sourceId) {
        return new DescriptionImageOutboxProjectionDto(
                sourceId, "FAILED", 3, "연결 실패로 인한 최대 재시도 초과");
    }

    /** 재시도 횟수가 있는 PENDING 상태의 아웃박스 프로젝션 DTO 생성. */
    public static DescriptionImageOutboxProjectionDto pendingOutboxProjectionDtoWithRetry(
            Long sourceId, int retryCount) {
        return new DescriptionImageOutboxProjectionDto(sourceId, "PENDING", retryCount, "이전 시도 실패");
    }

    // ========================================================================
    // DescriptionCompositeDto Fixtures
    // ========================================================================

    /** 기본 Composite DTO - PENDING 상태, 이미지 2개 (모두 COMPLETED). */
    public static DescriptionCompositeDto defaultCompositeDto() {
        return defaultCompositeDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 Composite DTO - PENDING 상태, 이미지 2개 (모두 COMPLETED). */
    public static DescriptionCompositeDto defaultCompositeDto(Long productGroupId) {
        DescriptionProjectionDto description = pendingDescriptionProjectionDto();
        List<DescriptionImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_2));
        List<DescriptionImageOutboxProjectionDto> outboxes =
                List.of(
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_2));
        return new DescriptionCompositeDto(productGroupId, description, images, outboxes);
    }

    /** PUBLISHED 상태 Composite DTO - 이미지 2개 (모두 COMPLETED). */
    public static DescriptionCompositeDto publishedCompositeDto(Long productGroupId) {
        DescriptionProjectionDto description = publishedDescriptionProjectionDto();
        List<DescriptionImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_2));
        List<DescriptionImageOutboxProjectionDto> outboxes =
                List.of(
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_2));
        return new DescriptionCompositeDto(productGroupId, description, images, outboxes);
    }

    /** 이미지가 없는 Composite DTO (이미지 미등록 상태). */
    public static DescriptionCompositeDto compositeDtoWithoutImages(Long productGroupId) {
        DescriptionProjectionDto description = pendingDescriptionProjectionDto();
        return new DescriptionCompositeDto(productGroupId, description, List.of(), List.of());
    }

    /** 이미지는 있지만 아웃박스가 없는 Composite DTO. */
    public static DescriptionCompositeDto compositeDtoWithoutOutboxes(Long productGroupId) {
        DescriptionProjectionDto description = pendingDescriptionProjectionDto();
        List<DescriptionImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_2));
        return new DescriptionCompositeDto(productGroupId, description, images, List.of());
    }

    /** 혼합 상태 Composite DTO - COMPLETED 1개, PENDING 1개, FAILED 1개. */
    public static DescriptionCompositeDto mixedStatusCompositeDto(Long productGroupId) {
        Long completedImageId = 210L;
        Long pendingImageId = 211L;
        Long failedImageId = 212L;

        DescriptionProjectionDto description = pendingDescriptionProjectionDto();
        List<DescriptionImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(completedImageId),
                        pendingImageProjectionDto(pendingImageId),
                        pendingImageProjectionDto(failedImageId));
        List<DescriptionImageOutboxProjectionDto> outboxes =
                List.of(
                        completedOutboxProjectionDto(completedImageId),
                        pendingOutboxProjectionDto(pendingImageId),
                        failedOutboxProjectionDto(failedImageId));
        return new DescriptionCompositeDto(productGroupId, description, images, outboxes);
    }
}
