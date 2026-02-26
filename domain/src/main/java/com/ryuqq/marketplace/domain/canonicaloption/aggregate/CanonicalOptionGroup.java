package com.ryuqq.marketplace.domain.canonicaloption.aggregate;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/** 캐노니컬 옵션 그룹 Aggregate Root (read-only). 시스템이 관리하는 정규화된 옵션 표준. DB migration으로만 데이터 관리. */
public class CanonicalOptionGroup {

    private final CanonicalOptionGroupId id;
    private final CanonicalOptionGroupCode code;
    private final CanonicalOptionGroupName name;
    private final boolean active;
    private final List<CanonicalOptionValue> values;
    private final Instant createdAt;
    private final Instant updatedAt;

    private CanonicalOptionGroup(
            CanonicalOptionGroupId id,
            CanonicalOptionGroupCode code,
            CanonicalOptionGroupName name,
            boolean active,
            List<CanonicalOptionValue> values,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.active = active;
        this.values = List.copyOf(values);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** 영속성에서 복원 시 사용. */
    public static CanonicalOptionGroup reconstitute(
            CanonicalOptionGroupId id,
            CanonicalOptionGroupCode code,
            CanonicalOptionGroupName name,
            boolean active,
            List<CanonicalOptionValue> values,
            Instant createdAt,
            Instant updatedAt) {
        return new CanonicalOptionGroup(id, code, name, active, values, createdAt, updatedAt);
    }

    public CanonicalOptionGroupId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public CanonicalOptionGroupCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public CanonicalOptionGroupName name() {
        return name;
    }

    public String nameKo() {
        return name.nameKo();
    }

    public String nameEn() {
        return name.nameEn();
    }

    public boolean isActive() {
        return active;
    }

    public List<CanonicalOptionValue> values() {
        return Collections.unmodifiableList(values);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
