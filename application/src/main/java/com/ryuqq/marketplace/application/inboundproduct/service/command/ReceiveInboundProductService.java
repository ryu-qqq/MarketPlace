package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductRegisterCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인바운드 상품 수신 서비스.
 *
 * <p>신규 수신 시 등록, 이미 수신된 상품 재수신 시 갱신을 처리합니다. 동기 변환 방식으로 즉시 내부 ProductGroup을 생성/수정합니다.
 */
@Service
public class ReceiveInboundProductService implements ReceiveInboundProductUseCase {

    private final InboundProductRegisterCoordinator registerCoordinator;
    private final InboundProductReadManager readManager;

    public ReceiveInboundProductService(
            InboundProductRegisterCoordinator registerCoordinator,
            InboundProductReadManager readManager) {
        this.registerCoordinator = registerCoordinator;
        this.readManager = readManager;
    }

    @Override
    @Transactional
    public InboundProductConversionResult execute(ReceiveInboundProductCommand command) {
        Optional<InboundProduct> existing =
                readManager.findByInboundSourceIdAndProductCode(
                        command.inboundSourceId(), command.externalProductCode());

        if (existing.isPresent()) {
            return registerCoordinator.reReceive(existing.get(), command);
        }
        return registerCoordinator.register(command);
    }
}
