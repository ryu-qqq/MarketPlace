package com.ryuqq.marketplace.application.externalsource.port.out.query;

import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import java.util.List;
import java.util.Optional;

/** ExternalSource 조회 포트. */
public interface ExternalSourceQueryPort {

    Optional<ExternalSource> findById(ExternalSourceId id);

    Optional<ExternalSource> findByCode(String code);

    List<ExternalSource> findByCriteria(ExternalSourceSearchParams params);

    long countByCriteria(ExternalSourceSearchParams params);
}
