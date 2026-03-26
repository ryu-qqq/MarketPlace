package com.ryuqq.marketplace.application.seller.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.SellerCommandFixtures;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.internal.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.internal.SellerRegistrationCoordinator;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
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
@DisplayName("RegisterSellerService 단위 테스트")
class RegisterSellerServiceTest {

    @InjectMocks private RegisterSellerService sut;

    @Mock private SellerCommandFactory commandFactory;
    @Mock private SellerRegistrationCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 셀러 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 셀러를 등록하고 ID를 반환한다")
        void execute_RegistersSeller_ReturnsSellerId() {
            // given
            RegisterSellerCommand command = SellerCommandFixtures.registerCommand();
            SellerRegistrationBundle bundle =
                    new SellerRegistrationBundle(
                            SellerFixtures.newSeller(), SellerFixtures.newSellerBusinessInfo());
            Long expectedSellerId = 1L;

            given(commandFactory.createRegistrationBundle(command)).willReturn(bundle);
            given(coordinator.register(bundle)).willReturn(expectedSellerId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedSellerId);
            then(commandFactory).should().createRegistrationBundle(command);
            then(coordinator).should().register(bundle);
        }
    }
}
