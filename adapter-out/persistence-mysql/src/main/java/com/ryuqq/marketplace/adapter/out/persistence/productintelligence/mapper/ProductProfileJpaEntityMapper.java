package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.id.ProductProfileId;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ConfidenceScore;
import com.ryuqq.marketplace.domain.productintelligence.vo.DecisionType;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productintelligence.vo.InspectionDecision;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductProfileJpaEntityMapper - 상품 프로파일 Entity-Domain 매퍼.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만. JSON 직렬화/역직렬화 포함.
 */
@SuppressWarnings("PMD.GodClass")
@Component
public class ProductProfileJpaEntityMapper {

    private final ObjectMapper objectMapper;

    public ProductProfileJpaEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ProductProfileJpaEntity toEntity(ProductProfile domain) {
        return ProductProfileJpaEntity.create(
                domain.idValue(),
                domain.productGroupId(),
                domain.previousProfileId(),
                domain.profileVersion(),
                toEntityStatus(domain.status()),
                domain.expectedAnalysisCount(),
                domain.completedAnalysisCount(),
                toAnalysisTypesCsv(domain.completedAnalysisTypes()),
                serializeJson(toAttributeDtos(domain.extractedAttributes())),
                serializeJson(toOptionDtos(domain.optionSuggestions())),
                serializeJson(toNoticeDtos(domain.noticeSuggestions())),
                domain.decision() != null
                        ? toEntityDecisionType(domain.decision().decisionType())
                        : null,
                domain.decision() != null ? domain.decision().overallConfidence().value() : null,
                domain.decision() != null ? serializeJson(domain.decision().reasons()) : null,
                domain.decision() != null ? domain.decision().decidedAt() : null,
                domain.rawAnalysisJson(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.analyzedAt(),
                domain.expiredAt(),
                domain.errorMessage(),
                domain.descriptionContentHash(),
                domain.version());
    }

    public ProductProfile toDomain(ProductProfileJpaEntity entity) {
        ProductProfileId id =
                entity.getId() != null
                        ? ProductProfileId.of(entity.getId())
                        : ProductProfileId.forNew();

        InspectionDecision decision = toDecision(entity);

        return ProductProfile.reconstitute(
                id,
                entity.getProductGroupId(),
                entity.getPreviousProfileId(),
                entity.getProfileVersion(),
                toDomainStatus(entity.getStatus()),
                entity.getExpectedAnalysisCount(),
                entity.getCompletedAnalysisCount(),
                parseAnalysisTypesCsv(entity.getCompletedAnalysisTypes()),
                toExtractedAttributes(entity.getExtractedAttributesJson()),
                toOptionSuggestions(entity.getOptionSuggestionsJson()),
                toNoticeSuggestions(entity.getNoticeSuggestionsJson()),
                decision,
                entity.getRawAnalysisJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getAnalyzedAt(),
                entity.getExpiredAt(),
                entity.getErrorMessage(),
                entity.getDescriptionContentHash(),
                entity.getVersion());
    }

    // ────────────────────────────────────────────────
    // Status 변환
    // ────────────────────────────────────────────────

    private ProductProfileJpaEntity.Status toEntityStatus(AnalysisStatus status) {
        return switch (status) {
            case PENDING -> ProductProfileJpaEntity.Status.PENDING;
            case ORCHESTRATING -> ProductProfileJpaEntity.Status.ORCHESTRATING;
            case ANALYZING -> ProductProfileJpaEntity.Status.ANALYZING;
            case AGGREGATING -> ProductProfileJpaEntity.Status.AGGREGATING;
            case COMPLETED -> ProductProfileJpaEntity.Status.COMPLETED;
            case FAILED -> ProductProfileJpaEntity.Status.FAILED;
        };
    }

    private AnalysisStatus toDomainStatus(ProductProfileJpaEntity.Status status) {
        return switch (status) {
            case PENDING -> AnalysisStatus.PENDING;
            case ORCHESTRATING -> AnalysisStatus.ORCHESTRATING;
            case ANALYZING -> AnalysisStatus.ANALYZING;
            case AGGREGATING -> AnalysisStatus.AGGREGATING;
            case COMPLETED -> AnalysisStatus.COMPLETED;
            case FAILED -> AnalysisStatus.FAILED;
        };
    }

    private ProductProfileJpaEntity.DecisionType toEntityDecisionType(DecisionType type) {
        return switch (type) {
            case AUTO_APPROVED -> ProductProfileJpaEntity.DecisionType.AUTO_APPROVED;
            case HUMAN_REVIEW -> ProductProfileJpaEntity.DecisionType.HUMAN_REVIEW;
            case AUTO_REJECTED -> ProductProfileJpaEntity.DecisionType.AUTO_REJECTED;
        };
    }

    private DecisionType toDomainDecisionType(ProductProfileJpaEntity.DecisionType type) {
        return switch (type) {
            case AUTO_APPROVED -> DecisionType.AUTO_APPROVED;
            case HUMAN_REVIEW -> DecisionType.HUMAN_REVIEW;
            case AUTO_REJECTED -> DecisionType.AUTO_REJECTED;
        };
    }

    // ────────────────────────────────────────────────
    // AnalysisTypes CSV 변환
    // ────────────────────────────────────────────────

    private String toAnalysisTypesCsv(Set<AnalysisType> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return types.stream().map(AnalysisType::name).sorted().collect(Collectors.joining(","));
    }

    private Set<AnalysisType> parseAnalysisTypesCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return EnumSet.noneOf(AnalysisType.class);
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(AnalysisType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(AnalysisType.class)));
    }

    // ────────────────────────────────────────────────
    // InspectionDecision 변환
    // ────────────────────────────────────────────────

    private InspectionDecision toDecision(ProductProfileJpaEntity entity) {
        if (entity.getDecisionType() == null) {
            return null;
        }
        List<String> reasons =
                deserializeJson(entity.getDecisionReasonsJson(), new TypeReference<>() {});
        return new InspectionDecision(
                toDomainDecisionType(entity.getDecisionType()),
                ConfidenceScore.of(entity.getOverallConfidence()),
                reasons != null ? reasons : List.of(),
                entity.getDecisionAt());
    }

    // ────────────────────────────────────────────────
    // ExtractedAttribute JSON 변환
    // ────────────────────────────────────────────────

    private List<ExtractedAttributeDto> toAttributeDtos(List<ExtractedAttribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return List.of();
        }
        return attributes.stream()
                .map(
                        a ->
                                new ExtractedAttributeDto(
                                        a.key(),
                                        a.value(),
                                        a.confidence().value(),
                                        a.source().name(),
                                        a.sourceDetail(),
                                        a.extractedAt()))
                .toList();
    }

    private List<ExtractedAttribute> toExtractedAttributes(String json) {
        List<ExtractedAttributeDto> dtos = deserializeJson(json, new TypeReference<>() {});
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(
                        d ->
                                new ExtractedAttribute(
                                        d.key(),
                                        d.value(),
                                        ConfidenceScore.of(d.confidence()),
                                        AnalysisSource.valueOf(d.source()),
                                        d.sourceDetail(),
                                        d.extractedAt()))
                .toList();
    }

    // ────────────────────────────────────────────────
    // OptionMappingSuggestion JSON 변환
    // ────────────────────────────────────────────────

    private List<OptionSuggestionDto> toOptionDtos(List<OptionMappingSuggestion> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }
        return suggestions.stream()
                .map(
                        s ->
                                new OptionSuggestionDto(
                                        s.sellerOptionGroupId(),
                                        s.sellerOptionValueId(),
                                        s.sellerOptionName(),
                                        s.suggestedCanonicalGroupId(),
                                        s.suggestedCanonicalValueId(),
                                        s.suggestedCanonicalValueName(),
                                        s.confidence().value(),
                                        s.source().name(),
                                        s.appliedAutomatically()))
                .toList();
    }

    private List<OptionMappingSuggestion> toOptionSuggestions(String json) {
        List<OptionSuggestionDto> dtos = deserializeJson(json, new TypeReference<>() {});
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(
                        d ->
                                new OptionMappingSuggestion(
                                        d.sellerOptionGroupId(),
                                        d.sellerOptionValueId(),
                                        d.sellerOptionName(),
                                        d.suggestedCanonicalGroupId(),
                                        d.suggestedCanonicalValueId(),
                                        d.suggestedCanonicalValueName(),
                                        ConfidenceScore.of(d.confidence()),
                                        AnalysisSource.valueOf(d.source()),
                                        d.appliedAutomatically()))
                .toList();
    }

    // ────────────────────────────────────────────────
    // NoticeSuggestion JSON 변환
    // ────────────────────────────────────────────────

    private List<NoticeSuggestionDto> toNoticeDtos(List<NoticeSuggestion> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            return List.of();
        }
        return suggestions.stream()
                .map(
                        s ->
                                new NoticeSuggestionDto(
                                        s.noticeFieldId(),
                                        s.fieldName(),
                                        s.currentValue(),
                                        s.suggestedValue(),
                                        s.confidence().value(),
                                        s.source().name(),
                                        s.appliedAutomatically()))
                .toList();
    }

    private List<NoticeSuggestion> toNoticeSuggestions(String json) {
        List<NoticeSuggestionDto> dtos = deserializeJson(json, new TypeReference<>() {});
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(
                        d ->
                                new NoticeSuggestion(
                                        d.noticeFieldId(),
                                        d.fieldName(),
                                        d.currentValue(),
                                        d.suggestedValue(),
                                        ConfidenceScore.of(d.confidence()),
                                        AnalysisSource.valueOf(d.source()),
                                        d.appliedAutomatically()))
                .toList();
    }

    // ────────────────────────────────────────────────
    // JSON 직렬화 헬퍼
    // ────────────────────────────────────────────────

    private String serializeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("ProductProfile JSON 직렬화 실패", e);
        }
    }

    private <T> T deserializeJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("ProductProfile JSON 역직렬화 실패", e);
        }
    }

    // ────────────────────────────────────────────────
    // JSON 직렬화용 내부 DTO
    // ────────────────────────────────────────────────

    record ExtractedAttributeDto(
            String key,
            String value,
            double confidence,
            String source,
            String sourceDetail,
            Instant extractedAt) {}

    record OptionSuggestionDto(
            Long sellerOptionGroupId,
            Long sellerOptionValueId,
            String sellerOptionName,
            Long suggestedCanonicalGroupId,
            Long suggestedCanonicalValueId,
            String suggestedCanonicalValueName,
            double confidence,
            String source,
            boolean appliedAutomatically) {}

    record NoticeSuggestionDto(
            Long noticeFieldId,
            String fieldName,
            String currentValue,
            String suggestedValue,
            double confidence,
            String source,
            boolean appliedAutomatically) {}
}
