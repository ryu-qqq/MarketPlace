package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.application.legacy.description.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;

/**
 * Legacy Description API 테스트 Fixtures.
 *
 * <p>Legacy 상품그룹 상세설명 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyDescriptionApiFixtures {

    private LegacyDescriptionApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final String DEFAULT_DETAIL_DESCRIPTION = "<p>상품 상세 설명입니다.</p>";
    public static final String UPDATED_DETAIL_DESCRIPTION = "<p>수정된 상품 상세 설명입니다.</p>";

    // ===== Request Fixtures =====

    public static LegacyUpdateProductDescriptionRequest request() {
        return new LegacyUpdateProductDescriptionRequest(DEFAULT_DETAIL_DESCRIPTION);
    }

    public static LegacyUpdateProductDescriptionRequest requestWith(String detailDescription) {
        return new LegacyUpdateProductDescriptionRequest(detailDescription);
    }

    public static LegacyUpdateProductDescriptionRequest updatedRequest() {
        return new LegacyUpdateProductDescriptionRequest(UPDATED_DETAIL_DESCRIPTION);
    }

    // ===== Command Fixtures =====

    public static LegacyUpdateDescriptionCommand legacyCommand(long productGroupId) {
        return new LegacyUpdateDescriptionCommand(productGroupId, DEFAULT_DETAIL_DESCRIPTION);
    }

    public static UpdateProductGroupDescriptionCommand descriptionCommand(long productGroupId) {
        return new UpdateProductGroupDescriptionCommand(productGroupId, DEFAULT_DETAIL_DESCRIPTION);
    }
}
