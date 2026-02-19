package com.ryuqq.marketplace.application.externalsource;

import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;

/**
 * ExternalSource Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExternalSourceCommandFixtures {

    private ExternalSourceCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_CODE = "SETOF";
    public static final String DEFAULT_NAME = "세토프 레거시";
    public static final String DEFAULT_TYPE = "LEGACY";
    public static final String DEFAULT_DESCRIPTION = "레거시 Setof 상품 데이터 소스";

    // ===== RegisterExternalSourceCommand Fixtures =====

    public static RegisterExternalSourceCommand registerCommand() {
        return new RegisterExternalSourceCommand(
                DEFAULT_CODE, DEFAULT_NAME, DEFAULT_TYPE, DEFAULT_DESCRIPTION);
    }

    public static RegisterExternalSourceCommand registerCommand(
            String code, String name, String type, String description) {
        return new RegisterExternalSourceCommand(code, name, type, description);
    }

    public static RegisterExternalSourceCommand registerCommandWithoutDescription() {
        return new RegisterExternalSourceCommand(DEFAULT_CODE, DEFAULT_NAME, DEFAULT_TYPE, null);
    }

    public static RegisterExternalSourceCommand registerCrawlingCommand() {
        return new RegisterExternalSourceCommand(
                "COUPANG_CRAWL", "쿠팡 크롤링", "CRAWLING", "쿠팡 크롤링 데이터 소스");
    }

    public static RegisterExternalSourceCommand registerPartnerCommand() {
        return new RegisterExternalSourceCommand("PARTNER_A", "파트너 A", "PARTNER", "파트너 A 연동");
    }

    // ===== UpdateExternalSourceCommand Fixtures =====

    public static UpdateExternalSourceCommand updateCommand() {
        return new UpdateExternalSourceCommand(1L, "수정된 세토프 레거시", "ACTIVE", "수정된 설명");
    }

    public static UpdateExternalSourceCommand updateCommand(long externalSourceId) {
        return new UpdateExternalSourceCommand(externalSourceId, "수정된 세토프 레거시", "ACTIVE", "수정된 설명");
    }

    public static UpdateExternalSourceCommand updateCommandWithInactive(long externalSourceId) {
        return new UpdateExternalSourceCommand(externalSourceId, "비활성 소스", "INACTIVE", null);
    }

    public static UpdateExternalSourceCommand updateCommandWithoutDescription(
            long externalSourceId) {
        return new UpdateExternalSourceCommand(externalSourceId, "수정된 이름", "ACTIVE", null);
    }
}
