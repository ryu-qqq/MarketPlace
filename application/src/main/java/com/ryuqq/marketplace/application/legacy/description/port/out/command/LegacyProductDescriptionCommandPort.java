package com.ryuqq.marketplace.application.legacy.description.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;

/** 세토프 DB product_group_detail_description 테이블 커맨드 Port. */
public interface LegacyProductDescriptionCommandPort {

    void persist(LegacyProductGroupId productGroupId, LegacyProductDescription description);

    /** content/cdnPath/publishStatus를 포함한 상세설명 저장. */
    void persistDescription(LegacyProductGroupDescription description);
}
