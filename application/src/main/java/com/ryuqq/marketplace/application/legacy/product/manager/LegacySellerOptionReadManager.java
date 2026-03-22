package com.ryuqq.marketplace.application.legacy.product.manager;

import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacySellerOptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 옵션그룹 Read Manager. */
@Component
public class LegacySellerOptionReadManager {

    private final LegacySellerOptionQueryPort queryPort;

    public LegacySellerOptionReadManager(LegacySellerOptionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerOptionGroups getByProductGroupId(long productGroupId) {
        List<SellerOptionGroup> groups = queryPort.findByProductGroupId(productGroupId);
        return SellerOptionGroups.reconstitute(groups);
    }
}
