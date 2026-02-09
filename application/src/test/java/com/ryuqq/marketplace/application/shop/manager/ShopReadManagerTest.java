package com.ryuqq.marketplace.application.shop.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.port.out.query.ShopQueryPort;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopNotFoundException;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ShopReadManager 단위 테스트")
class ShopReadManagerTest {

    @InjectMocks private ShopReadManager sut;

    @Mock private ShopQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 Shop 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Shop을 반환한다")
        void getById_Exists_ReturnsShop() {
            // given
            ShopId id = ShopId.of(1L);
            Shop expected = createShop(1L);

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Shop result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            ShopId id = ShopId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ShopNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 Shop 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 Shop 목록을 반환한다")
        void findByCriteria_ReturnsShops() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            List<Shop> expected = List.of(createShop(1L), createShop(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<Shop> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 Shop 개수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 Shop 개수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            ShopSearchCriteria criteria = ShopSearchCriteria.defaultCriteria();
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("existsByShopName() - Shop명 존재 여부 확인")
    class ExistsByShopNameTest {

        @Test
        @DisplayName("Shop명이 존재하면 true를 반환한다")
        void existsByShopName_Exists_ReturnsTrue() {
            // given
            String shopName = "기존 외부몰";

            given(queryPort.existsByShopName(shopName)).willReturn(true);

            // when
            boolean result = sut.existsByShopName(shopName);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().existsByShopName(shopName);
        }

        @Test
        @DisplayName("Shop명이 존재하지 않으면 false를 반환한다")
        void existsByShopName_NotExists_ReturnsFalse() {
            // given
            String shopName = "신규 외부몰";

            given(queryPort.existsByShopName(shopName)).willReturn(false);

            // when
            boolean result = sut.existsByShopName(shopName);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByShopNameExcluding() - Shop명 존재 여부 확인 (자기 제외)")
    class ExistsByShopNameExcludingTest {

        @Test
        @DisplayName("자기 제외 시 다른 Shop명이 존재하면 true를 반환한다")
        void existsByShopNameExcluding_OtherExists_ReturnsTrue() {
            // given
            String shopName = "중복 외부몰";
            ShopId excludeId = ShopId.of(1L);

            given(queryPort.existsByShopNameExcluding(shopName, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsByShopNameExcluding(shopName, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("자기 제외 시 다른 Shop명이 없으면 false를 반환한다")
        void existsByShopNameExcluding_OnlySelf_ReturnsFalse() {
            // given
            String shopName = "내 외부몰";
            ShopId excludeId = ShopId.of(1L);

            given(queryPort.existsByShopNameExcluding(shopName, excludeId)).willReturn(false);

            // when
            boolean result = sut.existsByShopNameExcluding(shopName, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByAccountId() - 계정ID 존재 여부 확인")
    class ExistsByAccountIdTest {

        @Test
        @DisplayName("계정ID가 존재하면 true를 반환한다")
        void existsByAccountId_Exists_ReturnsTrue() {
            // given
            String accountId = "existing-account-123";

            given(queryPort.existsByAccountId(accountId)).willReturn(true);

            // when
            boolean result = sut.existsByAccountId(accountId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("계정ID가 존재하지 않으면 false를 반환한다")
        void existsByAccountId_NotExists_ReturnsFalse() {
            // given
            String accountId = "new-account-999";

            given(queryPort.existsByAccountId(accountId)).willReturn(false);

            // when
            boolean result = sut.existsByAccountId(accountId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByAccountIdExcluding() - 계정ID 존재 여부 확인 (자기 제외)")
    class ExistsByAccountIdExcludingTest {

        @Test
        @DisplayName("자기 제외 시 다른 계정ID가 존재하면 true를 반환한다")
        void existsByAccountIdExcluding_OtherExists_ReturnsTrue() {
            // given
            String accountId = "duplicate-account-456";
            ShopId excludeId = ShopId.of(1L);

            given(queryPort.existsByAccountIdExcluding(accountId, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsByAccountIdExcluding(accountId, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("자기 제외 시 다른 계정ID가 없으면 false를 반환한다")
        void existsByAccountIdExcluding_OnlySelf_ReturnsFalse() {
            // given
            String accountId = "my-account-789";
            ShopId excludeId = ShopId.of(1L);

            given(queryPort.existsByAccountIdExcluding(accountId, excludeId)).willReturn(false);

            // when
            boolean result = sut.existsByAccountIdExcluding(accountId, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    private Shop createShop(Long shopId) {
        Instant now = Instant.now();
        return Shop.reconstitute(
                ShopId.of(shopId),
                "테스트 외부몰",
                "test-account-" + shopId,
                ShopStatus.ACTIVE,
                null,
                now,
                now);
    }
}
