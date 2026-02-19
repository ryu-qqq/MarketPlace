package com.ryuqq.marketplace.application.canonicaloption.port.out.query;

import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import java.util.Optional;

/** 캐노니컬 옵션 그룹 Query Port. */
public interface CanonicalOptionGroupQueryPort {
    Optional<CanonicalOptionGroup> findById(CanonicalOptionGroupId id);

    List<CanonicalOptionGroup> findByIds(List<CanonicalOptionGroupId> ids);

    List<CanonicalOptionGroup> findByCriteria(CanonicalOptionGroupSearchCriteria criteria);

    long countByCriteria(CanonicalOptionGroupSearchCriteria criteria);
}
