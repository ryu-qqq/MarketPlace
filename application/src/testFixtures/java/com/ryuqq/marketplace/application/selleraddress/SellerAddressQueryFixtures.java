package com.ryuqq.marketplace.application.selleraddress;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import java.util.List;

/**
 * SellerAddress Query 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAddressQueryFixtures {

    private SellerAddressQueryFixtures() {}

    // ===== SearchParams Fixtures =====

    public static SellerAddressSearchParams searchParams(Long sellerId) {
        return SellerAddressSearchParams.of(
                List.of(sellerId), null, null, null, null, defaultCommonSearchParams());
    }

    public static SellerAddressSearchParams searchParams(Long sellerId, List<String> addressTypes) {
        return SellerAddressSearchParams.of(
                List.of(sellerId), addressTypes, null, null, null, defaultCommonSearchParams());
    }

    public static SellerAddressSearchParams searchParams(Long sellerId, Boolean defaultAddress) {
        return SellerAddressSearchParams.of(
                List.of(sellerId), null, defaultAddress, null, null, defaultCommonSearchParams());
    }

    public static SellerAddressSearchParams searchParams(Long sellerId, int page, int size) {
        return SellerAddressSearchParams.of(
                List.of(sellerId), null, null, null, null, commonSearchParams(page, size));
    }

    public static SellerAddressSearchParams searchParams(
            List<Long> sellerIds,
            List<String> addressTypes,
            Boolean defaultAddress,
            String searchField,
            String searchWord,
            int page,
            int size) {
        return SellerAddressSearchParams.of(
                sellerIds,
                addressTypes,
                defaultAddress,
                searchField,
                searchWord,
                commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    // ===== Result Fixtures =====

    public static SellerAddressResult sellerAddressResult(Long id, Long sellerId) {
        return new SellerAddressResult(
                id,
                sellerId,
                "SHIPPING",
                "본사 창고",
                new SellerAddressResult.AddressResult("06164", "서울 강남구 역삼로 123", "5층"),
                true,
                null,
                null);
    }

    public static SellerAddressPageResult sellerAddressPageResult() {
        return SellerAddressPageResult.of(List.of(sellerAddressResult(1L, 1L)), 0, 20, 1L);
    }

    public static SellerAddressPageResult emptyPageResult() {
        return SellerAddressPageResult.of(List.of(), 0, 20, 0L);
    }
}
