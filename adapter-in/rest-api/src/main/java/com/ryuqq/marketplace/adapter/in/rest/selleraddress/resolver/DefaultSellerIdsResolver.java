package com.ryuqq.marketplace.adapter.in.rest.selleraddress.resolver;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 기본 SellerIdsResolver. request에 sellerIds가 있으면 사용, 없으면 path sellerId 1건.
 *
 * <p>셀러 권한일 때 현재 셀러로 고정하려면 별도 구현체(예: SecurityContext 기반)를 주입.
 */
@Component
public class DefaultSellerIdsResolver implements SellerIdsResolver {

    @Override
    public List<Long> resolve(List<Long> requestSellerIds, Long pathSellerId) {
        if (requestSellerIds != null && !requestSellerIds.isEmpty()) {
            return requestSellerIds;
        }
        return pathSellerId != null ? List.of(pathSellerId) : List.of();
    }
}
