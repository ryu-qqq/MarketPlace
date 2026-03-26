package com.ryuqq.marketplace.application.inboundqna.port.out.client;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.util.List;

/** 외부 판매채널 QnA 폴링 클라이언트 포트. */
public interface SalesChannelQnaClient {

    /** 이 클라이언트가 담당하는 판매채널 코드. */
    String channelCode();

    /** 외부 QnA 목록 조회 (Shop 단위). */
    List<ExternalQnaPayload> fetchNewQnas(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant from,
            Instant to,
            int batchSize);
}
