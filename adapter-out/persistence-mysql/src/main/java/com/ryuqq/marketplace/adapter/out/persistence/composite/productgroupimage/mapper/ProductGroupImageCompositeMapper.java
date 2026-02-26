package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ImageOutboxProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ImageProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDto;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult.ImageUploadDetail;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImageCompositeMapper - Composite DTO 변환 Mapper.
 *
 * <p>Persistence DTO를 Application Result로 변환.
 *
 * <p>이미지별 최신 아웃박스 상태를 매핑하여 집계 결과를 생성.
 */
@Component
public class ProductGroupImageCompositeMapper {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_FAILED = "FAILED";

    public ProductGroupImageUploadStatusResult toResult(ProductGroupImageCompositeDto dto) {
        Map<Long, ImageOutboxProjectionDto> outboxMap =
                dto.outboxes().stream()
                        .collect(
                                Collectors.toMap(
                                        ImageOutboxProjectionDto::sourceId, o -> o, (a, b) -> a));

        List<ImageUploadDetail> details =
                dto.images().stream().map(img -> toDetail(img, outboxMap)).toList();

        int completed = countByStatus(details, STATUS_COMPLETED);
        int pending = countByStatus(details, STATUS_PENDING);
        int processing = countByStatus(details, STATUS_PROCESSING);
        int failed = countByStatus(details, STATUS_FAILED);

        return new ProductGroupImageUploadStatusResult(
                dto.productGroupId(),
                details.size(),
                completed,
                pending,
                processing,
                failed,
                details);
    }

    private ImageUploadDetail toDetail(
            ImageProjectionDto img, Map<Long, ImageOutboxProjectionDto> outboxMap) {
        ImageOutboxProjectionDto outbox = outboxMap.get(img.imageId());
        return new ImageUploadDetail(
                img.imageId(),
                img.imageType(),
                img.originUrl(),
                img.uploadedUrl(),
                outbox != null ? outbox.status() : null,
                outbox != null ? outbox.retryCount() : 0,
                outbox != null ? outbox.errorMessage() : null);
    }

    private int countByStatus(List<ImageUploadDetail> details, String status) {
        return (int) details.stream().filter(d -> status.equals(d.outboxStatus())).count();
    }
}
