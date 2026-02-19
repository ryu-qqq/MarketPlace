package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription;

import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response.DescriptionPublishStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import java.util.List;

/**
 * ProductGroupDescription API 테스트 Fixtures.
 *
 * <p>ProductGroupDescription REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupDescriptionApiFixtures {

    private ProductGroupDescriptionApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_DESCRIPTION_ID = 10L;
    public static final String DEFAULT_CONTENT = "<p>상품 상세 설명 HTML 내용입니다.</p>";
    public static final String DEFAULT_CDN_PATH = "https://cdn.example.com/description/";

    // ===== UpdateProductGroupDescriptionApiRequest =====

    public static UpdateProductGroupDescriptionApiRequest updateRequest() {
        return new UpdateProductGroupDescriptionApiRequest(DEFAULT_CONTENT);
    }

    public static UpdateProductGroupDescriptionApiRequest updateRequest(String content) {
        return new UpdateProductGroupDescriptionApiRequest(content);
    }

    // ===== UpdateProductGroupDescriptionCommand =====

    public static UpdateProductGroupDescriptionCommand updateCommand(Long productGroupId) {
        return new UpdateProductGroupDescriptionCommand(productGroupId, DEFAULT_CONTENT);
    }

    // ===== DescriptionPublishStatusResult (Application) =====

    public static DescriptionPublishStatusResult publishStatusResult(Long productGroupId) {
        List<DescriptionPublishStatusResult.DescriptionImageUploadDetail> images =
                List.of(
                        new DescriptionPublishStatusResult.DescriptionImageUploadDetail(
                                200L,
                                "https://origin.example.com/desc1.jpg",
                                "https://cdn.example.com/desc1.jpg",
                                "COMPLETED",
                                0,
                                null),
                        new DescriptionPublishStatusResult.DescriptionImageUploadDetail(
                                201L,
                                "https://origin.example.com/desc2.jpg",
                                null,
                                "PENDING",
                                0,
                                null));
        return new DescriptionPublishStatusResult(
                productGroupId,
                DEFAULT_DESCRIPTION_ID,
                "DRAFT",
                DEFAULT_CDN_PATH,
                2,
                1,
                1,
                0,
                images);
    }

    public static DescriptionPublishStatusResult publishStatusResultPublished(Long productGroupId) {
        List<DescriptionPublishStatusResult.DescriptionImageUploadDetail> images =
                List.of(
                        new DescriptionPublishStatusResult.DescriptionImageUploadDetail(
                                200L,
                                "https://origin.example.com/desc1.jpg",
                                "https://cdn.example.com/desc1.jpg",
                                "COMPLETED",
                                0,
                                null));
        return new DescriptionPublishStatusResult(
                productGroupId,
                DEFAULT_DESCRIPTION_ID,
                "PUBLISHED",
                DEFAULT_CDN_PATH,
                1,
                1,
                0,
                0,
                images);
    }

    public static DescriptionPublishStatusResult publishStatusResultEmpty(Long productGroupId) {
        return DescriptionPublishStatusResult.empty(productGroupId);
    }

    // ===== DescriptionPublishStatusApiResponse =====

    public static DescriptionPublishStatusApiResponse publishStatusApiResponse(
            Long productGroupId) {
        List<DescriptionPublishStatusApiResponse.DescriptionImageUploadDetailResponse> images =
                List.of(
                        new DescriptionPublishStatusApiResponse
                                .DescriptionImageUploadDetailResponse(
                                200L,
                                "https://origin.example.com/desc1.jpg",
                                "https://cdn.example.com/desc1.jpg",
                                "COMPLETED",
                                0,
                                null),
                        new DescriptionPublishStatusApiResponse
                                .DescriptionImageUploadDetailResponse(
                                201L,
                                "https://origin.example.com/desc2.jpg",
                                null,
                                "PENDING",
                                0,
                                null));
        return new DescriptionPublishStatusApiResponse(
                productGroupId,
                DEFAULT_DESCRIPTION_ID,
                "DRAFT",
                DEFAULT_CDN_PATH,
                2,
                1,
                1,
                0,
                images);
    }
}
