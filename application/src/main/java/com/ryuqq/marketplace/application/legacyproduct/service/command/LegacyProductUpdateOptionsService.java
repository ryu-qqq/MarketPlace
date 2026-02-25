package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductsCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyOptionUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 옵션/상품 수정 Service.
 *
 * <p>Factory로 도메인 객체를 생성한 뒤 LegacyOptionUpdateCoordinator에 위임하여 diff 기반 옵션 업데이트를 수행합니다.
 */
@Service
public class LegacyProductUpdateOptionsService implements LegacyProductUpdateOptionsUseCase {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyOptionUpdateCoordinator optionUpdateCoordinator;
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;

    public LegacyProductUpdateOptionsService(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyOptionUpdateCoordinator optionUpdateCoordinator,
            LegacyProductQueryUseCase legacyProductQueryUseCase) {
        this.commandFactory = commandFactory;
        this.optionUpdateCoordinator = optionUpdateCoordinator;
        this.legacyProductQueryUseCase = legacyProductQueryUseCase;
    }

    @Override
    public LegacyProductGroupDetailResult execute(LegacyUpdateProductsCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        List<LegacyProduct> newProducts =
                commandFactory.createProductsForOptionUpdate(groupId, command.skus());
        optionUpdateCoordinator.execute(groupId, newProducts);
        return legacyProductQueryUseCase.execute(command.productGroupId());
    }
}
