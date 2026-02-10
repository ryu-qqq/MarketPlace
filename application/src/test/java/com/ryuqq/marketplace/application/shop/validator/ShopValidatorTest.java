package com.ryuqq.marketplace.application.shop.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopAccountIdDuplicateException;
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

    private static final Long SALES_CHANNEL_ID = 1L;

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
    @DisplayName("validateAccountNotDuplicate() - 계정ID 중복 검증")
    class ValidateAccountNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 계정ID는 예외 없이 통과한다")
        void validateAccountNotDuplicate_NoDuplicate_NoException() {
            // given
            String accountId = "new-account-123";

            given(readManager.existsBySalesChannelIdAndAccountId(SALES_CHANNEL_ID, accountId))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateAccountNotDuplicate(SALES_CHANNEL_ID, accountId);

            then(readManager)
                    .should()
                    .existsBySalesChannelIdAndAccountId(SALES_CHANNEL_ID, accountId);
        }

        @Test
        @DisplayName("중복된 계정ID이면 예외가 발생한다")
        void validateAccountNotDuplicate_Duplicate_ThrowsException() {
            // given
            String accountId = "duplicate-account-999";

            given(readManager.existsBySalesChannelIdAndAccountId(SALES_CHANNEL_ID, accountId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateAccountNotDuplicate(SALES_CHANNEL_ID, accountId))
                    .isInstanceOf(ShopAccountIdDuplicateException.class)
                    .hasMessageContaining(accountId);
        }
    }

    @Nested
    @DisplayName("validateAccountNotDuplicateExcluding() - 계정ID 중복 검증 (자기 제외)")
    class ValidateAccountNotDuplicateExcludingTest {

        @Test
        @DisplayName("자기 제외 시 중복되지 않으면 예외 없이 통과한다")
        void validateAccountNotDuplicateExcluding_NoDuplicate_NoException() {
            // given
            String accountId = "updated-account-456";
            ShopId excludeId = ShopId.of(1L);

            given(
                            readManager.existsBySalesChannelIdAndAccountIdExcluding(
                                    SALES_CHANNEL_ID, accountId, excludeId))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateAccountNotDuplicateExcluding(SALES_CHANNEL_ID, accountId, excludeId);

            then(readManager)
                    .should()
                    .existsBySalesChannelIdAndAccountIdExcluding(
                            SALES_CHANNEL_ID, accountId, excludeId);
        }

        @Test
        @DisplayName("자기 제외 시 중복되면 예외가 발생한다")
        void validateAccountNotDuplicateExcluding_Duplicate_ThrowsException() {
            // given
            String accountId = "duplicate-account-999";
            ShopId excludeId = ShopId.of(1L);

            given(
                            readManager.existsBySalesChannelIdAndAccountIdExcluding(
                                    SALES_CHANNEL_ID, accountId, excludeId))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateAccountNotDuplicateExcluding(
                                            SALES_CHANNEL_ID, accountId, excludeId))
                    .isInstanceOf(ShopAccountIdDuplicateException.class)
                    .hasMessageContaining(accountId);
        }
    }

    private Shop createShop(Long shopId) {
        Instant now = Instant.now();
        return Shop.reconstitute(
                ShopId.of(shopId),
                SALES_CHANNEL_ID,
                "테스트 외부몰",
                "test-account-" + shopId,
                ShopStatus.ACTIVE,
                null,
                now,
                now);
    }
}
