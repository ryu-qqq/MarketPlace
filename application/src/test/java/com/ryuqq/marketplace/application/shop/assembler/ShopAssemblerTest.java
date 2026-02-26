package com.ryuqq.marketplace.application.shop.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopAssembler 단위 테스트")
class ShopAssemblerTest {

    private ShopAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ShopAssembler();
    }

    @Nested
    @DisplayName("toResult() - Shop을 ShopResult로 변환")
    class ToResultTest {

        @Test
        @DisplayName("Shop 도메인을 ShopResult DTO로 변환한다")
        void toResult_Shop_ReturnsShopResult() {
            // given
            Shop shop = createShop(1L, "테스트 외부몰", "test-account-123");

            // when
            ShopResult result = sut.toResult(shop);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(shop.idValue());
            assertThat(result.shopName()).isEqualTo(shop.shopName());
            assertThat(result.accountId()).isEqualTo(shop.accountId());
            assertThat(result.status()).isEqualTo(shop.status().name());
            assertThat(result.createdAt()).isEqualTo(shop.createdAt());
            assertThat(result.updatedAt()).isEqualTo(shop.updatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태의 Shop을 변환한다")
        void toResult_InactiveShop_ReturnsInactiveResult() {
            // given
            Shop shop = createInactiveShop(2L);

            // when
            ShopResult result = sut.toResult(shop);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResults() - Shop 목록을 ShopResult 목록으로 변환")
    class ToResultsTest {

        @Test
        @DisplayName("Shop 목록을 ShopResult 목록으로 변환한다")
        void toResults_ShopList_ReturnsShopResultList() {
            // given
            List<Shop> shops =
                    List.of(
                            createShop(1L, "외부몰1", "account-1"),
                            createShop(2L, "외부몰2", "account-2"),
                            createShop(3L, "외부몰3", "account-3"));

            // when
            List<ShopResult> results = sut.toResults(shops);

            // then
            assertThat(results).hasSize(3);
            assertThat(results.get(0).shopName()).isEqualTo("외부몰1");
            assertThat(results.get(1).shopName()).isEqualTo("외부몰2");
            assertThat(results.get(2).shopName()).isEqualTo("외부몰3");
        }

        @Test
        @DisplayName("빈 Shop 목록을 빈 ShopResult 목록으로 변환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<Shop> emptyShops = Collections.emptyList();

            // when
            List<ShopResult> results = sut.toResults(emptyShops);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - Shop 목록을 ShopPageResult로 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("Shop 목록을 페이징 결과로 변환한다")
        void toPageResult_ShopList_ReturnsPageResult() {
            // given
            List<Shop> shops =
                    List.of(createShop(1L, "외부몰1", "acc-1"), createShop(2L, "외부몰2", "acc-2"));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            ShopPageResult result = sut.toPageResult(shops, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("빈 목록으로 빈 페이징 결과를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<Shop> emptyShops = Collections.emptyList();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            ShopPageResult result = sut.toPageResult(emptyShops, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("두 번째 페이지를 올바르게 변환한다")
        void toPageResult_SecondPage_ReturnsCorrectPageMeta() {
            // given
            List<Shop> shops = List.of(createShop(21L, "외부몰21", "acc-21"));
            int page = 1;
            int size = 20;
            long totalElements = 21L;

            // when
            ShopPageResult result = sut.toPageResult(shops, page, size, totalElements);

            // then
            assertThat(result.pageMeta().page()).isEqualTo(1);
            assertThat(result.pageMeta().totalPages()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(1);
        }
    }

    private Shop createShop(Long shopId, String shopName, String accountId) {
        Instant now = Instant.now();
        return Shop.reconstitute(
                ShopId.of(shopId), 1L, shopName, accountId, ShopStatus.ACTIVE, null, now, now);
    }

    private Shop createInactiveShop(Long shopId) {
        Instant now = Instant.now();
        return Shop.reconstitute(
                ShopId.of(shopId),
                1L,
                "비활성 외부몰",
                "inactive-account",
                ShopStatus.INACTIVE,
                null,
                now,
                now);
    }
}
