package com.ryuqq.marketplace.application.selleraddress.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.RegisterContext;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.selleraddress.SellerAddressCommandFixtures;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddressUpdateData;
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
@DisplayName("SellerAddressCommandFactory 단위 테스트")
class SellerAddressCommandFactoryTest {

    @InjectMocks private SellerAddressCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createRegisterContext() - 등록 Context 생성")
    class CreateRegisterContextTest {

        @Test
        @DisplayName("RegisterSellerAddressCommand로 RegisterContext를 생성한다")
        void createRegisterContext_ReturnsContext() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerShippingCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            RegisterContext<SellerAddress> result = sut.createRegisterContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.newEntity()).isNotNull();
            assertThat(result.newEntity().isNew()).isTrue();
            assertThat(result.newEntity().addressNameValue()).isEqualTo(command.addressName());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 주소로 설정된 주소를 생성한다")
        void createRegisterContext_DefaultAddress_CreatesDefaultAddress() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerShippingCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            RegisterContext<SellerAddress> result = sut.createRegisterContext(command);

            // then
            assertThat(result.newEntity().isDefaultAddress()).isTrue();
        }

        @Test
        @DisplayName("비기본 주소를 생성한다")
        void createRegisterContext_NonDefaultAddress_CreatesNonDefaultAddress() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressCommand command =
                    SellerAddressCommandFixtures.registerNonDefaultCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            RegisterContext<SellerAddress> result = sut.createRegisterContext(command);

            // then
            assertThat(result.newEntity().isDefaultAddress()).isFalse();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - 수정 Context 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateSellerAddressCommand로 UpdateContext를 생성한다")
        void createUpdateContext_ReturnsContext() {
            // given
            Long addressId = 1L;
            UpdateSellerAddressCommand command = SellerAddressCommandFixtures.updateCommand(addressId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerAddressId, SellerAddressUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(addressId);
            assertThat(result.updateData()).isNotNull();
            assertThat(result.updateData().addressName().value()).isEqualTo(command.addressName());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("기본 주소로 변경하는 UpdateContext를 생성한다")
        void createUpdateContext_SetAsDefault_ReturnsContext() {
            // given
            Long addressId = 1L;
            UpdateSellerAddressCommand command =
                    SellerAddressCommandFixtures.updateCommandSetDefault(addressId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerAddressId, SellerAddressUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(addressId);
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createDeleteContext() - 삭제 Context 생성")
    class CreateDeleteContextTest {

        @Test
        @DisplayName("DeleteSellerAddressCommand로 StatusChangeContext를 생성한다")
        void createDeleteContext_ReturnsContext() {
            // given
            Long addressId = 1L;
            DeleteSellerAddressCommand command = SellerAddressCommandFixtures.deleteCommand(addressId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<SellerAddressId> result = sut.createDeleteContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(addressId);
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }
}
