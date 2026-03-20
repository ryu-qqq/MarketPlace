package com.ryuqq.marketplace.application.seller.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.SellerCommandFixtures;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.internal.SellerUpdateBundle;
import com.ryuqq.marketplace.application.seller.internal.SellerUpdateCoordinator;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
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
@DisplayName("UpdateSellerFullService 단위 테스트")
class UpdateSellerFullServiceTest {

    @InjectMocks private UpdateSellerFullService sut;

    @Mock private SellerCommandFactory commandFactory;
    @Mock private SellerUpdateCoordinator updateCoordinator;

    @Nested
    @DisplayName("execute() - 셀러 전체 정보 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 셀러 전체 정보를 수정한다")
        void execute_UpdatesSellerFull() {
            // given
            Long sellerId = 1L;
            UpdateSellerFullCommand command = SellerCommandFixtures.updateFullCommand(sellerId);
            Instant changedAt = Instant.now();

            SellerUpdateBundle bundle =
                    new SellerUpdateBundle(
                            SellerId.of(sellerId),
                            SellerFixtures.sellerUpdateData(),
                            SellerFixtures.sellerBusinessInfoUpdateData(),
                            SellerFixtures.sellerCsUpdateData(),
                            SellerFixtures.sellerContractUpdateData(),
                            SellerFixtures.sellerSettlementUpdateData(),
                            changedAt);

            given(commandFactory.createUpdateBundle(command)).willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateBundle(command);
            then(updateCoordinator).should().update(bundle);
        }
    }
}
