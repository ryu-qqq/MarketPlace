package com.ryuqq.marketplace.application.selleraddress.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSortKey;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** SellerAddress Query Factory. */
@Component
public class SellerAddressQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SellerAddressQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public SellerAddressSearchCriteria createSearchCriteria(SellerAddressSearchParams params) {
        List<SellerId> sellerIds =
                params.sellerIds() == null
                        ? List.of()
                        : params.sellerIds().stream()
                                .filter(Objects::nonNull)
                                .map(SellerId::of)
                                .toList();
        List<AddressType> addressTypes = parseAddressTypes(params.addressTypes());

        SellerAddressSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<SellerAddressSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        String keyword =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return SellerAddressSearchCriteria.of(
                sellerIds, addressTypes, params.defaultAddress(), keyword, queryContext);
    }

    private SellerAddressSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SellerAddressSortKey.defaultKey();
        }
        for (SellerAddressSortKey key : SellerAddressSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return SellerAddressSortKey.defaultKey();
    }

    private List<AddressType> parseAddressTypes(List<String> addressTypes) {
        if (addressTypes == null || addressTypes.isEmpty()) {
            return Collections.emptyList();
        }
        return addressTypes.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> AddressType.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
