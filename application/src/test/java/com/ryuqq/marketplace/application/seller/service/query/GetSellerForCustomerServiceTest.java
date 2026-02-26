package com.ryuqq.marketplace.application.seller.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.dto.composite.SellerCompositeResult;
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
@DisplayName("GetSellerForCustomerService 단위 테스트")
class GetSellerForCustomerServiceTest {

    @InjectMocks private GetSellerForCustomerService sut;

    @Mock private SellerCompositionReadManager compositionReadManager;

    @Nested
    @DisplayName("execute() - 고객용 셀러 조회")
    class ExecuteTest {

        @Test
        @DisplayName("존재하는 셀러를 조회하면 SellerCompositeResult를 반환한다")
        void execute_ExistingSeller_ReturnsCompositeResult() {
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

            given(compositionReadManager.getSellerComposite(sellerId)).willReturn(expected);

            // when
            SellerCompositeResult result = sut.execute(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.seller().id()).isEqualTo(sellerId);
            assertThat(result.seller().sellerName()).isEqualTo("테스트 셀러");
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
