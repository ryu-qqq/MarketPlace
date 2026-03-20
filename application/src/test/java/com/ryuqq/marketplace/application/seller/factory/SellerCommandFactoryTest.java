package com.ryuqq.marketplace.application.seller.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.seller.SellerCommandFixtures;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import com.ryuqq.marketplace.application.seller.internal.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.internal.SellerUpdateBundle;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
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
@DisplayName("SellerCommandFactory 단위 테스트")
class SellerCommandFactoryTest {

    @InjectMocks private SellerCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createRegistrationBundle() - 등록 Bundle 생성")
    class CreateRegistrationBundleTest {

        @Test
        @DisplayName("RegisterSellerCommand로 SellerRegistrationBundle을 생성한다")
        void createRegistrationBundle_ReturnsBundle() {
            // given
            RegisterSellerCommand command = SellerCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SellerRegistrationBundle result = sut.createRegistrationBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.seller()).isNotNull();
            assertThat(result.seller().sellerNameValue()).isEqualTo(command.seller().sellerName());
            assertThat(result.businessInfo()).isNotNull();
            assertThat(result.businessInfo().companyNameValue())
                    .isEqualTo(command.businessInfo().companyName());
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우에도 Bundle을 생성한다")
        void createRegistrationBundle_WithoutOptionals_ReturnsBundle() {
            // given
            RegisterSellerCommand command =
                    new RegisterSellerCommand(
                            SellerCommandFixtures.sellerInfoCommandWithoutOptionals(),
                            SellerCommandFixtures.sellerBusinessInfoCommandWithoutOptionals());
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SellerRegistrationBundle result = sut.createRegistrationBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.seller().logoUrlValue()).isNull();
            assertThat(result.seller().descriptionValue()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateBundle() - 수정 Bundle 생성")
    class CreateUpdateBundleTest {

        @Test
        @DisplayName("UpdateSellerFullCommand로 SellerUpdateBundle을 생성한다")
        void createUpdateBundle_ReturnsBundle() {
            // given
            Long sellerId = 1L;
            UpdateSellerFullCommand command = SellerCommandFixtures.updateFullCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SellerUpdateBundle result = sut.createUpdateBundle(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId().value()).isEqualTo(sellerId);
            assertThat(result.sellerUpdateData()).isNotNull();
            assertThat(result.businessInfoUpdateData()).isNotNull();
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createSeller() - Seller 도메인 생성")
    class CreateSellerTest {

        @Test
        @DisplayName("RegisterSellerCommand로 Seller를 생성한다")
        void createSeller_ReturnsSeller() {
            // given
            RegisterSellerCommand command = SellerCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            Seller result = sut.createSeller(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isNew()).isTrue();
            assertThat(result.sellerNameValue()).isEqualTo(command.seller().sellerName());
            assertThat(result.displayNameValue()).isEqualTo(command.seller().displayName());
        }
    }

    @Nested
    @DisplayName("createUpdateContext(UpdateSellerCommand) - Seller UpdateContext 생성")
    class CreateUpdateContextSellerTest {

        @Test
        @DisplayName("UpdateSellerCommand로 UpdateContext를 생성한다")
        void createUpdateContext_ReturnsContext() {
            // given
            Long sellerId = 1L;
            UpdateSellerCommand command = SellerCommandFixtures.updateSellerCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerId, SellerUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(sellerId);
            assertThat(result.updateData().sellerName().value()).isEqualTo(command.sellerName());
            assertThat(result.updateData().displayName().value()).isEqualTo(command.displayName());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우에도 UpdateContext를 생성한다")
        void createUpdateContext_WithoutOptionals_ReturnsContext() {
            // given
            Long sellerId = 1L;
            UpdateSellerCommand command =
                    SellerCommandFixtures.updateSellerCommandWithoutOptionals(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerId, SellerUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().logoUrl()).isNull();
            assertThat(result.updateData().description()).isNull();
        }
    }

    @Nested
    @DisplayName(
            "createUpdateContext(UpdateSellerBusinessInfoCommand) - BusinessInfo UpdateContext 생성")
    class CreateUpdateContextBusinessInfoTest {

        @Test
        @DisplayName("UpdateSellerBusinessInfoCommand로 UpdateContext를 생성한다")
        void createUpdateContext_ReturnsContext() {
            // given
            Long sellerId = 1L;
            UpdateSellerBusinessInfoCommand command =
                    SellerCommandFixtures.updateBusinessInfoCommand(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerId, SellerBusinessInfoUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(sellerId);
            assertThat(result.updateData().registrationNumber().value())
                    .isEqualTo(command.registrationNumber());
            assertThat(result.updateData().companyName().value()).isEqualTo(command.companyName());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우에도 UpdateContext를 생성한다")
        void createUpdateContext_WithoutOptionals_ReturnsContext() {
            // given
            Long sellerId = 1L;
            UpdateSellerBusinessInfoCommand command =
                    SellerCommandFixtures.updateBusinessInfoCommandWithoutOptionals(sellerId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SellerId, SellerBusinessInfoUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().saleReportNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("createSellerUpdateData() - SellerUpdateData 생성")
    class CreateSellerUpdateDataTest {

        @Test
        @DisplayName("SellerInfoCommand로 SellerUpdateData를 생성한다")
        void createSellerUpdateData_ReturnsUpdateData() {
            // given
            UpdateSellerFullCommand.SellerInfoCommand info =
                    SellerCommandFixtures.updateFullSellerInfoCommand();

            // when
            SellerUpdateData result = sut.createSellerUpdateData(info);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerName().value()).isEqualTo(info.sellerName());
            assertThat(result.displayName().value()).isEqualTo(info.displayName());
            assertThat(result.logoUrl().value()).isEqualTo(info.logoUrl());
            assertThat(result.description().value()).isEqualTo(info.description());
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우에도 SellerUpdateData를 생성한다")
        void createSellerUpdateData_WithoutOptionals_ReturnsUpdateData() {
            // given
            UpdateSellerFullCommand.SellerInfoCommand info =
                    SellerCommandFixtures.updateFullSellerInfoCommandWithoutOptionals();

            // when
            SellerUpdateData result = sut.createSellerUpdateData(info);

            // then
            assertThat(result).isNotNull();
            assertThat(result.logoUrl()).isNull();
            assertThat(result.description()).isNull();
        }
    }

    @Nested
    @DisplayName("createBusinessInfoUpdateData() - BusinessInfoUpdateData 생성")
    class CreateBusinessInfoUpdateDataTest {

        @Test
        @DisplayName("SellerBusinessInfoCommand로 BusinessInfoUpdateData를 생성한다")
        void createBusinessInfoUpdateData_ReturnsUpdateData() {
            // given
            UpdateSellerFullCommand.SellerBusinessInfoCommand info =
                    SellerCommandFixtures.updateFullBusinessInfoCommand();

            // when
            SellerBusinessInfoUpdateData result = sut.createBusinessInfoUpdateData(info);

            // then
            assertThat(result).isNotNull();
            assertThat(result.registrationNumber().value()).isEqualTo(info.registrationNumber());
            assertThat(result.companyName().value()).isEqualTo(info.companyName());
            assertThat(result.representative().value()).isEqualTo(info.representative());
            assertThat(result.saleReportNumber().value()).isEqualTo(info.saleReportNumber());
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우에도 BusinessInfoUpdateData를 생성한다")
        void createBusinessInfoUpdateData_WithoutOptionals_ReturnsUpdateData() {
            // given
            UpdateSellerFullCommand.SellerBusinessInfoCommand info =
                    SellerCommandFixtures.updateFullBusinessInfoCommandWithoutOptionals();

            // when
            SellerBusinessInfoUpdateData result = sut.createBusinessInfoUpdateData(info);

            // then
            assertThat(result).isNotNull();
            assertThat(result.saleReportNumber()).isNull();
        }
    }
}
