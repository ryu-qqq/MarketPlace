package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerCompositionQueryPort;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerCompositionReadManager - 셀러 Composition 조회 매니저.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회를 담당합니다.
 */
@Component
public class SellerCompositionReadManager {

    private final SellerCompositionQueryPort compositionQueryPort;

    public SellerCompositionReadManager(SellerCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    /**
     * Customer용 셀러 Composite 조회 (Seller + Address + BusinessInfo + CS).
     *
     * @param sellerId 셀러 ID
     * @return 셀러 Composite 결과
     */
    @Transactional(readOnly = true)
    public SellerCompositeResult getSellerComposite(Long sellerId) {
        return compositionQueryPort
                .findSellerCompositeById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
    }

    /**
     * Admin용 셀러 Composite 조회 (Seller + Address + BusinessInfo + CS + Contract + Settlement).
     *
     * @param sellerId 셀러 ID
     * @return Admin용 셀러 Composite 결과
     */
    @Transactional(readOnly = true)
    public SellerAdminCompositeResult getAdminComposite(Long sellerId) {
        return compositionQueryPort
                .findAdminCompositeById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
    }

    /**
     * authTenantId로 Admin용 셀러 Composite 조회.
     *
     * @param authTenantId 인증 테넌트 ID
     * @return Admin용 셀러 Composite 결과
     */
    @Transactional(readOnly = true)
    public SellerAdminCompositeResult getAdminCompositeByAuthTenantId(String authTenantId) {
        return compositionQueryPort
                .findAdminCompositeByAuthTenantId(authTenantId)
                .orElseThrow(() -> new SellerNotFoundException(authTenantId));
    }

    @Transactional(readOnly = true)
    public SellerPolicyCompositeResult getPolicyComposite(Long sellerId) {
        return compositionQueryPort
                .findPolicyCompositeById(sellerId)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
    }
}
