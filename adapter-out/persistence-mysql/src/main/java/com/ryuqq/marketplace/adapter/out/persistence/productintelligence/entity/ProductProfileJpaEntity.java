package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

/**
 * ProductProfileJpaEntity - 상품 프로파일 JPA 엔티티.
 *
 * <p>AI 분석 파이프라인 결과를 저장하는 엔티티입니다.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 */
@Entity
@Table(name = "product_profiles")
public class ProductProfileJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "previous_profile_id")
    private Long previousProfileId;

    @Column(name = "profile_version", nullable = false)
    private int profileVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "expected_analysis_count", nullable = false)
    private int expectedAnalysisCount;

    @Column(name = "completed_analysis_count", nullable = false)
    private int completedAnalysisCount;

    @Column(name = "completed_analysis_types", length = 100)
    private String completedAnalysisTypes;

    @Column(name = "extracted_attributes_json", columnDefinition = "TEXT")
    private String extractedAttributesJson;

    @Column(name = "option_suggestions_json", columnDefinition = "TEXT")
    private String optionSuggestionsJson;

    @Column(name = "notice_suggestions_json", columnDefinition = "TEXT")
    private String noticeSuggestionsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_type", length = 20)
    private DecisionType decisionType;

    @Column(name = "overall_confidence")
    private Double overallConfidence;

    @Column(name = "decision_reasons_json", columnDefinition = "TEXT")
    private String decisionReasonsJson;

    @Column(name = "decision_at")
    private Instant decisionAt;

    @Column(name = "raw_analysis_json", columnDefinition = "TEXT")
    private String rawAnalysisJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "analyzed_at")
    private Instant analyzedAt;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "description_content_hash", length = 64)
    private String descriptionContentHash;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected ProductProfileJpaEntity() {}

    private ProductProfileJpaEntity(
            Long id,
            Long productGroupId,
            Long previousProfileId,
            int profileVersion,
            Status status,
            int expectedAnalysisCount,
            int completedAnalysisCount,
            String completedAnalysisTypes,
            String extractedAttributesJson,
            String optionSuggestionsJson,
            String noticeSuggestionsJson,
            DecisionType decisionType,
            Double overallConfidence,
            String decisionReasonsJson,
            Instant decisionAt,
            String rawAnalysisJson,
            Instant createdAt,
            Instant updatedAt,
            Instant analyzedAt,
            Instant expiredAt,
            String errorMessage,
            String descriptionContentHash,
            long version) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.previousProfileId = previousProfileId;
        this.profileVersion = profileVersion;
        this.status = status;
        this.expectedAnalysisCount = expectedAnalysisCount;
        this.completedAnalysisCount = completedAnalysisCount;
        this.completedAnalysisTypes = completedAnalysisTypes;
        this.extractedAttributesJson = extractedAttributesJson;
        this.optionSuggestionsJson = optionSuggestionsJson;
        this.noticeSuggestionsJson = noticeSuggestionsJson;
        this.decisionType = decisionType;
        this.overallConfidence = overallConfidence;
        this.decisionReasonsJson = decisionReasonsJson;
        this.decisionAt = decisionAt;
        this.rawAnalysisJson = rawAnalysisJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.analyzedAt = analyzedAt;
        this.expiredAt = expiredAt;
        this.errorMessage = errorMessage;
        this.descriptionContentHash = descriptionContentHash;
        this.version = version;
    }

    public static ProductProfileJpaEntity create(
            Long id,
            Long productGroupId,
            Long previousProfileId,
            int profileVersion,
            Status status,
            int expectedAnalysisCount,
            int completedAnalysisCount,
            String completedAnalysisTypes,
            String extractedAttributesJson,
            String optionSuggestionsJson,
            String noticeSuggestionsJson,
            DecisionType decisionType,
            Double overallConfidence,
            String decisionReasonsJson,
            Instant decisionAt,
            String rawAnalysisJson,
            Instant createdAt,
            Instant updatedAt,
            Instant analyzedAt,
            Instant expiredAt,
            String errorMessage,
            String descriptionContentHash,
            long version) {
        return new ProductProfileJpaEntity(
                id,
                productGroupId,
                previousProfileId,
                profileVersion,
                status,
                expectedAnalysisCount,
                completedAnalysisCount,
                completedAnalysisTypes,
                extractedAttributesJson,
                optionSuggestionsJson,
                noticeSuggestionsJson,
                decisionType,
                overallConfidence,
                decisionReasonsJson,
                decisionAt,
                rawAnalysisJson,
                createdAt,
                updatedAt,
                analyzedAt,
                expiredAt,
                errorMessage,
                descriptionContentHash,
                version);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public Long getPreviousProfileId() {
        return previousProfileId;
    }

    public int getProfileVersion() {
        return profileVersion;
    }

    public Status getStatus() {
        return status;
    }

    public int getExpectedAnalysisCount() {
        return expectedAnalysisCount;
    }

    public int getCompletedAnalysisCount() {
        return completedAnalysisCount;
    }

    public String getCompletedAnalysisTypes() {
        return completedAnalysisTypes;
    }

    public String getExtractedAttributesJson() {
        return extractedAttributesJson;
    }

    public String getOptionSuggestionsJson() {
        return optionSuggestionsJson;
    }

    public String getNoticeSuggestionsJson() {
        return noticeSuggestionsJson;
    }

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public Double getOverallConfidence() {
        return overallConfidence;
    }

    public String getDecisionReasonsJson() {
        return decisionReasonsJson;
    }

    public Instant getDecisionAt() {
        return decisionAt;
    }

    public String getRawAnalysisJson() {
        return rawAnalysisJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDescriptionContentHash() {
        return descriptionContentHash;
    }

    public long getVersion() {
        return version;
    }

    /** 분석 상태. */
    public enum Status {
        PENDING,
        ORCHESTRATING,
        ANALYZING,
        AGGREGATING,
        COMPLETED,
        FAILED
    }

    /** 판정 유형. */
    public enum DecisionType {
        AUTO_APPROVED,
        HUMAN_REVIEW,
        AUTO_REJECTED
    }
}
