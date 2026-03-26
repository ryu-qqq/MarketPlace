package com.ryuqq.marketplace.application.legacy.seller.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.seller.LegacySellerQueryFixtures;
import com.ryuqq.marketplace.application.legacy.seller.port.out.LegacySellerCompositionQueryPort;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
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
@DisplayName("LegacySellerCompositionReadManager 단위 테스트")
class LegacySellerCompositionReadManagerTest {

    @InjectMocks private LegacySellerCompositionReadManager sut;

    @Mock private LegacySellerCompositionQueryPort queryPort;

    @Nested
    @DisplayName("getAdminComposite() - 레거시 셀러 Admin Composite 조회")
    class GetAdminCompositeTest {

        @Test
        @DisplayName("존재하는 셀러 ID로 AdminComposite를 조회한다")
        void getAdminComposite_Exists_ReturnsResult() {
            // given
            long sellerId = 1L;
            SellerAdminCompositeResult expected =
                    LegacySellerQueryFixtures.sellerAdminCompositeResult(sellerId);

            given(queryPort.findAdminCompositeById(sellerId)).willReturn(Optional.of(expected));

            // when
            SellerAdminCompositeResult result = sut.getAdminComposite(sellerId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.seller().id()).isEqualTo(sellerId);
            then(queryPort).should().findAdminCompositeById(sellerId);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회 시 SellerNotFoundException이 발생한다")
        void getAdminComposite_NotExists_ThrowsSellerNotFoundException() {
            // given
            long sellerId = 999L;

            given(queryPort.findAdminCompositeById(sellerId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getAdminComposite(sellerId))
                    .isInstanceOf(SellerNotFoundException.class);
            then(queryPort).should().findAdminCompositeById(sellerId);
        }
    }
}
