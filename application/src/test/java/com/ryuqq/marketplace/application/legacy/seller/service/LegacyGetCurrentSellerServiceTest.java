package com.ryuqq.marketplace.application.legacy.seller.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.seller.LegacySellerQueryFixtures;
import com.ryuqq.marketplace.application.legacy.seller.manager.LegacySellerCompositionReadManager;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
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
@DisplayName("LegacyGetCurrentSellerService 단위 테스트")
class LegacyGetCurrentSellerServiceTest {

    @InjectMocks private LegacyGetCurrentSellerService sut;

    @Mock private LegacySellerCompositionReadManager compositionReadManager;

    @Nested
    @DisplayName("execute() - 레거시 현재 셀러 정보 조회")
    class ExecuteTest {

        @Test
        @DisplayName("셀러 ID로 SellerAdminCompositeResult를 반환한다")
        void execute_ValidSellerId_ReturnsSellerAdminCompositeResult() {
            // given
            long sellerId = 1L;
            SellerAdminCompositeResult expected =
                    LegacySellerQueryFixtures.sellerAdminCompositeResult(sellerId);

            given(compositionReadManager.getAdminComposite(sellerId)).willReturn(expected);

            // when
            SellerAdminCompositeResult result = sut.execute(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.seller().id()).isEqualTo(sellerId);
            then(compositionReadManager).should().getAdminComposite(sellerId);
        }

        @Test
        @DisplayName("조회 결과의 셀러 정보가 올바르게 반환된다")
        void execute_ValidSellerId_ReturnsCorrectSellerInfo() {
            // given
            long sellerId = 42L;
            SellerAdminCompositeResult expected =
                    LegacySellerQueryFixtures.sellerAdminCompositeResult(sellerId);

            given(compositionReadManager.getAdminComposite(sellerId)).willReturn(expected);

            // when
            SellerAdminCompositeResult result = sut.execute(sellerId);

            // then
            assertThat(result.seller().id()).isEqualTo(sellerId);
            assertThat(result.seller().active()).isTrue();
            assertThat(result.businessInfo()).isNotNull();
            assertThat(result.csInfo()).isNotNull();
            assertThat(result.contractInfo()).isNotNull();
            assertThat(result.settlementInfo()).isNotNull();
            then(compositionReadManager).should().getAdminComposite(sellerId);
        }
    }
}
