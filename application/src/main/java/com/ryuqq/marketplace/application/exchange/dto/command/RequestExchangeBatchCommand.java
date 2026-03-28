package com.ryuqq.marketplace.application.exchange.dto.command;

import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import java.util.List;

/** 교환 요청 일괄 처리 명령. */
public record RequestExchangeBatchCommand(
        List<ExchangeRequestItem> items, String requestedBy, long sellerId) {

    public record ExchangeRequestItem(
            Long orderItemId,
            int exchangeQty,
            ExchangeReasonType reasonType,
            String reasonDetail,
            long originalProductId,
            String originalSkuCode,
            long targetProductGroupId,
            long targetProductId,
            String targetSkuCode,
            int targetQuantity) {}
}
