package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.factory.InboundProductCommandFactory;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductCommandConverter;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductRegistrationResolver;
import com.ryuqq.marketplace.application.inboundproduct.internal.ResolvedPolicies;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductCommandManager;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.RetryPendingMappingUseCase;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PENDING_MAPPING 상태의 인바운드 상품을 재처리하는 서비스.
 *
 * <p>매핑 테이블을 재조회하여 브랜드/카테고리 매핑이 추가된 상품을 MAPPED 상태로 전이하고, rawPayload가 있으면 즉시 내부 ProductGroup
 * 등록(CONVERTED)까지 완료합니다.
 */
@Service
public class RetryPendingMappingService implements RetryPendingMappingUseCase {

    private static final Logger log = LoggerFactory.getLogger(RetryPendingMappingService.class);

    private final InboundProductReadManager readManager;
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;
    private final InboundProductRegistrationResolver registrationResolver;
    private final InboundProductCommandFactory commandFactory;
    private final InboundProductCommandConverter converter;
    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;
    private final TimeProvider timeProvider;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public RetryPendingMappingService(
            InboundProductReadManager readManager,
            InboundProductCommandManager commandManager,
            InboundProductMappingResolver mappingResolver,
            InboundProductRegistrationResolver registrationResolver,
            InboundProductCommandFactory commandFactory,
            InboundProductCommandConverter converter,
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.mappingResolver = mappingResolver;
        this.registrationResolver = registrationResolver;
        this.commandFactory = commandFactory;
        this.converter = converter;
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
        this.timeProvider = timeProvider;
    }

    @Override
    @Transactional
    public SchedulerBatchProcessingResult execute(int batchSize) {
        List<InboundProduct> pendingProducts = readManager.findPendingMapping(batchSize);

        if (pendingProducts.isEmpty()) {
            return SchedulerBatchProcessingResult.empty();
        }

        Instant now = timeProvider.now();
        int success = 0;
        int failed = 0;

        for (InboundProduct product : pendingProducts) {
            try {
                boolean processed = retryOne(product, now);
                if (processed) {
                    success++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
                log.error(
                        "PENDING_MAPPING 재처리 실패: id={}, code={}",
                        product.idValue(),
                        product.externalProductCodeValue(),
                        e);
            }
        }

        log.info(
                "PENDING_MAPPING 재처리 완료: total={}, success={}, failed={}",
                pendingProducts.size(),
                success,
                failed);

        return SchedulerBatchProcessingResult.of(pendingProducts.size(), success, failed);
    }

    private boolean retryOne(InboundProduct product, Instant now) {
        InboundProductMappingResult mapping = mappingResolver.resolveMappingAndApply(product, now);

        if (!mapping.isFullyMapped()) {
            return false;
        }

        String rawPayload = product.rawPayload();
        if (rawPayload == null || rawPayload.isBlank()) {
            commandManager.persist(product);
            log.info(
                    "PENDING_MAPPING -> MAPPED (payload 없음, 크롤러 재수신 대기): id={}, code={}",
                    product.idValue(),
                    product.externalProductCodeValue());
            return true;
        }

        ReceiveInboundProductCommand command = commandFactory.deserializePayload(rawPayload);
        if (command == null) {
            commandManager.persist(product);
            log.warn(
                    "PENDING_MAPPING -> MAPPED (payload 역직렬화 실패): id={}, code={}",
                    product.idValue(),
                    product.externalProductCodeValue());
            return true;
        }

        convertAndPersist(product, command, now);
        return true;
    }

    private void convertAndPersist(
            InboundProduct product, ReceiveInboundProductCommand command, Instant now) {

        SellerId sellerId = SellerId.of(product.sellerId());
        ResolvedPolicies resolved =
                registrationResolver.resolve(sellerId, product.internalCategoryId(), command);
        product.applyResolution(
                resolved.shippingPolicyId(),
                resolved.refundPolicyId(),
                resolved.noticeCategoryId(),
                now);

        RegisterProductGroupCommand registerCommand =
                converter.toRegisterCommand(command, product, resolved);
        ProductGroupRegistrationBundle bundle =
                bundleFactory.createProductGroupBundle(registerCommand);
        ProductGroupRegistrationResult result = registrationCoordinator.register(bundle);

        product.markConverted(result.productGroupId(), now);
        commandManager.persist(product);

        log.info(
                "PENDING_MAPPING -> CONVERTED 완료: id={}, code={}, productGroupId={}",
                product.idValue(),
                product.externalProductCodeValue(),
                result.productGroupId());
    }
}
