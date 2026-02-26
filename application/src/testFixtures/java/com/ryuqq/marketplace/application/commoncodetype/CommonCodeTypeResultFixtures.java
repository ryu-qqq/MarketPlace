package com.ryuqq.marketplace.application.commoncodetype;

import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;

/**
 * CommonCodeType Result DTO 테스트 Fixtures.
 *
 * <p>Application Layer 테스트에서 Result DTO 생성에 사용됩니다.
 */
public final class CommonCodeTypeResultFixtures {

    private CommonCodeTypeResultFixtures() {}

    // ===== CommonCodeTypeResult Fixtures =====

    public static CommonCodeTypeResult result() {
        return CommonCodeTypeResult.from(CommonCodeTypeFixtures.activeCommonCodeType());
    }

    public static CommonCodeTypeResult result(CommonCodeType domain) {
        return CommonCodeTypeResult.from(domain);
    }

    public static List<CommonCodeTypeResult> results(List<CommonCodeType> domains) {
        return domains.stream().map(CommonCodeTypeResult::from).toList();
    }

    // ===== CommonCodeTypePageResult Fixtures =====

    public static CommonCodeTypePageResult pageResult() {
        return CommonCodeTypePageResult.of(List.of(result()), PageMeta.of(0, 20, 1));
    }

    public static CommonCodeTypePageResult pageResult(
            List<CommonCodeTypeResult> results, int page, int size, long totalElements) {
        return CommonCodeTypePageResult.of(results, PageMeta.of(page, size, totalElements));
    }

    public static CommonCodeTypePageResult emptyPageResult() {
        return CommonCodeTypePageResult.empty(20);
    }
}
