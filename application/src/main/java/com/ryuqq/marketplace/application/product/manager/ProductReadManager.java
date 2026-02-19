package com.ryuqq.marketplace.application.product.manager;

import com.ryuqq.marketplace.application.product.port.out.query.ProductQueryPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException;
import com.ryuqq.marketplace.domain.product.exception.ProductOwnershipViolationException;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Product Read Manager. */
@Component
public class ProductReadManager {

    private final ProductQueryPort queryPort;

    public ProductReadManager(ProductQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Product getById(ProductId id) {
        return queryPort.findById(id).orElseThrow(() -> new ProductNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Product> findByProductGroupId(ProductGroupId productGroupId) {
        return queryPort.findByProductGroupId(productGroupId);
    }

    /**
     * 상품 그룹 내 특정 상품들을 배치 조회합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param ids 상품 ID 목록
     * @return 조회된 상품 목록
     * @throws ProductOwnershipViolationException 요청 수와 조회 수가 불일치할 경우
     */
    @Transactional(readOnly = true)
    public List<Product> getByProductGroupIdAndIds(
            ProductGroupId productGroupId, List<ProductId> ids) {
        List<Product> products = queryPort.findByProductGroupIdAndIdIn(productGroupId, ids);
        if (products.size() != ids.size()) {
            throw new ProductOwnershipViolationException(
                    productGroupId.value(), ids.size(), products.size());
        }
        return products;
    }
}
