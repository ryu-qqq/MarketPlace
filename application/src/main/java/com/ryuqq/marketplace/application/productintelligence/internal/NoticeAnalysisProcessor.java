package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.application.productintelligence.manager.IntelligencePublishManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileCommandManager;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.NoticeChangeDetector;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notice 분석 처리기.
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
public class NoticeAnalysisProcessor {

    private static final Logger log = LoggerFactory.getLogger(NoticeAnalysisProcessor.class);

    private final NoticeAnalysisAiClient aiClient;
    private final NoticeChangeDetector changeDetector;
    private final ProductProfileReadManager profileReadManager;
    private final ProductProfileCommandManager profileCommandManager;
    private final IntelligencePublishManager publishManager;
    private final ProductNoticeReadManager productNoticeReadManager;

    public NoticeAnalysisProcessor(
            NoticeAnalysisAiClient aiClient,
            NoticeChangeDetector changeDetector,
            ProductProfileReadManager profileReadManager,
            ProductProfileCommandManager profileCommandManager,
            IntelligencePublishManager publishManager,
            ProductNoticeReadManager productNoticeReadManager) {
        this.aiClient = aiClient;
        this.changeDetector = changeDetector;
        this.profileReadManager = profileReadManager;
        this.profileCommandManager = profileCommandManager;
        this.publishManager = publishManager;
        this.productNoticeReadManager = productNoticeReadManager;
    }

    @Transactional
    public void process(ProductProfile profile, Long productGroupId) {
        Instant now = Instant.now();

        // 1. 이전 프로파일의 분석 결과 로드
        List<NoticeSuggestion> previousResults = loadPreviousResults(profile.previousProfileId());

        // 2. 코드 레벨 변경 감지 (Approach B)
        boolean dataHasChanged = changeDetector.hasChanged(productGroupId, previousResults);
        List<NoticeSuggestion> results;
        if (profile.shouldCarryForward(previousResults, dataHasChanged)) {
            // 변경 없음 -> 이전 결과 이월, AI 호출 스킵
            results = previousResults;
            log.info(
                    "Notice 분석: 변경 없음, 이전 결과 이월. profileId={}, productGroupId={}",
                    profile.idValue(),
                    productGroupId);
        } else {
            // 변경 있음 (또는 최초 분석) -> 고시정보 조회 후 AI 호출
            ProductNotice productNotice =
                    productNoticeReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));
            results = aiClient.analyze(productNotice, previousResults);
        }

        // 3. 분석 결과 기록 + 저장
        boolean allCompleted = profile.recordNoticeAnalysis(results, now);
        profileCommandManager.persist(profile);

        log.info(
                "Notice 분석 완료: profileId={}, productGroupId={}, suggestionCount={},"
                        + " allCompleted={}",
                profile.idValue(),
                productGroupId,
                results.size(),
                allCompleted);

        // 4. 마지막 완료 시 -> Aggregation 큐 발행
        if (allCompleted) {
            publishManager.publishToAggregation(profile.idValue(), productGroupId);
        }
    }

    private List<NoticeSuggestion> loadPreviousResults(Long previousProfileId) {
        if (previousProfileId == null) {
            return List.of();
        }
        return profileReadManager
                .findById(previousProfileId)
                .map(ProductProfile::noticeSuggestions)
                .orElse(List.of());
    }
}
