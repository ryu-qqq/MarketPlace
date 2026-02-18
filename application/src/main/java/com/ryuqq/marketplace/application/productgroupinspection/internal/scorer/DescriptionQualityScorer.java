package com.ryuqq.marketplace.application.productgroupinspection.internal.scorer;

import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 상세설명 품질을 평가하는 Scorer. */
@Component
public class DescriptionQualityScorer implements InspectionScorer {

    private final ProductGroupDescriptionReadManager descriptionReadManager;

    public DescriptionQualityScorer(ProductGroupDescriptionReadManager descriptionReadManager) {
        this.descriptionReadManager = descriptionReadManager;
    }

    @Override
    public InspectionScoreType type() {
        return InspectionScoreType.DESCRIPTION_QUALITY;
    }

    @Override
    public int score(Long productGroupId) {
        Optional<ProductGroupDescription> descOpt =
                descriptionReadManager.findByProductGroupId(ProductGroupId.of(productGroupId));
        if (descOpt.isEmpty()) {
            return 0;
        }
        DescriptionPublishStatus status = descOpt.get().publishStatus();
        return switch (status) {
            case PUBLISHED -> 100;
            case PUBLISH_READY -> 50;
            case PENDING -> 0;
        };
    }
}
