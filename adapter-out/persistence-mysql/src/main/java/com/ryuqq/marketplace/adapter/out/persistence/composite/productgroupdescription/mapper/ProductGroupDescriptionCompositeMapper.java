package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionImageOutboxProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionImageProjectionDto;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult.DescriptionImageUploadDetail;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionCompositeMapper - Composite DTO 변환 Mapper.
 *
 * <p>Persistence DTO를 Application Result로 변환.
 *
 * <p>이미지별 최신 아웃박스 상태를 매핑하여 집계 결과를 생성.
 */
@Component
public class ProductGroupDescriptionCompositeMapper {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_FAILED = "FAILED";

    public DescriptionPublishStatusResult toResult(DescriptionCompositeDto dto) {
        Map<Long, DescriptionImageOutboxProjectionDto> outboxMap =
                dto.outboxes().stream()
                        .collect(
                                Collectors.toMap(
                                        DescriptionImageOutboxProjectionDto::sourceId,
                                        o -> o,
                                        (a, b) -> a));

        List<DescriptionImageUploadDetail> details =
                dto.images().stream().map(img -> toDetail(img, outboxMap)).toList();

        int completed = countByStatus(details, STATUS_COMPLETED);
        int pending = countByStatus(details, STATUS_PENDING);
        int failed = countByStatus(details, STATUS_FAILED);

        return new DescriptionPublishStatusResult(
                dto.productGroupId(),
                dto.description().descriptionId(),
                dto.description().publishStatus(),
                dto.description().cdnPath(),
                details.size(),
                completed,
                pending,
                failed,
                details);
    }

    private DescriptionImageUploadDetail toDetail(
            DescriptionImageProjectionDto img,
            Map<Long, DescriptionImageOutboxProjectionDto> outboxMap) {
        DescriptionImageOutboxProjectionDto outbox = outboxMap.get(img.imageId());
        return new DescriptionImageUploadDetail(
                img.imageId(),
                img.originUrl(),
                img.uploadedUrl(),
                outbox != null ? outbox.status() : null,
                outbox != null ? outbox.retryCount() : 0,
                outbox != null ? outbox.errorMessage() : null);
    }

    private int countByStatus(List<DescriptionImageUploadDetail> details, String status) {
        return (int) details.stream().filter(d -> status.equals(d.outboxStatus())).count();
    }
}
