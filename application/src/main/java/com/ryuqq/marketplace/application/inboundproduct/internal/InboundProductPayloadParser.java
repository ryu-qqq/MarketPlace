package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;

/**
 * InboundProduct rawPayloadJson 변환 전략 인터페이스.
 *
 * <p>sourceCode별로 rawPayloadJson 파싱 → 등록/수정 번들 변환을 수행합니다.
 */
public interface InboundProductPayloadParser {

    /** 이 파서가 처리할 수 있는 소스 코드인지 판별한다. */
    boolean supports(String sourceCode);

    /**
     * InboundProduct의 rawPayloadJson 파싱 → 신규 등록 번들 변환.
     *
     * @param product 인바운드 상품
     * @return 등록 번들
     */
    ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product);

    /**
     * InboundProduct의 rawPayloadJson 파싱 → 수정 번들 변환.
     *
     * @param product 인바운드 상품
     * @return 수정 번들 (변경 사항이 없으면 empty)
     */
    Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product);
}
