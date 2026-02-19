package com.ryuqq.marketplace.application.seller.service.query;

import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.seller.port.in.query.ResolveSellerIdByOrganizationUseCase;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

/**
 * ResolveSellerIdByOrganizationService - organizationId로 sellerId 조회 서비스.
 *
 * <p>organizationId → sellerId 매핑은 셀러 생성 후 불변이므로 로컬 캐시를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Service
public class ResolveSellerIdByOrganizationService implements ResolveSellerIdByOrganizationUseCase {

    private final SellerReadManager sellerReadManager;
    private final ConcurrentMap<String, Long> cache = new ConcurrentHashMap<>();

    public ResolveSellerIdByOrganizationService(SellerReadManager sellerReadManager) {
        this.sellerReadManager = sellerReadManager;
    }

    public void clearCache() {
        cache.clear();
    }

    @Override
    public Optional<Long> execute(String organizationId) {
        if (organizationId == null || organizationId.isBlank()) {
            return Optional.empty();
        }

        Long cached = cache.get(organizationId);
        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<Long> sellerId = sellerReadManager.findSellerIdByOrganizationId(organizationId);
        sellerId.ifPresent(id -> cache.put(organizationId, id));
        return sellerId;
    }
}
