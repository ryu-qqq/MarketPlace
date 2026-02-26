package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyProductIdMappingId;
import java.time.Instant;

/**
 * 레거시 상품(SKU) ID 매핑 Aggregate.
 *
 * <p>레거시 Product(SKU) ID와 내부 Product ID 간의 매핑을 저장합니다. 변환 완료 시 SKU별로 생성되며, 이후 수정/조회 시 내부 Product를
 * 추적하는 데 사용됩니다.
 *
 * <p>그룹 레벨 매핑은 {@code legacyProductGroupId}로 함께 저장하여 그룹별 SKU 일괄 조회를 지원합니다.
 */
public class LegacyProductIdMapping {

    private final LegacyProductIdMappingId id;
    private final long legacyProductId;
    private final long internalProductId;
    private final long legacyProductGroupId;
    private final long internalProductGroupId;
    private final Instant createdAt;

    private LegacyProductIdMapping(
            LegacyProductIdMappingId id,
            long legacyProductId,
            long internalProductId,
            long legacyProductGroupId,
            long internalProductGroupId,
            Instant createdAt) {
        this.id = id;
        this.legacyProductId = legacyProductId;
        this.internalProductId = internalProductId;
        this.legacyProductGroupId = legacyProductGroupId;
        this.internalProductGroupId = internalProductGroupId;
        this.createdAt = createdAt;
    }

    /**
     * 새 매핑 생성.
     *
     * @param legacyProductId 레거시 Product(SKU) ID
     * @param internalProductId 내부 Product ID
     * @param legacyProductGroupId 레거시 상품그룹 ID (그룹 참조)
     * @param internalProductGroupId 내부 상품그룹 ID
     * @param now 현재 시각
     * @return 새 LegacyProductIdMapping 인스턴스
     */
    public static LegacyProductIdMapping forNew(
            long legacyProductId,
            long internalProductId,
            long legacyProductGroupId,
            long internalProductGroupId,
            Instant now) {
        return new LegacyProductIdMapping(
                LegacyProductIdMappingId.forNew(),
                legacyProductId,
                internalProductId,
                legacyProductGroupId,
                internalProductGroupId,
                now);
    }

    /**
     * DB에서 재구성.
     *
     * @param id 매핑 ID
     * @param legacyProductId 레거시 Product(SKU) ID
     * @param internalProductId 내부 Product ID
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @param internalProductGroupId 내부 상품그룹 ID
     * @param createdAt 생성일시
     * @return 재구성된 LegacyProductIdMapping 인스턴스
     */
    public static LegacyProductIdMapping reconstitute(
            LegacyProductIdMappingId id,
            long legacyProductId,
            long internalProductId,
            long legacyProductGroupId,
            long internalProductGroupId,
            Instant createdAt) {
        return new LegacyProductIdMapping(
                id,
                legacyProductId,
                internalProductId,
                legacyProductGroupId,
                internalProductGroupId,
                createdAt);
    }

    public boolean isNew() {
        return id.isNew();
    }

    // Getters
    public LegacyProductIdMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public long legacyProductId() {
        return legacyProductId;
    }

    public long internalProductId() {
        return internalProductId;
    }

    public long legacyProductGroupId() {
        return legacyProductGroupId;
    }

    public long internalProductGroupId() {
        return internalProductGroupId;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
