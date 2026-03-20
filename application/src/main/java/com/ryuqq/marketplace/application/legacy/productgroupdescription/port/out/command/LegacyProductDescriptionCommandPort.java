package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyProductDescription;

/** 레거시 상품 상세설명 저장 Port. */
public interface LegacyProductDescriptionCommandPort {

    void persist(LegacyProductGroupId productGroupId, LegacyProductDescription description);

    /** content/cdnPath/publishStatus를 포함한 상세설명 저장. */
    void persistDescription(LegacyProductGroupDescription description);

    /** 표준 커맨드를 받아서 luxurydb에 저장. */
    void update(UpdateProductGroupDescriptionCommand command);
}
