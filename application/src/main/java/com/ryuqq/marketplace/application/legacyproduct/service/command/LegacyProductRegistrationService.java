package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult.ConversionAction;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductRegisterCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductRegistrationUseCase;
import com.ryuqq.marketplace.application.product.manager.ProductReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductConversionFailedException;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductMappingNotReadyException;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 등록 서비스.
 *
 * <p>InboundProductRegisterCoordinator로 등록 후, ProductGroupReadManager와 ProductReadManager로 상품 그룹 및
 * 활성화된 상품 목록을 조회하여 반환합니다.
 *
 * <p>Use Case 간 의존을 피하기 위해 Coordinator 및 ReadManager를 직접 주입합니다.
 */
@Service
public class LegacyProductRegistrationService implements LegacyProductRegistrationUseCase {

    private final InboundProductRegisterCoordinator registerCoordinator;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductReadManager productReadManager;

    public LegacyProductRegistrationService(
            InboundProductRegisterCoordinator registerCoordinator,
            ProductGroupReadManager productGroupReadManager,
            ProductReadManager productReadManager) {
        this.registerCoordinator = registerCoordinator;
        this.productGroupReadManager = productGroupReadManager;
        this.productReadManager = productReadManager;
    }

    @Override
    public LegacyProductRegistrationResult execute(ReceiveInboundProductCommand command) {
        InboundProductConversionResult result = registerCoordinator.register(command);

        if (result.action() == ConversionAction.CONVERT_FAILED) {
            throw new InboundProductConversionFailedException(result.inboundProductId());
        }

        if (result.action() == ConversionAction.PENDING_MAPPING) {
            throw new InboundProductMappingNotReadyException(result.inboundProductId());
        }

        ProductGroupId productGroupId = ProductGroupId.of(result.internalProductGroupId());
        ProductGroup productGroup = productGroupReadManager.getById(productGroupId);
        List<Product> products = productReadManager.findByProductGroupId(productGroupId);
        return new LegacyProductRegistrationResult(productGroup, products);
    }
}
