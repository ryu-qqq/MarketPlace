package com.ryuqq.marketplace.domain.product.vo;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * 상품 생성 데이터 컬렉션 VO.
 *
 * <p>ProductGroupImages, SellerOptionGroups와 동일한 패턴으로, ProductCreationData 컬렉션을 불변으로 관리합니다.
 */
public class ProductCreations {

    private final List<ProductCreationData> creationDataList;

    private ProductCreations(List<ProductCreationData> creationDataList) {
        this.creationDataList = creationDataList;
    }

    /** 신규 생성 시 사용. */
    public static ProductCreations of(List<ProductCreationData> creationDataList) {
        return new ProductCreations(List.copyOf(creationDataList));
    }

    /**
     * Product 리스트로 변환.
     *
     * @param productGroupId 확정된 ProductGroupId
     * @param allOptionValueIds persist 후 확정된 모든 SellerOptionValueId (그룹 순서대로 플랫)
     * @param now 생성 시각
     * @return Product 도메인 객체 리스트
     */
    public List<Product> toProducts(
            ProductGroupId productGroupId,
            List<SellerOptionValueId> allOptionValueIds,
            Instant now) {
        return creationDataList.stream()
                .map(data -> data.toProduct(productGroupId, allOptionValueIds, now))
                .toList();
    }

    // === 조회 ===

    public List<ProductCreationData> toList() {
        return Collections.unmodifiableList(creationDataList);
    }

    public int size() {
        return creationDataList.size();
    }

    public boolean isEmpty() {
        return creationDataList.isEmpty();
    }
}
