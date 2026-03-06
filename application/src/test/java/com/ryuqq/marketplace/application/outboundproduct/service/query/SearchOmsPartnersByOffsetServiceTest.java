package com.ryuqq.marketplace.application.outboundproduct.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsPartnerQueryFactory;
import com.ryuqq.marketplace.application.seller.assembler.SellerAssembler;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.Collections;
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
@DisplayName("SearchOmsPartnersByOffsetService 단위 테스트")
class SearchOmsPartnersByOffsetServiceTest {

    @InjectMocks private SearchOmsPartnersByOffsetService sut;

    @Mock private SellerReadManager readManager;
    @Mock private OmsPartnerQueryFactory queryFactory;
    @Mock private SellerAssembler assembler;

    @Nested
    @DisplayName("execute() - OMS 파트너(셀러) 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 파트너 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedPartners() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams(0, 20);
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            List<Seller> sellers =
                    List.of(SellerFixtures.activeSeller(1L), SellerFixtures.activeSeller(2L));
            long totalElements = 2L;

            SellerResult result1 = createSellerResult(1L, "파트너1");
            SellerResult result2 = createSellerResult(2L, "파트너2");
            SellerPageResult expected =
                    SellerPageResult.of(
                            List.of(result1, result2), totalElements, params.page(), params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(sellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(sellers, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.content()).hasSize(2);
            assertThat(result.totalCount()).isEqualTo(2L);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(sellers, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams(0, 20);
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            List<Seller> emptySellers = Collections.emptyList();
            long totalElements = 0L;

            SellerPageResult expected =
                    SellerPageResult.of(List.of(), totalElements, params.page(), params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptySellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(emptySellers, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalCount()).isZero();
        }

        @Test
        @DisplayName("키워드로 파트너를 검색할 수 있다")
        void execute_WithKeyword_SearchesPartners() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams("테스트");
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            List<Seller> sellers = List.of(SellerFixtures.activeSeller(1L));
            long totalElements = 1L;

            SellerPageResult expected =
                    SellerPageResult.of(
                            List.of(createSellerResult(1L, "테스트 파트너")),
                            totalElements,
                            params.page(),
                            params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(sellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(sellers, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("키워드가 없으면 전체 파트너를 검색한다")
        void execute_WithoutKeyword_ReturnsAllPartners() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams();
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();

            List<Seller> sellers =
                    List.of(
                            SellerFixtures.activeSeller(1L),
                            SellerFixtures.activeSeller(2L),
                            SellerFixtures.activeSeller(3L));
            long totalElements = 3L;

            SellerPageResult expected =
                    SellerPageResult.of(
                            List.of(
                                    createSellerResult(1L, "파트너1"),
                                    createSellerResult(2L, "파트너2"),
                                    createSellerResult(3L, "파트너3")),
                            totalElements,
                            params.page(),
                            params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(sellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(sellers, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).hasSize(3);
        }
    }

    private SellerResult createSellerResult(Long id, String sellerName) {
        return new SellerResult(id, sellerName, sellerName + " 스토어", null, null, true, null, null);
    }
}
