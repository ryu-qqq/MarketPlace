package com.ryuqq.marketplace.application.selleraddress.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.RegisterContext;
import com.ryuqq.marketplace.application.selleraddress.SellerAddressCommandFixtures;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.SellerAddressFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
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
@DisplayName("RegisterSellerAddressService 단위 테스트")
class RegisterSellerAddressServiceTest {

    @InjectMocks private RegisterSellerAddressService sut;

    @Mock private SellerAddressCommandFactory commandFactory;
    @Mock private SellerAddressCommandManager commandManager;
    @Mock private SellerAddressReadManager readManager;
    @Mock private SellerAddressValidator validator;

    @Nested
    @DisplayName("execute() - 셀러 주소 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 셀러 주소를 등록하고 ID를 반환한다")
        void execute_RegistersSellerAddress_ReturnsAddressId() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerShippingCommand(sellerId);
            SellerAddress newAddress = SellerAddressFixtures.newShippingAddress(sellerId);
            RegisterContext<SellerAddress> context =
                    new RegisterContext<>(newAddress, newAddress.createdAt());
            Long expectedAddressId = 1L;

            given(commandFactory.createRegisterContext(command)).willReturn(context);
            given(readManager.findDefaultBySellerId(any(SellerId.class), any(AddressType.class)))
                    .willReturn(Optional.empty());
            given(commandManager.persist(newAddress)).willReturn(expectedAddressId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedAddressId);
            then(validator).should().validateNoDuplicateAddressName(any(), any(), any());
            then(commandFactory).should().createRegisterContext(command);
            then(commandManager).should().persist(newAddress);
        }

        @Test
        @DisplayName("기본 주소로 등록 시 기존 기본 주소를 해제한다")
        void execute_DefaultAddress_UnmarksExistingDefault() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerShippingCommand(sellerId);
            SellerAddress newAddress = SellerAddressFixtures.newShippingAddress(sellerId);
            SellerAddress existingDefault =
                    SellerAddressFixtures.defaultShippingAddress(10L, sellerId);
            RegisterContext<SellerAddress> context =
                    new RegisterContext<>(newAddress, newAddress.createdAt());

            given(commandFactory.createRegisterContext(command)).willReturn(context);
            given(readManager.findDefaultBySellerId(any(SellerId.class), any(AddressType.class)))
                    .willReturn(Optional.of(existingDefault));
            given(commandManager.persist(any(SellerAddress.class))).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(existingDefault);
            then(commandManager).should().persist(newAddress);
        }

        @Test
        @DisplayName("비기본 주소로 등록 시 기존 기본 주소를 유지한다")
        void execute_NonDefaultAddress_KeepsExistingDefault() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerNonDefaultCommand(sellerId);
            SellerAddress newAddress = SellerAddressFixtures.newShippingAddress(sellerId);
            RegisterContext<SellerAddress> context =
                    new RegisterContext<>(newAddress, newAddress.createdAt());

            given(commandFactory.createRegisterContext(command)).willReturn(context);
            given(commandManager.persist(newAddress)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(readManager).shouldHaveNoInteractions();
            then(commandManager).should().persist(newAddress);
        }
    }
}
