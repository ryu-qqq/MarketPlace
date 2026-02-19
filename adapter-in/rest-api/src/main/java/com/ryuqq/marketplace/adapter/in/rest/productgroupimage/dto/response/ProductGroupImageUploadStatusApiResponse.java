package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import java.util.List;

public record ProductGroupImageUploadStatusApiResponse(
        Long productGroupId,
        int totalCount,
        int completedCount,
        int pendingCount,
        int processingCount,
        int failedCount,
        List<ImageUploadDetailResponse> images) {

    public record ImageUploadDetailResponse(
            Long imageId,
            String imageType,
            String originUrl,
            String uploadedUrl,
            String outboxStatus,
            int retryCount,
            String errorMessage) {}

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
