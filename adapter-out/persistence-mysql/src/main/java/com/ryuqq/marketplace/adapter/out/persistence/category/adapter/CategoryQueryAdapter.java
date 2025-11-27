package com.ryuqq.marketplace.adapter.out.persistence.category.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.category.mapper.CategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryQueryDslRepository;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CategoryQueryAdapter - Category Query Port 구현
 */
@Component
@Transactional(readOnly = true)
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryQueryDslRepository queryDslRepository;
    private final CategoryJpaEntityMapper mapper;

    public CategoryQueryAdapter(
            CategoryJpaRepository jpaRepository,
            CategoryQueryDslRepository queryDslRepository,
            CategoryJpaEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return jpaRepository.findById(categoryId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByCode(String code) {
        return jpaRepository.findByCode(code)
            .map(mapper::toDomain);
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return queryDslRepository.findByParentId(parentId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findAllActiveVisible() {
        return queryDslRepository.findAllActiveVisible().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findAll() {
        return queryDslRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findListableLeaves(CategorySearchQuery query) {
        return queryDslRepository.findListableLeaves(query).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findDescendants(Long categoryId) {
        return queryDslRepository.findDescendants(categoryId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findAncestors(Long categoryId) {
        return queryDslRepository.findAncestors(categoryId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> search(String keyword) {
        return queryDslRepository.search(keyword).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Category> findUpdatedSince(LocalDateTime since) {
        return queryDslRepository.findUpdatedSince(since).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public boolean hasChildren(Long categoryId) {
        return queryDslRepository.hasChildren(categoryId);
    }
}
