package com.ryuqq.marketplace.application.inboundproduct.internal;

import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductBundleFactory;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundProductConversionCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(InboundProductConversionCoordinator.class);

    private final InboundProductBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;

    public InboundProductConversionCoordinator(
            InboundProductBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator) {
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
    }

    public void convert(InboundProduct product, Instant now) {
        try {
            handleNewRegistration(product, now);
        } catch (Exception e) {
            log.error(
                    "인바운드 상품 변환 실패 (재시도 가능): inboundProductId={}, externalCode={}",
                    product.idValue(),
                    product.externalProductCodeValue(),
                    e);
            product.markConvertFailed(now);
        }
    }

    private void handleNewRegistration(InboundProduct product, Instant now) {
        ProductGroupRegistrationBundle bundle = bundleFactory.toRegistrationBundle(product);
        ProductGroupRegistrationResult result = registrationCoordinator.register(bundle);
        product.markConverted(result.productGroupId(), now);
        log.info(
                "인바운드 상품 신규 등록 변환 완료: inboundProductId={}, productGroupId={}",
                product.idValue(),
                result.productGroupId());
    }
}
