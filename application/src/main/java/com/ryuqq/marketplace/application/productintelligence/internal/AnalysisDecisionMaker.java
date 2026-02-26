package com.ryuqq.marketplace.application.productintelligence.internal;

import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.ConfidenceScore;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 분석 결과 기반 최종 판정 로직.
 *
 * <p>Rule 기반으로 3개 분석 결과를 평가하고, 소스별 신뢰도 보정 + 타입별 가중치를 적용하여 최종 판정을 내립니다.
 *
 * <p>향후 LLM 기반 판정으로 확장 가능합니다.
 */
@Component
public class AnalysisDecisionMaker {

    /** 소스별 신뢰도 보정 계수. 결정론적 소스일수록 높은 신뢰. */
    private static final Map<AnalysisSource, Double> SOURCE_TRUST_FACTORS =
            Map.of(
                    AnalysisSource.RULE_ENGINE, 1.00,
                    AnalysisSource.DESCRIPTION_TEXT, 0.95,
                    AnalysisSource.LLM_INFERENCE, 0.90,
                    AnalysisSource.IMAGE_MULTIMODAL, 0.85);

    /** 타입별 가중치 (합 = 1.0). 옵션 매핑이 실질적으로 가장 중요. */
    private static final double ATTRIBUTE_WEIGHT = 0.30;

    private static final double OPTION_WEIGHT = 0.40;
    private static final double NOTICE_WEIGHT = 0.30;

    /**
     * 분석 결과를 종합하여 최종 판정을 내립니다.
     *
     * @param profile 분석 완료된 ProductProfile
     * @param now 현재 시각
     * @return 최종 판정 결과
     */
    public InspectionDecision decide(ProductProfile profile, Instant now) {
        List<TypeEvaluation> evaluations =
                List.of(
                        evaluateAttributes(profile.extractedAttributes()),
                        evaluateOptions(profile.optionSuggestions()),
                        evaluateNotices(profile.noticeSuggestions()));

        List<String> reasons =
                evaluations.stream()
                        .map(TypeEvaluation::toReasonString)
                        .collect(Collectors.toCollection(ArrayList::new));

        double overallConfidence =
                evaluations.stream().mapToDouble(TypeEvaluation::weightedScore).sum();

        if (overallConfidence >= ConfidenceScore.AUTO_APPLY_THRESHOLD) {
            return InspectionDecision.autoApprove(overallConfidence, reasons, now);
        } else if (overallConfidence >= ConfidenceScore.REVIEW_THRESHOLD) {
            return InspectionDecision.humanReview(overallConfidence, reasons, now);
        } else {
            return InspectionDecision.autoReject(overallConfidence, reasons, now);
        }
    }

    private TypeEvaluation evaluateAttributes(List<ExtractedAttribute> attributes) {
        if (attributes.isEmpty()) {
            return new TypeEvaluation("속성 추출", 0, 0, 0.0, ATTRIBUTE_WEIGHT);
        }
        double avgAdjusted =
                attributes.stream()
                        .mapToDouble(a -> adjustBySource(a.confidenceValue(), a.source()))
                        .average()
                        .orElse(0.0);
        long autoApplicable =
                attributes.stream().filter(ExtractedAttribute::isAutoApplicable).count();
        return new TypeEvaluation(
                "속성 추출", attributes.size(), autoApplicable, avgAdjusted, ATTRIBUTE_WEIGHT);
    }

    private TypeEvaluation evaluateOptions(List<OptionMappingSuggestion> options) {
        if (options.isEmpty()) {
            return new TypeEvaluation("옵션 매핑", 0, 0, 0.0, OPTION_WEIGHT);
        }
        double avgAdjusted =
                options.stream()
                        .mapToDouble(o -> adjustBySource(o.confidenceValue(), o.source()))
                        .average()
                        .orElse(0.0);
        long autoApplicable =
                options.stream().filter(OptionMappingSuggestion::isAutoApplicable).count();
        return new TypeEvaluation(
                "옵션 매핑", options.size(), autoApplicable, avgAdjusted, OPTION_WEIGHT);
    }

    private TypeEvaluation evaluateNotices(List<NoticeSuggestion> notices) {
        if (notices.isEmpty()) {
            return new TypeEvaluation("고시정보 보강", 0, 0, 0.0, NOTICE_WEIGHT);
        }
        double avgAdjusted =
                notices.stream()
                        .mapToDouble(n -> adjustBySource(n.confidenceValue(), n.source()))
                        .average()
                        .orElse(0.0);
        long autoApplicable = notices.stream().filter(NoticeSuggestion::isAutoApplicable).count();
        return new TypeEvaluation(
                "고시정보 보강", notices.size(), autoApplicable, avgAdjusted, NOTICE_WEIGHT);
    }

    private double adjustBySource(double rawConfidence, AnalysisSource source) {
        double trustFactor = SOURCE_TRUST_FACTORS.getOrDefault(source, 0.90);
        return Math.min(rawConfidence * trustFactor, 1.0);
    }

    /** 타입별 평가 결과를 캡슐화하는 내부 record. */
    private record TypeEvaluation(
            String typeName,
            int itemCount,
            long autoApplicableCount,
            double adjustedConfidence,
            double weight) {

        double weightedScore() {
            return adjustedConfidence * weight;
        }

        boolean isEmpty() {
            return itemCount == 0;
        }

        String toReasonString() {
            if (isEmpty()) {
                return typeName + ": 결과 없음 (0.0 반영)";
            }
            return String.format(
                    "%s: %d건 (자동적용 %d건), 보정 신뢰도 %.0f%%, 가중치 %.0f%%",
                    typeName,
                    itemCount,
                    autoApplicableCount,
                    adjustedConfidence * 100,
                    weight * 100);
        }
    }
}
