package com.ryuqq.marketplace.application.productgroupinspection.internal;

import com.ryuqq.marketplace.application.productgroupinspection.internal.scorer.InspectionScorer;
import com.ryuqq.marketplace.application.productgroupinspection.manager.InspectionEnhancementPublishManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.InspectionVerificationPublishManager;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionResult;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scoring 단계 처리기.
 *
 * <p>Scorer를 실행하여 점수를 계산하고, 결과에 따라 Enhancement 또는 Verification 큐로 라우팅합니다.
 */
@Component
public class InspectionScoringProcessor {

    private static final Logger log = LoggerFactory.getLogger(InspectionScoringProcessor.class);

    private final ProductGroupInspectionOutboxCommandManager outboxCommandManager;
    private final List<InspectionScorer> scorers;
    private final InspectionEnhancementPublishManager enhancementPublishManager;
    private final InspectionVerificationPublishManager verificationPublishManager;

    public InspectionScoringProcessor(
            ProductGroupInspectionOutboxCommandManager outboxCommandManager,
            List<InspectionScorer> scorers,
            InspectionEnhancementPublishManager enhancementPublishManager,
            InspectionVerificationPublishManager verificationPublishManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.scorers = List.copyOf(scorers);
        this.enhancementPublishManager = enhancementPublishManager;
        this.verificationPublishManager = verificationPublishManager;
    }

    @Transactional
    public void process(ProductGroupInspectionOutbox outbox, String messageBody) {
        Instant now = Instant.now();
        outbox.startScoring(now);

        Map<InspectionScoreType, Integer> scoreResults = runScorers(outbox.productGroupId());
        int totalScore = calculateTotalScore(scoreResults);
        boolean passed = totalScore >= InspectionResult.PASSING_SCORE;

        List<String> failureReasons = new ArrayList<>();
        if (!passed) {
            failureReasons.add("점수 미달: " + totalScore + "/" + InspectionResult.PASSING_SCORE);
        }

        InspectionResult result =
                InspectionResult.of(scoreResults, totalScore, passed, failureReasons);
        outbox.saveScoringResult(result, result.toString());

        if (passed) {
            outbox.startVerifying(now);
            outboxCommandManager.persist(outbox);
            verificationPublishManager.publish(messageBody);
            log.info(
                    "Scoring 통과 → Verification: outboxId={}, totalScore={}",
                    outbox.idValue(),
                    totalScore);
        } else {
            outbox.startEnhancing(now);
            outboxCommandManager.persist(outbox);
            enhancementPublishManager.publish(messageBody);
            log.info(
                    "Scoring 미달 → Enhancement: outboxId={}, totalScore={}",
                    outbox.idValue(),
                    totalScore);
        }
    }

    private Map<InspectionScoreType, Integer> runScorers(Long productGroupId) {
        Map<InspectionScoreType, Integer> results = new EnumMap<>(InspectionScoreType.class);
        for (InspectionScorer scorer : scorers) {
            try {
                int score = scorer.score(productGroupId);
                results.put(scorer.type(), Math.max(0, Math.min(100, score)));
            } catch (Exception e) {
                log.warn(
                        "Score 계산 중 오류: scorer={}, productGroupId={}, error={}",
                        scorer.type(),
                        productGroupId,
                        e.getMessage());
                results.put(scorer.type(), 0);
            }
        }
        return results;
    }

    private int calculateTotalScore(Map<InspectionScoreType, Integer> scoreResults) {
        int weightedSum = 0;
        int totalWeight = 0;
        for (Map.Entry<InspectionScoreType, Integer> entry : scoreResults.entrySet()) {
            int weight = entry.getKey().weight();
            weightedSum += entry.getValue() * weight;
            totalWeight += weight;
        }
        return totalWeight > 0 ? weightedSum / totalWeight : 0;
    }
}
