package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductRegisterCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import org.springframework.stereotype.Service;

/**
 * 인바운드 상품 신규 등록 전용 서비스.
 *
 * <p>중복 수신은 DB 유니크 키(uk_source_product)가 차단합니다. 기존 상품 업데이트는 레거시 컨트롤러의 개별 수정 엔드포인트에서 별도 처리합니다.
 */
@Service
public class ReceiveInboundProductService implements ReceiveInboundProductUseCase {

    private final InboundProductRegisterCoordinator registerCoordinator;

    public ReceiveInboundProductService(InboundProductRegisterCoordinator registerCoordinator) {
        this.registerCoordinator = registerCoordinator;
    }

    @Override
    public InboundProductConversionResult execute(ReceiveInboundProductCommand command) {

        return registerCoordinator.register(command);
    }
}
