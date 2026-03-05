package com.ryuqq.marketplace.application.seller.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.seller.SellerCommandFixtures;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerCommandManager;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
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
@DisplayName("UpdateSellerService 단위 테스트")
class UpdateSellerServiceTest {

    @InjectMocks private UpdateSellerService sut;

    @Mock private SellerCommandFactory commandFactory;
    @Mock private SellerCommandManager commandManager;
    @Mock private SellerValidator validator;
    @Mock private SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    @Nested
    @DisplayName("execute() - 셀러 기본정보 수정")
    class ExecuteTest {

        @Test
        @DisplayName("존재하는 셀러의 기본정보를 수정한다")
        void execute_ExistingSeller_UpdatesSuccessfully() {
            // given
            Long sellerId = 1L;
            UpdateSellerCommand command = SellerCommandFixtures.updateSellerCommand(sellerId);
            Instant changedAt = Instant.now();

            SellerUpdateData updateData =
                    SellerUpdateData.of(
                            "수정된 셀러명", "수정된 스토어", "https://example.com/new-logo.png", "수정된 설명입니다.");
            UpdateContext<SellerId, SellerUpdateData> context =
                    new UpdateContext<>(SellerId.of(sellerId), updateData, changedAt);

            Seller existingSeller = SellerFixtures.activeSeller();

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(existingSeller);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(existingSeller);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 수정 시 예외가 발생한다")
        void execute_NonExistingSeller_ThrowsException() {
            // given
            Long sellerId = 999L;
            UpdateSellerCommand command = SellerCommandFixtures.updateSellerCommand(sellerId);
            Instant changedAt = Instant.now();

            SellerUpdateData updateData = SellerUpdateData.of("수정된 셀러명", "수정된 스토어", null, null);
            UpdateContext<SellerId, SellerUpdateData> context =
                    new UpdateContext<>(SellerId.of(sellerId), updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id()))
                    .willThrow(new SellerNotFoundException(sellerId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(SellerNotFoundException.class);

            then(commandManager).should(never()).persist(any(Seller.class));
        }
    }
}
