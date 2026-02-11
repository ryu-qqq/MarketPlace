package com.ryuqq.marketplace.application.seller;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;

/**
 * Seller Query 테스트 Fixtures.
 *
 * <p>Seller 관련 Query 파라미터 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class SellerQueryFixtures {

    private SellerQueryFixtures() {}

    public static SellerSearchParams searchParams() {
        return SellerSearchParams.of(null, null, null, defaultCommonSearchParams());
    }

    public static SellerSearchParams searchParams(Boolean active) {
        return SellerSearchParams.of(active, null, null, defaultCommonSearchParams());
    }

    public static SellerSearchParams searchParams(String searchWord) {
        return SellerSearchParams.of(null, null, searchWord, defaultCommonSearchParams());
    }

    public static SellerSearchParams searchParams(String searchField, String searchWord) {
        return SellerSearchParams.of(null, searchField, searchWord, defaultCommonSearchParams());
    }

    public static SellerSearchParams searchParams(int page, int size) {
        return SellerSearchParams.of(null, null, null, commonSearchParams(page, size));
    }

    public static SellerSearchParams searchParams(
            Boolean active, String searchField, String searchWord, int page, int size) {
        return SellerSearchParams.of(
                active, searchField, searchWord, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }
}
