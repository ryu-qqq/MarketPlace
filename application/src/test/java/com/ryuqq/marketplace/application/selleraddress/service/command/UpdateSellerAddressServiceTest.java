package com.ryuqq.marketplace.application.selleraddress.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.selleraddress.SellerAddressCommandFixtures;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddressUpdateData;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;
import java.util.Optional;
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
@DisplayName("UpdateSellerAddressService 단위 테스트")
class UpdateSellerAddressServiceTest {

    @InjectMocks private UpdateSellerAddressService sut;

    @Mock private SellerAddressCommandFactory commandFactory;
    @Mock private SellerAddressCommandManager commandManager;
    @Mock private SellerAddressReadManager readManager;
    @Mock private SellerAddressValidator validator;

    @Nested
    @DisplayName("execute() - 셀러 주소 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 셀러 주소를 수정한다")
        void execute_UpdatesSellerAddress() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            UpdateSellerAddressCommand command =
                    SellerAddressCommandFixtures.updateCommand(addressId);
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(addressId, sellerId, "기존 주소");
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<SellerAddressId, SellerAddressUpdateData> context =
                    new UpdateContext<>(
                            SellerAddressId.of(addressId),
                            SellerAddressUpdateData.of(null, null),
                            changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(address);

            // when
            sut.execute(command);

            // then
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(address);
        }

        @Test
        @DisplayName("기본 주소로 변경 시 기존 기본 주소를 해제한다")
        void execute_SetAsDefault_UnmarksExistingDefault() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            UpdateSellerAddressCommand command =
                    SellerAddressCommandFixtures.updateCommandSetDefault(addressId);
            SellerAddress address =
                    SellerAddressFixtures.nonDefaultShippingAddress(addressId, sellerId, "주소");
            SellerAddress existingDefault =
                    SellerAddressFixtures.defaultShippingAddress(10L, sellerId);
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<SellerAddressId, SellerAddressUpdateData> context =
                    new UpdateContext<>(
                            SellerAddressId.of(addressId),
                            SellerAddressUpdateData.of(null, null),
                            changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(address);
            given(readManager.findDefaultBySellerId(any(SellerId.class), any(AddressType.class)))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(existingDefault);
            then(commandManager).should().persist(address);
        }

        @Test
        @DisplayName("자신이 이미 기본 주소인 경우 기본 해제 로직을 수행하지 않는다")
        void execute_AlreadyDefault_DoesNotUnmarkSelf() {
            // given
            Long addressId = 1L;
            Long sellerId = 1L;
            UpdateSellerAddressCommand command =
                    SellerAddressCommandFixtures.updateCommandSetDefault(addressId);
            SellerAddress address =
                    SellerAddressFixtures.defaultShippingAddress(addressId, sellerId);
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<SellerAddressId, SellerAddressUpdateData> context =
                    new UpdateContext<>(
                            SellerAddressId.of(addressId),
                            SellerAddressUpdateData.of(null, null),
                            changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(address);
            given(readManager.findDefaultBySellerId(any(SellerId.class), any(AddressType.class)))
                    .willReturn(Optional.of(address));

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(address);
        }
    }
}
