package com.ryuqq.marketplace.application.product.port.out.command;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import java.util.List;

/** ProductOptionMapping Command Port. */
public interface ProductOptionMappingCommandPort {

    void persist(ProductOptionMapping mapping);

    /**
     * 지정된 productId로 옵션 매핑 목록을 저장합니다.
     *
     * <p>신규 등록 시 Product persist 후 생성된 ID를 OptionMapping에 전달할 때 사용합니다.
     */
    void persistAllForProduct(Long productId, List<ProductOptionMapping> mappings);
}
