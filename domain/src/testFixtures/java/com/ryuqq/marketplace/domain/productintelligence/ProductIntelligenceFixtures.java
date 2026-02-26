package com.ryuqq.marketplace.domain.productintelligence;

import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.id.IntelligenceOutboxId;
import com.ryuqq.marketplace.domain.productintelligence.id.ProductProfileId;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ConfidenceScore;
import com.ryuqq.marketplace.domain.productintelligence.vo.DecisionType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

/**
 * ProductIntelligence 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ProductProfile, IntelligenceOutbox 관련 도메인 객체들을 생성합니다.
 */
public final class ProductIntelligenceFixtures {

    private ProductIntelligenceFixtures() {}

    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_PROFILE_ID = 1L;
    public static final Long DEFAULT_OUTBOX_ID = 1L;
    public static final int DEFAULT_PROFILE_VERSION = 1;
    public static final String DEFAULT_IDEMPOTENCY_KEY =
            "PI:" + DEFAULT_PRODUCT_GROUP_ID + ":1740556800000";

    // ===== ProductProfile Fixtures =====

    /** PENDING 상태의 신규 ProductProfile 생성. */
    public static ProductProfile pendingProductProfile() {
        Instant now = Instant.now();
        return ProductProfile.forNew(DEFAULT_PRODUCT_GROUP_ID, null, DEFAULT_PROFILE_VERSION, now);
    }

    /** PENDING 상태의 기존 ProductProfile 재구성. */
    public static ProductProfile existingPendingProductProfile() {
        Instant now = Instant.now();
        return ProductProfile.reconstitute(
                ProductProfileId.of(DEFAULT_PROFILE_ID),
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_PROFILE_VERSION,
                AnalysisStatus.PENDING,
                3,
                0,
                EnumSet.noneOf(AnalysisType.class),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                0L);
    }

    /** 특정 productGroupId의 PENDING 상태 ProductProfile 재구성. */
    public static ProductProfile existingPendingProductProfile(
            Long profileId, Long productGroupId) {
        Instant now = Instant.now();
        return ProductProfile.reconstitute(
                ProductProfileId.of(profileId),
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                AnalysisStatus.PENDING,
                3,
                0,
                EnumSet.noneOf(AnalysisType.class),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                0L);
    }

    /** COMPLETED 상태의 ProductProfile 재구성 (AUTO_APPROVED). */
    public static ProductProfile completedProductProfile() {
        Instant now = Instant.now();
        InspectionDecision decision =
                new InspectionDecision(
                        DecisionType.AUTO_APPROVED,
                        ConfidenceScore.of(0.95),
                        List.of("분석 완료"),
                        now);
        return ProductProfile.reconstitute(
                ProductProfileId.of(DEFAULT_PROFILE_ID),
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_PROFILE_VERSION,
                AnalysisStatus.COMPLETED,
                3,
                3,
                EnumSet.of(AnalysisType.DESCRIPTION, AnalysisType.OPTION, AnalysisType.NOTICE),
                List.of(defaultExtractedAttribute()),
                List.of(defaultOptionMappingSuggestion()),
                List.of(defaultNoticeSuggestion()),
                decision,
                null,
                now,
                now,
                now,
                null,
                null,
                "abc123",
                0L);
    }

    /** FAILED 상태의 ProductProfile 재구성. */
    public static ProductProfile failedProductProfile() {
        Instant now = Instant.now();
        return ProductProfile.reconstitute(
                ProductProfileId.of(DEFAULT_PROFILE_ID),
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                DEFAULT_PROFILE_VERSION,
                AnalysisStatus.FAILED,
                3,
                0,
                EnumSet.noneOf(AnalysisType.class),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                now,
                now,
                null,
                null,
                "분석 실패",
                null,
                0L);
    }

    // ===== IntelligenceOutbox Fixtures =====

    /** PENDING 상태의 신규 IntelligenceOutbox 생성. */
    public static IntelligenceOutbox newPendingOutbox() {
        Instant now = Instant.now();
        return IntelligenceOutbox.forNew(DEFAULT_PRODUCT_GROUP_ID, now);
    }

    /** 기존 PENDING 상태의 IntelligenceOutbox 재구성. */
    public static IntelligenceOutbox existingPendingOutbox() {
        Instant now = Instant.now();
        return IntelligenceOutbox.reconstitute(
                IntelligenceOutboxId.of(DEFAULT_OUTBOX_ID),
                DEFAULT_PRODUCT_GROUP_ID,
                null,
                IntelligenceOutboxStatus.PENDING,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** 특정 productGroupId의 PENDING 상태 IntelligenceOutbox 재구성. */
    public static IntelligenceOutbox existingPendingOutbox(Long outboxId, Long productGroupId) {
        Instant now = Instant.now();
        return IntelligenceOutbox.reconstitute(
                IntelligenceOutboxId.of(outboxId),
                productGroupId,
                null,
                IntelligenceOutboxStatus.PENDING,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                "PI:" + productGroupId + ":" + now.toEpochMilli());
    }

    // ===== VO Fixtures =====

    /** 기본 ExtractedAttribute 생성. */
    public static ExtractedAttribute defaultExtractedAttribute() {
        return new ExtractedAttribute(
                "소재",
                "면100%",
                ConfidenceScore.of(0.9),
                AnalysisSource.DESCRIPTION_TEXT,
                "상세설명",
                Instant.now());
    }

    /** 기본 OptionMappingSuggestion 생성. */
    public static OptionMappingSuggestion defaultOptionMappingSuggestion() {
        return new OptionMappingSuggestion(
                10L,
                100L,
                "색상",
                1L,
                10L,
                "블랙",
                ConfidenceScore.of(0.85),
                AnalysisSource.RULE_ENGINE,
                true);
    }

    /** 기본 NoticeSuggestion 생성. */
    public static NoticeSuggestion defaultNoticeSuggestion() {
        return new NoticeSuggestion(
                1L,
                "소재",
                null,
                "면100%",
                ConfidenceScore.of(0.9),
                AnalysisSource.DESCRIPTION_TEXT,
                false);
    }
}
