package com.ryuqq.marketplace.application.legacy.order.internal;

import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;

/** 레거시 주문 상태별 처리 전략. */
public interface LegacyOrderUpdateStrategy {

    String supportedStatus();

    void execute(LegacyOrderUpdateCommand command);
}
