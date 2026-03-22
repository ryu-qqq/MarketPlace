package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.mapper.LegacyCommonCodeEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.repository.LegacyCommonCodeQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.commoncode.port.out.query.LegacyCommonCodeQueryPort;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB common_code Query Adapter.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository 사용.
 */
@Component
public class LegacyCommonCodeQueryAdapter implements LegacyCommonCodeQueryPort {

    private final LegacyCommonCodeQueryDslRepository queryDslRepository;
    private final LegacyCommonCodeEntityMapper mapper;

    public LegacyCommonCodeQueryAdapter(
            LegacyCommonCodeQueryDslRepository queryDslRepository,
            LegacyCommonCodeEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyCommonCode> findByCodeGroupId(Long codeGroupId) {
        return queryDslRepository.findByCodeGroupId(codeGroupId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
