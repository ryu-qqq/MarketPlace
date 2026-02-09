package com.ryuqq.marketplace.application.shop.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shop.ShopQueryFixtures;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchField;
import com.ryuqq.marketplace.domain.shop.query.ShopSortKey;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShopQueryFactory 단위 테스트")
class ShopQueryFactoryTest {

    private ShopQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @BeforeEach
    void setUp() {
        sut = new ShopQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("createCriteria() - ShopSearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("ShopSearchParams로 ShopSearchCriteria를 생성한다")
        void createCriteria_FromParams_CreatesCriteria() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(0, 20);
            SortDirection direction = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(ShopSortKey.defaultKey(), direction, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection())).willReturn(direction);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    ShopSortKey.defaultKey(), direction, pageRequest, false))
                    .willReturn(queryContext);

            // when
            ShopSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isEqualTo(queryContext);
            then(commonVoFactory).should().parseSortDirection(params.sortDirection());
            then(commonVoFactory).should().createPageRequest(params.page(), params.size());
        }

        @Test
        @DisplayName("상태 필터를 Criteria에 반영한다")
        void createCriteria_WithStatuses_ReflectsStatuses() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(List.of("ACTIVE", "INACTIVE"));
            SortDirection direction = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(ShopSortKey.defaultKey(), direction, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection())).willReturn(direction);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    ShopSortKey.defaultKey(), direction, pageRequest, false))
                    .willReturn(queryContext);

            // when
            ShopSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses())
                    .containsExactlyInAnyOrder(ShopStatus.ACTIVE, ShopStatus.INACTIVE);
        }

        @Test
        @DisplayName("검색 필드와 검색어를 Criteria에 반영한다")
        void createCriteria_WithSearchFieldAndWord_ReflectsSearch() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams("shopName", "테스트");
            SortDirection direction = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<ShopSortKey> queryContext =
                    QueryContext.of(ShopSortKey.defaultKey(), direction, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection())).willReturn(direction);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    ShopSortKey.defaultKey(), direction, pageRequest, false))
                    .willReturn(queryContext);

            // when
            ShopSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(ShopSearchField.SHOP_NAME);
            assertThat(result.searchWord()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("정렬 키가 없으면 기본 정렬 키를 사용한다")
        void createCriteria_NullSortKey_UsesDefaultKey() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(0, 20);
            SortDirection direction = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);

            given(commonVoFactory.parseSortDirection(params.sortDirection())).willReturn(direction);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            sut.createCriteria(params);

            // then
            then(commonVoFactory)
                    .should()
                    .createQueryContext(ShopSortKey.defaultKey(), direction, pageRequest, false);
        }
    }
}
