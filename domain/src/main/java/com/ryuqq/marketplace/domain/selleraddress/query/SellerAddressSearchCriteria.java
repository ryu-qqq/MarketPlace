package com.ryuqq.marketplace.domain.selleraddress.query;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;

/**
 * SellerAddress 검색 조건 Criteria.
 *
 * @param sellerIds 셀러 ID 목록 (필수, 1건 이상)
 * @param addressTypes 주소 유형 필터 (null/empty면 전체)
 * @param defaultAddress 기본 주소 필터 (null이면 전체)
 * @param keyword 검색어 (null이면 전체)
 * @param queryContext 정렬 및 페이징 정보
 */
public record SellerAddressSearchCriteria(
        List<SellerId> sellerIds,
        List<AddressType> addressTypes,
        Boolean defaultAddress,
        String keyword,
        QueryContext<SellerAddressSortKey> queryContext) {

    public SellerAddressSearchCriteria {
        if (sellerIds == null || sellerIds.isEmpty()) {
            throw new IllegalArgumentException("sellerIds는 1건 이상 필수입니다");
        }
        sellerIds = List.copyOf(sellerIds);
        addressTypes = addressTypes == null ? null : List.copyOf(addressTypes);
    }

    public static SellerAddressSearchCriteria of(
            List<SellerId> sellerIds,
            List<AddressType> addressTypes,
            Boolean defaultAddress,
            String keyword,
            QueryContext<SellerAddressSortKey> queryContext) {
        return new SellerAddressSearchCriteria(
                sellerIds, addressTypes, defaultAddress, keyword, queryContext);
    }

    public static SellerAddressSearchCriteria bySellerId(SellerId sellerId) {
        return new SellerAddressSearchCriteria(
                List.of(sellerId),
                null,
                null,
                null,
                QueryContext.defaultOf(SellerAddressSortKey.defaultKey()));
    }

    public List<Long> sellerIdValues() {
        return sellerIds.stream().map(SellerId::value).toList();
    }

    public boolean hasAddressTypesFilter() {
        return addressTypes != null && !addressTypes.isEmpty();
    }

    public List<String> addressTypeNames() {
        return addressTypes == null
                ? List.of()
                : addressTypes.stream().map(AddressType::name).toList();
    }

    public boolean hasDefaultFilter() {
        return defaultAddress != null;
    }

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }

    public int size() {
        return queryContext.size();
    }

    public long offset() {
        return queryContext.offset();
    }

    public int page() {
        return queryContext.page();
    }
}
