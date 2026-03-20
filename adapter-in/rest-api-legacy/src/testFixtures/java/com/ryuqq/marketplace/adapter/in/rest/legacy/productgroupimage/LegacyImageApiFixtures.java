package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import java.util.List;

/**
 * Legacy Image API 테스트 Fixtures.
 *
 * <p>Legacy 상품그룹 이미지 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyImageApiFixtures {

    private LegacyImageApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final String DEFAULT_IMAGE_TYPE_THUMBNAIL = "THUMBNAIL";
    public static final String DEFAULT_IMAGE_TYPE_DETAIL = "DETAIL";
    public static final String DEFAULT_PRODUCT_IMAGE_URL =
            "https://cdn.example.com/products/thumbnail.jpg";
    public static final String DEFAULT_ORIGIN_URL =
            "https://origin.example.com/products/thumbnail.jpg";
    public static final String DETAIL_PRODUCT_IMAGE_URL =
            "https://cdn.example.com/products/detail.jpg";
    public static final String DETAIL_ORIGIN_URL = "https://origin.example.com/products/detail.jpg";

    // ===== Request Fixtures =====

    public static LegacyCreateProductImageRequest thumbnailRequest() {
        return new LegacyCreateProductImageRequest(
                DEFAULT_IMAGE_TYPE_THUMBNAIL, DEFAULT_PRODUCT_IMAGE_URL, DEFAULT_ORIGIN_URL);
    }

    public static LegacyCreateProductImageRequest detailRequest() {
        return new LegacyCreateProductImageRequest(
                DEFAULT_IMAGE_TYPE_DETAIL, DETAIL_PRODUCT_IMAGE_URL, DETAIL_ORIGIN_URL);
    }

    public static List<LegacyCreateProductImageRequest> requestList() {
        return List.of(thumbnailRequest(), detailRequest());
    }

    public static List<LegacyCreateProductImageRequest> singleRequestList() {
        return List.of(thumbnailRequest());
    }

    public static LegacyCreateProductImageRequest requestWith(
            String type, String productImageUrl, String originUrl) {
        return new LegacyCreateProductImageRequest(type, productImageUrl, originUrl);
    }

    // ===== Command Fixtures =====

    public static LegacyUpdateImagesCommand command(long productGroupId) {
        List<LegacyUpdateImagesCommand.ImageEntry> entries =
                List.of(
                        new LegacyUpdateImagesCommand.ImageEntry(
                                DEFAULT_IMAGE_TYPE_THUMBNAIL,
                                DEFAULT_PRODUCT_IMAGE_URL,
                                DEFAULT_ORIGIN_URL),
                        new LegacyUpdateImagesCommand.ImageEntry(
                                DEFAULT_IMAGE_TYPE_DETAIL,
                                DETAIL_PRODUCT_IMAGE_URL,
                                DETAIL_ORIGIN_URL));
        return new LegacyUpdateImagesCommand(productGroupId, entries);
    }

    public static LegacyUpdateImagesCommand singleImageCommand(long productGroupId) {
        List<LegacyUpdateImagesCommand.ImageEntry> entries =
                List.of(
                        new LegacyUpdateImagesCommand.ImageEntry(
                                DEFAULT_IMAGE_TYPE_THUMBNAIL,
                                DEFAULT_PRODUCT_IMAGE_URL,
                                DEFAULT_ORIGIN_URL));
        return new LegacyUpdateImagesCommand(productGroupId, entries);
    }
}
