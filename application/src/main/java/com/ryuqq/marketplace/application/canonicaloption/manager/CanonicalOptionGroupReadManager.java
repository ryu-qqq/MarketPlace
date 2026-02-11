package com.ryuqq.marketplace.application.canonicaloption.manager;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.exception.CanonicalOptionGroupNotFoundException;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 캐노니컬 옵션 그룹 Read Manager. */
@Component
public class CanonicalOptionGroupReadManager {

    private final CanonicalOptionGroupQueryPort queryPort;

    public CanonicalOptionGroupReadManager(CanonicalOptionGroupQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public CanonicalOptionGroup getById(CanonicalOptionGroupId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CanonicalOptionGroupNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<CanonicalOptionGroup> findByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
