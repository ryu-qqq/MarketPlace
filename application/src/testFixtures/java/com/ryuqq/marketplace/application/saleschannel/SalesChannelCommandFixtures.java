package com.ryuqq.marketplace.application.saleschannel;

import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;

/**
 * SalesChannel Command 테스트 Fixtures.
 *
 * <p>SalesChannel 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SalesChannelCommandFixtures {

    private SalesChannelCommandFixtures() {}

    // ===== 기본 값 상수 =====
    public static final String DEFAULT_CHANNEL_NAME = "테스트 판매채널";
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== RegisterSalesChannelCommand =====

    public static RegisterSalesChannelCommand registerCommand() {
        return new RegisterSalesChannelCommand(DEFAULT_CHANNEL_NAME);
    }

    public static RegisterSalesChannelCommand registerCommand(String channelName) {
        return new RegisterSalesChannelCommand(channelName);
    }

    // ===== UpdateSalesChannelCommand =====

    public static UpdateSalesChannelCommand updateCommand(Long salesChannelId) {
        return new UpdateSalesChannelCommand(salesChannelId, "수정된 판매채널", DEFAULT_STATUS);
    }

    public static UpdateSalesChannelCommand updateCommand(Long salesChannelId, String channelName) {
        return new UpdateSalesChannelCommand(salesChannelId, channelName, DEFAULT_STATUS);
    }

    public static UpdateSalesChannelCommand updateCommand(
            Long salesChannelId, String channelName, String status) {
        return new UpdateSalesChannelCommand(salesChannelId, channelName, status);
    }
}
