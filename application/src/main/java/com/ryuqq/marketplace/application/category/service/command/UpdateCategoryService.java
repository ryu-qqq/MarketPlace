package com.ryuqq.marketplace.application.category.service.command;

import com.ryuqq.marketplace.application.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.category.dto.command.UpdateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.port.in.command.UpdateCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.out.command.CategoryPersistencePort;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateCategoryService - 카테고리 수정 Command Service
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
public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public UpdateCategoryService(
            CategoryPersistencePort persistencePort,
            CategoryQueryPort queryPort,
            CategoryAssembler assembler) {
        this.persistencePort = persistencePort;
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    @Transactional
    public CategoryResponse execute(UpdateCategoryCommand command) {
        Category category = queryPort.findById(command.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        // 이름 업데이트
        if (command.nameKo() != null || command.nameEn() != null) {
            category.updateName(CategoryName.of(
                command.nameKo() != null ? command.nameKo() : category.nameKo(),
                command.nameEn() != null ? command.nameEn() : category.nameEn()
            ));
        }

        // 정렬 순서 업데이트
        if (command.sortOrder() != null) {
            category.updateSortOrder(SortOrder.of(command.sortOrder()));
        }

        // 표시 설정 업데이트
        if (command.isVisible() != null || command.isListable() != null) {
            category.updateVisibility(CategoryVisibility.of(
                command.isVisible() != null ? command.isVisible() : category.isVisible(),
                command.isListable() != null ? command.isListable() : category.isListable()
            ));
        }

        // 메타 정보 업데이트
        if (hasMeta(command)) {
            category.updateMeta(CategoryMeta.of(
                command.displayName() != null ? command.displayName() : category.metaDisplayName(),
                command.seoSlug() != null ? command.seoSlug() : category.seoSlug(),
                command.iconUrl() != null ? command.iconUrl() : category.iconUrl()
            ));
        }

        // 저장
        Category saved = persistencePort.persist(category);

        return assembler.toResponse(saved);
    }

    private boolean hasMeta(UpdateCategoryCommand command) {
        return command.displayName() != null
            || command.seoSlug() != null
            || command.iconUrl() != null;
    }
}
