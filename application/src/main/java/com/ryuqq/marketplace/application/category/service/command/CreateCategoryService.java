package com.ryuqq.marketplace.application.category.service.command;

import com.ryuqq.marketplace.application.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.category.dto.command.CreateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.port.in.command.CreateCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.out.command.CategoryPersistencePort;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.exception.CategoryCodeDuplicateException;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.ProductGroup;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CreateCategoryService - 카테고리 생성 Command Service
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
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    public CreateCategoryService(
            CategoryPersistencePort persistencePort,
            CategoryQueryPort queryPort,
            CategoryAssembler assembler) {
        this.persistencePort = persistencePort;
        this.queryPort = queryPort;
        this.assembler = assembler;
    }

    @Override
    @Transactional
    public CategoryResponse execute(CreateCategoryCommand command) {
        // 코드 중복 검증
        if (persistencePort.existsByCode(command.code())) {
            throw new CategoryCodeDuplicateException(command.code());
        }

        Category category;

        if (command.isRootCategory()) {
            // 루트 카테고리 생성
            category = Category.createRoot(
                CategoryCode.of(command.code()),
                CategoryName.of(command.nameKo(), command.nameEn()),
                command.department() != null ? Department.fromString(command.department()) : Department.FASHION,
                command.productGroup() != null ? ProductGroup.fromString(command.productGroup()) : ProductGroup.ETC
            );
        } else {
            // 부모 카테고리 조회
            Category parent = queryPort.findById(command.parentId())
                .orElseThrow(() -> new CategoryNotFoundException(command.parentId()));

            // 자식 카테고리 생성
            category = Category.createChild(
                CategoryCode.of(command.code()),
                CategoryName.of(command.nameKo(), command.nameEn()),
                parent
            );

            // 부모를 non-leaf로 변경
            parent.markAsNotLeaf();
            persistencePort.persist(parent);
        }

        // 추가 설정
        if (command.sortOrder() != null) {
            category.updateSortOrder(SortOrder.of(command.sortOrder()));
        }
        if (command.isVisible() != null || command.isListable() != null) {
            category.updateVisibility(CategoryVisibility.of(
                command.isVisible() != null ? command.isVisible() : true,
                command.isListable() != null ? command.isListable() : true
            ));
        }
        if (hasMeta(command)) {
            category.updateMeta(CategoryMeta.of(
                command.displayName(),
                command.seoSlug(),
                command.iconUrl()
            ));
        }

        // 저장
        Category saved = persistencePort.persist(category);

        return assembler.toResponse(saved);
    }

    private boolean hasMeta(CreateCategoryCommand command) {
        return (command.displayName() != null && !command.displayName().isBlank())
            || (command.seoSlug() != null && !command.seoSlug().isBlank())
            || (command.iconUrl() != null && !command.iconUrl().isBlank());
    }
}
