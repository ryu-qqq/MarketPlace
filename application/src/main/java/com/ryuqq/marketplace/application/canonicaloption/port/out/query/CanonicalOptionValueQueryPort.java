package com.ryuqq.marketplace.application.canonicaloption.port.out.query;

import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import java.util.Map;

/** 캐노니컬 옵션 값 Query Port. */
public interface CanonicalOptionValueQueryPort {
    List<CanonicalOptionValue> findByCanonicalOptionGroupId(Long canonicalOptionGroupId);

    Map<Long, List<CanonicalOptionValue>> findGroupedByCanonicalOptionGroupIds(
            List<Long> canonicalOptionGroupIds);
}
