package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;

/**
 * Legacy Notice API 테스트 Fixtures.
 *
 * <p>Legacy 고시정보 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyNoticeApiFixtures {

    private LegacyNoticeApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final String DEFAULT_MATERIAL = "면 100%";
    public static final String DEFAULT_COLOR = "블랙";
    public static final String DEFAULT_SIZE = "FREE";
    public static final String DEFAULT_MAKER = "자체제작";
    public static final String DEFAULT_ORIGIN = "대한민국";
    public static final String DEFAULT_WASHING_METHOD = "손세탁";
    public static final String DEFAULT_YEAR_MONTH = "2024-01";
    public static final String DEFAULT_ASSURANCE_STANDARD = "KC 인증";
    public static final String DEFAULT_AS_PHONE = "02-1234-5678";

    // ===== Request Fixtures =====

    public static LegacyCreateProductNoticeRequest request() {
        return new LegacyCreateProductNoticeRequest(
                DEFAULT_MATERIAL,
                DEFAULT_COLOR,
                DEFAULT_SIZE,
                DEFAULT_MAKER,
                DEFAULT_ORIGIN,
                DEFAULT_WASHING_METHOD,
                DEFAULT_YEAR_MONTH,
                DEFAULT_ASSURANCE_STANDARD,
                DEFAULT_AS_PHONE);
    }

    public static LegacyCreateProductNoticeRequest requestWithNulls() {
        return new LegacyCreateProductNoticeRequest(
                null, null, null, null, null, null, null, null, null);
    }

    public static LegacyCreateProductNoticeRequest requestWith(
            String material, String color, String size) {
        return new LegacyCreateProductNoticeRequest(
                material,
                color,
                size,
                DEFAULT_MAKER,
                DEFAULT_ORIGIN,
                DEFAULT_WASHING_METHOD,
                DEFAULT_YEAR_MONTH,
                DEFAULT_ASSURANCE_STANDARD,
                DEFAULT_AS_PHONE);
    }

    // ===== Command Fixtures =====

    public static LegacyUpdateNoticeCommand command(long productGroupId) {
        return new LegacyUpdateNoticeCommand(
                productGroupId,
                DEFAULT_MATERIAL,
                DEFAULT_COLOR,
                DEFAULT_SIZE,
                DEFAULT_MAKER,
                DEFAULT_ORIGIN,
                DEFAULT_WASHING_METHOD,
                DEFAULT_YEAR_MONTH,
                DEFAULT_ASSURANCE_STANDARD,
                DEFAULT_AS_PHONE);
    }

    public static LegacyUpdateNoticeCommand commandWithEmptyValues(long productGroupId) {
        return new LegacyUpdateNoticeCommand(productGroupId, "", "", "", "", "", "", "", "", "");
    }
}
