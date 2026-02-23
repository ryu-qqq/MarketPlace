package com.ryuqq.marketplace.application.productintelligence.internal.changedetector;

import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.DescriptionChangeDetector;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Description 변경 감지 구현체.
 *
 * <p>현재 Description의 Content Hash를 계산하여 최신 완료 프로파일에 저장된 hash와 비교합니다.
 */
@Component
public class DescriptionChangeDetectorImpl implements DescriptionChangeDetector {

    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final ProductProfileReadManager profileReadManager;
    private final DescriptionContentHashCalculator hashCalculator;

    public DescriptionChangeDetectorImpl(
            ProductGroupDescriptionReadManager descriptionReadManager,
            ProductProfileReadManager profileReadManager,
            DescriptionContentHashCalculator hashCalculator) {
        this.descriptionReadManager = descriptionReadManager;
        this.profileReadManager = profileReadManager;
        this.hashCalculator = hashCalculator;
    }

    @Override
    public boolean hasChanged(Long productGroupId, List<ExtractedAttribute> previousResults) {
        if (previousResults.isEmpty()) {
            return true;
        }

        ProductGroupDescription description =
                descriptionReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));

        String currentHash = hashCalculator.compute(description);

        Optional<ProductProfile> profileOpt =
                profileReadManager.findLatestCompletedByProductGroupId(productGroupId);
        if (profileOpt.isEmpty()) {
            return true;
        }

        String storedHash = profileOpt.get().descriptionContentHash();
        if (storedHash == null) {
            return true;
        }

        return !currentHash.equals(storedHash);
    }
}
