package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;

/**
 * 인바운드 상품 신규 등록 전용 UseCase.
 *
 * <p>동일 externalProductCode로 중복 수신 시 기존 결과를 반환합니다 (멱등성). 기존 상품 업데이트는 이 UseCase의 범위 밖입니다.
 */
public interface ReceiveInboundProductUseCase {
    InboundProductConversionResult execute(ReceiveInboundProductCommand command);
}
