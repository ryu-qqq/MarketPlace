package com.ryuqq.marketplace.domain.saleschannel;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannelUpdateData;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.ChannelName;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;

/**
 * SalesChannel 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 SalesChannel 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelFixtures {

    private SalesChannelFixtures() {}

    // ===== ID Fixtures =====
    public static SalesChannelId defaultSalesChannelId() {
        return SalesChannelId.of(1L);
    }

    public static SalesChannelId salesChannelId(Long value) {
        return SalesChannelId.of(value);
    }

    public static SalesChannelId newSalesChannelId() {
        return SalesChannelId.forNew();
    }

    // ===== VO Fixtures =====
    public static ChannelName defaultChannelName() {
        return ChannelName.of("테스트 채널");
    }

    public static ChannelName channelName(String value) {
        return ChannelName.of(value);
    }

    // ===== Aggregate Fixtures =====
    public static SalesChannel newSalesChannel() {
        return SalesChannel.forNew("테스트 채널", CommonVoFixtures.now());
    }

    public static SalesChannel newSalesChannel(String channelName) {
        return SalesChannel.forNew(channelName, CommonVoFixtures.now());
    }

    public static SalesChannel activeSalesChannel() {
        return SalesChannel.reconstitute(
                SalesChannelId.of(1L),
                "테스트 채널",
                SalesChannelStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannel activeSalesChannel(Long id) {
        return SalesChannel.reconstitute(
                SalesChannelId.of(id),
                "테스트 채널",
                SalesChannelStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannel activeSalesChannel(Long id, String channelName) {
        return SalesChannel.reconstitute(
                SalesChannelId.of(id),
                channelName,
                SalesChannelStatus.ACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannel inactiveSalesChannel() {
        return SalesChannel.reconstitute(
                SalesChannelId.of(2L),
                "비활성 채널",
                SalesChannelStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static SalesChannel inactiveSalesChannel(Long id) {
        return SalesChannel.reconstitute(
                SalesChannelId.of(id),
                "비활성 채널",
                SalesChannelStatus.INACTIVE,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== UpdateData Fixtures =====
    public static SalesChannelUpdateData salesChannelUpdateData() {
        return SalesChannelUpdateData.of("수정된 채널", SalesChannelStatus.ACTIVE);
    }

    public static SalesChannelUpdateData salesChannelUpdateData(
            String channelName, SalesChannelStatus status) {
        return SalesChannelUpdateData.of(channelName, status);
    }
}
