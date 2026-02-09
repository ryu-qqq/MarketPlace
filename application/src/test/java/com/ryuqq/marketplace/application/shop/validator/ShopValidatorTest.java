package com.ryuqq.marketplace.application.shop.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopAccountIdDuplicateException;
import com.ryuqq.marketplace.domain.shop.exception.ShopNameDuplicateException;
import com.ryuqq.marketplace.domain.shop.exception.ShopNotFoundException;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
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
@DisplayName("ShopValidator 단위 테스트")
class ShopValidatorTest {

    @InjectMocks private ShopValidator sut;

    @Mock private ShopReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 Shop 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 Shop을 반환한다")
        void findExistingOrThrow_Exists_ReturnsShop() {
            // given
            ShopId id = ShopId.of(1L);
            Shop expected = createShop(1L);

            given(readManager.getById(id)).willReturn(expected);

            // when
            Shop result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            ShopId id = ShopId.of(999L);

            given(readManager.getById(id)).willThrow(new ShopNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(ShopNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("validateShopNameNotDuplicate() - Shop명 중복 검증")
    class ValidateShopNameNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 Shop명은 예외 없이 통과한다")
        void validateShopNameNotDuplicate_NoDuplicate_NoException() {
            // given
            String shopName = "신규 외부몰";

            given(readManager.existsByShopName(shopName)).willReturn(false);

            // when & then (no exception)
            sut.validateShopNameNotDuplicate(shopName);

            then(readManager).should().existsByShopName(shopName);
        }

        @Test
        @DisplayName("중복된 Shop명이면 예외가 발생한다")
        void validateShopNameNotDuplicate_Duplicate_ThrowsException() {
            // given
            String shopName = "중복 외부몰";

            given(readManager.existsByShopName(shopName)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateShopNameNotDuplicate(shopName))
                    .isInstanceOf(ShopNameDuplicateException.class)
                    .hasMessageContaining(shopName);
        }
    }

    @Nested
    @DisplayName("validateShopNameNotDuplicateExcluding() - Shop명 중복 검증 (자기 제외)")
    class ValidateShopNameNotDuplicateExcludingTest {

        @Test
        @DisplayName("자기 제외 시 중복되지 않으면 예외 없이 통과한다")
        void validateShopNameNotDuplicateExcluding_NoDuplicate_NoException() {
            // given
            String shopName = "수정된 외부몰";
            ShopId excludeId = ShopId.of(1L);

            given(readManager.existsByShopNameExcluding(shopName, excludeId)).willReturn(false);

            // when & then (no exception)
            sut.validateShopNameNotDuplicateExcluding(shopName, excludeId);

            then(readManager).should().existsByShopNameExcluding(shopName, excludeId);
        }

        @Test
        @DisplayName("자기 제외 시 중복되면 예외가 발생한다")
        void validateShopNameNotDuplicateExcluding_Duplicate_ThrowsException() {
            // given
            String shopName = "중복 외부몰";
            ShopId excludeId = ShopId.of(1L);

            given(readManager.existsByShopNameExcluding(shopName, excludeId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateShopNameNotDuplicateExcluding(shopName, excludeId))
                    .isInstanceOf(ShopNameDuplicateException.class)
                    .hasMessageContaining(shopName);
        }
    }

    @Nested
    @DisplayName("validateAccountIdNotDuplicate() - 계정ID 중복 검증")
    class ValidateAccountIdNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 계정ID는 예외 없이 통과한다")
        void validateAccountIdNotDuplicate_NoDuplicate_NoException() {
            // given
            String accountId = "new-account-123";

            given(readManager.existsByAccountId(accountId)).willReturn(false);

            // when & then (no exception)
            sut.validateAccountIdNotDuplicate(accountId);

            then(readManager).should().existsByAccountId(accountId);
        }

        @Test
        @DisplayName("중복된 계정ID이면 예외가 발생한다")
        void validateAccountIdNotDuplicate_Duplicate_ThrowsException() {
            // given
            String accountId = "duplicate-account-999";

            given(readManager.existsByAccountId(accountId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateAccountIdNotDuplicate(accountId))
                    .isInstanceOf(ShopAccountIdDuplicateException.class)
                    .hasMessageContaining(accountId);
        }
    }

    @Nested
    @DisplayName("validateAccountIdNotDuplicateExcluding() - 계정ID 중복 검증 (자기 제외)")
    class ValidateAccountIdNotDuplicateExcludingTest {

        @Test
        @DisplayName("자기 제외 시 중복되지 않으면 예외 없이 통과한다")
        void validateAccountIdNotDuplicateExcluding_NoDuplicate_NoException() {
            // given
            String accountId = "updated-account-456";
            ShopId excludeId = ShopId.of(1L);

            given(readManager.existsByAccountIdExcluding(accountId, excludeId)).willReturn(false);

            // when & then (no exception)
            sut.validateAccountIdNotDuplicateExcluding(accountId, excludeId);

            then(readManager).should().existsByAccountIdExcluding(accountId, excludeId);
        }

        @Test
        @DisplayName("자기 제외 시 중복되면 예외가 발생한다")
        void validateAccountIdNotDuplicateExcluding_Duplicate_ThrowsException() {
            // given
            String accountId = "duplicate-account-999";
            ShopId excludeId = ShopId.of(1L);

            given(readManager.existsByAccountIdExcluding(accountId, excludeId)).willReturn(true);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateAccountIdNotDuplicateExcluding(accountId, excludeId))
                    .isInstanceOf(ShopAccountIdDuplicateException.class)
                    .hasMessageContaining(accountId);
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
