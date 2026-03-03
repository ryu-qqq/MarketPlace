package com.ryuqq.marketplace.application.outboundproduct.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsShopQueryFactory;
import com.ryuqq.marketplace.application.shop.ShopQueryFixtures;
import com.ryuqq.marketplace.application.shop.assembler.ShopAssembler;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
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
@DisplayName("SearchOmsShopsByOffsetService 단위 테스트")
class SearchOmsShopsByOffsetServiceTest {

    @InjectMocks private SearchOmsShopsByOffsetService sut;

    @Mock private ShopReadManager readManager;
    @Mock private OmsShopQueryFactory queryFactory;
    @Mock private ShopAssembler assembler;

    @Nested
    @DisplayName("execute() - OMS 쇼핑몰 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 쇼핑몰 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedShops() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams(0, 20);
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            List<Shop> shops = List.of(createShop(1L, "쇼핑몰1"), createShop(2L, "쇼핑몰2"));
            long totalElements = 2L;

            ShopPageResult expected =
                    ShopPageResult.of(
                            List.of(
                                    ShopQueryFixtures.shopResult(1L, "쇼핑몰1"),
                                    ShopQueryFixtures.shopResult(2L, "쇼핑몰2")),
                            params.page(),
                            params.size(),
                            totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(shops);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(shops, params.page(), params.size(), totalElements))
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
            then(assembler)
                    .should()
                    .toPageResult(shops, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams(0, 20);
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            List<Shop> emptyShops = Collections.emptyList();
            long totalElements = 0L;

            ShopPageResult expected = ShopQueryFixtures.emptyShopPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyShops);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(emptyShops, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("키워드로 쇼핑몰을 검색할 수 있다")
        void execute_WithKeyword_SearchesShops() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams("네이버");
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            List<Shop> shops = List.of(createShop(1L, "네이버 스마트스토어"));
            long totalElements = 1L;

            ShopPageResult expected =
                    ShopPageResult.of(
                            List.of(ShopQueryFixtures.shopResult(1L, "네이버 스마트스토어")),
                            params.page(),
                            params.size(),
                            totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(shops);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(shops, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("키워드가 없으면 전체 쇼핑몰을 검색한다")
        void execute_WithoutKeyword_ReturnsAllShops() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams();
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();

            List<Shop> shops =
                    List.of(createShop(1L, "쇼핑몰1"), createShop(2L, "쇼핑몰2"), createShop(3L, "쇼핑몰3"));
            long totalElements = 3L;

            ShopPageResult expected =
                    ShopPageResult.of(
                            List.of(
                                    ShopQueryFixtures.shopResult(1L, "쇼핑몰1"),
                                    ShopQueryFixtures.shopResult(2L, "쇼핑몰2"),
                                    ShopQueryFixtures.shopResult(3L, "쇼핑몰3")),
                            params.page(),
                            params.size(),
                            totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(shops);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(shops, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            ShopPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(3);
        }
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
