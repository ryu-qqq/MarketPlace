package com.ryuqq.marketplace.domain.externalsource.aggregate;

import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceCode;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import java.time.Instant;

/** ExternalSource Aggregate Root. */
public class ExternalSource {

    private final ExternalSourceId id;
    private final ExternalSourceCode code;
    private String name;
    private final ExternalSourceType type;
    private ExternalSourceStatus status;
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;

    private ExternalSource(
            ExternalSourceId id,
            ExternalSourceCode code,
            String name,
            ExternalSourceType type,
            ExternalSourceStatus status,
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

    /** 신규 ExternalSource 생성 팩토리. */
    public static ExternalSource forNew(
            ExternalSourceCode code,
            String name,
            ExternalSourceType type,
            String description,
            Instant now) {
        return new ExternalSource(
                ExternalSourceId.forNew(),
                code,
                name,
                type,
                ExternalSourceStatus.ACTIVE,
                description,
                now,
                now);
    }

    /** 영속성에서 복원 시 사용. */
    public static ExternalSource reconstitute(
            ExternalSourceId id,
            ExternalSourceCode code,
            String name,
            ExternalSourceType type,
            ExternalSourceStatus status,
            String description,
            Instant createdAt,
            Instant updatedAt) {
        return new ExternalSource(id, code, name, type, status, description, createdAt, updatedAt);
    }

    /** 기본 정보 수정. */
    public void update(String name, String description, Instant now) {
        this.name = name;
        this.description = description;
        this.updatedAt = now;
    }

    /** 활성화. */
    public void activate(Instant now) {
        this.status = ExternalSourceStatus.ACTIVE;
        this.updatedAt = now;
    }

    /** 비활성화. */
    public void deactivate(Instant now) {
        this.status = ExternalSourceStatus.INACTIVE;
        this.updatedAt = now;
    }

    public ExternalSourceId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public ExternalSourceCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public String name() {
        return name;
    }

    public ExternalSourceType type() {
        return type;
    }

    public ExternalSourceStatus status() {
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
