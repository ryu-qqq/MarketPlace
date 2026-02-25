package com.ryuqq.marketplace.domain.inboundsource;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceCode;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;

/** InboundSource 도메인 테스트 Fixtures. */
public final class InboundSourceFixtures {

    private InboundSourceFixtures() {}

    // ===== ID Fixtures =====
    public static InboundSourceId defaultId() {
        return InboundSourceId.of(1L);
    }

    public static InboundSourceId id(Long value) {
        return InboundSourceId.of(value);
    }

    public static InboundSourceId newId() {
        return InboundSourceId.forNew();
    }

    // ===== VO Fixtures =====
    public static InboundSourceCode defaultCode() {
        return InboundSourceCode.of("SETOF");
    }

    public static InboundSourceCode code(String value) {
        return InboundSourceCode.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static InboundSource newInboundSource() {
        return InboundSource.forNew(
                defaultCode(),
                "세토프 레거시",
                InboundSourceType.LEGACY,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.now());
    }

    public static InboundSource newInboundSource(String code, String name, InboundSourceType type) {
        return InboundSource.forNew(
                InboundSourceCode.of(code), name, type, null, CommonVoFixtures.now());
    }

    public static InboundSource activeInboundSource() {
        return InboundSource.reconstitute(
                InboundSourceId.of(1L),
                defaultCode(),
                "세토프 레거시",
                InboundSourceType.LEGACY,
                InboundSourceStatus.ACTIVE,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundSource activeInboundSource(Long id) {
        return InboundSource.reconstitute(
                InboundSourceId.of(id),
                defaultCode(),
                "세토프 레거시",
                InboundSourceType.LEGACY,
                InboundSourceStatus.ACTIVE,
                "레거시 Setof 상품 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundSource activeInboundSource(Long id, String code) {
        return InboundSource.reconstitute(
                InboundSourceId.of(id),
                InboundSourceCode.of(code),
                code + " Source",
                InboundSourceType.LEGACY,
                InboundSourceStatus.ACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundSource crawlingSource() {
        return InboundSource.reconstitute(
                InboundSourceId.of(2L),
                InboundSourceCode.of("COUPANG_CRAWL"),
                "쿠팡 크롤링",
                InboundSourceType.CRAWLING,
                InboundSourceStatus.ACTIVE,
                "쿠팡 크롤링 데이터 소스",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundSource partnerSource() {
        return InboundSource.reconstitute(
                InboundSourceId.of(3L),
                InboundSourceCode.of("PARTNER_A"),
                "파트너 A",
                InboundSourceType.PARTNER,
                InboundSourceStatus.ACTIVE,
                "파트너 A 연동",
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static InboundSource inactiveInboundSource() {
        return InboundSource.reconstitute(
                InboundSourceId.of(4L),
                InboundSourceCode.of("INACTIVE_SOURCE"),
                "비활성 소스",
                InboundSourceType.LEGACY,
                InboundSourceStatus.INACTIVE,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
