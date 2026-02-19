package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_ADMIN_VALIDATION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_APPROVAL_STATUS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_AUTHENTICATION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.auth.LegacyAuthEndpoints.AUTH_SELLER;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyAdminApprovalStatusRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyAdminInsertRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
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

/** 세토프 레거시 인증 API 호환 컨트롤러. */
@RestController
public class LegacyAuthController {

    @PostMapping(AUTH_AUTHENTICATION)
    public ResponseEntity<ApiResponse<LegacyAuthTokenResponse>> getAccessToken(
            @RequestBody LegacyCreateAuthTokenRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(AUTH)
    public ResponseEntity<ApiResponse<Long>> insertAdmin(
            @RequestBody LegacyAdminInsertRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(AUTH_ID)
    public ResponseEntity<ApiResponse<Long>> updateAdmin(
            @PathVariable long authId, @RequestBody LegacyAdminInsertRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH)
    public ResponseEntity<ApiResponse<Object>> getAdmins(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH_ADMIN_VALIDATION)
    public ResponseEntity<ApiResponse<Boolean>> getAdminValidation(@RequestParam String email) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(AUTH_SELLER)
    public ResponseEntity<ApiResponse<Object>> getAdminsBySellerId(
            @PathVariable long sellerId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(AUTH_APPROVAL_STATUS)
    public ResponseEntity<ApiResponse<List<Long>>> updateApprovalStatus(
            @RequestBody LegacyAdminApprovalStatusRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
