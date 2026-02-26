package com.ryuqq.marketplace.adapter.in.rest.selleraddress.resolver;

import java.util.List;

/**
 * 셀러 주소 검색 시 적용할 sellerIds 결정.
 *
 * <p>슈퍼 관리자: request의 sellerIds 사용. 셀러: 현재 로그인 셀러 1건으로 고정(구현체에서 SecurityContext 등 사용).
 */
public interface SellerIdsResolver {

    /**
     * 검색에 사용할 셀러 ID 목록 반환.
     *
     * @param requestSellerIds 요청의 sellerIds (query param, null/empty 가능)
     * @param pathSellerId URL path 의 sellerId (단일 조회/하위 호환용)
     * @return 적용할 sellerIds (비어 있으면 안 됨)
     */
    List<Long> resolve(List<Long> requestSellerIds, Long pathSellerId);
}
