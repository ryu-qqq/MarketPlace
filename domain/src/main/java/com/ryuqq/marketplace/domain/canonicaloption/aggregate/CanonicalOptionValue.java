package com.ryuqq.marketplace.domain.canonicaloption.aggregate;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueName;

/** 캐노니컬 옵션 값 (read-only child entity). */
public class CanonicalOptionValue {

    private final CanonicalOptionValueId id;
    private final CanonicalOptionValueCode code;
    private final CanonicalOptionValueName name;
    private final int sortOrder;

    private CanonicalOptionValue(
            CanonicalOptionValueId id,
            CanonicalOptionValueCode code,
            CanonicalOptionValueName name,
            int sortOrder) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    /** 영속성에서 복원 시 사용. */
    public static CanonicalOptionValue reconstitute(
            CanonicalOptionValueId id,
            CanonicalOptionValueCode code,
            CanonicalOptionValueName name,
            int sortOrder) {
        return new CanonicalOptionValue(id, code, name, sortOrder);
    }

    public CanonicalOptionValueId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public CanonicalOptionValueCode code() {
        return code;
    }

    public String codeValue() {
        return code.value();
    }

    public CanonicalOptionValueName name() {
        return name;
    }

    public String nameKo() {
        return name.nameKo();
    }

    public String nameEn() {
        return name.nameEn();
    }

    public int sortOrder() {
        return sortOrder;
    }
}
