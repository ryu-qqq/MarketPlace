package com.ryuqq.marketplace.application.legacy.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.auth.LegacyAuthFixtures;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacySellerAuthCompositeQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
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
@DisplayName("LegacySellerAuthCompositeReadManager 단위 테스트")
class LegacySellerAuthCompositeReadManagerTest {

    @InjectMocks private LegacySellerAuthCompositeReadManager sut;

    @Mock private LegacySellerAuthCompositeQueryPort queryPort;

    @Nested
    @DisplayName("getByEmail() - 이메일로 셀러 인증 정보 조회")
    class GetByEmailTest {

        @Test
        @DisplayName("존재하는 이메일로 셀러 인증 정보를 조회한다")
        void getByEmail_Exists_ReturnsAuthResult() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            LegacySellerAuthResult expected = LegacyAuthFixtures.approvedSellerAuthResult();

            given(queryPort.findByEmail(email)).willReturn(Optional.of(expected));

            // when
            LegacySellerAuthResult result = sut.getByEmail(email);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.sellerId()).isEqualTo(LegacyAuthFixtures.DEFAULT_SELLER_ID);
            assertThat(result.email()).isEqualTo(email);
            assertThat(result.roleType()).isEqualTo(LegacyAuthFixtures.DEFAULT_ROLE_TYPE);
            assertThat(result.isApproved()).isTrue();
            then(queryPort).should().findByEmail(email);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회 시 SellerAdminNotFoundException이 발생한다")
        void getByEmail_NotExists_ThrowsSellerAdminNotFoundException() {
            // given
            String email = "notfound@test.com";

            given(queryPort.findByEmail(email)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getByEmail(email))
                    .isInstanceOf(SellerAdminNotFoundException.class);
            then(queryPort).should().findByEmail(email);
        }

        @Test
        @DisplayName("미승인 셀러 이메일로 조회 시 인증 결과를 반환한다 (승인 여부 검증은 Validator 책임)")
        void getByEmail_PendingSeller_ReturnsAuthResult() {
            // given
            String email = LegacyAuthFixtures.DEFAULT_EMAIL;
            LegacySellerAuthResult pendingResult = LegacyAuthFixtures.pendingSellerAuthResult();

            given(queryPort.findByEmail(email)).willReturn(Optional.of(pendingResult));

            // when
            LegacySellerAuthResult result = sut.getByEmail(email);

            // then
            assertThat(result).isEqualTo(pendingResult);
            assertThat(result.isApproved()).isFalse();
            then(queryPort).should().findByEmail(email);
        }
    }
}
