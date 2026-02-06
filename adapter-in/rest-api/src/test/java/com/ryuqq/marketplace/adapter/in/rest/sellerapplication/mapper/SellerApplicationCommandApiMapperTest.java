package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.ApplySellerApplicationApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.command.RejectSellerApplicationApiRequest;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApproveSellerApplicationCommand;
import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplicationCommandApiMapper 단위 테스트")
class SellerApplicationCommandApiMapperTest {

    private SellerApplicationCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerApplicationCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand() - 신청 요청 변환")
    class ToCommandTest {

        @Test
        @DisplayName("ApplySellerApplicationApiRequest를 ApplySellerApplicationCommand로 변환한다")
        void toCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ApplySellerApplicationApiRequest request = SellerApplicationApiFixtures.applyRequest();

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.sellerInfo().sellerName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SELLER_NAME);
            assertThat(command.sellerInfo().displayName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_DISPLAY_NAME);
            assertThat(command.sellerInfo().logoUrl())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_LOGO_URL);
            assertThat(command.sellerInfo().description())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_DESCRIPTION);
        }

        @Test
        @DisplayName("사업자 정보를 변환한다")
        void toCommand_ConvertsBusinessInfo_ReturnsBusinessCommand() {
            // given
            ApplySellerApplicationApiRequest request = SellerApplicationApiFixtures.applyRequest();

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.businessInfo().registrationNumber())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_REGISTRATION_NUMBER);
            assertThat(command.businessInfo().companyName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_COMPANY_NAME);
            assertThat(command.businessInfo().representative())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_REPRESENTATIVE);
            assertThat(command.businessInfo().saleReportNumber())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SALE_REPORT_NUMBER);
            assertThat(command.businessInfo().businessAddress().zipCode())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_ZIP_CODE);
        }

        @Test
        @DisplayName("CS 연락처를 변환한다")
        void toCommand_ConvertsCsContact_ReturnsCsCommand() {
            // given
            ApplySellerApplicationApiRequest request = SellerApplicationApiFixtures.applyRequest();

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.csContact().phone())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_PHONE);
            assertThat(command.csContact().email())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_EMAIL);
            assertThat(command.csContact().mobile())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_MOBILE);
        }

        @Test
        @DisplayName("정산 정보를 변환한다")
        void toCommand_ConvertsSettlementInfo_ReturnsSettlementCommand() {
            // given
            ApplySellerApplicationApiRequest request = SellerApplicationApiFixtures.applyRequest();

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.settlementInfo().bankCode())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_BANK_CODE);
            assertThat(command.settlementInfo().bankName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_BANK_NAME);
            assertThat(command.settlementInfo().accountNumber())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_ACCOUNT_NUMBER);
            assertThat(command.settlementInfo().accountHolderName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_ACCOUNT_HOLDER);
            assertThat(command.settlementInfo().settlementCycle())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SETTLEMENT_CYCLE);
            assertThat(command.settlementInfo().settlementDay())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SETTLEMENT_DAY);
        }

        @Test
        @DisplayName("정산 주기·정산일이 null이면 command에 기본값 MONTHLY, 1로 넣는다")
        void toCommand_WhenSettlementCycleAndDayNull_FillsDefaults() {
            // given
            ApplySellerApplicationApiRequest request =
                    new ApplySellerApplicationApiRequest(
                            SellerApplicationApiFixtures.defaultSellerInfo(),
                            SellerApplicationApiFixtures.defaultBusinessInfo(),
                            SellerApplicationApiFixtures.defaultCsContact(),
                            SellerApplicationApiFixtures.defaultContactInfo(),
                            SellerApplicationApiFixtures.settlementInfoWithNullCycleAndDay());

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.settlementInfo().settlementCycle()).isEqualTo("MONTHLY");
            assertThat(command.settlementInfo().settlementDay()).isEqualTo(1);
        }

        @Test
        @DisplayName("displayName이 blank이면 sellerName으로 대체한다")
        void toCommand_BlankDisplayName_UsesSellerName() {
            // given
            ApplySellerApplicationApiRequest request =
                    new ApplySellerApplicationApiRequest(
                            SellerApplicationApiFixtures.sellerInfoWithBlankDisplayName(),
                            SellerApplicationApiFixtures.defaultBusinessInfo(),
                            SellerApplicationApiFixtures.defaultCsContact(),
                            SellerApplicationApiFixtures.defaultContactInfo(),
                            SellerApplicationApiFixtures.defaultSettlementInfo());

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.sellerInfo().displayName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SELLER_NAME);
        }

        @Test
        @DisplayName("displayName이 null이면 sellerName으로 대체한다")
        void toCommand_NullDisplayName_UsesSellerName() {
            // given
            ApplySellerApplicationApiRequest request =
                    new ApplySellerApplicationApiRequest(
                            SellerApplicationApiFixtures.sellerInfoWithNullDisplayName(),
                            SellerApplicationApiFixtures.defaultBusinessInfo(),
                            SellerApplicationApiFixtures.defaultCsContact(),
                            SellerApplicationApiFixtures.defaultContactInfo(),
                            SellerApplicationApiFixtures.defaultSettlementInfo());

            // when
            ApplySellerApplicationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.sellerInfo().displayName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SELLER_NAME);
        }
    }

    @Nested
    @DisplayName("toApproveCommand() - 승인 요청 변환")
    class ToApproveCommandTest {

        @Test
        @DisplayName("applicationId를 ApproveSellerApplicationCommand로 변환한다")
        void toApproveCommand_ConvertsId_ReturnsApproveCommand() {
            // given
            Long applicationId = 1L;

            // when
            ApproveSellerApplicationCommand command = mapper.toApproveCommand(applicationId);

            // then
            assertThat(command.sellerApplicationId()).isEqualTo(1L);
            assertThat(command.processedBy()).isNull();
        }
    }

    @Nested
    @DisplayName("toRejectCommand() - 거절 요청 변환")
    class ToRejectCommandTest {

        @Test
        @DisplayName("RejectSellerApplicationApiRequest를 RejectSellerApplicationCommand로 변환한다")
        void toRejectCommand_ConvertsRequest_ReturnsRejectCommand() {
            // given
            Long applicationId = 1L;
            RejectSellerApplicationApiRequest request =
                    SellerApplicationApiFixtures.rejectRequest();

            // when
            RejectSellerApplicationCommand command = mapper.toRejectCommand(applicationId, request);

            // then
            assertThat(command.sellerApplicationId()).isEqualTo(1L);
            assertThat(command.rejectionReason())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_REJECTION_REASON);
            assertThat(command.processedBy()).isNull();
        }
    }
}
