package com.ryuqq.marketplace.domain.externalbrandmapping.aggregate;

import com.ryuqq.marketplace.domain.externalbrandmapping.id.ExternalBrandMappingId;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingUpdateData;
import java.time.Instant;

/** ExternalBrandMapping Aggregate Root. */
public class ExternalBrandMapping {

    private final ExternalBrandMappingId id;
    private final Long externalSourceId;
    private final String externalBrandCode;
    private String externalBrandName;
    private Long internalBrandId;
    private ExternalBrandMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private ExternalBrandMapping(
            ExternalBrandMappingId id,
            Long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            ExternalBrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.externalSourceId = externalSourceId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.internalBrandId = internalBrandId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 ExternalBrandMapping 생성 팩토리. */
    public static ExternalBrandMapping forNew(
            Long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            Instant now) {
        return new ExternalBrandMapping(
                ExternalBrandMappingId.forNew(),
                externalSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
                ExternalBrandMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ExternalBrandMapping reconstitute(
            ExternalBrandMappingId id,
            Long externalSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            ExternalBrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new ExternalBrandMapping(
                id,
                externalSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
                status,
                createdAt,
                updatedAt);
    }

    /** 매핑 정보 수정. */
    public void update(ExternalBrandMappingUpdateData updateData, Instant now) {
        this.externalBrandName = updateData.externalBrandName();
        this.internalBrandId = updateData.internalBrandId();
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public ExternalBrandMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long externalSourceId() {
        return externalSourceId;
    }

    public String externalBrandCode() {
        return externalBrandCode;
    }

    public String externalBrandName() {
        return externalBrandName;
    }

    public Long internalBrandId() {
        return internalBrandId;
    }

    public ExternalBrandMappingStatus status() {
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
