package com.ryuqq.marketplace.application.selleradmin.dto.command;

/**
 * 셀러 관리자 가입 신청 Command.
 *
 * <p>가입 신청에 필요한 정보를 전달합니다. 승인 전까지 authUserId는 null이며, 비밀번호는 승인 시 인증 서버로 전달됩니다.
 *
 * @param sellerId 셀러 ID
 * @param loginId 로그인 ID (이메일 형식 권장)
 * @param name 관리자 이름
 * @param phoneNumber 휴대폰 번호
 * @param password 비밀번호 (인증 서버 전달용)
 * @author ryu-qqq
 * @since 1.1.0
 */
public record ApplySellerAdminCommand(
        Long sellerId, String loginId, String name, String phoneNumber, String password) {}
