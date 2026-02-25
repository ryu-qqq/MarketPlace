package com.ryuqq.marketplace.domain.legacy.product.aggregate;

import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;

/**
 * 레거시(세토프) 상품 Aggregate Root.
 *
 * <p>세토프 DB의 product + product_stock 테이블에 대응하며, 상품-옵션 매핑(LegacyProductOption)을 VO로 포함합니다.
 */
public class LegacyProduct {

    private final LegacyProductId id;
    private final LegacyProductGroupId productGroupId;
    private String soldOutYn;
    private String displayYn;
    private int stockQuantity;
    private List<LegacyProductOption> options;

    private LegacyProduct(
            LegacyProductId id,
            LegacyProductGroupId productGroupId,
            String soldOutYn,
            String displayYn,
            int stockQuantity,
            List<LegacyProductOption> options) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.soldOutYn = soldOutYn;
        this.displayYn = displayYn;
        this.stockQuantity = stockQuantity;
        this.options = options == null ? List.of() : List.copyOf(options);
    }

    /** 신규 레거시 상품 생성. */
    public static LegacyProduct forNew(
            LegacyProductGroupId productGroupId,
            String soldOutYn,
            String displayYn,
            int stockQuantity,
            List<LegacyProductOption> options) {
        return new LegacyProduct(
                LegacyProductId.forNew(),
                productGroupId,
                soldOutYn,
                displayYn,
                stockQuantity,
                options);
    }

    /** DB에서 복원. */
    public static LegacyProduct reconstitute(
            Long id,
            Long productGroupId,
            String soldOutYn,
            String displayYn,
            int stockQuantity,
            List<LegacyProductOption> options) {
        return new LegacyProduct(
                LegacyProductId.of(id),
                LegacyProductGroupId.of(productGroupId),
                soldOutYn,
                displayYn,
                stockQuantity,
                options);
    }

    public LegacyProductId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public LegacyProductGroupId productGroupId() {
        return productGroupId;
    }

    public Long productGroupIdValue() {
        return productGroupId.value();
    }

    public String soldOutYn() {
        return soldOutYn;
    }

    public String displayYn() {
        return displayYn;
    }

    public int stockQuantity() {
        return stockQuantity;
    }

    public List<LegacyProductOption> options() {
        return options;
    }
}
