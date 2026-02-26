package com.ryuqq.marketplace.application.productgroupdescription.service.query;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionCompositeReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.query.GetDescriptionPublishStatusUseCase;
import org.springframework.stereotype.Service;

/** 상세설명 발행 상태 조회 서비스. */
@Service
public class GetDescriptionPublishStatusService implements GetDescriptionPublishStatusUseCase {

    private final DescriptionCompositeReadManager compositeReadManager;

    public GetDescriptionPublishStatusService(
            DescriptionCompositeReadManager compositeReadManager) {
        this.compositeReadManager = compositeReadManager;
    }

    @Override
    public DescriptionPublishStatusResult execute(Long productGroupId) {
        return compositeReadManager.getPublishStatus(productGroupId);
    }
}
