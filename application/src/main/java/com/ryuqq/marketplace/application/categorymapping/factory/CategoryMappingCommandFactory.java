package com.ryuqq.marketplace.application.categorymapping.factory;

import com.ryuqq.marketplace.application.categorymapping.dto.command.DeleteCategoryMappingCommand;
import com.ryuqq.marketplace.application.categorymapping.dto.command.RegisterCategoryMappingCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.id.CategoryMappingId;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * CategoryMapping Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class CategoryMappingCommandFactory {

    private final TimeProvider timeProvider;

    public CategoryMappingCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 CategoryMapping 도메인 객체 생성.
     *
     * @param command 등록 Command
     * @return CategoryMapping 도메인 객체
     */
    public CategoryMapping create(RegisterCategoryMappingCommand command) {
        Instant now = timeProvider.now();
        return CategoryMapping.forNew(
                command.salesChannelCategoryId(), command.internalCategoryId(), now);
    }

    /**
     * 비활성화 StatusChangeContext 생성.
     *
     * @param command 삭제(비활성화) Command
     * @return StatusChangeContext
     */
    public StatusChangeContext<CategoryMappingId> createDeactivateContext(
            DeleteCategoryMappingCommand command) {
        return new StatusChangeContext<>(
                CategoryMappingId.of(command.categoryMappingId()), timeProvider.now());
    }
}
