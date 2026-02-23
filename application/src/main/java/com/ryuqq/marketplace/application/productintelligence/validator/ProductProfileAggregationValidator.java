package com.ryuqq.marketplace.application.productintelligence.validator;

import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.exception.ProductProfileNotFoundException;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aggregation 실행을 위한 프로파일 검증.
 *
 * <p>프로파일을 로드하고 Aggregation 실행 가능 여부(상태 + 분석 완료)를 검증합니다. 검증 통과 시 프로파일을 Optional로 반환하여 Service에서
 * 간결하게 분기할 수 있습니다.
 */
@Component
public class ProductProfileAggregationValidator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductProfileAggregationValidator.class);

    private final ProductProfileReadManager profileReadManager;

    public ProductProfileAggregationValidator(ProductProfileReadManager profileReadManager) {
        this.profileReadManager = profileReadManager;
    }

    /**
     * Aggregation 실행을 위한 프로파일 검증.
     *
     * @param profileId 프로파일 ID
     * @return 집계 가능하면 Optional.of(profile), 스킵이면 empty
     * @throws ProductProfileNotFoundException 프로파일 미존재 시
     */
    public Optional<ProductProfile> validateForAggregation(Long profileId) {
        ProductProfile profile =
                profileReadManager
                        .findById(profileId)
                        .orElseThrow(() -> new ProductProfileNotFoundException(profileId));

        if (!profile.hasExpectedStatus(AnalysisStatus.ANALYZING)) {
            log.info(
                    "Aggregation 스킵 (상태 불일치): profileId={}, status={}",
                    profileId,
                    profile.status());
            return Optional.empty();
        }

        if (!profile.isAllAnalysisCompleted()) {
            log.warn(
                    "Aggregation 스킵 (분석 미완료): profileId={}, completed={}/{}",
                    profileId,
                    profile.completedAnalysisCount(),
                    profile.expectedAnalysisCount());
            return Optional.empty();
        }

        return Optional.of(profile);
    }
}
