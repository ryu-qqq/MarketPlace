package com.ryuqq.marketplace.application.shop.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shop.ShopCommandFixtures;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.aggregate.ShopUpdateData;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.time.Instant;
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
@DisplayName("ShopCommandFactory 단위 테스트")
class ShopCommandFactoryTest {

    private ShopCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        sut = new ShopCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create() - Shop 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterShopCommand로 Shop 도메인 객체를 생성한다")
        void create_FromCommand_CreatesShop() {
            // given
            RegisterShopCommand command = ShopCommandFixtures.registerCommand();
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            Shop result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.shopName()).isEqualTo(command.shopName());
            assertThat(result.accountId()).isEqualTo(command.accountId());
            assertThat(result.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(result.createdAt()).isEqualTo(now);
            assertThat(result.updatedAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("신규 Shop은 ACTIVE 상태로 생성된다")
        void create_NewShop_StatusIsActive() {
            // given
            RegisterShopCommand command =
                    ShopCommandFixtures.registerCommand("새외부몰", "new-account");
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            Shop result = sut.create(command);

            // then
            assertThat(result.status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(result.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - UpdateContext 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateShopCommand로 UpdateContext를 생성한다")
        void createUpdateContext_FromCommand_CreatesContext() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command = ShopCommandFixtures.updateCommand(shopId);
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<ShopId, ShopUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(shopId);
            assertThat(result.updateData().shopName()).isEqualTo(command.shopName());
            assertThat(result.updateData().accountId()).isEqualTo(command.accountId());
            assertThat(result.updateData().status()).isEqualTo(ShopStatus.ACTIVE);
            assertThat(result.changedAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("UpdateContext에 INACTIVE 상태를 반영한다")
        void createUpdateContext_InactiveStatus_ReflectsStatus() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command =
                    ShopCommandFixtures.updateCommand(shopId, "수정외부몰", "upd-acc", "INACTIVE");
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<ShopId, ShopUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result.updateData().status()).isEqualTo(ShopStatus.INACTIVE);
        }

        @Test
        @DisplayName("UpdateContext의 changedAt은 TimeProvider.now()를 사용한다")
        void createUpdateContext_UsesTimeProvider() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command = ShopCommandFixtures.updateCommand(shopId);
            Instant expectedTime = Instant.parse("2024-06-01T10:00:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            UpdateContext<ShopId, ShopUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result.changedAt()).isEqualTo(expectedTime);
        }
    }
}
