package com.ryuqq.marketplace.application.selleraddress.factory;

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

    public SellerAddressSearchCriteria createSearchCriteria(SellerAddressSearchParams params) {
        List<SellerId> sellerIds =
                params.sellerIds() == null
                        ? List.of()
                        : params.sellerIds().stream()
                                .filter(Objects::nonNull)
                                .map(SellerId::of)
                                .toList();
        List<AddressType> addressTypes = parseAddressTypes(params.addressTypes());

        QueryContext<SellerAddressSortKey> queryContext =
                QueryContext.of(
                        SellerAddressSortKey.defaultKey(),
                        SortDirection.defaultDirection(),
                        PageRequest.of(params.page(), params.size()));

        String keyword =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return SellerAddressSearchCriteria.of(
                sellerIds, addressTypes, params.defaultAddress(), keyword, queryContext);
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
