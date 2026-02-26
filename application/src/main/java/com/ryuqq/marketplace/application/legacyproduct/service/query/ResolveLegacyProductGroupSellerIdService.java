package com.ryuqq.marketplace.application.legacyproduct.service.query;

import com.ryuqq.marketplace.application.legacyproduct.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.out.query.LegacyProductGroupQueryPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** 레거시 productGroupId로 sellerId 조회 서비스. */
@Service
public class ResolveLegacyProductGroupSellerIdService
        implements ResolveLegacyProductGroupSellerIdUseCase {

    private final LegacyProductGroupQueryPort legacyProductGroupQueryPort;

    public ResolveLegacyProductGroupSellerIdService(
            LegacyProductGroupQueryPort legacyProductGroupQueryPort) {
        this.legacyProductGroupQueryPort = legacyProductGroupQueryPort;
    }

    @Override
    public Optional<Long> execute(long productGroupId) {
        return legacyProductGroupQueryPort
                .findById(LegacyProductGroupId.of(productGroupId))
                .map(pg -> pg.sellerId());
    }
}
