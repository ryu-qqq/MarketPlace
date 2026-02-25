package com.ryuqq.marketplace.domain.inboundsource.aggregate;

import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceCode;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceUpdateData;
import java.time.Instant;

/** InboundSource Aggregate Root. */
public class InboundSource {

    private final InboundSourceId id;
    private final InboundSourceCode code;
    private String name;
    private final InboundSourceType type;
    private InboundSourceStatus status;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;

    private InboundSource(
            InboundSourceId id,
            InboundSourceCode code,
            String name,
            InboundSourceType type,
            InboundSourceStatus status,
            String description,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 신규 InboundSource 생성 팩토리. */
    public static InboundSource forNew(
            InboundSourceCode code,
            String name,
            InboundSourceType type,
            String description,
            Instant now) {
        return new InboundSource(
                InboundSourceId.forNew(),
                code,
                name,
                type,
                InboundSourceStatus.ACTIVE,
                description,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static InboundSource reconstitute(
            InboundSourceId id,
            InboundSourceCode code,
            String name,
            InboundSourceType type,
            InboundSourceStatus status,
            String description,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundSource(id, code, name, type, status, description, createdAt, updatedAt);
    }

    /** 기본 정보 수정. */
    public void update(InboundSourceUpdateData updateData, Instant now) {
        this.name = updateData.name();
        this.description = updateData.description();
        this.status = updateData.status();
        this.updatedAt = now;
    }

    public InboundSourceId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public InboundSourceCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public String name() {
        return name;
    }

    public InboundSourceType type() {
        return type;
    }

    public InboundSourceStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public String description() {
        return description;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
