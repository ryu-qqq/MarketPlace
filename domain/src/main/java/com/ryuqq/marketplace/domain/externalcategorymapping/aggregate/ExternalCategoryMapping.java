package com.ryuqq.marketplace.domain.externalcategorymapping.aggregate;

import com.ryuqq.marketplace.domain.externalcategorymapping.id.ExternalCategoryMappingId;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import java.time.Instant;

/** ExternalCategoryMapping Aggregate Root. */
public class ExternalCategoryMapping {

    private final ExternalCategoryMappingId id;
    private final Long externalSourceId;
    private final String externalCategoryCode;
    private String externalCategoryName;
    private Long internalCategoryId;
    private ExternalCategoryMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private ExternalCategoryMapping(
            ExternalCategoryMappingId id,
            Long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            ExternalCategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.externalSourceId = externalSourceId;
        this.externalCategoryCode = externalCategoryCode;
        this.externalCategoryName = externalCategoryName;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 ExternalCategoryMapping 생성 팩토리. */
    public static ExternalCategoryMapping forNew(
            Long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            Instant now) {
        return new ExternalCategoryMapping(
                ExternalCategoryMappingId.forNew(),
                externalSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                ExternalCategoryMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ExternalCategoryMapping reconstitute(
            ExternalCategoryMappingId id,
            Long externalSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            ExternalCategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new ExternalCategoryMapping(
                id,
                externalSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                status,
                createdAt,
                updatedAt);
    }

    /** 매핑 정보 수정. */
    public void update(String externalCategoryName, Long internalCategoryId, Instant now) {
        this.externalCategoryName = externalCategoryName;
        this.internalCategoryId = internalCategoryId;
        this.updatedAt = now;
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = ExternalCategoryMappingStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = ExternalCategoryMappingStatus.INACTIVE;
        this.updatedAt = now;
    }

    public ExternalCategoryMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long externalSourceId() {
        return externalSourceId;
    }

    public String externalCategoryCode() {
        return externalCategoryCode;
    }

    public String externalCategoryName() {
        return externalCategoryName;
    }

    public Long internalCategoryId() {
        return internalCategoryId;
    }

    public ExternalCategoryMappingStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
