package com.ryuqq.marketplace.application.productgroup.manager;

import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ProductGroup Read Manager. */
@Component
public class ProductGroupReadManager {

    private final ProductGroupQueryPort queryPort;

    public ProductGroupReadManager(ProductGroupQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ProductGroup getById(ProductGroupId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new ProductGroupNotFoundException(id.value()));
    }

    /**
     * 셀러 소유의 상품 그룹을 배치 조회합니다.
     *
     * @param ids 상품 그룹 ID 목록
     * @param sellerId 셀러 ID
     * @return 조회된 상품 그룹 목록
     * @throws ProductGroupOwnershipViolationException 요청 수와 조회 수가 불일치할 경우
     */
    @Transactional(readOnly = true)
    public List<ProductGroup> getByIdsAndSellerId(List<ProductGroupId> ids, long sellerId) {
        List<ProductGroup> productGroups = queryPort.findByIdsAndSellerId(ids, sellerId);
        if (productGroups.size() != ids.size()) {
            throw new ProductGroupOwnershipViolationException(
                    sellerId, ids.size(), productGroups.size());
        }
        return productGroups;
    }

    @Transactional(readOnly = true)
    public List<ProductGroup> findByCriteria(ProductGroupSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
