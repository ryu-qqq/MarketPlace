package com.ryuqq.marketplace.application.selleroption.manager;

import com.ryuqq.marketplace.application.selleroption.port.out.query.SellerOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import org.springframework.stereotype.Component;

/**
 * SellerOptionGroup Read Manager.
 *
 * <p>SellerOptionGroup 조회를 관리합니다.
 */
@Component
public class SellerOptionGroupReadManager {

    private final SellerOptionGroupQueryPort queryPort;

    public SellerOptionGroupReadManager(SellerOptionGroupQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /** productGroupId로 활성 SellerOptionGroups 조회. */
    public SellerOptionGroups getByProductGroupId(ProductGroupId productGroupId) {
        return SellerOptionGroups.reconstitute(queryPort.findByProductGroupId(productGroupId));
    }
}
