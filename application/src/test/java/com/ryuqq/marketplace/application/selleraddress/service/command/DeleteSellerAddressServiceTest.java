package com.ryuqq.marketplace.application.selleraddress.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.selleraddress.SellerAddressCommandFixtures;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
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
@DisplayName("DeleteSellerAddressService 단위 테스트")
class DeleteSellerAddressServiceTest {

    @InjectMocks private DeleteSellerAddressService sut;

    @Mock private SellerAddressCommandFactory commandFactory;
    @Mock private SellerAddressCommandManager commandManager;
    @Mock private SellerAddressValidator validator;

    @Nested
    @DisplayName("execute() - 셀러 주소 삭제")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 셀러 주소를 삭제한다")
        void execute_DeletesSellerAddress() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            DeleteSellerAddressCommand command = SellerAddressCommandFixtures.deleteCommand(addressId);
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(addressId, sellerId, "테스트 주소");
            Instant changedAt = CommonVoFixtures.now();
            StatusChangeContext<SellerAddressId> context =
                    new StatusChangeContext<>(SellerAddressId.of(addressId), changedAt);

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(address);

            // when
            sut.execute(command);

            // then
            then(validator).should().findExistingOrThrow(context.id());
            then(validator).should().validateNotDefaultAddress(address);
            then(commandManager).should().persist(address);
        }

        @Test
        @DisplayName("비기본 주소는 삭제할 수 있다")
        void execute_NonDefaultAddress_CanDelete() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            DeleteSellerAddressCommand command = SellerAddressCommandFixtures.deleteCommand(addressId);
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(addressId, sellerId, "비기본 주소");
            Instant changedAt = CommonVoFixtures.now();
            StatusChangeContext<SellerAddressId> context =
                    new StatusChangeContext<>(SellerAddressId.of(addressId), changedAt);

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(address);

            // when
            sut.execute(command);

            // then
            then(validator).should().validateNotDefaultAddress(address);
            then(commandManager).should().persist(address);
        }
    }
}
