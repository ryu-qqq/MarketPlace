package com.ryuqq.marketplace.domain.shop.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopUpdateData Value Object 단위 테스트")
class ShopUpdateDataTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 ShopUpdateData를 생성한다")
        void createWithOf() {
            // given
            String shopName = "수정된 외부몰명";
            String accountId = "updated-account";
            ShopStatus status = ShopStatus.ACTIVE;

            // when
            ShopUpdateData updateData = ShopUpdateData.of(shopName, accountId, status);

            // then
            assertThat(updateData).isNotNull();
            assertThat(updateData.shopName()).isEqualTo(shopName);
            assertThat(updateData.accountId()).isEqualTo(accountId);
            assertThat(updateData.status()).isEqualTo(status);
        }

        @Test
        @DisplayName("INACTIVE 상태로 ShopUpdateData를 생성한다")
        void createWithInactiveStatus() {
            // when
            ShopUpdateData updateData =
                    ShopUpdateData.of("외부몰명", "account-id", ShopStatus.INACTIVE);

            // then
            assertThat(updateData.status()).isEqualTo(ShopStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 ShopUpdateData는 동등하다")
        void sameValuesAreEqual() {
            // given
            ShopUpdateData data1 = ShopUpdateData.of("외부몰", "account", ShopStatus.ACTIVE);
            ShopUpdateData data2 = ShopUpdateData.of("외부몰", "account", ShopStatus.ACTIVE);

            // then
            assertThat(data1).isEqualTo(data2);
            assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
        }

        @Test
        @DisplayName("다른 shopName을 가진 ShopUpdateData는 동등하지 않다")
        void differentShopNameNotEquals() {
            // given
            ShopUpdateData data1 = ShopUpdateData.of("외부몰1", "account", ShopStatus.ACTIVE);
            ShopUpdateData data2 = ShopUpdateData.of("외부몰2", "account", ShopStatus.ACTIVE);

            // then
            assertThat(data1).isNotEqualTo(data2);
        }

        @Test
        @DisplayName("다른 accountId를 가진 ShopUpdateData는 동등하지 않다")
        void differentAccountIdNotEquals() {
            // given
            ShopUpdateData data1 = ShopUpdateData.of("외부몰", "account1", ShopStatus.ACTIVE);
            ShopUpdateData data2 = ShopUpdateData.of("외부몰", "account2", ShopStatus.ACTIVE);

            // then
            assertThat(data1).isNotEqualTo(data2);
        }

        @Test
        @DisplayName("다른 status를 가진 ShopUpdateData는 동등하지 않다")
        void differentStatusNotEquals() {
            // given
            ShopUpdateData data1 = ShopUpdateData.of("외부몰", "account", ShopStatus.ACTIVE);
            ShopUpdateData data2 = ShopUpdateData.of("외부몰", "account", ShopStatus.INACTIVE);

            // then
            assertThat(data1).isNotEqualTo(data2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShopUpdateData는 record이므로 불변이다")
        void shopUpdateDataIsImmutable() {
            // given
            String shopName = "외부몰";
            String accountId = "account";
            ShopStatus status = ShopStatus.ACTIVE;

            ShopUpdateData updateData = ShopUpdateData.of(shopName, accountId, status);

            // then (record는 setter가 없으므로 값 변경 불가)
            assertThat(updateData.shopName()).isEqualTo(shopName);
            assertThat(updateData.accountId()).isEqualTo(accountId);
            assertThat(updateData.status()).isEqualTo(status);
        }
    }

    @Nested
    @DisplayName("Fixtures와의 통합 테스트")
    class FixturesIntegrationTest {

        @Test
        @DisplayName("ShopFixtures로부터 ShopUpdateData를 생성한다")
        void createFromFixtures() {
            // when
            ShopUpdateData updateData = ShopFixtures.shopUpdateData();

            // then
            assertThat(updateData).isNotNull();
            assertThat(updateData.shopName()).isNotBlank();
            assertThat(updateData.accountId()).isNotBlank();
            assertThat(updateData.status()).isNotNull();
        }
    }
}
