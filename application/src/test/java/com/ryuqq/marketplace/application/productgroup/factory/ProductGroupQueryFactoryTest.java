package com.ryuqq.marketplace.application.productgroup.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.productgroup.ProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSortKey;
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
@DisplayName("ProductGroupQueryFactory 단위 테스트")
class ProductGroupQueryFactoryTest {

    @InjectMocks private ProductGroupQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 Criteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("기본 SearchParams로 ProductGroupSearchCriteria를 생성한다")
        void createCriteria_DefaultParams_ReturnsCriteria() {
            // given
            ProductGroupSearchParams params = ProductGroupQueryFixtures.searchParams();
            QueryContext<ProductGroupSortKey> queryContext =
                    QueryContext.defaultOf(ProductGroupSortKey.defaultKey());

            given(commonVoFactory.parseSortDirection("DESC")).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(
                            commonVoFactory.createQueryContext(
                                    ProductGroupSortKey.defaultKey(),
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false))
                    .willReturn(queryContext);

            // when
            ProductGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).isEmpty();
            assertThat(result.sellerIds()).isEmpty();
            assertThat(result.brandIds()).isEmpty();
            assertThat(result.categoryIds()).isEmpty();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("상태 필터가 있는 SearchParams로 Criteria를 생성한다")
        void createCriteria_WithStatusFilter_ReturnsCriteriaWithStatuses() {
            // given
            ProductGroupSearchParams params =
                    ProductGroupQueryFixtures.searchParams(List.of("ACTIVE", "DRAFT"));
            QueryContext<ProductGroupSortKey> queryContext =
                    QueryContext.defaultOf(ProductGroupSortKey.defaultKey());

            given(commonVoFactory.parseSortDirection("DESC")).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(
                            commonVoFactory.createQueryContext(
                                    ProductGroupSortKey.defaultKey(),
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false))
                    .willReturn(queryContext);

            // when
            ProductGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(2);
        }

        @Test
        @DisplayName("검색어가 있는 SearchParams로 Criteria를 생성한다")
        void createCriteria_WithSearchWord_ReturnsCriteriaWithSearchWord() {
            // given
            String searchWord = "테스트 상품";
            ProductGroupSearchParams params =
                    ProductGroupQueryFixtures.searchParamsWithSearchWord(searchWord);
            QueryContext<ProductGroupSortKey> queryContext =
                    QueryContext.defaultOf(ProductGroupSortKey.defaultKey());

            given(commonVoFactory.parseSortDirection("DESC")).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(
                            commonVoFactory.createQueryContext(
                                    ProductGroupSortKey.defaultKey(),
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false))
                    .willReturn(queryContext);

            // when
            ProductGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo(searchWord);
        }
    }
}
