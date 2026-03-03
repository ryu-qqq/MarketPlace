package com.ryuqq.marketplace.application.shop.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.ShopQueryFixtures;
import com.ryuqq.marketplace.application.shop.assembler.ShopAssembler;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import com.ryuqq.marketplace.application.shop.factory.ShopQueryFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
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
@DisplayName("SearchShopByOffsetService 단위 테스트")
class SearchShopByOffsetServiceTest {

    @InjectMocks private SearchShopByOffsetService sut;

    @Mock private ShopReadManager readManager;
    @Mock private ShopQueryFactory queryFactory;
    @Mock private ShopAssembler assembler;

    @Nested
    @DisplayName("execute() - Shop 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 Shop 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(0, 20);
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            List<Shop> shops = List.of(createShop(1L, "외부몰1"), createShop(2L, "외부몰2"));
            long totalCount = 2L;

            List<ShopResult> shopResults =
                    List.of(
                            ShopQueryFixtures.shopResult(1L, "외부몰1"),
                            ShopQueryFixtures.shopResult(2L, "외부몰2"));
            ShopPageResult expected =
                    ShopPageResult.of(shopResults, params.page(), params.size(), totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(shops);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(shops, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(2L);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler).should().toPageResult(shops, params.page(), params.size(), totalCount);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(0, 20);
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            List<Shop> emptyShops = Collections.emptyList();
            long totalCount = 0L;

            ShopPageResult expected = ShopQueryFixtures.emptyShopPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyShops);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(emptyShops, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 적용된 검색을 수행한다")
        void execute_WithStatusFilter_FiltersResults() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams(List.of("ACTIVE"));
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            List<Shop> activeShops = List.of(createShop(1L, "활성 외부몰"));
            long totalCount = 1L;

            ShopPageResult expected =
                    ShopPageResult.of(
                            List.of(ShopQueryFixtures.shopResult(1L, "활성 외부몰")),
                            params.page(),
                            params.size(),
                            totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(activeShops);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(activeShops, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("검색어로 Shop을 검색할 수 있다")
        void execute_WithSearchWord_SearchesShops() {
            // given
            ShopSearchParams params = ShopQueryFixtures.searchParams("shopName", "테스트");
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            List<Shop> searchedShops = List.of(createShop(1L, "테스트외부몰"));
            long totalCount = 1L;

            ShopPageResult expected =
                    ShopPageResult.of(
                            List.of(ShopQueryFixtures.shopResult(1L, "테스트외부몰")),
                            params.page(),
                            params.size(),
                            totalCount);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(searchedShops);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toPageResult(searchedShops, params.page(), params.size(), totalCount))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
        }

        private Shop createShop(Long shopId, String shopName) {
            Instant now = Instant.now();
            return Shop.reconstitute(
                    ShopId.of(shopId),
                    1L,
                    shopName,
                    "test-account-" + shopId,
                    ShopStatus.ACTIVE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    now,
                    now);
        }
    }
}
