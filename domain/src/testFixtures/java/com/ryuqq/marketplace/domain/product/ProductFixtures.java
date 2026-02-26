package com.ryuqq.marketplace.domain.product;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.Collections;

/**
 * Product 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 Product 관련 객체들을 생성합니다.
 */
public final class ProductFixtures {

    private ProductFixtures() {}

    // ===== 기본 값 상수 =====
    public static final int DEFAULT_REGULAR_PRICE = 100000;
    public static final int DEFAULT_CURRENT_PRICE = 80000;
    public static final int DEFAULT_SALE_PRICE = 60000;
    public static final int DEFAULT_DISCOUNT_RATE = 25;
    public static final int DEFAULT_STOCK_QUANTITY = 100;
    public static final int DEFAULT_SORT_ORDER = 1;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final String DEFAULT_SKU_CODE = "SKU-TEST-001";

    // ===== Product Aggregate Fixtures =====

    /** 신규 Product 생성 (forNew, ACTIVE 상태). */
    public static Product newProduct() {
        return Product.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                DEFAULT_STOCK_QUANTITY,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.now());
    }

    /** 활성 상태의 Product (reconstitute, ACTIVE). */
    public static Product activeProduct() {
        return Product.reconstitute(
                ProductId.of(1L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                CommonVoFixtures.money(DEFAULT_SALE_PRICE),
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                ProductStatus.ACTIVE,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** ID 지정 활성 상태 Product. */
    public static Product activeProduct(Long id) {
        return Product.reconstitute(
                ProductId.of(id),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                CommonVoFixtures.money(DEFAULT_SALE_PRICE),
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                ProductStatus.ACTIVE,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 비활성 상태의 Product (reconstitute, INACTIVE). */
    public static Product inactiveProduct() {
        return Product.reconstitute(
                ProductId.of(2L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                CommonVoFixtures.money(DEFAULT_SALE_PRICE),
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                ProductStatus.INACTIVE,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 품절 상태의 Product (reconstitute, SOLDOUT). */
    public static Product soldOutProduct() {
        return Product.reconstitute(
                ProductId.of(3L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                CommonVoFixtures.money(DEFAULT_SALE_PRICE),
                DEFAULT_DISCOUNT_RATE,
                0, // 품절이므로 재고 0
                ProductStatus.SOLDOUT,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 삭제 상태의 Product (reconstitute, DELETED). */
    public static Product deletedProduct() {
        return Product.reconstitute(
                ProductId.of(4L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                CommonVoFixtures.money(DEFAULT_SALE_PRICE),
                DEFAULT_DISCOUNT_RATE,
                DEFAULT_STOCK_QUANTITY,
                ProductStatus.DELETED,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    /** 세일 중이 아닌 Product (salePrice = null, discountRate = 0). */
    public static Product productWithoutSale() {
        return Product.reconstitute(
                ProductId.of(5L),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SkuCode.of(DEFAULT_SKU_CODE),
                CommonVoFixtures.money(DEFAULT_REGULAR_PRICE),
                CommonVoFixtures.money(DEFAULT_CURRENT_PRICE),
                null, // 세일 가격 없음
                0, // 할인율 0
                DEFAULT_STOCK_QUANTITY,
                ProductStatus.ACTIVE,
                DEFAULT_SORT_ORDER,
                Collections.emptyList(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== ProductOptionMapping Fixtures =====

    /** 기본 ProductOptionMapping. */
    public static ProductOptionMapping defaultOptionMapping() {
        return ProductOptionMapping.forNew(ProductId.of(1L), SellerOptionValueId.of(100L));
    }

    /** 특정 ID의 ProductOptionMapping. */
    public static ProductOptionMapping optionMapping(Long productId, Long sellerOptionValueId) {
        return ProductOptionMapping.forNew(
                ProductId.of(productId), SellerOptionValueId.of(sellerOptionValueId));
    }
}
