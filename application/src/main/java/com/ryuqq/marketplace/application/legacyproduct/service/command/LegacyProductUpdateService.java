package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductUpdateCoordinator;
import com.ryuqq.marketplace.application.inboundproduct.manager.InboundProductReadManager;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateUseCase;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 수정 서비스.
 *
 * <p>레거시 수정 요청을 InboundProduct 기준으로 반영합니다. 수정 요청에 없는 필드는 기존 InboundProduct 값을 유지합니다.
 */
@Service
public class LegacyProductUpdateService implements LegacyProductUpdateUseCase {

    private final InboundProductReadManager inboundProductReadManager;
    private final InboundProductUpdateCoordinator inboundProductUpdateCoordinator;

    public LegacyProductUpdateService(
            InboundProductReadManager inboundProductReadManager,
            InboundProductUpdateCoordinator inboundProductUpdateCoordinator) {
        this.inboundProductReadManager = inboundProductReadManager;
        this.inboundProductUpdateCoordinator = inboundProductUpdateCoordinator;
    }

    @Override
    public void execute(ReceiveInboundProductCommand command) {
        InboundProduct existing =
                inboundProductReadManager.findByInboundSourceIdAndProductCodeOrThrow(
                        command.inboundSourceId(), command.externalProductCode());

        ReceiveInboundProductCommand mergedCommand = mergeWithExisting(command, existing);
        inboundProductUpdateCoordinator.update(existing, mergedCommand);
    }

    private ReceiveInboundProductCommand mergeWithExisting(
            ReceiveInboundProductCommand command, InboundProduct existing) {
        String descriptionHtml =
                command.descriptionHtml() != null
                        ? command.descriptionHtml()
                        : existing.descriptionHtml();
        String rawPayloadJson =
                command.rawPayloadJson() != null
                        ? command.rawPayloadJson()
                        : existing.rawPayloadJson();

        return new ReceiveInboundProductCommand(
                command.inboundSourceId(),
                command.externalProductCode(),
                existing.productName(),
                existing.externalBrandCode(),
                existing.externalCategoryCode(),
                existing.sellerId(),
                existing.regularPrice(),
                existing.currentPrice(),
                existing.optionType(),
                descriptionHtml,
                rawPayloadJson);
    }
}
