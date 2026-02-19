package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "상세설명 발행 상태 응답")
public record DescriptionPublishStatusApiResponse(
        @Schema(description = "상품 그룹 ID", example = "1") Long productGroupId,
        @Schema(description = "상세설명 ID", example = "1") Long descriptionId,
        @Schema(
                        description = "발행 상태 (PENDING, PROCESSING, COMPLETED, FAILED)",
                        example = "COMPLETED")
                String publishStatus,
        @Schema(description = "CDN 경로", example = "https://cdn.example.com/desc/1") String cdnPath,
        @Schema(description = "전체 이미지 수", example = "5") int totalImageCount,
        @Schema(description = "완료 이미지 수", example = "4") int completedImageCount,
        @Schema(description = "대기 이미지 수", example = "1") int pendingImageCount,
        @Schema(description = "실패 이미지 수", example = "0") int failedImageCount,
        @Schema(description = "이미지 상세 목록") List<DescriptionImageUploadDetailResponse> images) {
    @Schema(description = "상세설명 이미지 업로드 상세")
    public record DescriptionImageUploadDetailResponse(
            @Schema(description = "이미지 ID", example = "1") Long imageId,
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

    public static DescriptionPublishStatusApiResponse from(DescriptionPublishStatusResult result) {
        return new DescriptionPublishStatusApiResponse(
                result.productGroupId(),
                result.descriptionId(),
                result.publishStatus(),
                result.cdnPath(),
                result.totalImageCount(),
                result.completedImageCount(),
                result.pendingImageCount(),
                result.failedImageCount(),
                result.images().stream()
                        .map(
                                detail ->
                                        new DescriptionImageUploadDetailResponse(
                                                detail.imageId(),
                                                detail.originUrl(),
                                                detail.uploadedUrl(),
                                                detail.outboxStatus(),
                                                detail.retryCount(),
                                                detail.errorMessage()))
                        .toList());
    }
}
