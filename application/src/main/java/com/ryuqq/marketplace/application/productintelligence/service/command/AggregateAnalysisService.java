package com.ryuqq.marketplace.application.productintelligence.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.productintelligence.dto.command.AggregateAnalysisCommand;
import com.ryuqq.marketplace.application.productintelligence.factory.ProductProfileCommandFactory;
import com.ryuqq.marketplace.application.productintelligence.internal.AnalysisDecisionMaker;
import com.ryuqq.marketplace.application.productintelligence.internal.PostAnalysisProductGroupCoordinator;
import com.ryuqq.marketplace.application.productintelligence.manager.ProductProfileCommandManager;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.AggregateAnalysisUseCase;
import com.ryuqq.marketplace.application.productintelligence.validator.ProductProfileAggregationValidator;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 분석 결과 집계 + 최종 판정 서비스.
 *
 * <p>모든 Analyzer가 완료된 후 호출됩니다. 3개 분석 결과를 종합하여 최종 판정(AUTO_APPROVED / HUMAN_REVIEW / AUTO_REJECTED)을
 * 내립니다.
 *
 * <p>흐름: Aggregation 큐 수신 → ProductProfile 로드 → AGGREGATING 전환 → DecisionMaker 판정 → COMPLETED 전환 →
 * 저장
 */
@Service
public class AggregateAnalysisService implements AggregateAnalysisUseCase {

    private static final Logger log = LoggerFactory.getLogger(AggregateAnalysisService.class);

    private final ProductProfileAggregationValidator validator;
    private final ProductProfileCommandFactory commandFactory;
    private final ProductProfileCommandManager profileCommandManager;
    private final AnalysisDecisionMaker decisionMaker;
    private final PostAnalysisProductGroupCoordinator postAnalysisCoordinator;

    public AggregateAnalysisService(
            ProductProfileAggregationValidator validator,
            ProductProfileCommandFactory commandFactory,
            ProductProfileCommandManager profileCommandManager,
            AnalysisDecisionMaker decisionMaker,
            PostAnalysisProductGroupCoordinator postAnalysisCoordinator) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.profileCommandManager = profileCommandManager;
        this.decisionMaker = decisionMaker;
        this.postAnalysisCoordinator = postAnalysisCoordinator;
    }

    @Override
    public void execute(AggregateAnalysisCommand command) {
        StatusChangeContext<Long> context = commandFactory.createAggregationContext(command);

        validator
                .validateForAggregation(context.id())
                .ifPresent(
                        profile ->
                                aggregate(profile, command.productGroupId(), context.changedAt()));
    }

    private void aggregate(ProductProfile profile, Long productGroupId, Instant now) {
        profile.startAggregating(now);

        InspectionDecision decision = decisionMaker.decide(profile, now);

        profile.complete(decision, null, now);

        profileCommandManager.persist(profile);

        postAnalysisCoordinator.execute(productGroupId, decision, now);

        log.info(
                "분석 최종 판정 완료: profileId={}, productGroupId={}, decision={}, confidence={}%",
                profile.idValue(),
                productGroupId,
                decision.decisionType(),
                decision.overallConfidencePercentage());
    }
}
