package com.ryuqq.marketplace.application.claimsync.port.out.client;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;

import java.time.Instant;
import java.util.List;

/** 외부 판매채널 클레임 조회 클라이언트. */
public interface SalesChannelClaimClient {

    boolean supports(String channelCode);

    List<ExternalClaimPayload> fetchClaimChanges(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime);
}
