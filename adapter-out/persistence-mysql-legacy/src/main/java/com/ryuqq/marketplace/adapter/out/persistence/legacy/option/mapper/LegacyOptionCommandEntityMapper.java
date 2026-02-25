package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import org.springframework.stereotype.Component;

/** 세토프 옵션 도메인 객체 → JPA Entity 변환 커맨드 Mapper. */
@Component
public class LegacyOptionCommandEntityMapper {

    public LegacyOptionGroupEntity toEntity(LegacyOptionGroup data) {
        return LegacyOptionGroupEntity.create(data.optionName().name());
    }

    public LegacyOptionDetailEntity toEntity(LegacyOptionDetail data) {
        return LegacyOptionDetailEntity.create(data.optionGroupIdValue(), data.optionValue());
    }
}
