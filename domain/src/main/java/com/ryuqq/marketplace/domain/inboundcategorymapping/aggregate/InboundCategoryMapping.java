package com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate;

import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
import java.time.Instant;

/** InboundCategoryMapping Aggregate Root. */
public class InboundCategoryMapping {

    private final InboundCategoryMappingId id;
    private final Long inboundSourceId;
    private final String externalCategoryCode;
    private String externalCategoryName;
    private Long internalCategoryId;
    private InboundCategoryMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private InboundCategoryMapping(
            InboundCategoryMappingId id,
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            InboundCategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalCategoryCode = externalCategoryCode;
        this.externalCategoryName = externalCategoryName;
        this.internalCategoryId = internalCategoryId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 InboundCategoryMapping 생성 팩토리. */
    public static InboundCategoryMapping forNew(
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            Instant now) {
        return new InboundCategoryMapping(
                InboundCategoryMappingId.forNew(),
                inboundSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                InboundCategoryMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static InboundCategoryMapping reconstitute(
            InboundCategoryMappingId id,
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            InboundCategoryMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundCategoryMapping(
                id,
                inboundSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                status,
                createdAt,
                updatedAt);
    }

    /** 매핑 정보 수정. */
    public void update(InboundCategoryMappingUpdateData updateData, Instant now) {
        this.externalCategoryName = updateData.externalCategoryName();
        this.internalCategoryId = updateData.internalCategoryId();
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public InboundCategoryMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long inboundSourceId() {
        return inboundSourceId;
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

    public InboundCategoryMappingStatus status() {
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
