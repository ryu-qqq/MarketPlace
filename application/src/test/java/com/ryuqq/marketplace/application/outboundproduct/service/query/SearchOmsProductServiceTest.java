package com.ryuqq.marketplace.application.outboundproduct.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsProductQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.manager.OmsProductCompositionReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
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
@DisplayName("SearchOmsProductService 단위 테스트")
class SearchOmsProductServiceTest {

    @InjectMocks private SearchOmsProductService sut;

    @Mock private OmsProductCompositionReadManager compositionReadManager;
    @Mock private OmsProductQueryFactory queryFactory;

    @Nested
    @DisplayName("execute() - OMS 상품 목록 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 OMS 상품 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            OmsProductSearchParams params = OmsProductQueryFixtures.omsProductSearchParams(0, 20);
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            List<OmsProductListResult> results =
                    List.of(
                            OmsProductQueryFixtures.omsProductListResult(1L),
                            OmsProductQueryFixtures.omsProductListResult(2L));
            long totalElements = 2L;

            OmsProductPageResult expected =
                    OmsProductPageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(2L);
            then(queryFactory).should().createCriteria(params);
            then(compositionReadManager).should().findByCriteria(criteria);
            then(compositionReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            OmsProductSearchParams params = OmsProductQueryFixtures.omsProductSearchParams(0, 20);
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            List<OmsProductListResult> emptyResults = Collections.emptyList();
            long totalElements = 0L;

            OmsProductPageResult expected =
                    OmsProductPageResult.of(
                            emptyResults, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(emptyResults);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("상품 상태 필터가 적용된 검색을 수행한다")
        void execute_WithStatusFilter_FiltersResults() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(List.of("ACTIVE"));
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            List<OmsProductListResult> results =
                    List.of(
                            OmsProductQueryFixtures.omsProductListResult(
                                    1L, "COMPLETED", "ACTIVE"));
            long totalElements = 1L;

            OmsProductPageResult expected =
                    OmsProductPageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("연동 상태 필터와 상품 상태 필터를 함께 적용할 수 있다")
        void execute_WithStatusAndSyncStatusFilter_FiltersResults() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(
                            List.of("ACTIVE"), List.of("FAILED"));
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            List<OmsProductListResult> results =
                    List.of(OmsProductQueryFixtures.omsProductListResult(1L, "FAILED", "ACTIVE"));
            long totalElements = 1L;

            OmsProductPageResult expected =
                    OmsProductPageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.results().get(0).syncStatus()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("검색어로 OMS 상품을 검색할 수 있다")
        void execute_WithSearchWord_SearchesProducts() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithSearch("productName", "테스트");
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            List<OmsProductListResult> results =
                    List.of(OmsProductQueryFixtures.omsProductListResult(1L));
            long totalElements = 1L;

            OmsProductPageResult expected =
                    OmsProductPageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("쇼핑몰 ID 필터로 OMS 상품을 검색할 수 있다")
        void execute_WithShopFilter_FiltersResults() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithShops(List.of(1L, 2L));
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            List<OmsProductListResult> results =
                    List.of(
                            OmsProductQueryFixtures.omsProductListResult(1L),
                            OmsProductQueryFixtures.omsProductListResult(2L));
            long totalElements = 2L;

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            OmsProductPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(2);
        }
    }
}
