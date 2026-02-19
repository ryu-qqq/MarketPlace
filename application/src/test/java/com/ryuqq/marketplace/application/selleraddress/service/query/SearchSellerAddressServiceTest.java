package com.ryuqq.marketplace.application.selleraddress.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleraddress.SellerAddressQueryFixtures;
import com.ryuqq.marketplace.application.selleraddress.assembler.SellerAddressAssembler;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressQueryFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSortKey;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchSellerAddressService 단위 테스트")
class SearchSellerAddressServiceTest {

    @InjectMocks private SearchSellerAddressService sut;

    @Mock private SellerAddressQueryFactory queryFactory;
    @Mock private SellerAddressReadManager readManager;
    @Mock private SellerAddressAssembler assembler;

    @Nested
    @DisplayName("execute() - 셀러 주소 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 셀러 주소를 검색한다")
        void execute_SearchesSellerAddresses() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchParams params = SellerAddressQueryFixtures.searchParams(sellerId);
            SellerAddressSearchCriteria criteria =
                    SellerAddressSearchCriteria.of(
                            List.of(SellerId.of(sellerId)),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAddressSortKey.defaultKey()));
            List<SellerAddress> addresses =
                    List.of(SellerAddressFixtures.defaultShippingAddress(1L, sellerId));
            long totalCount = 1L;
            SellerAddressPageResult expectedResult =
                    SellerAddressQueryFixtures.sellerAddressPageResult();

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.search(criteria)).willReturn(addresses);
            given(readManager.count(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(addresses, 0, 20, totalCount)).willReturn(expectedResult);

            // when
            SellerAddressPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createSearchCriteria(params);
            then(readManager).should().search(criteria);
            then(readManager).should().count(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지 결과를 반환한다")
        void execute_NoResults_ReturnsEmptyPageResult() {
            // given
            Long sellerId = 1L;
            SellerAddressSearchParams params = SellerAddressQueryFixtures.searchParams(sellerId);
            SellerAddressSearchCriteria criteria =
                    SellerAddressSearchCriteria.of(
                            List.of(SellerId.of(sellerId)),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAddressSortKey.defaultKey()));
            List<SellerAddress> addresses = List.of();
            long totalCount = 0L;
            SellerAddressPageResult expectedResult = SellerAddressQueryFixtures.emptyPageResult();

            given(queryFactory.createSearchCriteria(params)).willReturn(criteria);
            given(readManager.search(criteria)).willReturn(addresses);
            given(readManager.count(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(addresses, 0, 20, totalCount)).willReturn(expectedResult);

            // when
            SellerAddressPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.content()).isEmpty();
        }
    }
}
