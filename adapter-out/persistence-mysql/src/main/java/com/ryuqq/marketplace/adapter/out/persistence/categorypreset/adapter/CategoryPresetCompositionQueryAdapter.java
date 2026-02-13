package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite.CategoryMappingWithCategoryDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetQueryDslRepository;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult.InternalCategory;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult.MappingCategory;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetCompositionQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** CategoryPreset Composition 조회 Adapter. */
@Component
public class CategoryPresetCompositionQueryAdapter implements CategoryPresetCompositionQueryPort {

    private final CategoryPresetQueryDslRepository presetRepository;
    private final CategoryMappingQueryDslRepository categoryMappingRepository;

    public CategoryPresetCompositionQueryAdapter(
            CategoryPresetQueryDslRepository presetRepository,
            CategoryMappingQueryDslRepository categoryMappingRepository) {
        this.presetRepository = presetRepository;
        this.categoryMappingRepository = categoryMappingRepository;
    }

    @Override
    public Optional<CategoryPresetDetailResult> findDetailById(Long id) {
        Optional<CategoryPresetDetailCompositeDto> compositeOpt =
                presetRepository.findDetailCompositeById(id);
        if (compositeOpt.isEmpty()) {
            return Optional.empty();
        }

        CategoryPresetDetailCompositeDto composite = compositeOpt.get();

        MappingCategory mappingCategory =
                new MappingCategory(
                        composite.externalCategoryCode(), composite.categoryDisplayPath());

        List<InternalCategory> internalCategories = buildInternalCategories(id);

        return Optional.of(
                new CategoryPresetDetailResult(
                        composite.id(),
                        composite.shopId(),
                        composite.shopName(),
                        composite.salesChannelId(),
                        composite.salesChannelName(),
                        composite.accountId(),
                        composite.presetName(),
                        mappingCategory,
                        internalCategories,
                        composite.createdAt(),
                        composite.updatedAt()));
    }

    private List<InternalCategory> buildInternalCategories(Long presetId) {
        List<CategoryMappingWithCategoryDto> mappings =
                categoryMappingRepository.findMappedCategoriesByPresetId(presetId);

        return mappings.stream()
                .map(dto -> new InternalCategory(dto.internalCategoryId(), dto.displayPath()))
                .toList();
    }
}
