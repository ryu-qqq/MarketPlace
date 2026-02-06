package com.ryuqq.marketplace.adapter.in.rest.selleraddress;

import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.RegisterSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.UpdateSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.query.SearchSellerAddressesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressApiResponse;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * SellerAddress API 테스트 Fixtures.
 *
 * <p>SellerAddress REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 */
public final class SellerAddressApiFixtures {

    private SellerAddressApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_ADDRESS_TYPE = "SHIPPING";
    public static final String DEFAULT_ADDRESS_NAME = "본사 창고";
    public static final String DEFAULT_ZIP_CODE = "06164";
    public static final String DEFAULT_LINE1 = "서울 강남구 역삼로 123";
    public static final String DEFAULT_LINE2 = "5층";
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_ISO_DATE = "2025-01-23T10:30:00+09:00";

    // ===== SearchSellerAddressesApiRequest =====

    public static SearchSellerAddressesApiRequest searchRequest() {
        return new SearchSellerAddressesApiRequest(null, null, null, null, null, null, null);
    }

    public static SearchSellerAddressesApiRequest searchRequest(
            List<Long> sellerIds,
            List<String> addressTypes,
            Boolean defaultAddress,
            String searchField,
            String searchWord,
            Integer page,
            Integer size) {
        return new SearchSellerAddressesApiRequest(
                sellerIds, addressTypes, defaultAddress, searchField, searchWord, page, size);
    }

    public static SearchSellerAddressesApiRequest searchRequestWithSellerIds(List<Long> sellerIds) {
        return new SearchSellerAddressesApiRequest(sellerIds, null, null, null, null, 0, 20);
    }

    // ===== RegisterSellerAddressApiRequest =====

    public static RegisterSellerAddressApiRequest registerRequest() {
        return new RegisterSellerAddressApiRequest(
                DEFAULT_ADDRESS_TYPE, DEFAULT_ADDRESS_NAME, defaultAddressRequest(), false);
    }

    public static RegisterSellerAddressApiRequest.AddressRequest defaultAddressRequest() {
        return new RegisterSellerAddressApiRequest.AddressRequest(
                DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2);
    }

    // ===== UpdateSellerAddressApiRequest =====

    public static UpdateSellerAddressApiRequest updateRequest() {
        return new UpdateSellerAddressApiRequest("물류센터", updateAddressRequest(), true);
    }

    public static UpdateSellerAddressApiRequest.AddressRequest updateAddressRequest() {
        return new UpdateSellerAddressApiRequest.AddressRequest(
                DEFAULT_ZIP_CODE, DEFAULT_LINE1, "6층");
    }

    // ===== SellerAddressResult (Application) =====

    public static SellerAddressResult sellerAddressResult(Long id) {
        return sellerAddressResult(id, 1L, DEFAULT_ADDRESS_TYPE, DEFAULT_ADDRESS_NAME, false);
    }

    public static SellerAddressResult sellerAddressResult(
            Long id,
            Long sellerId,
            String addressType,
            String addressName,
            boolean defaultAddress) {
        return new SellerAddressResult(
                id,
                sellerId,
                addressType,
                addressName,
                new SellerAddressResult.AddressResult(
                        DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2),
                defaultAddress,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<SellerAddressResult> sellerAddressResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> sellerAddressResult((long) i))
                .toList();
    }

    public static SellerAddressPageResult pagedResult(int contentCount, int page, int size) {
        List<SellerAddressResult> content = sellerAddressResults(contentCount);
        return SellerAddressPageResult.of(content, page, size, contentCount);
    }

    public static SellerAddressPageResult emptyPagedResult() {
        return SellerAddressPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== SellerAddressApiResponse =====

    public static SellerAddressApiResponse apiResponse(Long id) {
        return new SellerAddressApiResponse(
                id,
                1L,
                DEFAULT_ADDRESS_TYPE,
                DEFAULT_ADDRESS_NAME,
                new SellerAddressApiResponse.AddressResponse(
                        DEFAULT_ZIP_CODE, DEFAULT_LINE1, DEFAULT_LINE2),
                false,
                DEFAULT_ISO_DATE,
                DEFAULT_ISO_DATE);
    }

    public static List<SellerAddressApiResponse> apiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> apiResponse((long) i)).toList();
    }
}
