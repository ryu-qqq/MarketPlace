package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPolicyCompositeResult;
import java.util.Optional;

/**
 * SellerCompositionQueryPort - 셀러 Composition 조회 Port.
 *
 * <p>크로스 도메인 조인을 통한 성능 최적화된 조회를 제공합니다.
 */
public interface SellerCompositionQueryPort {

    /**
     * Customer용 셀러 Composite 조회 (Seller + Address + BusinessInfo + CS).
     *
     * @param sellerId 셀러 ID
     * @return 셀러 Composite 결과
     */
    Optional<SellerCompositeResult> findSellerCompositeById(Long sellerId);

    /**
     * Admin용 셀러 Composite 조회 (Seller + Address + BusinessInfo + CS + Contract + Settlement).
     *
     * @param sellerId 셀러 ID
     * @return Admin용 셀러 Composite 결과
     */
    Optional<SellerAdminCompositeResult> findAdminCompositeById(Long sellerId);

    /**
     * authTenantId로 Admin용 셀러 Composite 조회.
     *
     * @param authTenantId 인증 테넌트 ID
     * @return Admin용 셀러 Composite 결과
     */
    Optional<SellerAdminCompositeResult> findAdminCompositeByAuthTenantId(String authTenantId);

    Optional<SellerPolicyCompositeResult> findPolicyCompositeById(Long sellerId);
}
