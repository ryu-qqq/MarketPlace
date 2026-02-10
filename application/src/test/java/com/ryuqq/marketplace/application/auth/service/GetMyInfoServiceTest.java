package com.ryuqq.marketplace.application.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.auth.AuthResultFixtures;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.manager.AuthManager;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetMyInfoService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyInfoService 단위 테스트")
class GetMyInfoServiceTest {

    @InjectMocks private GetMyInfoService sut;

    @Mock private AuthManager authManager;

    @Mock private SellerAdminReadManager sellerAdminReadManager;

    @Nested
    @DisplayName("execute() - 내 정보 조회")
    class ExecuteTest {

        @Test
        @DisplayName("셀러 관리자가 존재하면 핸드폰 번호와 셀러 ID를 포함한 결과를 반환한다")
        void execute_WithSellerAdmin_ReturnsEnrichedResult() {
            // given
            String accessToken = "test-access-token";
            MyInfoResult authInfo = AuthResultFixtures.myInfoResultWithEmptyRoles();

            SellerAdmin sellerAdmin =
                    SellerAdmin.forNew(
                            new SellerAdminId("sa-001"),
                            new SellerId(100L),
                            authInfo.userId(),
                            new LoginId("admin@example.com"),
                            new AdminName("관리자"),
                            new PhoneNumber("010-1234-5678"),
                            Instant.now());

            given(authManager.getMyInfo(accessToken)).willReturn(authInfo);
            given(sellerAdminReadManager.findByAuthUserId(authInfo.userId()))
                    .willReturn(Optional.of(sellerAdmin));

            // when
            MyInfoResult result = sut.execute(accessToken);

            // then
            assertThat(result.sellerId()).isEqualTo(100L);
            assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(result.userId()).isEqualTo(authInfo.userId());
            then(authManager).should().getMyInfo(accessToken);
            then(sellerAdminReadManager).should().findByAuthUserId(authInfo.userId());
        }

        @Test
        @DisplayName("셀러 관리자가 없으면 AuthHub 정보만 반환한다")
        void execute_WithoutSellerAdmin_ReturnsAuthInfoOnly() {
            // given
            String accessToken = "test-access-token";
            MyInfoResult authInfo = AuthResultFixtures.myInfoResultWithEmptyRoles();

            given(authManager.getMyInfo(accessToken)).willReturn(authInfo);
            given(sellerAdminReadManager.findByAuthUserId(authInfo.userId()))
                    .willReturn(Optional.empty());

            // when
            MyInfoResult result = sut.execute(accessToken);

            // then
            assertThat(result.sellerId()).isNull();
            assertThat(result.phoneNumber()).isNull();
            assertThat(result.userId()).isEqualTo(authInfo.userId());
        }
    }
}
