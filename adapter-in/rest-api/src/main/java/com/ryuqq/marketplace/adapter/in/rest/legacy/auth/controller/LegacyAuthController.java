package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_ADMIN_VALIDATION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_APPROVAL_STATUS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_AUTHENTICATION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_SELLER;

import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyAdminApprovalStatusRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyAdminInsertRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAdministratorResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 인증 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyAuthController {

    @PostMapping(AUTH_AUTHENTICATION)
    public ResponseEntity<LegacyApiResponse<LegacyAuthTokenResponse>> getAccessToken(
            @RequestBody LegacyCreateAuthTokenRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(AUTH)
    public ResponseEntity<LegacyApiResponse<Long>> insertAdmin(
            @RequestBody LegacyAdminInsertRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(AUTH_ID)
    public ResponseEntity<LegacyApiResponse<Long>> updateAdmin(
            @PathVariable long authId, @RequestBody LegacyAdminInsertRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyAdministratorResponse>>>
            getAdmins(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH_ADMIN_VALIDATION)
    public ResponseEntity<LegacyApiResponse<Boolean>> getAdminValidation(
            @RequestParam String email) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH_SELLER)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyAdministratorResponse>>>
            getAdminsBySellerId(@PathVariable long sellerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(AUTH_APPROVAL_STATUS)
    public ResponseEntity<LegacyApiResponse<List<Long>>> updateApprovalStatus(
            @RequestBody LegacyAdminApprovalStatusRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
