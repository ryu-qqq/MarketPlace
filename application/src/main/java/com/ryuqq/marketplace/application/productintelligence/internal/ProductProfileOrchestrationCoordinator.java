package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileCommandManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 프로파일 오케스트레이션 Coordinator.
 *
 * <p>새 ProductProfile 생성 시 이전 프로파일을 조회하여 previousProfileId를 연결하고, 버전을 계산하여 ANALYZING 상태의 신규 프로파일을
 * 생성합니다.
 *
 * <p>프로파일 이력은 만료 없이 히스토리로 관리됩니다. 각 Analyzer는 previousProfileId를 통해 이전 분석 결과를 참조하여 재분석 여부를 스스로 판단합니다
 * (Approach B: Analyzer Self-Judgment).
 */
@Component
public class ProductProfileOrchestrationCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(ProductProfileOrchestrationCoordinator.class);

    private final ProductProfileReadManager profileReadManager;
    private final ProductProfileCommandManager profileCommandManager;

    public ProductProfileOrchestrationCoordinator(
            ProductProfileReadManager profileReadManager,
            ProductProfileCommandManager profileCommandManager) {
        this.profileReadManager = profileReadManager;
        this.profileCommandManager = profileCommandManager;
    }

    /**
     * 새 ProductProfile을 ANALYZING 상태로 생성합니다.
     *
     * <p>최신 프로파일 1건을 조회하여 nextVersion과 previousProfileId를 결정합니다. 최신 프로파일이 COMPLETED이면 해당 ID를, 아니면
     * 해당 프로파일의 previousProfileId를 체이닝하여 가장 최근 완료된 분석 결과를 참조합니다.
     *
     * @param productGroupId 상품그룹 ID
     * @return 생성된 profileId
     */
    @Transactional
    public Long createAndStartAnalyzing(Long productGroupId) {
        Instant now = Instant.now();

        int nextVersion;
        Long previousProfileId;

        var latestOpt = profileReadManager.findLatestByProductGroupId(productGroupId);
        if (latestOpt.isPresent()) {
            ProductProfile latest = latestOpt.get();
            nextVersion = latest.profileVersion() + 1;
            previousProfileId =
                    latest.hasExpectedStatus(AnalysisStatus.COMPLETED)
                            ? latest.idValue()
                            : latest.previousProfileId();
        } else {
            nextVersion = 1;
            previousProfileId = null;
        }

        ProductProfile profile =
                ProductProfile.forNewAnalyzing(productGroupId, previousProfileId, nextVersion, now);

        Long profileId = profileCommandManager.persist(profile);

        log.info(
                "ProductProfile 생성 완료: profileId={}, productGroupId={}, version={},"
                        + " previousProfileId={}",
                profileId,
                productGroupId,
                nextVersion,
                previousProfileId);

        return profileId;
    }
}
