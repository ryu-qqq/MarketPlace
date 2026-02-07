package com.ryuqq.marketplace.application.selleradmin.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.manager.SellerAdminReadManager;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
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
@DisplayName("ChangeSellerAdminPasswordService 단위 테스트")
class ChangeSellerAdminPasswordServiceTest {

    private static final String SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    private static final String AUTH_USER_ID = "auth-user-123";
    private static final String NEW_PASSWORD = "NewPass123!";

    @InjectMocks private ChangeSellerAdminPasswordService sut;

    @Mock private SellerAdminReadManager readManager;
    @Mock private SellerAdminIdentityClient identityClient;

    private static SellerAdmin activeSellerAdminWithAuth() {
        SellerAdminId id = SellerAdminId.of(SELLER_ADMIN_ID);
        SellerId sellerId = CommonVoFixtures.defaultSellerId();
        LoginId loginId = LoginId.of("admin@test.com");
        AdminName name = AdminName.of("홍길동");
        return SellerAdmin.forNew(
                id,
                sellerId,
                AUTH_USER_ID,
                loginId,
                name,
                CommonVoFixtures.defaultPhoneNumber(),
                CommonVoFixtures.now());
    }

    @Nested
    @DisplayName("execute() - 비밀번호 변경")
    class ExecuteTest {

        @Test
        @DisplayName("ACTIVE이고 authUserId가 있으면 IdentityClient에 새 비밀번호로 변경을 요청한다")
        void execute_ActiveAdmin_CallsIdentityClient() {
            // given
            SellerAdmin sellerAdmin = activeSellerAdminWithAuth();
            ChangeSellerAdminPasswordCommand command =
                    new ChangeSellerAdminPasswordCommand(SELLER_ADMIN_ID, NEW_PASSWORD);

            given(readManager.getById(SellerAdminId.of(SELLER_ADMIN_ID))).willReturn(sellerAdmin);
            doNothing().when(identityClient).changeSellerAdminPassword(AUTH_USER_ID, NEW_PASSWORD);

            // when
            sut.execute(command);

            // then
            then(readManager).should().getById(SellerAdminId.of(SELLER_ADMIN_ID));
            then(identityClient).should().changeSellerAdminPassword(AUTH_USER_ID, NEW_PASSWORD);
        }
    }
}
