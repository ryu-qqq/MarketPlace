package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 크롤링 소스 rawPayloadJson 파서 (스켈레톤).
 *
 * <p>크롤링 JSON 구조 확정 후 구현 예정. 인터페이스와 Provider 등록만 완료합니다.
 */
@Component
public class CrawlingPayloadParser implements InboundProductPayloadParser {

    @Override
    public boolean supports(ExternalSourceType sourceType) {
        return sourceType == ExternalSourceType.CRAWLING;
    }

    @Override
    public ProductGroupRegistrationBundle toRegistrationBundle(InboundProduct product) {
        throw new UnsupportedOperationException("크롤링 페이로드 파싱은 향후 구현 예정입니다.");
    }

    @Override
    public Optional<ProductGroupUpdateBundle> toUpdateBundle(InboundProduct product) {
        return Optional.empty();
    }
}
