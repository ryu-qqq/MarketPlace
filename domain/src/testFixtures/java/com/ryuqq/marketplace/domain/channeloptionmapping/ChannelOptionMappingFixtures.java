package com.ryuqq.marketplace.domain.channeloptionmapping;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.vo.ExternalOptionCode;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;

/**
 * ChannelOptionMapping 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ChannelOptionMapping 관련 객체들을 생성합니다.
 */
public final class ChannelOptionMappingFixtures {

    private ChannelOptionMappingFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final Long DEFAULT_CANONICAL_OPTION_VALUE_ID = 100L;
    public static final String DEFAULT_EXTERNAL_CODE = "EXT-OPTION-001";

    // ===== ID Fixtures =====
    public static ChannelOptionMappingId defaultChannelOptionMappingId() {
        return ChannelOptionMappingId.of(1L);
    }

    public static ChannelOptionMappingId channelOptionMappingId(Long value) {
        return ChannelOptionMappingId.of(value);
    }

    public static ChannelOptionMappingId newChannelOptionMappingId() {
        return ChannelOptionMappingId.forNew();
    }

    // ===== SalesChannelId Fixtures =====
    public static SalesChannelId defaultSalesChannelId() {
        return SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID);
    }

    public static SalesChannelId salesChannelId(Long value) {
        return SalesChannelId.of(value);
    }

    // ===== CanonicalOptionValueId Fixtures =====
    public static CanonicalOptionValueId defaultCanonicalOptionValueId() {
        return CanonicalOptionValueId.of(DEFAULT_CANONICAL_OPTION_VALUE_ID);
    }

    public static CanonicalOptionValueId canonicalOptionValueId(Long value) {
        return CanonicalOptionValueId.of(value);
    }

    // ===== ExternalOptionCode Fixtures =====
    public static ExternalOptionCode defaultExternalOptionCode() {
        return ExternalOptionCode.of(DEFAULT_EXTERNAL_CODE);
    }

    public static ExternalOptionCode externalOptionCode(String value) {
        return ExternalOptionCode.of(value);
    }

    // ===== ChannelOptionMapping Aggregate Fixtures =====
    public static ChannelOptionMapping newChannelOptionMapping() {
        return ChannelOptionMapping.forNew(
                defaultSalesChannelId(),
                defaultCanonicalOptionValueId(),
                defaultExternalOptionCode(),
                CommonVoFixtures.now());
    }

    public static ChannelOptionMapping newChannelOptionMapping(
            Long salesChannelId, Long canonicalOptionValueId, String externalCode) {
        return ChannelOptionMapping.forNew(
                SalesChannelId.of(salesChannelId),
                CanonicalOptionValueId.of(canonicalOptionValueId),
                ExternalOptionCode.of(externalCode),
                CommonVoFixtures.now());
    }

    public static ChannelOptionMapping existingChannelOptionMapping() {
        return ChannelOptionMapping.reconstitute(
                ChannelOptionMappingId.of(1L),
                defaultSalesChannelId(),
                defaultCanonicalOptionValueId(),
                defaultExternalOptionCode(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ChannelOptionMapping existingChannelOptionMapping(Long id) {
        return ChannelOptionMapping.reconstitute(
                ChannelOptionMappingId.of(id),
                defaultSalesChannelId(),
                defaultCanonicalOptionValueId(),
                defaultExternalOptionCode(),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static ChannelOptionMapping existingChannelOptionMapping(
            Long id, Long salesChannelId, Long canonicalOptionValueId, String externalCode) {
        return ChannelOptionMapping.reconstitute(
                ChannelOptionMappingId.of(id),
                SalesChannelId.of(salesChannelId),
                CanonicalOptionValueId.of(canonicalOptionValueId),
                ExternalOptionCode.of(externalCode),
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }
}
