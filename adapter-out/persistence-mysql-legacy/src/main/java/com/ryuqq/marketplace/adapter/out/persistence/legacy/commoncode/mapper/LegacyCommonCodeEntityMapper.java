package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import org.springframework.stereotype.Component;

/** 세토프 공통 코드 Entity ↔ Domain 변환 Mapper. */
@Component
public class LegacyCommonCodeEntityMapper {

    public LegacyCommonCode toDomain(LegacyCommonCodeEntity entity) {
        return LegacyCommonCode.reconstitute(
                entity.getId(),
                entity.getCodeGroupId(),
                entity.getCodeDetail(),
                entity.getCodeDetailDisplayName(),
                entity.getDisplayOrder());
    }
}
