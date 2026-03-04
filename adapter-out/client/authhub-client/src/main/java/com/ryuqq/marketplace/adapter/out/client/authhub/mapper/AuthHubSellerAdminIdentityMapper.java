package com.ryuqq.marketplace.adapter.out.client.authhub.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesResponse;
import com.ryuqq.marketplace.adapter.out.client.authhub.config.AuthHubProperties;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminIdentityProvisioningResult;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * AuthHub SellerAdmin Identity Mapper.
 *
 * <p>셀러 관리자 Outbox 페이로드와 AuthHub SDK 객체 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class AuthHubSellerAdminIdentityMapper {

    private static final List<String> DEFAULT_ROLES = List.of("ADMIN");

    private final ObjectMapper objectMapper;
    private final String serviceCode;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring-managed singleton bean, immutable after injection")
    public AuthHubSellerAdminIdentityMapper(
            ObjectMapper objectMapper, AuthHubProperties authHubProperties) {
        this.objectMapper = objectMapper;
        this.serviceCode = authHubProperties.getServiceCode();
    }

    /**
     * Outbox 페이로드를 SDK 사용자 생성 요청으로 변환합니다.
     *
     * <p>페이로드에서 loginId, phoneNumber, organizationId를 추출하여 CreateUserWithRolesRequest를 생성합니다.
     *
     * @param payload JSON 페이로드
     * @param tempPassword 임시 비밀번호
     * @return SDK CreateUserWithRoles 요청
     * @throws IllegalArgumentException 페이로드 파싱 실패 시
     */
    public CreateUserWithRolesRequest toCreateUserRequest(String payload, String tempPassword) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String organizationId = getTextOrThrow(node, "organizationId");
            String loginId = getTextOrThrow(node, "loginId");
            String phoneNumber = node.has("phoneNumber") ? node.get("phoneNumber").asText() : null;

            return new CreateUserWithRolesRequest(
                    organizationId, loginId, phoneNumber, tempPassword, serviceCode, DEFAULT_ROLES);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse outbox payload: " + e.getMessage(), e);
        }
    }

    /**
     * SDK 응답을 성공 결과로 변환합니다.
     *
     * @param response SDK CreateUserWithRoles 응답
     * @return SellerAdmin Identity Provisioning 성공 결과
     */
    public SellerAdminIdentityProvisioningResult toSuccessResult(
            CreateUserWithRolesResponse response) {
        return SellerAdminIdentityProvisioningResult.success(response.userId());
    }

    /**
     * 영구 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return SellerAdmin Identity Provisioning 영구 실패 결과
     */
    public SellerAdminIdentityProvisioningResult toPermanentFailure(
            String errorCode, String errorMessage) {
        return SellerAdminIdentityProvisioningResult.permanentFailure(errorCode, errorMessage);
    }

    /**
     * 재시도 가능한 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return SellerAdmin Identity Provisioning 재시도 가능 실패 결과
     */
    public SellerAdminIdentityProvisioningResult toRetryableFailure(
            String errorCode, String errorMessage) {
        return SellerAdminIdentityProvisioningResult.retryableFailure(errorCode, errorMessage);
    }

    private String getTextOrThrow(JsonNode node, String fieldName) {
        if (!node.has(fieldName) || node.get(fieldName).isNull()) {
            throw new IllegalArgumentException(
                    "Required field missing in outbox payload: " + fieldName);
        }
        return node.get(fieldName).asText();
    }
}
