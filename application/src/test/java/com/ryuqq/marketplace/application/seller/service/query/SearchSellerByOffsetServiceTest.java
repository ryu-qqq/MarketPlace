package com.ryuqq.marketplace.application.seller.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.SellerQueryFixtures;
import com.ryuqq.marketplace.application.seller.assembler.SellerAssembler;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import com.ryuqq.marketplace.application.seller.factory.SellerQueryFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.time.Instant;
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
@DisplayName("SearchSellerByOffsetService 단위 테스트")
class SearchSellerByOffsetServiceTest {

    @InjectMocks private SearchSellerByOffsetService sut;

    @Mock private SellerReadManager readManager;
    @Mock private SellerQueryFactory queryFactory;
    @Mock private SellerAssembler assembler;

    @Nested
    @DisplayName("execute() - 셀러 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 셀러 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            SellerSearchParams params = SellerQueryFixtures.searchParams(0, 20);
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();
            List<Seller> sellers =
                    List.of(SellerFixtures.activeSeller(1L), SellerFixtures.activeSeller(2L));
            long totalCount = 2L;

            List<SellerResult> sellerResults =
                    List.of(createSellerResult(1L, "셀러1"), createSellerResult(2L, "셀러2"));
            SellerPageResult expected =
                    SellerPageResult.of(sellerResults, totalCount, params.page(), params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(sellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(sellers, params.page(), params.size(), totalCount))
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
                    .toPageResult(sellers, params.page(), params.size(), totalCount);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            SellerSearchParams params = SellerQueryFixtures.searchParams(0, 20);
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();
            List<Seller> emptySellers = Collections.emptyList();
            long totalCount = 0L;

            SellerPageResult expected =
                    SellerPageResult.of(
                            Collections.emptyList(), totalCount, params.page(), params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptySellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(emptySellers, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalCount()).isZero();
        }

        @Test
        @DisplayName("활성화 필터가 적용된 검색을 수행한다")
        void execute_WithActiveFilter_FiltersResults() {
            // given
            SellerSearchParams params = SellerQueryFixtures.searchParams(true);
            SellerSearchCriteria criteria = SellerSearchCriteria.defaultCriteria();
            List<Seller> activeSellers = List.of(SellerFixtures.activeSeller(1L));
            long totalCount = 1L;

            SellerPageResult expected =
                    SellerPageResult.of(
                            List.of(createSellerResult(1L, "활성 셀러")),
                            totalCount,
                            params.page(),
                            params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(activeSellers);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(activeSellers, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            SellerPageResult result = sut.execute(params);

            // then
            assertThat(result.content()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        private SellerResult createSellerResult(Long sellerId, String sellerName) {
            Instant now = Instant.now();
            return new SellerResult(
                    sellerId, sellerName, "테스트 스토어", "http://logo.png", "테스트 설명", true, now, now);
        }
    }
}
