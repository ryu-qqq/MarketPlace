package com.ryuqq.marketplace.application.productgroup.dto.bundle;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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

    /** Product 리스트로 변환. */
    public List<Product> toProducts(ProductGroupId productGroupId, Instant now) {
        return creationDataList.stream().map(data -> data.toProduct(productGroupId, now)).toList();
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
