package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "상품 그룹 이미지 업로드 상태 응답")
public record ProductGroupImageUploadStatusApiResponse(
        @Schema(description = "상품 그룹 ID", example = "1") Long productGroupId,
        @Schema(description = "전체 이미지 수", example = "5") int totalCount,
        @Schema(description = "완료 수", example = "4") int completedCount,
        @Schema(description = "대기 수", example = "1") int pendingCount,
        @Schema(description = "처리중 수", example = "0") int processingCount,
        @Schema(description = "실패 수", example = "0") int failedCount,
        @Schema(description = "이미지 상세 목록") List<ImageUploadDetailResponse> images) {

    @Schema(description = "이미지 업로드 상세")
    public record ImageUploadDetailResponse(
            @Schema(description = "이미지 ID", example = "1") Long imageId,
            @Schema(description = "이미지 유형 (THUMBNAIL, DETAIL)", example = "THUMBNAIL")
                    String imageType,
            @Schema(description = "원본 URL", example = "https://example.com/image.jpg")
                    String originUrl,
            @Schema(description = "업로드 URL", example = "https://cdn.example.com/image.jpg")
                    String uploadedUrl,
            @Schema(
                            description = "아웃박스 상태 (PENDING, PROCESSING, COMPLETED, FAILED)",
                            example = "COMPLETED")
                    String outboxStatus,
            @Schema(description = "재시도 횟수", example = "0") int retryCount,
            @Schema(description = "에러 메시지", example = "") String errorMessage) {}

    public static ProductGroupImageUploadStatusApiResponse from(
            ProductGroupImageUploadStatusResult result) {
        return new ProductGroupImageUploadStatusApiResponse(
                result.productGroupId(),
                result.totalCount(),
                result.completedCount(),
                result.pendingCount(),
                result.processingCount(),
                result.failedCount(),
                result.images().stream()
                        .map(
                                detail ->
                                        new ImageUploadDetailResponse(
                                                detail.imageId(),
                                                detail.imageType(),
                                                detail.originUrl(),
                                                detail.uploadedUrl(),
                                                detail.outboxStatus(),
                                                detail.retryCount(),
                                                detail.errorMessage()))
                        .toList());
    }
}
