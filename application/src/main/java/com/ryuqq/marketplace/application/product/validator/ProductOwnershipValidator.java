package com.ryuqq.marketplace.application.product.validator;

import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 상품 소유권 검증기.
 *
 * <p>상품 ID 목록으로 조회한 뒤, 해당 상품들이 속한 ProductGroup의 셀러 소유권을 검증합니다.
 */
@Component
public class ProductOwnershipValidator {

    private final ProductReadManager productReadManager;
    private final ProductGroupReadManager productGroupReadManager;

    public ProductOwnershipValidator(
            ProductReadManager productReadManager,
            ProductGroupReadManager productGroupReadManager) {
        this.productReadManager = productReadManager;
        this.productGroupReadManager = productGroupReadManager;
    }

    /**
     * 상품 ID 목록에 대해 소유권을 검증하고 조회된 상품 목록을 반환합니다.
     *
     * @param productIds 상품 ID 목록
     * @param sellerId 셀러 ID
     * @return 소유권이 검증된 상품 목록
     * @throws com.ryuqq.marketplace.domain.product.exception.ProductNotFoundException 상품을 찾을 수 없는
     *     경우
     * @throws
     *     com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException
     *     소유권 검증 실패 시
     */
    public List<Product> validateAndGet(List<ProductId> productIds, long sellerId) {
        List<Product> products = productReadManager.getByIds(productIds);

        List<ProductGroupId> productGroupIds =
                products.stream().map(Product::productGroupId).distinct().toList();

        productGroupReadManager.getByIdsAndSellerId(productGroupIds, sellerId);

        return products;
    }
}
