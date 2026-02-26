package com.ryuqq.marketplace.application.seller.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.seller.SellerCommandFixtures;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoCommandManager;
import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.exception.BusinessInfoNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
@DisplayName("UpdateSellerBusinessInfoService 단위 테스트")
class UpdateSellerBusinessInfoServiceTest {

    @InjectMocks private UpdateSellerBusinessInfoService sut;

    @Mock private SellerCommandFactory commandFactory;
    @Mock private SellerBusinessInfoCommandManager commandManager;
    @Mock private SellerBusinessInfoValidator validator;

    @Nested
    @DisplayName("execute() - 사업자 정보 수정")
    class ExecuteTest {

        @Test
        @DisplayName("존재하는 셀러의 사업자 정보를 수정한다")
        void execute_ExistingBusinessInfo_UpdatesSuccessfully() {
            // given
            Long sellerId = 1L;
            UpdateSellerBusinessInfoCommand command =
                    SellerCommandFixtures.updateBusinessInfoCommand(sellerId);
            Instant changedAt = Instant.now();

            SellerBusinessInfoUpdateData updateData = SellerFixtures.sellerBusinessInfoUpdateData();
            UpdateContext<SellerId, SellerBusinessInfoUpdateData> context =
                    new UpdateContext<>(SellerId.of(sellerId), updateData, changedAt);

            SellerBusinessInfo existingBusinessInfo = SellerFixtures.activeSellerBusinessInfo();

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(existingBusinessInfo);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(existingBusinessInfo);
        }

        @Test
        @DisplayName("존재하지 않는 사업자 정보 수정 시 예외가 발생한다")
        void execute_NonExistingBusinessInfo_ThrowsException() {
            // given
            Long sellerId = 999L;
            UpdateSellerBusinessInfoCommand command =
                    SellerCommandFixtures.updateBusinessInfoCommand(sellerId);
            Instant changedAt = Instant.now();

            SellerBusinessInfoUpdateData updateData = SellerFixtures.sellerBusinessInfoUpdateData();
            UpdateContext<SellerId, SellerBusinessInfoUpdateData> context =
                    new UpdateContext<>(SellerId.of(sellerId), updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id()))
                    .willThrow(new BusinessInfoNotFoundException());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(BusinessInfoNotFoundException.class);

            then(commandManager).should(never()).persist(any(SellerBusinessInfo.class));
        }
    }
}
