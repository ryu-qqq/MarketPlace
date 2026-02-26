package com.ryuqq.marketplace.domain.product.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;

/**
 * Product 수정 입력 데이터.
 *
 * <p>Application Factory에서 이름 기반 옵션 resolve가 완료된 상태로 도메인에 전달됩니다.
 *
 * <p>각 Entry의 productId가 null이면 신규 Product, non-null이면 기존 Product를 의미합니다.
 *
 * @param productGroupId 상품 그룹 ID
 * @param entries 수정 엔트리 목록
 * @param updatedAt 수정 시각
 */
public record ProductUpdateData(
        ProductGroupId productGroupId, List<Entry> entries, Instant updatedAt) {

    public ProductUpdateData {
        entries = List.copyOf(entries);
    }

    /**
     * 개별 Product 수정 엔트리.
     *
     * <p>productId가 null이면 신규, non-null이면 기존.
     *
     * <p>resolvedOptionValueIds는 신규 Product만 사용합니다. 기존 Product는 빈 리스트입니다.
     *
     * @param productId 기존 Product ID (nullable: null이면 신규)
     * @param skuCode SKU 코드 (nullable: 기존 Product에서 미제공 시 기존 값 유지)
     * @param regularPrice 정가
     * @param currentPrice 판매가
     * @param stockQuantity 재고 수량
     * @param sortOrder 정렬 순서
     * @param resolvedOptionValueIds resolve 완료된 SellerOptionValueId 목록
     */
    public record Entry(
            Long productId,
            SkuCode skuCode,
            Money regularPrice,
            Money currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SellerOptionValueId> resolvedOptionValueIds) {

        public Entry {
            resolvedOptionValueIds = List.copyOf(resolvedOptionValueIds);
        }
    }
}
