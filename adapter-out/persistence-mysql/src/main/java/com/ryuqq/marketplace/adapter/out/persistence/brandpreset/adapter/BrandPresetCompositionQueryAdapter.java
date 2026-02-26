package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite.BrandMappingWithBrandDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository.BrandMappingQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetQueryDslRepository;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult.InternalBrand;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult.MappingBrand;
import com.ryuqq.marketplace.application.brandpreset.port.out.query.BrandPresetCompositionQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** BrandPreset Composition 조회 Adapter. */
@Component
public class BrandPresetCompositionQueryAdapter implements BrandPresetCompositionQueryPort {

    private final BrandPresetQueryDslRepository presetRepository;
    private final BrandMappingQueryDslRepository brandMappingRepository;

    public BrandPresetCompositionQueryAdapter(
            BrandPresetQueryDslRepository presetRepository,
            BrandMappingQueryDslRepository brandMappingRepository) {
        this.presetRepository = presetRepository;
        this.brandMappingRepository = brandMappingRepository;
    }

    @Override
    public Optional<BrandPresetDetailResult> findDetailById(Long id) {
        Optional<BrandPresetDetailCompositeDto> compositeOpt =
                presetRepository.findDetailCompositeById(id);
        if (compositeOpt.isEmpty()) {
            return Optional.empty();
        }

        BrandPresetDetailCompositeDto composite = compositeOpt.get();

        MappingBrand mappingBrand =
                new MappingBrand(composite.externalBrandCode(), composite.externalBrandName());

        List<InternalBrand> internalBrands = buildInternalBrands(id);

        return Optional.of(
                new BrandPresetDetailResult(
                        composite.id(),
                        composite.shopId(),
                        composite.shopName(),
                        composite.salesChannelId(),
                        composite.salesChannelName(),
                        composite.accountId(),
                        composite.presetName(),
                        mappingBrand,
                        internalBrands,
                        composite.createdAt(),
                        composite.updatedAt()));
    }

    private List<InternalBrand> buildInternalBrands(Long presetId) {
        List<BrandMappingWithBrandDto> mappings =
                brandMappingRepository.findMappedBrandsByPresetId(presetId);

        return mappings.stream()
                .map(dto -> new InternalBrand(dto.internalBrandId(), dto.brandName()))
                .toList();
    }
}
