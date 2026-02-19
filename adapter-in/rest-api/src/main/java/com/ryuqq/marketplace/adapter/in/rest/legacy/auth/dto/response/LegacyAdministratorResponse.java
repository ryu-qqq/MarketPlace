package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response;

/** 세토프 AdministratorResponse 호환 응답 DTO. */
public record LegacyAdministratorResponse(
        long id,
        String email,
        String fullName,
        String phoneNumber,
        long sellerId,
        String sellerName,
        String approvalStatus) {}
