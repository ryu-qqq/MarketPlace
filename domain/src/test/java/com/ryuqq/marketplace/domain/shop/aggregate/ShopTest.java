package com.ryuqq.marketplace.domain.shop.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Shop Aggregate 단위 테스트")
class ShopTest {

    private static final Long SALES_CHANNEL_ID = 1L;

    @Nested
    @DisplayName("forNew() - 신규 Shop 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 Shop을 생성한다")
        void createNewShopWithRequiredFields() {
            // given
            String shopName = ShopFixtures.defaultShopName();
            String accountId = ShopFixtures.defaultAccountId();
            Instant now = CommonVoFixtures.now();

            // when
            Shop shop = Shop.forNew(SALES_CHANNEL_ID, shopName, accountId, now);

            // then
            assertThat(shop).isNotNull();
            assertThat(shop.id().isNew()).isTrue();
            assertThat(shop.salesChannelId()).isEqualTo(SALES_CHANNEL_ID);
            assertThat(shop.shopName()).isEqualTo(shopName);
            assertThat(shop.accountId()).isEqualTo(accountId);
            assertThat(shop.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(shop.isActive()).isTrue();
            assertThat(shop.isDeleted()).isFalse();
            assertThat(shop.createdAt()).isEqualTo(now);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("shopName이 null이면 예외가 발생한다")
        void createWithNullShopName_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Shop.forNew(
                                            SALES_CHANNEL_ID,
                                            null,
                                            ShopFixtures.defaultAccountId(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부몰명");
        }

        @Test
        @DisplayName("shopName이 빈 문자열이면 예외가 발생한다")
        void createWithBlankShopName_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Shop.forNew(
                                            SALES_CHANNEL_ID,
                                            "   ",
                                            ShopFixtures.defaultAccountId(),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부몰명");
        }

        @Test
        @DisplayName("accountId가 null이면 예외가 발생한다")
        void createWithNullAccountId_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Shop.forNew(
                                            SALES_CHANNEL_ID,
                                            ShopFixtures.defaultShopName(),
                                            null,
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("계정 ID");
        }

        @Test
        @DisplayName("accountId가 빈 문자열이면 예외가 발생한다")
        void createWithBlankAccountId_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    Shop.forNew(
                                            SALES_CHANNEL_ID,
                                            ShopFixtures.defaultShopName(),
                                            "  ",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("계정 ID");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 Shop을 복원한다")
        void reconstituteActiveShop() {
            // given
            ShopId id = ShopId.of(1L);
            String shopName = ShopFixtures.defaultShopName();
            String accountId = ShopFixtures.defaultAccountId();
            ShopStatus status = ShopStatus.ACTIVE;
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            Shop shop =
                    Shop.reconstitute(
                            id,
                            SALES_CHANNEL_ID,
                            shopName,
                            accountId,
                            status,
                            null,
                            createdAt,
                            updatedAt);

            // then
            assertThat(shop).isNotNull();
            assertThat(shop.id()).isEqualTo(id);
            assertThat(shop.id().isNew()).isFalse();
            assertThat(shop.salesChannelId()).isEqualTo(SALES_CHANNEL_ID);
            assertThat(shop.shopName()).isEqualTo(shopName);
            assertThat(shop.accountId()).isEqualTo(accountId);
            assertThat(shop.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(shop.isActive()).isTrue();
            assertThat(shop.isDeleted()).isFalse();
            assertThat(shop.createdAt()).isEqualTo(createdAt);
            assertThat(shop.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 Shop을 복원한다")
        void reconstituteInactiveShop() {
            // given
            ShopId id = ShopId.of(2L);
            ShopStatus status = ShopStatus.INACTIVE;
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            Shop shop =
                    Shop.reconstitute(
                            id,
                            SALES_CHANNEL_ID,
                            ShopFixtures.defaultShopName(),
                            ShopFixtures.defaultAccountId(),
                            status,
                            null,
                            createdAt,
                            updatedAt);

            // then
            assertThat(shop.status()).isEqualTo(ShopStatus.INACTIVE);
            assertThat(shop.isActive()).isFalse();
            assertThat(shop.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("영속성에서 삭제된 Shop을 복원한다")
        void reconstituteDeletedShop() {
            // given
            ShopId id = ShopId.of(3L);
            Instant deletedAt = CommonVoFixtures.yesterday();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            Shop shop =
                    Shop.reconstitute(
                            id,
                            SALES_CHANNEL_ID,
                            ShopFixtures.defaultShopName(),
                            ShopFixtures.defaultAccountId(),
                            ShopStatus.INACTIVE,
                            deletedAt,
                            createdAt,
                            updatedAt);

            // then
            assertThat(shop.isDeleted()).isTrue();
            assertThat(shop.deletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("update() - Shop 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("Shop 정보를 수정한다")
        void updateShopInfo() {
            // given
            Shop shop = ShopFixtures.activeShop();
            ShopUpdateData updateData = ShopFixtures.shopUpdateData();
            Instant now = CommonVoFixtures.now();

            // when
            shop.update(updateData, now);

            // then
            assertThat(shop.shopName()).isEqualTo(updateData.shopName());
            assertThat(shop.accountId()).isEqualTo(updateData.accountId());
            assertThat(shop.status()).isEqualTo(updateData.status());
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("Shop 상태를 INACTIVE로 수정한다")
        void updateShopStatusToInactive() {
            // given
            Shop shop = ShopFixtures.activeShop();
            ShopUpdateData updateData = ShopFixtures.inactiveShopUpdateData();
            Instant now = CommonVoFixtures.now();

            // when
            shop.update(updateData, now);

            // then
            assertThat(shop.status()).isEqualTo(ShopStatus.INACTIVE);
            assertThat(shop.isActive()).isFalse();
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("shopName이 null이면 예외가 발생한다")
        void updateWithNullShopName_ThrowsException() {
            // given
            Shop shop = ShopFixtures.activeShop();
            ShopUpdateData updateData = ShopUpdateData.of(null, "account", ShopStatus.ACTIVE);

            // when & then
            assertThatThrownBy(() -> shop.update(updateData, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("외부몰명");
        }

        @Test
        @DisplayName("accountId가 null이면 예외가 발생한다")
        void updateWithNullAccountId_ThrowsException() {
            // given
            Shop shop = ShopFixtures.activeShop();
            ShopUpdateData updateData = ShopUpdateData.of("shopName", null, ShopStatus.ACTIVE);

            // when & then
            assertThatThrownBy(() -> shop.update(updateData, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("계정 ID");
        }
    }

    @Nested
    @DisplayName("activate() / deactivate() - 활성화 상태 변경")
    class ActivationTest {

        @Test
        @DisplayName("비활성 Shop을 활성화한다")
        void activateInactiveShop() {
            // given
            Shop shop = ShopFixtures.inactiveShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.activate(now);

            // then
            assertThat(shop.isActive()).isTrue();
            assertThat(shop.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 Shop을 활성화해도 상태가 유지된다")
        void activateAlreadyActiveShop() {
            // given
            Shop shop = ShopFixtures.activeShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.activate(now);

            // then
            assertThat(shop.isActive()).isTrue();
            assertThat(shop.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("활성 Shop을 비활성화한다")
        void deactivateActiveShop() {
            // given
            Shop shop = ShopFixtures.activeShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.deactivate(now);

            // then
            assertThat(shop.isActive()).isFalse();
            assertThat(shop.status()).isEqualTo(ShopStatus.INACTIVE);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("비활성 Shop을 비활성화해도 상태가 유지된다")
        void deactivateAlreadyInactiveShop() {
            // given
            Shop shop = ShopFixtures.inactiveShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.deactivate(now);

            // then
            assertThat(shop.isActive()).isFalse();
            assertThat(shop.status()).isEqualTo(ShopStatus.INACTIVE);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("delete() - 삭제")
    class DeletionTest {

        @Test
        @DisplayName("Shop을 삭제(Soft Delete)한다")
        void deleteShop() {
            // given
            Shop shop = ShopFixtures.activeShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.delete(now);

            // then
            assertThat(shop.isDeleted()).isTrue();
            assertThat(shop.deletedAt()).isEqualTo(now);
            assertThat(shop.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("비활성 Shop도 삭제할 수 있다")
        void deleteInactiveShop() {
            // given
            Shop shop = ShopFixtures.inactiveShop();
            Instant now = CommonVoFixtures.now();

            // when
            shop.delete(now);

            // then
            assertThat(shop.isDeleted()).isTrue();
            assertThat(shop.deletedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            Shop shop = ShopFixtures.activeShop(100L);

            // when
            Long idValue = shop.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("신규 Shop의 idValue()는 null을 반환한다")
        void newShopIdValueReturnsNull() {
            // given
            Shop shop = ShopFixtures.newShop();

            // when
            Long idValue = shop.idValue();

            // then
            assertThat(idValue).isNull();
        }

        @Test
        @DisplayName("모든 getter 메서드가 올바른 값을 반환한다")
        void allGettersReturnCorrectValues() {
            // given
            ShopId id = ShopId.of(1L);
            String shopName = "테스트 외부몰";
            String accountId = "test-account";
            ShopStatus status = ShopStatus.ACTIVE;
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.now();

            Shop shop =
                    Shop.reconstitute(
                            id,
                            SALES_CHANNEL_ID,
                            shopName,
                            accountId,
                            status,
                            null,
                            createdAt,
                            updatedAt);

            // then
            assertThat(shop.id()).isEqualTo(id);
            assertThat(shop.idValue()).isEqualTo(1L);
            assertThat(shop.salesChannelId()).isEqualTo(SALES_CHANNEL_ID);
            assertThat(shop.shopName()).isEqualTo(shopName);
            assertThat(shop.accountId()).isEqualTo(accountId);
            assertThat(shop.status()).isEqualTo(status);
            assertThat(shop.isActive()).isTrue();
            assertThat(shop.deletionStatus()).isNotNull();
            assertThat(shop.isDeleted()).isFalse();
            assertThat(shop.deletedAt()).isNull();
            assertThat(shop.createdAt()).isEqualTo(createdAt);
            assertThat(shop.updatedAt()).isEqualTo(updatedAt);
        }
    }
}
