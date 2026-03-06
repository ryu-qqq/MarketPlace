package com.ryuqq.marketplace.application.shop.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.shop.ShopCommandFixtures;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import com.ryuqq.marketplace.application.shop.factory.ShopCommandFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopWriteManager;
import com.ryuqq.marketplace.application.shop.validator.ShopValidator;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.aggregate.ShopUpdateData;
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
@DisplayName("UpdateShopService 단위 테스트")
class UpdateShopServiceTest {

    @InjectMocks private UpdateShopService sut;

    @Mock private ShopCommandFactory commandFactory;
    @Mock private ShopWriteManager writeManager;
    @Mock private ShopValidator validator;

    @Nested
    @DisplayName("execute() - Shop 수정")
    class ExecuteTest {

        @Test
        @DisplayName("UpdateContext로 Shop을 수정한다")
        void execute_UpdatesShop() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command = ShopCommandFixtures.updateCommand(shopId);
            UpdateContext<ShopId, ShopUpdateData> context = createUpdateContext(shopId);
            Shop existingShop = createExistingShop(shopId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(existingShop);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(validator)
                    .should()
                    .validateAccountNotDuplicateExcluding(
                            existingShop.salesChannelId(),
                            context.updateData().accountId(),
                            context.id());
            then(writeManager).should().persist(existingShop);
        }

        @Test
        @DisplayName("판매채널+계정ID 중복 검증을 자기 자신 제외하고 수행한다")
        void execute_ValidatesAccountIdExcludingSelf() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command =
                    ShopCommandFixtures.updateCommand(shopId, "수정된외부몰", "updated-acc-99", "ACTIVE");
            UpdateContext<ShopId, ShopUpdateData> context = createUpdateContext(shopId);
            Shop existingShop = createExistingShop(shopId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(existingShop);

            // when
            sut.execute(command);

            // then
            then(validator)
                    .should()
                    .validateAccountNotDuplicateExcluding(
                            existingShop.salesChannelId(),
                            context.updateData().accountId(),
                            context.id());
        }

        @Test
        @DisplayName("Shop 상태를 INACTIVE로 변경할 수 있다")
        void execute_UpdatesStatusToInactive() {
            // given
            Long shopId = 1L;
            UpdateShopCommand command = ShopCommandFixtures.updateCommand(shopId, "INACTIVE");
            UpdateContext<ShopId, ShopUpdateData> context =
                    createUpdateContext(shopId, ShopStatus.INACTIVE);
            Shop existingShop = createExistingShop(shopId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(existingShop);

            // when
            sut.execute(command);

            // then
            then(writeManager).should().persist(existingShop);
        }

        private UpdateContext<ShopId, ShopUpdateData> createUpdateContext(Long shopId) {
            return createUpdateContext(shopId, ShopStatus.ACTIVE);
        }

        private UpdateContext<ShopId, ShopUpdateData> createUpdateContext(
                Long shopId, ShopStatus status) {
            ShopId id = ShopId.of(shopId);
            ShopUpdateData updateData = ShopUpdateData.of("수정된 외부몰", "updated-account-456", status);
            return new UpdateContext<>(id, updateData, Instant.now());
        }

        private Shop createExistingShop(Long shopId) {
            Instant now = Instant.now();
            return Shop.reconstitute(
                    ShopId.of(shopId),
                    1L,
                    "기존 외부몰",
                    "old-account-123",
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
