package com.ryuqq.marketplace.application.externalsource.port.out.query;

import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
import java.util.List;
import java.util.Optional;

/** ExternalSource 조회 포트. */
public interface ExternalSourceQueryPort {

    Optional<ExternalSource> findById(ExternalSourceId id);

    Optional<ExternalSource> findByCode(String code);

    List<ExternalSource> findByCriteria(ExternalSourceSearchCriteria criteria);

    long countByCriteria(ExternalSourceSearchCriteria criteria);
}
