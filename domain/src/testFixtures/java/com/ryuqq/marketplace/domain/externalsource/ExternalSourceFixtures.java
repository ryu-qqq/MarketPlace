package com.ryuqq.marketplace.domain.externalsource;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceCode;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;

/** ExternalSource 도메인 테스트 Fixtures. */
public final class ExternalSourceFixtures {

    private ExternalSourceFixtures() {}

    // ===== ID Fixtures =====
    public static ExternalSourceId defaultId() {
        return ExternalSourceId.of(1L);
    }

    public static ExternalSourceId id(Long value) {
        return ExternalSourceId.of(value);
    }

    public static ExternalSourceId newId() {
        return ExternalSourceId.forNew();
    }

    // ===== VO Fixtures =====
    public static ExternalSourceCode defaultCode() {
        return ExternalSourceCode.of("SETOF");
    }

    public static ExternalSourceCode code(String value) {
        return ExternalSourceCode.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static ExternalSource newExternalSource() {
        return ExternalSource.forNew(
                defaultCode(),
                "세토프 레거시",
                ExternalSourceType.LEGACY,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.now());
    }

    public static ExternalSource newExternalSource(
            String code, String name, ExternalSourceType type) {
        return ExternalSource.forNew(
                ExternalSourceCode.of(code), name, type, null, CommonVoFixtures.now());
    }

    public static ExternalSource activeExternalSource() {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(1L),
                defaultCode(),
                "세토프 레거시",
                ExternalSourceType.LEGACY,
                ExternalSourceStatus.ACTIVE,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalSource activeExternalSource(Long id) {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(id),
                defaultCode(),
                "세토프 레거시",
                ExternalSourceType.LEGACY,
                ExternalSourceStatus.ACTIVE,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalSource activeExternalSource(Long id, String code) {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(id),
                ExternalSourceCode.of(code),
                code + " Source",
                ExternalSourceType.LEGACY,
                ExternalSourceStatus.ACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalSource crawlingSource() {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(2L),
                ExternalSourceCode.of("COUPANG_CRAWL"),
                "쿠팡 크롤링",
                ExternalSourceType.CRAWLING,
                ExternalSourceStatus.ACTIVE,
                "쿠팡 크롤링 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalSource partnerSource() {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(3L),
                ExternalSourceCode.of("PARTNER_A"),
                "파트너 A",
                ExternalSourceType.PARTNER,
                ExternalSourceStatus.ACTIVE,
                "파트너 A 연동",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ExternalSource inactiveExternalSource() {
        return ExternalSource.reconstitute(
                ExternalSourceId.of(4L),
                ExternalSourceCode.of("INACTIVE_SOURCE"),
                "비활성 소스",
                ExternalSourceType.LEGACY,
                ExternalSourceStatus.INACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
