package com.ryuqq.marketplace.application.seller.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.assembler.SellerCompositeAssembler;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
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
@DisplayName("GetSellerForAdminService 단위 테스트")
class GetSellerForAdminServiceTest {

    @InjectMocks private GetSellerForAdminService sut;

    @Mock private SellerCompositionReadManager compositionReadManager;
    @Mock private SellerCompositeAssembler compositeAssembler;

    @Nested
    @DisplayName("execute() - Admin용 셀러 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("셀러 ID로 AdminComposite와 Policy 정보를 조합하여 반환한다")
        void execute_ReturnsFullCompositeResult() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();

            SellerAdminCompositeResult adminComposite = createAdminComposite(sellerId, now);
            SellerPolicyCompositeResult policyComposite =
                    new SellerPolicyCompositeResult(
                            sellerId, Collections.emptyList(), Collections.emptyList());

            SellerCompositeResult sellerComposite = createSellerComposite(sellerId, now);
            SellerFullCompositeResult expected =
                    new SellerFullCompositeResult(
                            sellerComposite,
                            policyComposite,
                            new SellerFullCompositeResult.ContractInfo(
                                    1L,
                                    BigDecimal.valueOf(10.0),
                                    LocalDate.now(),
                                    LocalDate.now().plusYears(1),
                                    "ACTIVE",
                                    null,
                                    now,
                                    now),
                            new SellerFullCompositeResult.SettlementInfo(
                                    1L,
                                    "088",
                                    "신한은행",
                                    "123-456-789",
                                    "테스트 주식회사",
                                    "MONTHLY",
                                    15,
                                    true,
                                    now,
                                    now,
                                    now));

            given(compositionReadManager.getAdminComposite(sellerId)).willReturn(adminComposite);
            given(compositionReadManager.getPolicyComposite(sellerId)).willReturn(policyComposite);
            given(compositeAssembler.assemble(adminComposite, policyComposite))
                    .willReturn(expected);

            // when
            SellerFullCompositeResult result = sut.execute(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.sellerComposite().seller().id()).isEqualTo(sellerId);
            then(compositionReadManager).should().getAdminComposite(sellerId);
            then(compositionReadManager).should().getPolicyComposite(sellerId);
            then(compositeAssembler).should().assemble(adminComposite, policyComposite);
        }
    }

    private SellerAdminCompositeResult createAdminComposite(Long sellerId, Instant now) {
        SellerAdminCompositeResult.SellerInfo sellerInfo =
                new SellerAdminCompositeResult.SellerInfo(
                        sellerId,
                        "테스트 셀러",
                        "테스트 스토어",
                        "http://example.com/logo.png",
                        "테스트 설명",
                        true,
                        now,
                        now);

        SellerAdminCompositeResult.BusinessInfo businessInfo =
                new SellerAdminCompositeResult.BusinessInfo(
                        1L,
                        "123-45-67890",
                        "테스트 주식회사",
                        "홍길동",
                        "2024-서울강남-0001",
                        "06141",
                        "서울시 강남구",
                        "테스트빌딩");

        SellerAdminCompositeResult.CsInfo csInfo =
                new SellerAdminCompositeResult.CsInfo(
                        1L,
                        "02-1234-5678",
                        "010-1234-5678",
                        "cs@test.com",
                        "09:00",
                        "18:00",
                        "MON,TUE,WED,THU,FRI",
                        "https://kakao.test");

        SellerAdminCompositeResult.ContractInfo contractInfo =
                new SellerAdminCompositeResult.ContractInfo(
                        1L,
                        BigDecimal.valueOf(10.0),
                        LocalDate.now(),
                        LocalDate.now().plusYears(1),
                        "ACTIVE",
                        null,
                        now,
                        now);

        SellerAdminCompositeResult.SettlementInfo settlementInfo =
                new SellerAdminCompositeResult.SettlementInfo(
                        1L,
                        "088",
                        "신한은행",
                        "123-456-789",
                        "테스트 주식회사",
                        "MONTHLY",
                        15,
                        true,
                        now,
                        now,
                        now);

        return new SellerAdminCompositeResult(
                sellerInfo, businessInfo, csInfo, contractInfo, settlementInfo);
    }

    private SellerCompositeResult createSellerComposite(Long sellerId, Instant now) {
        SellerCompositeResult.SellerInfo sellerInfo =
                new SellerCompositeResult.SellerInfo(
                        sellerId,
                        "테스트 셀러",
                        "테스트 스토어",
                        "http://example.com/logo.png",
                        "테스트 설명",
                        true,
                        now,
                        now);

        SellerCompositeResult.BusinessInfo businessInfo =
                new SellerCompositeResult.BusinessInfo(
                        1L,
                        "123-45-67890",
                        "테스트 주식회사",
                        "홍길동",
                        "2024-서울강남-0001",
                        "06141",
                        "서울시 강남구",
                        "테스트빌딩");

        SellerCompositeResult.CsInfo csInfo =
                new SellerCompositeResult.CsInfo(
                        1L,
                        "02-1234-5678",
                        "010-1234-5678",
                        "cs@test.com",
                        "09:00",
                        "18:00",
                        "MON,TUE,WED,THU,FRI",
                        "https://kakao.test");

        return new SellerCompositeResult(sellerInfo, businessInfo, csInfo);
    }
}
