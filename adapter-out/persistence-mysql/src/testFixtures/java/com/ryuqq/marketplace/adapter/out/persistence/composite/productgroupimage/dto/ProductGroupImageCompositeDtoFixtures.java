package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto;

import java.util.List;

/**
 * ProductGroupImageCompositeDto 테스트 Fixtures.
 *
 * <p>Composite Query 테스트용 DTO 생성.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ProductGroupImageCompositeDtoFixtures {

    private ProductGroupImageCompositeDtoFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_IMAGE_ID_1 = 10L;
    public static final Long DEFAULT_IMAGE_ID_2 = 11L;
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_UPLOADED_URL = "https://s3.example.com/uploaded.jpg";
    public static final String DEFAULT_IMAGE_TYPE = "THUMBNAIL";

    // ========================================================================
    // ImageProjectionDto Fixtures
    // ========================================================================

    /** 업로드 완료된 이미지 프로젝션 DTO 생성. */
    public static ImageProjectionDto completedImageProjectionDto(Long imageId) {
        return new ImageProjectionDto(
                imageId, DEFAULT_IMAGE_TYPE, DEFAULT_ORIGIN_URL, DEFAULT_UPLOADED_URL);
    }

    /** 업로드 대기 중인 이미지 프로젝션 DTO 생성 (uploadedUrl null). */
    public static ImageProjectionDto pendingImageProjectionDto(Long imageId) {
        return new ImageProjectionDto(imageId, DEFAULT_IMAGE_TYPE, DEFAULT_ORIGIN_URL, null);
    }

    /** 특정 타입의 이미지 프로젝션 DTO 생성. */
    public static ImageProjectionDto imageProjectionDto(Long imageId, String imageType) {
        return new ImageProjectionDto(
                imageId,
                imageType,
                DEFAULT_ORIGIN_URL + "?id=" + imageId,
                DEFAULT_UPLOADED_URL + "?id=" + imageId);
    }

    // ========================================================================
    // ImageOutboxProjectionDto Fixtures
    // ========================================================================

    /** COMPLETED 상태의 아웃박스 프로젝션 DTO 생성. */
    public static ImageOutboxProjectionDto completedOutboxProjectionDto(Long sourceId) {
        return new ImageOutboxProjectionDto(sourceId, "COMPLETED", 0, null);
    }

    /** PENDING 상태의 아웃박스 프로젝션 DTO 생성. */
    public static ImageOutboxProjectionDto pendingOutboxProjectionDto(Long sourceId) {
        return new ImageOutboxProjectionDto(sourceId, "PENDING", 0, null);
    }

    /** PROCESSING 상태의 아웃박스 프로젝션 DTO 생성. */
    public static ImageOutboxProjectionDto processingOutboxProjectionDto(Long sourceId) {
        return new ImageOutboxProjectionDto(sourceId, "PROCESSING", 1, null);
    }

    /** FAILED 상태의 아웃박스 프로젝션 DTO 생성. */
    public static ImageOutboxProjectionDto failedOutboxProjectionDto(Long sourceId) {
        return new ImageOutboxProjectionDto(sourceId, "FAILED", 3, "연결 실패로 인한 최대 재시도 초과");
    }

    /** 재시도 횟수가 있는 PENDING 상태의 아웃박스 프로젝션 DTO 생성. */
    public static ImageOutboxProjectionDto pendingOutboxProjectionDtoWithRetry(
            Long sourceId, int retryCount) {
        return new ImageOutboxProjectionDto(sourceId, "PENDING", retryCount, "이전 시도 실패");
    }

    // ========================================================================
    // ProductGroupImageCompositeDto Fixtures
    // ========================================================================

    /** 기본 Composite DTO - 이미지 2개, 아웃박스 2개 (모두 COMPLETED). */
    public static ProductGroupImageCompositeDto defaultCompositeDto() {
        return defaultCompositeDto(DEFAULT_PRODUCT_GROUP_ID);
    }

    /** 지정 productGroupId의 Composite DTO - 이미지 2개, 아웃박스 2개 (모두 COMPLETED). */
    public static ProductGroupImageCompositeDto defaultCompositeDto(Long productGroupId) {
        List<ImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_2));
        List<ImageOutboxProjectionDto> outboxes =
                List.of(
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedOutboxProjectionDto(DEFAULT_IMAGE_ID_2));
        return new ProductGroupImageCompositeDto(productGroupId, images, outboxes);
    }

    /** 이미지는 있지만 아웃박스가 없는 Composite DTO (아웃박스 미생성 상태). */
    public static ProductGroupImageCompositeDto compositeDtoWithoutOutboxes(Long productGroupId) {
        List<ImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_1),
                        completedImageProjectionDto(DEFAULT_IMAGE_ID_2));
        return new ProductGroupImageCompositeDto(productGroupId, images, List.of());
    }

    /** 이미지가 없는 Composite DTO (빈 상태). */
    public static ProductGroupImageCompositeDto emptyCompositeDto(Long productGroupId) {
        return new ProductGroupImageCompositeDto(productGroupId, List.of(), List.of());
    }

    /** 혼합 상태 Composite DTO - COMPLETED 1개, PENDING 1개, FAILED 1개. */
    public static ProductGroupImageCompositeDto mixedStatusCompositeDto(Long productGroupId) {
        Long completedImageId = 20L;
        Long pendingImageId = 21L;
        Long failedImageId = 22L;

        List<ImageProjectionDto> images =
                List.of(
                        completedImageProjectionDto(completedImageId),
                        pendingImageProjectionDto(pendingImageId),
                        pendingImageProjectionDto(failedImageId));
        List<ImageOutboxProjectionDto> outboxes =
                List.of(
                        completedOutboxProjectionDto(completedImageId),
                        pendingOutboxProjectionDto(pendingImageId),
                        failedOutboxProjectionDto(failedImageId));
        return new ProductGroupImageCompositeDto(productGroupId, images, outboxes);
    }

    /** 단일 이미지 Composite DTO (PROCESSING 상태). */
    public static ProductGroupImageCompositeDto singleProcessingCompositeDto(Long productGroupId) {
        Long imageId = 30L;
        return new ProductGroupImageCompositeDto(
                productGroupId,
                List.of(completedImageProjectionDto(imageId)),
                List.of(processingOutboxProjectionDto(imageId)));
    }
}
