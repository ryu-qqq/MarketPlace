package com.ryuqq.marketplace.domain.productintelligence.aggregate;

import com.ryuqq.marketplace.domain.productintelligence.exception.AnalysisAlreadyCompletedException;
import com.ryuqq.marketplace.domain.productintelligence.exception.AnalysisNotAllCompletedException;
import com.ryuqq.marketplace.domain.productintelligence.exception.InvalidProfileStateException;
import com.ryuqq.marketplace.domain.productintelligence.id.ProductProfileId;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * 상품 프로파일 Aggregate.
 *
 * <p>상품 검수 과정에서 AI가 분석한 결과를 축적하는 핵심 도메인 모델입니다. Description 분석, Option 매핑, Notice 보강 결과를 구조화하여 저장하고,
 * Aggregator가 최종 판정을 내리는 데 사용됩니다.
 *
 * <p><strong>파이프라인 흐름</strong>:
 *
 * <ul>
 *   <li>Orchestrator: PENDING → ORCHESTRATING → ANALYZING (3개 Analyzer 분배)
 *   <li>각 Analyzer: 결과 기록 (completedAnalysisCount++)
 *   <li>모든 분석 완료: ANALYZING → AGGREGATING
 *   <li>Aggregator: 판정 후 COMPLETED
 * </ul>
 *
 * <p><strong>동시성 제어</strong>: version 필드를 통한 낙관적 락. 여러 Analyzer가 동시에 결과를 저장할 수 있으므로 낙관적 락으로 충돌을
 * 감지합니다.
 */
public class ProductProfile {

    private static final int DEFAULT_ANALYSIS_COUNT = 3;

    private final ProductProfileId id;
    private final Long productGroupId;
    private final Long previousProfileId;
    private final int profileVersion;
    private AnalysisStatus status;
    private final int expectedAnalysisCount;
    private int completedAnalysisCount;
    private final Set<AnalysisType> completedAnalysisTypes;

    private List<ExtractedAttribute> extractedAttributes;
    private List<OptionMappingSuggestion> optionSuggestions;
    private List<NoticeSuggestion> noticeSuggestions;

    private InspectionDecision decision;
    private String rawAnalysisJson;

    private final Instant createdAt;
    private Instant updatedAt;
    private Instant analyzedAt;
    private Instant expiredAt;
    private String errorMessage;
    private String descriptionContentHash;
    private long version;

    private ProductProfile(
            ProductProfileId id,
            Long productGroupId,
            Long previousProfileId,
            int profileVersion,
            AnalysisStatus status,
            int expectedAnalysisCount,
            int completedAnalysisCount,
            Set<AnalysisType> completedAnalysisTypes,
            List<ExtractedAttribute> extractedAttributes,
            List<OptionMappingSuggestion> optionSuggestions,
            List<NoticeSuggestion> noticeSuggestions,
            InspectionDecision decision,
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
        this.extractedAttributes = extractedAttributes;
        this.optionSuggestions = optionSuggestions;
        this.noticeSuggestions = noticeSuggestions;
        this.decision = decision;
        this.rawAnalysisJson = rawAnalysisJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.analyzedAt = analyzedAt;
        this.expiredAt = expiredAt;
        this.errorMessage = errorMessage;
        this.descriptionContentHash = descriptionContentHash;
        this.version = version;
    }

    /**
     * 신규 프로파일 생성. Orchestrator에서 호출.
     *
     * @param productGroupId 상품그룹 ID
     * @param previousProfileId 이전 프로파일 ID (nullable, 최초 등록 시 null)
     * @param profileVersion 프로파일 버전 (1부터 시작)
     * @param now 생성 시각
     */
    public static ProductProfile forNew(
            Long productGroupId, Long previousProfileId, int profileVersion, Instant now) {
        return new ProductProfile(
                ProductProfileId.forNew(),
                productGroupId,
                previousProfileId,
                profileVersion,
                AnalysisStatus.PENDING,
                DEFAULT_ANALYSIS_COUNT,
                0,
                EnumSet.noneOf(AnalysisType.class),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
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

    /**
     * 분석 대기 상태의 신규 프로파일 생성.
     *
     * <p>PENDING → ORCHESTRATING → ANALYZING 전이를 한 번에 수행합니다. Outbox Relay에서 프로파일 생성 즉시 3개 Analyzer
     * 큐로 발행하므로, 중간 상태를 거칠 필요가 없습니다.
     *
     * @param productGroupId 상품그룹 ID
     * @param previousProfileId 이전 완료 프로파일 ID (nullable)
     * @param profileVersion 프로파일 버전
     * @param now 생성 시각
     */
    public static ProductProfile forNewAnalyzing(
            Long productGroupId, Long previousProfileId, int profileVersion, Instant now) {
        return new ProductProfile(
                ProductProfileId.forNew(),
                productGroupId,
                previousProfileId,
                profileVersion,
                AnalysisStatus.ANALYZING,
                DEFAULT_ANALYSIS_COUNT,
                0,
                EnumSet.noneOf(AnalysisType.class),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
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

    /** DB에서 재구성. */
    public static ProductProfile reconstitute(
            ProductProfileId id,
            Long productGroupId,
            Long previousProfileId,
            int profileVersion,
            AnalysisStatus status,
            int expectedAnalysisCount,
            int completedAnalysisCount,
            Set<AnalysisType> completedAnalysisTypes,
            List<ExtractedAttribute> extractedAttributes,
            List<OptionMappingSuggestion> optionSuggestions,
            List<NoticeSuggestion> noticeSuggestions,
            InspectionDecision decision,
            String rawAnalysisJson,
            Instant createdAt,
            Instant updatedAt,
            Instant analyzedAt,
            Instant expiredAt,
            String errorMessage,
            String descriptionContentHash,
            long version) {
        return new ProductProfile(
                id,
                productGroupId,
                previousProfileId,
                profileVersion,
                status,
                expectedAnalysisCount,
                completedAnalysisCount,
                completedAnalysisTypes != null
                        ? EnumSet.copyOf(completedAnalysisTypes)
                        : EnumSet.noneOf(AnalysisType.class),
                extractedAttributes != null
                        ? new ArrayList<>(extractedAttributes)
                        : new ArrayList<>(),
                optionSuggestions != null ? new ArrayList<>(optionSuggestions) : new ArrayList<>(),
                noticeSuggestions != null ? new ArrayList<>(noticeSuggestions) : new ArrayList<>(),
                decision,
                rawAnalysisJson,
                createdAt,
                updatedAt,
                analyzedAt,
                expiredAt,
                errorMessage,
                descriptionContentHash,
                version);
    }

    // ────────────────────────────────────────────────
    // 상태 전이 메서드
    // ────────────────────────────────────────────────

    /** 오케스트레이션 시작 (PENDING → ORCHESTRATING). */
    public void startOrchestrating(Instant now) {
        assertStatus(AnalysisStatus.PENDING, "ORCHESTRATING으로 전환");
        this.status = AnalysisStatus.ORCHESTRATING;
        this.updatedAt = now;
    }

    /** 분석 시작 (ORCHESTRATING → ANALYZING). Analyzer 큐 분배 완료 후 호출. */
    public void startAnalyzing(Instant now) {
        assertStatus(AnalysisStatus.ORCHESTRATING, "ANALYZING으로 전환");
        this.status = AnalysisStatus.ANALYZING;
        this.updatedAt = now;
    }

    /**
     * Description 분석 결과 기록.
     *
     * @param attributes 추출된 속성 목록
     * @param now 현재 시각
     * @return 모든 분석이 완료되면 true (Aggregation 큐 발행 트리거)
     */
    public boolean recordDescriptionAnalysis(List<ExtractedAttribute> attributes, Instant now) {
        assertAnalyzing("Description 분석 결과 기록");
        assertNotAlreadyCompleted(AnalysisType.DESCRIPTION);
        this.extractedAttributes = new ArrayList<>(attributes);
        return completeAnalysis(AnalysisType.DESCRIPTION, now);
    }

    /**
     * Option 분석 결과 기록.
     *
     * @param suggestions 옵션 매핑 제안 목록
     * @param now 현재 시각
     * @return 모든 분석이 완료되면 true
     */
    public boolean recordOptionAnalysis(List<OptionMappingSuggestion> suggestions, Instant now) {
        assertAnalyzing("Option 분석 결과 기록");
        assertNotAlreadyCompleted(AnalysisType.OPTION);
        this.optionSuggestions = new ArrayList<>(suggestions);
        return completeAnalysis(AnalysisType.OPTION, now);
    }

    /**
     * Notice 분석 결과 기록.
     *
     * @param suggestions 고시정보 보강 제안 목록
     * @param now 현재 시각
     * @return 모든 분석이 완료되면 true
     */
    public boolean recordNoticeAnalysis(List<NoticeSuggestion> suggestions, Instant now) {
        assertAnalyzing("Notice 분석 결과 기록");
        assertNotAlreadyCompleted(AnalysisType.NOTICE);
        this.noticeSuggestions = new ArrayList<>(suggestions);
        return completeAnalysis(AnalysisType.NOTICE, now);
    }

    /** 집계 시작 (ANALYZING → AGGREGATING). 모든 분석 완료 후 Aggregator에서 호출. */
    public void startAggregating(Instant now) {
        if (status != AnalysisStatus.ANALYZING) {
            throw new InvalidProfileStateException(status, "AGGREGATING 전환");
        }
        if (!isAllAnalysisCompleted()) {
            throw new AnalysisNotAllCompletedException(
                    completedAnalysisCount, expectedAnalysisCount);
        }
        this.status = AnalysisStatus.AGGREGATING;
        this.updatedAt = now;
    }

    /**
     * 최종 판정 완료 (AGGREGATING → COMPLETED).
     *
     * @param decision 판정 결과
     * @param rawJson LLM 원본 응답 JSON (디버깅/학습용)
     * @param now 현재 시각
     */
    public void complete(InspectionDecision decision, String rawJson, Instant now) {
        assertStatus(AnalysisStatus.AGGREGATING, "COMPLETED로 전환");
        this.status = AnalysisStatus.COMPLETED;
        this.decision = decision;
        this.rawAnalysisJson = rawJson;
        this.analyzedAt = now;
        this.updatedAt = now;
        this.errorMessage = null;
    }

    /** 실패 처리. 어떤 상태에서든 실패로 전환 가능. */
    public void fail(String errorMessage, Instant now) {
        this.status = AnalysisStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = now;
    }

    /** 프로파일 만료 처리. 상품 수정 시 이전 프로파일을 만료시킵니다. */
    public void expire(Instant now) {
        this.expiredAt = now;
        this.updatedAt = now;
    }

    /**
     * 옵션 매핑 제안의 자동 적용 결과를 반영합니다.
     *
     * @param appliedSuggestions 자동 적용된 제안 목록 (appliedAutomatically=true)
     */
    public void updateOptionSuggestions(List<OptionMappingSuggestion> appliedSuggestions) {
        this.optionSuggestions = new ArrayList<>(appliedSuggestions);
    }

    /**
     * 고시정보 보강 제안의 자동 적용 결과를 반영합니다.
     *
     * @param appliedSuggestions 자동 적용된 제안 목록 (appliedAutomatically=true)
     */
    public void updateNoticeSuggestions(List<NoticeSuggestion> appliedSuggestions) {
        this.noticeSuggestions = new ArrayList<>(appliedSuggestions);
    }

    // ────────────────────────────────────────────────
    // 내부 헬퍼
    // ────────────────────────────────────────────────

    private boolean completeAnalysis(AnalysisType type, Instant now) {
        this.completedAnalysisTypes.add(type);
        this.completedAnalysisCount = this.completedAnalysisTypes.size();
        this.updatedAt = now;
        return isAllAnalysisCompleted();
    }

    private void assertAnalyzing(String action) {
        if (status != AnalysisStatus.ANALYZING) {
            throw new InvalidProfileStateException(status, action);
        }
    }

    private void assertNotAlreadyCompleted(AnalysisType type) {
        if (completedAnalysisTypes.contains(type)) {
            throw new AnalysisAlreadyCompletedException(type);
        }
    }

    private void assertStatus(AnalysisStatus expected, String action) {
        if (this.status != expected) {
            throw new InvalidProfileStateException(status, action);
        }
    }

    // ────────────────────────────────────────────────
    // 조회 메서드
    // ────────────────────────────────────────────────

    public boolean isNew() {
        return id.isNew();
    }

    public boolean isAllAnalysisCompleted() {
        return completedAnalysisCount >= expectedAnalysisCount;
    }

    public boolean isExpired() {
        return expiredAt != null;
    }

    /**
     * 현재 상태가 기대 상태인지 확인합니다. CAS(Compare-And-Swap) 기반 멱등성 보장에 사용.
     *
     * @param expected 기대 상태
     * @return 현재 상태가 기대 상태와 일치하면 true
     */
    public boolean hasExpectedStatus(AnalysisStatus expected) {
        return this.status == expected;
    }

    /**
     * 해당 분석 타입의 실행이 가능한지 확인합니다.
     *
     * <p>ANALYZING 상태이면서 아직 해당 타입이 완료되지 않은 경우에만 true. Service에서 멱등성 검증을 한 곳에서 처리하기 위한 편의 메서드입니다.
     *
     * @param type 실행하려는 분석 타입
     * @return 실행 가능하면 true
     */
    public boolean canExecuteAnalysis(AnalysisType type) {
        return status == AnalysisStatus.ANALYZING && !completedAnalysisTypes.contains(type);
    }

    /**
     * 이전 분석 결과를 이월해야 하는지 판단합니다.
     *
     * <p>이전 결과가 존재하고 입력 데이터에 변경이 없으면 이월 대상입니다. ChangeDetector 호출 결과(boolean)를 받아 순수 비즈니스 판단만 수행합니다.
     *
     * @param previousResults 이전 프로파일의 분석 결과 (빈 리스트 = 최초 분석)
     * @param dataHasChanged ChangeDetector가 감지한 변경 여부
     * @return 이월 가능하면 true (AI 호출 스킵)
     */
    public boolean shouldCarryForward(List<?> previousResults, boolean dataHasChanged) {
        return !previousResults.isEmpty() && !dataHasChanged;
    }

    // ────────────────────────────────────────────────
    // Getters
    // ────────────────────────────────────────────────

    public ProductProfileId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long productGroupId() {
        return productGroupId;
    }

    public Long previousProfileId() {
        return previousProfileId;
    }

    public int profileVersion() {
        return profileVersion;
    }

    public AnalysisStatus status() {
        return status;
    }

    public int expectedAnalysisCount() {
        return expectedAnalysisCount;
    }

    public int completedAnalysisCount() {
        return completedAnalysisCount;
    }

    public Set<AnalysisType> completedAnalysisTypes() {
        return Collections.unmodifiableSet(completedAnalysisTypes);
    }

    public List<ExtractedAttribute> extractedAttributes() {
        return Collections.unmodifiableList(extractedAttributes);
    }

    public List<OptionMappingSuggestion> optionSuggestions() {
        return Collections.unmodifiableList(optionSuggestions);
    }

    public List<NoticeSuggestion> noticeSuggestions() {
        return Collections.unmodifiableList(noticeSuggestions);
    }

    public InspectionDecision decision() {
        return decision;
    }

    public String rawAnalysisJson() {
        return rawAnalysisJson;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant analyzedAt() {
        return analyzedAt;
    }

    public Instant expiredAt() {
        return expiredAt;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public String descriptionContentHash() {
        return descriptionContentHash;
    }

    public void updateDescriptionContentHash(String hash) {
        this.descriptionContentHash = hash;
    }

    public long version() {
        return version;
    }
}
