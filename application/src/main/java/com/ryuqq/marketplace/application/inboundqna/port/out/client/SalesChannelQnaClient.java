package com.ryuqq.marketplace.application.inboundqna.port.out.client;

import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import java.time.Instant;
import java.util.List;

/** 외부 판매채널 QnA 폴링 클라이언트 포트. */
public interface SalesChannelQnaClient {

    /** 해당 채널코드를 지원하는지 여부. */
    boolean supports(String channelCode);

    /** 외부 QnA 목록 조회. */
    List<ExternalQnaPayload> fetchNewQnas(
            long salesChannelId, Instant from, Instant to, int batchSize);
}
