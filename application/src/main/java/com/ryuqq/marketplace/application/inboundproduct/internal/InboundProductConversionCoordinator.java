package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductConversionFactory;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
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
    private final ExternalSourceReadManager externalSourceReadManager;

    public InboundProductConversionCoordinator(
            InboundProductConversionFactory conversionFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator,
            ExternalSourceReadManager externalSourceReadManager) {
        this.conversionFactory = conversionFactory;
        this.registrationCoordinator = registrationCoordinator;
        this.externalSourceReadManager = externalSourceReadManager;
    }

    public ExternalSourceType convert(InboundProduct product, Instant now) {
        ExternalSource source = externalSourceReadManager.getById(product.inboundSourceId());
        ExternalSourceType sourceType = source.type();
        try {
            handleNewRegistration(product, sourceType, now);
        } catch (Exception e) {
            log.error(
                    "인바운드 상품 변환 실패: inboundProductId={}, externalCode={}",
                    product.idValue(),
                    product.externalProductCodeValue(),
                    e);
            product.markConvertFailed(now);
        }
        return sourceType;
    }

    private void handleNewRegistration(
            InboundProduct product, ExternalSourceType sourceType, Instant now) {
        ProductGroupRegistrationBundle bundle =
                conversionFactory.toRegistrationBundle(product, sourceType);
        Long productGroupId = registrationCoordinator.register(bundle);
        product.markConverted(productGroupId, now);

        log.info(
                "인바운드 상품 신규 등록 변환 완료: inboundProductId={}, productGroupId={}",
                product.idValue(),
                productGroupId);
    }
}
