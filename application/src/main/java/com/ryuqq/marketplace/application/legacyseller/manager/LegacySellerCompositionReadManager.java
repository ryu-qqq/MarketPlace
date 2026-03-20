package com.ryuqq.marketplace.application.legacyseller.manager;

import com.ryuqq.marketplace.application.legacyseller.port.out.LegacySellerCompositionQueryPort;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 셀러 Composition 조회 매니저.
 *
 * <p>레거시 스키마(luxurydb)에서 셀러 복합 정보를 조회합니다.
 * 새 스키마 전환 시 내부 구현만 교체하면 됩니다.
 */
@Component
public class LegacySellerCompositionReadManager {

    private final LegacySellerCompositionQueryPort queryPort;

    public LegacySellerCompositionReadManager(LegacySellerCompositionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 레거시 셀러 ID로 Admin용 셀러 Composite 조회.
     *
     * @param sellerId 레거시 셀러 ID
     * @return Admin용 셀러 Composite 결과
     * @throws SellerNotFoundException 셀러 미발견 시
     */
    @Transactional(readOnly = true)
    public SellerAdminCompositeResult getAdminComposite(long sellerId) {
        return queryPort
                .findAdminCompositeById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
    }
}
