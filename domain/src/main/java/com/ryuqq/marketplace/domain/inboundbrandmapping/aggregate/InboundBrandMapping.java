package com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate;

import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
import java.time.Instant;

/** InboundBrandMapping Aggregate Root. */
public class InboundBrandMapping {

    private final InboundBrandMappingId id;
    private final Long inboundSourceId;
    private final String externalBrandCode;
    private String externalBrandName;
    private Long internalBrandId;
    private InboundBrandMappingStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private InboundBrandMapping(
            InboundBrandMappingId id,
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            InboundBrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalBrandCode = externalBrandCode;
        this.externalBrandName = externalBrandName;
        this.internalBrandId = internalBrandId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 InboundBrandMapping 생성 팩토리. */
    public static InboundBrandMapping forNew(
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            Instant now) {
        return new InboundBrandMapping(
                InboundBrandMappingId.forNew(),
                inboundSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
                InboundBrandMappingStatus.ACTIVE,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static InboundBrandMapping reconstitute(
            InboundBrandMappingId id,
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            InboundBrandMappingStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundBrandMapping(
                id,
                inboundSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
                status,
                createdAt,
                updatedAt);
    }

    /** 매핑 정보 수정. */
    public void update(InboundBrandMappingUpdateData updateData, Instant now) {
        this.externalBrandName = updateData.externalBrandName();
        this.internalBrandId = updateData.internalBrandId();
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public InboundBrandMappingId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public Long inboundSourceId() {
        return inboundSourceId;
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

    public InboundBrandMappingStatus status() {
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
