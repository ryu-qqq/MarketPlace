package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductConversionFactory;
import com.ryuqq.marketplace.application.inboundsource.manager.InboundSourceReadManager;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundPayloadInvalidException;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundProductConversionCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductConversionCoordinator.class);

    private final InboundProductConversionFactory conversionFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;
    private final InboundSourceReadManager inboundSourceReadManager;

    public InboundProductConversionCoordinator(
            InboundProductConversionFactory conversionFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator,
            InboundSourceReadManager inboundSourceReadManager) {
        this.conversionFactory = conversionFactory;
        this.registrationCoordinator = registrationCoordinator;
        this.inboundSourceReadManager = inboundSourceReadManager;
    }

    public void convert(InboundProduct product, Instant now) {
        InboundSource source = inboundSourceReadManager.getById(product.inboundSourceId());
        String sourceCode = source.codeValue();
        try {
            handleNewRegistration(product, sourceCode, now);
        } catch (InboundPayloadInvalidException e) {
            log.error(
                    "인바운드 상품 페이로드 복구 불가: inboundProductId={}, externalCode={}, reason={}",
                    product.idValue(),
                    product.externalProductCodeValue(),
                    e.getMessage());
            product.markPermanentlyFailed(now);
        } catch (Exception e) {
            log.error(
                    "인바운드 상품 변환 실패 (재시도 가능): inboundProductId={}, externalCode={}",
                    product.idValue(),
                    product.externalProductCodeValue(),
                    e);
            product.markConvertFailed(now);
        }
    }

    private void handleNewRegistration(InboundProduct product, String sourceCode, Instant now) {
        ProductGroupRegistrationBundle bundle =
                conversionFactory.toRegistrationBundle(product, sourceCode);
        Long productGroupId = registrationCoordinator.register(bundle);
        product.markConverted(productGroupId, now);
        log.info(
                "인바운드 상품 신규 등록 변환 완료: inboundProductId={}, productGroupId={}",
                product.idValue(),
                productGroupId);
    }
}
