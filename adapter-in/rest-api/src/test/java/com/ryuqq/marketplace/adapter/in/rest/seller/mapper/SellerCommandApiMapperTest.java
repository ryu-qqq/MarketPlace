package com.ryuqq.marketplace.adapter.in.rest.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.seller.SellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.RegisterSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.command.UpdateSellerFullApiRequest;
import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerCommandApiMapper 단위 테스트")
class SellerCommandApiMapperTest {

    private SellerCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(RegisterSellerApiRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterSellerApiRequest를 RegisterSellerCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterSellerApiRequest request = SellerApiFixtures.registerRequest();

            // when
            RegisterSellerCommand command = mapper.toCommand(request);

            // then
            assertThat(command.seller()).isNotNull();
            assertThat(command.seller().sellerName()).isEqualTo("테스트셀러");
            assertThat(command.seller().displayName()).isEqualTo("테스트 브랜드");
            assertThat(command.seller().logoUrl()).isEqualTo("https://example.com/logo.png");
            assertThat(command.seller().description()).isEqualTo("테스트 셀러 설명입니다.");
        }

        @Test
        @DisplayName("BusinessInfo가 올바르게 변환된다")
        void toCommand_ConvertsBusinessInfo_ReturnsBusinessCommand() {
            // given
            RegisterSellerApiRequest request = SellerApiFixtures.registerRequest();

            // when
            RegisterSellerCommand command = mapper.toCommand(request);

            // then
            assertThat(command.businessInfo()).isNotNull();
            assertThat(command.businessInfo().registrationNumber()).isEqualTo("123-45-67890");
            assertThat(command.businessInfo().companyName()).isEqualTo("테스트컴퍼니");
            assertThat(command.businessInfo().representative()).isEqualTo("홍길동");
            assertThat(command.businessInfo().saleReportNumber()).isEqualTo("제2025-서울강남-1234호");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateSellerApiRequest) - 기본정보 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateSellerApiRequest를 UpdateSellerCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerApiRequest request = SellerApiFixtures.updateRequest();

            // when
            UpdateSellerCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(10L);
            assertThat(command.sellerName()).isEqualTo("수정된 셀러명");
            assertThat(command.displayName()).isEqualTo("수정된 표시명");
            assertThat(command.logoUrl()).isEqualTo("https://example.com/new-logo.png");
            assertThat(command.description()).isEqualTo("수정된 설명");
        }

        @Test
        @DisplayName("CsInfo 정보가 올바르게 변환된다")
        void toCommand_ConvertsCsInfo_ReturnsCsCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerApiRequest request = SellerApiFixtures.updateRequest();

            // when
            UpdateSellerCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.csInfo()).isNotNull();
            assertThat(command.csInfo().phone()).isEqualTo("02-9999-8888");
            assertThat(command.csInfo().email()).isEqualTo("newcs@example.com");
            assertThat(command.csInfo().mobile()).isEqualTo("010-9999-8888");
        }

        @Test
        @DisplayName("BusinessInfo 정보가 올바르게 변환된다")
        void toCommand_ConvertsBusinessInfo_ReturnsBusinessCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerApiRequest request = SellerApiFixtures.updateRequest();

            // when
            UpdateSellerCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.businessInfo()).isNotNull();
            assertThat(command.businessInfo().registrationNumber()).isEqualTo("999-88-77777");
            assertThat(command.businessInfo().companyName()).isEqualTo("새로운컴퍼니");
            assertThat(command.businessInfo().representative()).isEqualTo("김철수");
            assertThat(command.businessInfo().saleReportNumber()).isEqualTo("제2025-서울종로-9999호");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateSellerFullApiRequest) - 전체 수정 요청 변환")
    class ToUpdateFullCommandTest {

        @Test
        @DisplayName("UpdateSellerFullApiRequest를 UpdateSellerFullCommand로 변환한다")
        void toCommand_ConvertsFullRequest_ReturnsFullCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            // when
            UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(10L);
            assertThat(command.seller()).isNotNull();
            assertThat(command.seller().sellerName()).isEqualTo("전체수정 셀러명");
            assertThat(command.seller().displayName()).isEqualTo("전체수정 표시명");
        }

        @Test
        @DisplayName("BusinessInfo가 올바르게 변환된다")
        void toCommand_ConvertsBusinessInfo_ReturnsBusinessCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            // when
            UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.businessInfo()).isNotNull();
            assertThat(command.businessInfo().registrationNumber()).isEqualTo("777-66-55555");
            assertThat(command.businessInfo().companyName()).isEqualTo("전체수정컴퍼니");
            assertThat(command.businessInfo().representative()).isEqualTo("이영희");
        }

        @Test
        @DisplayName("CsInfo가 올바르게 변환된다")
        void toCommand_ConvertsCsInfo_ReturnsCsCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            // when
            UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.csInfo()).isNotNull();
            assertThat(command.csInfo().csContact()).isNotNull();
            assertThat(command.csInfo().csContact().phone()).isEqualTo("02-7777-6666");
            assertThat(command.csInfo().operatingHours()).isNotNull();
            assertThat(command.csInfo().operatingDays()).isEqualTo("MON,TUE,WED,THU,FRI");
            assertThat(command.csInfo().kakaoChannelUrl())
                    .isEqualTo("https://kakao.com/channel/test");
        }

        @Test
        @DisplayName("ContractInfo가 올바르게 변환된다")
        void toCommand_ConvertsContractInfo_ReturnsContractCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            // when
            UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.contractInfo()).isNotNull();
            assertThat(command.contractInfo().commissionRate()).isEqualTo(15.5);
            assertThat(command.contractInfo().specialTerms()).isEqualTo("특약사항입니다");
        }

        @Test
        @DisplayName("SettlementInfo가 올바르게 변환된다")
        void toCommand_ConvertsSettlementInfo_ReturnsSettlementCommand() {
            // given
            Long sellerId = 10L;
            UpdateSellerFullApiRequest request = SellerApiFixtures.updateFullRequest();

            // when
            UpdateSellerFullCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.settlementInfo()).isNotNull();
            assertThat(command.settlementInfo().bankAccount()).isNotNull();
            assertThat(command.settlementInfo().bankAccount().bankCode()).isEqualTo("004");
            assertThat(command.settlementInfo().bankAccount().bankName()).isEqualTo("KB국민은행");
            assertThat(command.settlementInfo().settlementCycle()).isEqualTo("WEEKLY");
            assertThat(command.settlementInfo().settlementDay()).isEqualTo(5);
        }
    }
}
