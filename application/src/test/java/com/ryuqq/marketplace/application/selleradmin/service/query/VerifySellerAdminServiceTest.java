package com.ryuqq.marketplace.application.selleradmin.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.selleradmin.SellerAdminQueryFixtures;
import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.exception.SellerAdminNotFoundException;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
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
@DisplayName("VerifySellerAdminService 단위 테스트")
class VerifySellerAdminServiceTest {

    private static final String SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    private static final String AUTH_USER_ID = "auth-user-123";

    @InjectMocks private VerifySellerAdminService sut;

    @Mock private SellerAdminReadManager readManager;

    private static SellerAdmin activeSellerAdmin() {
        SellerAdminId id = SellerAdminId.of(SELLER_ADMIN_ID);
        LoginId loginId = LoginId.of("admin@test.com");
        AdminName name = AdminName.of(SellerAdminQueryFixtures.DEFAULT_NAME);
        return SellerAdmin.forNew(
                id,
                CommonVoFixtures.defaultSellerId(),
                AUTH_USER_ID,
                loginId,
                name,
                CommonVoFixtures.defaultPhoneNumber(),
                CommonVoFixtures.now());
    }

    @Nested
    @DisplayName("execute() - 셀러 관리자 본인 확인")
    class ExecuteTest {

        @Test
        @DisplayName("이름과 로그인 ID로 셀러 관리자가 존재하면 전체 정보를 반환한다")
        void execute_ExistingAdmin_ReturnsFullResult() {
            // given
            VerifySellerAdminQuery query = SellerAdminQueryFixtures.verifyQuery();
            SellerAdmin sellerAdmin = activeSellerAdmin();

            given(readManager.findByNameAndLoginId(query.name(), query.loginId()))
                    .willReturn(Optional.of(sellerAdmin));

            // when
            VerifySellerAdminResult result = sut.execute(query);

            // then
            assertThat(result.exists()).isTrue();
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.sellerAdminId()).isEqualTo(SELLER_ADMIN_ID);
            assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
            then(readManager).should().findByNameAndLoginId(query.name(), query.loginId());
        }

        @Test
        @DisplayName("이름과 로그인 ID로 셀러 관리자가 존재하지 않으면 SellerAdminNotFoundException을 던진다")
        void execute_NonExistingAdmin_ThrowsException() {
            // given
            VerifySellerAdminQuery query = SellerAdminQueryFixtures.verifyQuery("없는사람", "unknown");

            given(readManager.findByNameAndLoginId(query.name(), query.loginId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.execute(query))
                    .isInstanceOf(SellerAdminNotFoundException.class);
            then(readManager).should().findByNameAndLoginId(query.name(), query.loginId());
        }
    }
}
