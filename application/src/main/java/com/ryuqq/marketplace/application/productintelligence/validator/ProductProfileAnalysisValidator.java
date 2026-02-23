package com.ryuqq.marketplace.application.productintelligence.validator;

import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.exception.ProductProfileNotFoundException;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 프로파일 로드 + 분석 실행 가능 여부 검증.
 *
 * <p>3개 Analyzer Service에서 반복되던 프로파일 조회 + 상태/중복 체크 로직을 한 곳으로 추출합니다. 검증 통과 시 프로파일을 Optional로 반환하여
 * Service에서 간결하게 분기할 수 있습니다.
 */
@Component
public class ProductProfileAnalysisValidator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductProfileAnalysisValidator.class);

    private final ProductProfileReadManager profileReadManager;

    public ProductProfileAnalysisValidator(ProductProfileReadManager profileReadManager) {
        this.profileReadManager = profileReadManager;
    }

    /**
     * 프로파일을 로드하고 분석 실행 가능 여부를 검증합니다.
     *
     * @param profileId 프로파일 ID
     * @param analysisType 실행하려는 분석 타입
     * @return 실행 가능하면 프로파일을 담은 Optional, 스킵해야 하면 empty
     * @throws ProductProfileNotFoundException 프로파일이 존재하지 않을 경우
     */
    public Optional<ProductProfile> validateAndLoad(Long profileId, AnalysisType analysisType) {
        ProductProfile profile =
                profileReadManager
                        .findById(profileId)
                        .orElseThrow(() -> new ProductProfileNotFoundException(profileId));

        if (!profile.canExecuteAnalysis(analysisType)) {
            log.info(
                    "{} 분석 스킵: profileId={}, status={}, completedTypes={}",
                    analysisType,
                    profileId,
                    profile.status(),
                    profile.completedAnalysisTypes());
            return Optional.empty();
        }
        return Optional.of(profile);
    }
}
