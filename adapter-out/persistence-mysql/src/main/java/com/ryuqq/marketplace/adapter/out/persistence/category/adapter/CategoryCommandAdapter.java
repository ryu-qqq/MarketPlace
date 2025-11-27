package com.ryuqq.marketplace.adapter.out.persistence.category.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.mapper.CategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryJpaRepository;
import com.ryuqq.marketplace.application.category.port.out.command.CategoryPersistencePort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.vo.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CategoryCommandAdapter - Category Persistence Port 구현
 *
 * <p><strong>Soft Delete 전략</strong>: 물리 삭제 금지, status 변경</p>
 */
@Component
public class CategoryCommandAdapter implements CategoryPersistencePort {

    private final CategoryJpaRepository repository;
    private final CategoryJpaEntityMapper mapper;

    public CategoryCommandAdapter(CategoryJpaRepository repository, CategoryJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Category persist(Category category) {
        CategoryJpaEntity entity = mapper.toEntity(category);
        CategoryJpaEntity saved = repository.save(entity);

        // 새 카테고리인 경우 path 업데이트
        if (category.idValue() == null) {
            String newPath = calculatePath(saved);
            Category updated = Category.reconstitute(
                CategoryId.of(saved.getId()),
                CategoryCode.of(saved.getCode()),
                CategoryName.of(saved.getNameKo(), saved.getNameEn()),
                saved.getParentId(),
                CategoryDepth.of(saved.getDepth()),
                CategoryPath.of(newPath),
                SortOrder.of(saved.getSortOrder()),
                saved.isLeaf(),
                saved.getStatus(),
                CategoryVisibility.of(saved.isVisible(), saved.isListable()),
                saved.getDepartment(),
                saved.getProductGroup(),
                saved.getGenderScope(),
                saved.getAgeGroup(),
                CategoryMeta.of(saved.getDisplayName(), saved.getSeoSlug(), saved.getIconUrl()),
                saved.getVersion()
            );
            CategoryJpaEntity updatedEntity = mapper.toEntity(updated);
            saved = repository.save(updatedEntity);
        }

        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void persistAll(List<Category> categories) {
        List<CategoryJpaEntity> entities = categories.stream()
            .map(mapper::toEntity)
            .toList();
        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        // Soft Delete: status를 INACTIVE로 변경
        repository.findById(categoryId).ifPresent(entity -> {
            Category category = mapper.toDomain(entity);
            category.changeStatus(CategoryStatus.INACTIVE);
            repository.save(mapper.toEntity(category));
        });
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }

    private String calculatePath(CategoryJpaEntity entity) {
        if (entity.getParentId() == null) {
            return String.valueOf(entity.getId());
        } else {
            return repository.findById(entity.getParentId())
                .map(parent -> parent.getPath() + "/" + entity.getId())
                .orElse(String.valueOf(entity.getId()));
        }
    }
}
