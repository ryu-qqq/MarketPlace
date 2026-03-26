package com.ryuqq.marketplace.application.seller.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.dto.response.SellerCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPolicyCompositeResult;
import com.ryuqq.marketplace.application.seller.port.out.query.SellerCompositionQueryPort;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import java.time.Instant;
import java.util.Collections;
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
@DisplayName("SellerCompositionReadManager 단위 테스트")
class SellerCompositionReadManagerTest {

    @InjectMocks private SellerCompositionReadManager sut;

    @Mock private SellerCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("getSellerComposite() - 셀러 Composite 조회")
    class GetSellerCompositeTest {

        @Test
        @DisplayName("존재하는 셀러의 Composite를 조회한다")
        void getSellerComposite_Exists_ReturnsComposite() {
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

            SellerCompositeResult expected = new SellerCompositeResult(sellerInfo, null, null);

            given(compositionQueryPort.findSellerCompositeById(sellerId))
                    .willReturn(Optional.of(expected));

            // when
            SellerCompositeResult result = sut.getSellerComposite(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.seller().id()).isEqualTo(sellerId);
            then(compositionQueryPort).should().findSellerCompositeById(sellerId);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 조회 시 예외가 발생한다")
        void getSellerComposite_NotExists_ThrowsException() {
            // given
            Long sellerId = 999L;

            given(compositionQueryPort.findSellerCompositeById(sellerId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getSellerComposite(sellerId))
                    .isInstanceOf(SellerNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getPolicyComposite() - 정책 Composite 조회")
    class GetPolicyCompositeTest {

        @Test
        @DisplayName("존재하는 셀러의 정책 Composite를 조회한다")
        void getPolicyComposite_Exists_ReturnsComposite() {
            // given
            Long sellerId = 1L;
            SellerPolicyCompositeResult expected =
                    new SellerPolicyCompositeResult(
                            sellerId, Collections.emptyList(), Collections.emptyList());

            given(compositionQueryPort.findPolicyCompositeById(sellerId))
                    .willReturn(Optional.of(expected));

            // when
            SellerPolicyCompositeResult result = sut.getPolicyComposite(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findPolicyCompositeById(sellerId);
        }

        @Test
        @DisplayName("존재하지 않는 셀러의 정책 조회 시 예외가 발생한다")
        void getPolicyComposite_NotExists_ThrowsException() {
            // given
            Long sellerId = 999L;

            given(compositionQueryPort.findPolicyCompositeById(sellerId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getPolicyComposite(sellerId))
                    .isInstanceOf(SellerNotFoundException.class);
        }
    }
}
