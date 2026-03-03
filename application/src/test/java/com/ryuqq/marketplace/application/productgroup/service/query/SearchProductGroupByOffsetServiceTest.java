package com.ryuqq.marketplace.application.productgroup.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.productgroup.ProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupQueryFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
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
@DisplayName("SearchProductGroupByOffsetService 단위 테스트")
class SearchProductGroupByOffsetServiceTest {

    @InjectMocks private SearchProductGroupByOffsetService sut;

    @Mock private ProductGroupReadFacade readFacade;
    @Mock private ProductGroupQueryFactory queryFactory;
    @Mock private ProductGroupAssembler assembler;
    @Mock private CategoryReadManager categoryReadManager;

    @Nested
    @DisplayName("execute() - 상품 그룹 목록 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 상품 그룹 목록을 조회하고 PageResult를 반환한다")
        void execute_ValidSearchParams_ReturnsPageResult() {
            // given
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams();
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            ProductGroupPageResult expected = ProductGroupPageResult.empty(20);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getListBundle(criteria)).willReturn(bundle);
            given(assembler.toPageResult(bundle, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            ProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(expected);
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().getListBundle(criteria);
            then(assembler).should().toPageResult(bundle, criteria.page(), criteria.size());
        }

        @Test
        @DisplayName("셀러 ID 필터로 검색하면 해당 셀러의 상품 그룹을 반환한다")
        void execute_WithSellerFilter_ReturnsFilteredPageResult() {
            // given
            Long sellerId = 1L;
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams(sellerId);
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            ProductGroupPageResult expected = ProductGroupPageResult.empty(20);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getListBundle(criteria)).willReturn(bundle);
            given(assembler.toPageResult(bundle, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            ProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().getListBundle(criteria);
        }

        @Test
        @DisplayName("상태 필터로 검색하면 해당 상태의 상품 그룹을 반환한다")
        void execute_WithStatusFilter_ReturnsFilteredPageResult() {
            // given
            ProductGroupSearchParams params =
                    ProductGroupQueryFixtures.searchParams(List.of("ACTIVE"));
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            ProductGroupPageResult expected = ProductGroupPageResult.empty(20);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getListBundle(criteria)).willReturn(bundle);
            given(assembler.toPageResult(bundle, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            ProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("카테고리 ID 필터가 있으면 하위 카테고리까지 확장하여 검색한다")
        void execute_WithCategoryFilter_ExpandsDescendantCategories() {
            // given
            List<Long> originalCategoryIds = List.of(1L);
            List<Long> expandedCategoryIds = List.of(1L, 10L, 20L, 30L);
            ProductGroupSearchParams params =
                    ProductGroupQueryFixtures.searchParamsWithCategoryIds(originalCategoryIds);
            ProductGroupSearchParams expandedParams = params.withCategoryIds(expandedCategoryIds);
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            ProductGroupPageResult expected = ProductGroupPageResult.empty(20);

            given(categoryReadManager.expandWithDescendants(originalCategoryIds))
                    .willReturn(expandedCategoryIds);
            given(queryFactory.createCriteria(expandedParams)).willReturn(criteria);
            given(readFacade.getListBundle(criteria)).willReturn(bundle);
            given(assembler.toPageResult(bundle, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            ProductGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(categoryReadManager).should().expandWithDescendants(originalCategoryIds);
            then(queryFactory).should().createCriteria(expandedParams);
        }

        @Test
        @DisplayName("카테고리 ID가 없으면 확장 없이 그대로 검색한다")
        void execute_WithoutCategoryFilter_SkipsExpansion() {
            // given
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams();
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            ProductGroupPageResult expected = ProductGroupPageResult.empty(20);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.getListBundle(criteria)).willReturn(bundle);
            given(assembler.toPageResult(bundle, criteria.page(), criteria.size()))
                    .willReturn(expected);

            // when
            sut.execute(params);

            // then
            then(categoryReadManager).shouldHaveNoInteractions();
        }
    }
}
