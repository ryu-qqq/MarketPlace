package com.ryuqq.marketplace.application.inboundproduct.factory;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductPayloadParser;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductPayloadParserProvider;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * InboundProduct -> ProductGroup 변환 팩토리.
 *
 * <p>InboundProductPayloadParserProvider에 위임하여 ExternalSourceType별 rawPayloadJson 파싱 → 등록/수정 번들 변환을
 * 수행합니다. Factory는 순수 변환만 담당하며, ExternalSourceType 조회는 호출자(Coordinator)에서 수행합니다.
 */
@Component
public class InboundProductConversionFactory {

    private final InboundProductPayloadParserProvider parserProvider;

    public InboundProductConversionFactory(InboundProductPayloadParserProvider parserProvider) {
        this.parserProvider = parserProvider;
    }

    /**
     * InboundProduct -> ProductGroupRegistrationBundle 변환 (신규 등록용).
     *
     * @param product 인바운드 상품
     * @param sourceType 외부 소스 타입
     * @return 등록 번들
     */
    public ProductGroupRegistrationBundle toRegistrationBundle(
            InboundProduct product, ExternalSourceType sourceType) {
        InboundProductPayloadParser parser = parserProvider.getParser(sourceType);
        return parser.toRegistrationBundle(product);
    }

    /**
     * InboundProduct -> 업데이트 커맨드 변환 (기존 수정용).
     *
     * @param product 인바운드 상품
     * @param sourceType 외부 소스 타입
     * @return 수정 번들 (변경 사항이 없으면 empty)
     */
    public Optional<ProductGroupUpdateBundle> toUpdateCommand(
            InboundProduct product, ExternalSourceType sourceType) {
        InboundProductPayloadParser parser = parserProvider.getParser(sourceType);
        return parser.toUpdateBundle(product);
    }
}
