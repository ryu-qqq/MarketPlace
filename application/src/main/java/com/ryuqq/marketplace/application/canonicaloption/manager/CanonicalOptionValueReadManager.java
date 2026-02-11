package com.ryuqq.marketplace.application.canonicaloption.manager;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionValueQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 캐노니컬 옵션 값 Read Manager. */
@Component
public class CanonicalOptionValueReadManager {

    private final CanonicalOptionValueQueryPort queryPort;

    public CanonicalOptionValueReadManager(CanonicalOptionValueQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<CanonicalOptionValue> getByCanonicalOptionGroupId(Long canonicalOptionGroupId) {
        return queryPort.findByCanonicalOptionGroupId(canonicalOptionGroupId);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<CanonicalOptionValue>> getGroupedByCanonicalOptionGroupIds(
            List<Long> canonicalOptionGroupIds) {
        if (canonicalOptionGroupIds.isEmpty()) {
            return Map.of();
        }
        return queryPort.findGroupedByCanonicalOptionGroupIds(canonicalOptionGroupIds);
    }
}
