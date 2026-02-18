package com.ryuqq.marketplace.adapter.out.client.authhub.adapter;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.exception.AuthHubBadRequestException;
import com.ryuqq.authhub.sdk.exception.AuthHubException;
import com.ryuqq.authhub.sdk.exception.AuthHubServerException;
import com.ryuqq.authhub.sdk.model.auth.ChangePasswordRequest;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.mapper.AuthHubSellerAdminIdentityMapper;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminIdentityProvisioningResult;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import java.security.SecureRandom;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * AuthHub 셀러 관리자 Identity 서비스 클라이언트 어댑터.
 *
 * <p>AuthHub SDK를 사용하여 셀러 관리자 사용자를 생성하고 비밀번호를 관리합니다.
 *
 * <p>Outbox 패턴과 함께 사용되며, 멱등키를 통해 중복 요청을 방지합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class AuthHubSellerAdminIdentityClientAdapter implements SellerAdminIdentityClient {

    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 16;

    private final UserApi userApi;
    private final AuthApi authApi;
    private final AuthHubSellerAdminIdentityMapper mapper;
    private final SecureRandom secureRandom;

    public AuthHubSellerAdminIdentityClientAdapter(
            UserApi userApi, AuthApi authApi, AuthHubSellerAdminIdentityMapper mapper) {
        this.userApi = userApi;
        this.authApi = authApi;
        this.mapper = mapper;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public SellerAdminIdentityProvisioningResult provisionSellerAdminIdentity(
            SellerAdminAuthOutbox outbox) {
        String tempPassword = generateTempPassword();
        CreateUserWithRolesRequest request =
                mapper.toCreateUserRequest(outbox.payload(), tempPassword);

        try {
            ApiResponse<CreateUserWithRolesResponse> response =
                    userApi.createUserWithRoles(request);

            return mapper.toSuccessResult(response.data());

        } catch (AuthHubBadRequestException e) {
            return mapper.toPermanentFailure("BAD_REQUEST", e.getMessage());

        } catch (AuthHubServerException e) {
            return mapper.toRetryableFailure("SERVER_ERROR", e.getMessage());

        } catch (AuthHubException e) {
            boolean retryable = e instanceof AuthHubServerException;
            if (retryable) {
                return mapper.toRetryableFailure("AUTHHUB_ERROR", e.getMessage());
            }
            return mapper.toPermanentFailure("AUTHHUB_ERROR", e.getMessage());
        }
    }

    @Override
    public void resetSellerAdminPassword(String authUserId) {
        String tempPassword = generateTempPassword();
        authApi.changePassword(authUserId, new ChangePasswordRequest(null, tempPassword));
    }

    @Override
    public void changeSellerAdminPassword(String authUserId, String newPassword) {
        authApi.changePassword(authUserId, new ChangePasswordRequest(null, newPassword));
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(
                    TEMP_PASSWORD_CHARS.charAt(secureRandom.nextInt(TEMP_PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
