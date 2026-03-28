package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 productGroupId로 sellerId 조회 서비스.
 *
 * <p>요청 PK를 market PK로 resolve 후, market에서 ProductGroup의 셀러ID를 조회합니다.
 * 반환되는 sellerId는 market internal 셀러ID입니다.
 */
@Service
public class ResolveLegacyProductGroupSellerIdService
        implements ResolveLegacyProductGroupSellerIdUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ResolveLegacyProductGroupSellerIdService.class);

    private final LegacyProductIdResolver productIdResolver;
    private final ProductGroupReadManager productGroupReadManager;

    public ResolveLegacyProductGroupSellerIdService(
            LegacyProductIdResolver productIdResolver,
            ProductGroupReadManager productGroupReadManager) {
        this.productIdResolver = productIdResolver;
        this.productGroupReadManager = productGroupReadManager;
    }

    @Override
    public Optional<Long> execute(long productGroupId) {
        long resolvedId = productIdResolver.resolveProductGroupId(productGroupId);
        try {
            ProductGroup pg = productGroupReadManager.getById(ProductGroupId.of(resolvedId));
            return Optional.of(pg.sellerId().value());
        } catch (Exception e) {
            log.debug("상품그룹 셀러 조회 실패: productGroupId={}, resolvedId={}", productGroupId, resolvedId);
            return Optional.empty();
        }
    }
}
