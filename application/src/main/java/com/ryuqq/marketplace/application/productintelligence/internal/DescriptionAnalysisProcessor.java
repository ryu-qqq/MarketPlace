package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productintelligence.internal.changedetector.DescriptionContentHashCalculator;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligencePublishManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileCommandManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.DescriptionChangeDetector;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description 분석 처리기.
 *
 * <p>Approach B(Analyzer Self-Judgment) 구조:
 *
 * <ol>
 *   <li>이전 프로파일의 분석 결과를 로드
 *   <li>ChangeDetector로 코드 레벨 변경 감지
 *   <li>변경 없음 → 이전 결과 이월 (AI 호출 스킵)
 *   <li>변경 있음 → AI 호출 (이전 결과를 컨텍스트로 전달)
 * </ol>
 *
 * <p>낙관적 락(version)으로 동시성을 제어하며, 버전 충돌 시 SQS visibility timeout 후 재시도됩니다.
 */
@Component
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class DescriptionAnalysisProcessor {

    private static final Logger log = LoggerFactory.getLogger(DescriptionAnalysisProcessor.class);

    private final DescriptionAnalysisAiClient aiClient;
    private final DescriptionChangeDetector changeDetector;
    private final ProductProfileReadManager profileReadManager;
    private final ProductProfileCommandManager profileCommandManager;
    private final IntelligencePublishManager publishManager;
    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final DescriptionContentHashCalculator hashCalculator;

    public DescriptionAnalysisProcessor(
            DescriptionAnalysisAiClient aiClient,
            DescriptionChangeDetector changeDetector,
            ProductProfileReadManager profileReadManager,
            ProductProfileCommandManager profileCommandManager,
            IntelligencePublishManager publishManager,
            ProductGroupDescriptionReadManager descriptionReadManager,
            DescriptionContentHashCalculator hashCalculator) {
        this.aiClient = aiClient;
        this.changeDetector = changeDetector;
        this.profileReadManager = profileReadManager;
        this.profileCommandManager = profileCommandManager;
        this.publishManager = publishManager;
        this.descriptionReadManager = descriptionReadManager;
        this.hashCalculator = hashCalculator;
    }

    @Transactional
    public void process(ProductProfile profile, Long productGroupId) {
        Instant now = Instant.now();

        // 1. 이전 프로파일의 분석 결과 로드
        List<ExtractedAttribute> previousResults = loadPreviousResults(profile.previousProfileId());

        // 2. Description 조회 (AI 호출 + hash 계산에 공통 사용)
        ProductGroupDescription description =
                descriptionReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));

        // 3. 코드 레벨 변경 감지 (Approach B)
        boolean dataHasChanged = changeDetector.hasChanged(productGroupId, previousResults);
        List<ExtractedAttribute> results;
        if (profile.shouldCarryForward(previousResults, dataHasChanged)) {
            // 변경 없음 -> 이전 결과 이월, AI 호출 스킵
            results = previousResults;
            log.info(
                    "Description 분석: 변경 없음, 이전 결과 이월. profileId={}, productGroupId={}",
                    profile.idValue(),
                    productGroupId);
        } else {
            // 변경 있음 (또는 최초 분석) -> AI 호출
            results = aiClient.analyze(description, previousResults);
        }

        // 4. Description content hash 계산 + 저장
        String contentHash = hashCalculator.compute(description);
        profile.updateDescriptionContentHash(contentHash);

        // 5. 분석 결과 기록 + 저장
        boolean allCompleted = profile.recordDescriptionAnalysis(results, now);
        profileCommandManager.persist(profile);

        log.info(
                "Description 분석 완료: profileId={}, productGroupId={}, extractedCount={},"
                        + " allCompleted={}",
                profile.idValue(),
                productGroupId,
                results.size(),
                allCompleted);

        // 6. 마지막 완료 시 -> Aggregation 큐 발행
        if (allCompleted) {
            publishManager.publishToAggregation(profile.idValue(), productGroupId);
        }
    }

    private List<ExtractedAttribute> loadPreviousResults(Long previousProfileId) {
        if (previousProfileId == null) {
            return List.of();
        }
        return profileReadManager
                .findById(previousProfileId)
                .map(ProductProfile::extractedAttributes)
                .orElse(List.of());
    }
}
