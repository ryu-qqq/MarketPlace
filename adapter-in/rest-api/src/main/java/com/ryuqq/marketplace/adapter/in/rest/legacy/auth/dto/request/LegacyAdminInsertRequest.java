package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request;

/** 세토프 AdministratorsInsertRequestDto 호환 요청 DTO. */
public record LegacyAdminInsertRequest(
        String roleType,
        String passwordHash,
        String email,
        String fullName,
        String phoneNumber,
        long sellerId) {}
