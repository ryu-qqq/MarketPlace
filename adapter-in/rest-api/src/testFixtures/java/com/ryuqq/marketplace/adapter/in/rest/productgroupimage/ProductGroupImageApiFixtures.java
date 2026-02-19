package com.ryuqq.marketplace.adapter.in.rest.productgroupimage;

import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response.ProductGroupImageUploadStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import java.util.List;

/**
 * ProductGroupImage API 테스트 Fixtures.
 *
 * <p>ProductGroupImage REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupImageApiFixtures {

    private ProductGroupImageApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_IMAGE_ID = 100L;
    public static final String DEFAULT_IMAGE_TYPE = "MAIN";
    public static final String DEFAULT_ORIGIN_URL = "https://origin.example.com/img1.jpg";
    public static final String DEFAULT_UPLOADED_URL = "https://cdn.example.com/img1.jpg";

    // ===== UpdateProductGroupImagesApiRequest =====

    public static UpdateProductGroupImagesApiRequest updateRequest() {
        List<UpdateProductGroupImagesApiRequest.ImageRequest> images =
                List.of(
                        new UpdateProductGroupImagesApiRequest.ImageRequest(
                                "MAIN", "https://origin.example.com/img1.jpg", 1),
                        new UpdateProductGroupImagesApiRequest.ImageRequest(
                                "DETAIL", "https://origin.example.com/img2.jpg", 2));
        return new UpdateProductGroupImagesApiRequest(images);
    }

    public static UpdateProductGroupImagesApiRequest updateRequestSingle() {
        List<UpdateProductGroupImagesApiRequest.ImageRequest> images =
                List.of(
                        new UpdateProductGroupImagesApiRequest.ImageRequest(
                                "MAIN", "https://origin.example.com/main.jpg", 1));
        return new UpdateProductGroupImagesApiRequest(images);
    }

    // ===== UpdateProductGroupImagesCommand =====

    public static UpdateProductGroupImagesCommand updateCommand(Long productGroupId) {
        return new UpdateProductGroupImagesCommand(
                productGroupId,
                List.of(
                        new UpdateProductGroupImagesCommand.ImageCommand(
                                "MAIN", "https://origin.example.com/img1.jpg", 1),
                        new UpdateProductGroupImagesCommand.ImageCommand(
                                "DETAIL", "https://origin.example.com/img2.jpg", 2)));
    }

    // ===== ProductGroupImageUploadStatusResult (Application) =====

    public static ProductGroupImageUploadStatusResult uploadStatusResult(Long productGroupId) {
        List<ProductGroupImageUploadStatusResult.ImageUploadDetail> images =
                List.of(
                        new ProductGroupImageUploadStatusResult.ImageUploadDetail(
                                100L,
                                "MAIN",
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "COMPLETED",
                                0,
                                null),
                        new ProductGroupImageUploadStatusResult.ImageUploadDetail(
                                101L,
                                "DETAIL",
                                "https://origin.example.com/img2.jpg",
                                null,
                                "PENDING",
                                0,
                                null));
        return new ProductGroupImageUploadStatusResult(productGroupId, 2, 1, 1, 0, 0, images);
    }

    public static ProductGroupImageUploadStatusResult uploadStatusResultAllCompleted(
            Long productGroupId) {
        List<ProductGroupImageUploadStatusResult.ImageUploadDetail> images =
                List.of(
                        new ProductGroupImageUploadStatusResult.ImageUploadDetail(
                                100L,
                                "MAIN",
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "COMPLETED",
                                0,
                                null));
        return new ProductGroupImageUploadStatusResult(productGroupId, 1, 1, 0, 0, 0, images);
    }

    public static ProductGroupImageUploadStatusResult uploadStatusResultWithFailed(
            Long productGroupId) {
        List<ProductGroupImageUploadStatusResult.ImageUploadDetail> images =
                List.of(
                        new ProductGroupImageUploadStatusResult.ImageUploadDetail(
                                100L,
                                "MAIN",
                                "https://origin.example.com/img1.jpg",
                                null,
                                "FAILED",
                                3,
                                "업로드 실패: 네트워크 오류"));
        return new ProductGroupImageUploadStatusResult(productGroupId, 1, 0, 0, 0, 1, images);
    }

    // ===== ProductGroupImageUploadStatusApiResponse =====

    public static ProductGroupImageUploadStatusApiResponse uploadStatusApiResponse(
            Long productGroupId) {
        List<ProductGroupImageUploadStatusApiResponse.ImageUploadDetailResponse> images =
                List.of(
                        new ProductGroupImageUploadStatusApiResponse.ImageUploadDetailResponse(
                                100L,
                                "MAIN",
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "COMPLETED",
                                0,
                                null),
                        new ProductGroupImageUploadStatusApiResponse.ImageUploadDetailResponse(
                                101L,
                                "DETAIL",
                                "https://origin.example.com/img2.jpg",
                                null,
                                "PENDING",
                                0,
                                null));
        return new ProductGroupImageUploadStatusApiResponse(productGroupId, 2, 1, 1, 0, 0, images);
    }
}
