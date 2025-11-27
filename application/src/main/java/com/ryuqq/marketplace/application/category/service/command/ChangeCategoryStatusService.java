package com.ryuqq.marketplace.application.category.service.command;

import com.ryuqq.marketplace.application.category.dto.command.ChangeCategoryStatusCommand;
import com.ryuqq.marketplace.application.category.port.in.command.ChangeCategoryStatusUseCase;
import com.ryuqq.marketplace.application.category.port.out.command.CategoryPersistencePort;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ChangeCategoryStatusService - 카테고리 상태 변경 Command Service
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지</li>
 *   <li>@Transactional 경계: Command Service에만 적용</li>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Service
public class ChangeCategoryStatusService implements ChangeCategoryStatusUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;

    public ChangeCategoryStatusService(
            CategoryPersistencePort persistencePort,
            CategoryQueryPort queryPort) {
        this.persistencePort = persistencePort;
        this.queryPort = queryPort;
    }

    @Override
    @Transactional
    public void execute(ChangeCategoryStatusCommand command) {
        Category category = queryPort.findById(command.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        CategoryStatus newStatus = CategoryStatus.fromString(command.newStatus());

        // DEPRECATED 상태 변경 시 대체 카테고리 검증
        if (newStatus == CategoryStatus.DEPRECATED && command.hasReplacement()) {
            queryPort.findById(command.replacementCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.replacementCategoryId()));
        }

        category.changeStatus(newStatus);
        persistencePort.persist(category);
    }
}
