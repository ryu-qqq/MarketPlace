package com.ryuqq.marketplace.application.seller.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
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
@DisplayName("GetSellerPublicProfileService 단위 테스트")
class GetSellerPublicProfileServiceTest {

    @InjectMocks private GetSellerPublicProfileService sut;

    @Mock private SellerCompositionReadManager compositionReadManager;

    @Nested
    @DisplayName("execute() - 셀러 공개 프로필 조회")
    class ExecuteTest {

        @Test
        @DisplayName("존재하는 셀러의 공개 프로필(셀러명, 표시명, 회사명, 대표자명)을 반환한다")
        void execute_ExistingSeller_ReturnsPublicProfile() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();

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

            SellerCompositeResult composite =
                    new SellerCompositeResult(sellerInfo, businessInfo, csInfo);

            given(compositionReadManager.getSellerComposite(sellerId)).willReturn(composite);

            // when
            SellerPublicProfileResult result = sut.execute(sellerId);

            // then
            assertThat(result.sellerName()).isEqualTo("테스트 셀러");
            assertThat(result.displayName()).isEqualTo("테스트 스토어");
            assertThat(result.companyName()).isEqualTo("테스트 주식회사");
            assertThat(result.representative()).isEqualTo("홍길동");
            then(compositionReadManager).should().getSellerComposite(sellerId);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 조회 시 예외가 발생한다")
        void execute_NonExistingSeller_ThrowsException() {
            // given
            Long sellerId = 999L;

            given(compositionReadManager.getSellerComposite(sellerId))
                    .willThrow(new SellerNotFoundException(sellerId));

            // when & then
            assertThatThrownBy(() -> sut.execute(sellerId))
                    .isInstanceOf(SellerNotFoundException.class);
        }
    }
}
