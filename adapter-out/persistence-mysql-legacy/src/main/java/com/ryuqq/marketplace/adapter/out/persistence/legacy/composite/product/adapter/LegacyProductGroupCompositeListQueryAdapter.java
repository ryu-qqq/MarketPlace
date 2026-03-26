package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupListQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupListQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupCompositeListQueryPort;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.legacy.productgroup.query.LegacyProductGroupSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 Composite 목록 조회 Adapter.
 *
 * <p>{@link LegacyProductGroupCompositeListQueryPort}의 구현체입니다. 3-Phase Query로 상품그룹 목록을 조회하고
 * LegacyProductGroupDetailBundle 목록으로 조립하여 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class LegacyProductGroupCompositeListQueryAdapter
        implements LegacyProductGroupCompositeListQueryPort {

    private final LegacyProductGroupListQueryDslRepository listQueryDslRepository;
    private final LegacyProductCompositeMapper productCompositeMapper;

    public LegacyProductGroupCompositeListQueryAdapter(
            LegacyProductGroupListQueryDslRepository listQueryDslRepository,
            LegacyProductCompositeMapper productCompositeMapper) {
        this.listQueryDslRepository = listQueryDslRepository;
        this.productCompositeMapper = productCompositeMapper;
    }

    @Override
    public List<LegacyProductGroupDetailBundle> searchProductGroups(
            LegacyProductGroupSearchCriteria criteria) {
        // Phase 1: ID 목록 조회
        List<Long> productGroupIds = listQueryDslRepository.fetchProductGroupIds(criteria);
        if (productGroupIds.isEmpty()) {
            return List.of();
        }

        // Phase 2: 상품그룹 상세 조회
        List<LegacyProductGroupListQueryDto> details =
                listQueryDslRepository.fetchProductGroupDetails(productGroupIds);

        // Phase 3: 상품+옵션 조회
        List<LegacyProductOptionQueryDto> productRows =
                listQueryDslRepository.fetchProductsWithOptions(productGroupIds);

        // 상품을 productGroupId 기준으로 그룹핑
        Map<Long, List<LegacyProductCompositeResult>> productsByGroupId =
                groupProductsByProductGroupId(productRows);

        // 순서 보장: Phase 1 ID 순서대로 번들 조립
        Map<Long, LegacyProductGroupListQueryDto> detailMap =
                details.stream()
                        .collect(
                                Collectors.toMap(
                                        LegacyProductGroupListQueryDto::productGroupId, d -> d));

        return productGroupIds.stream()
                .filter(detailMap::containsKey)
                .map(
                        id -> {
                            LegacyProductGroupListQueryDto dto = detailMap.get(id);
                            LegacyProductGroupCompositeResult composite = toCompositeResult(dto);
                            List<LegacyProductCompositeResult> products =
                                    productsByGroupId.getOrDefault(id, List.of());
                            return LegacyProductGroupDetailBundle.of(composite, products);
                        })
                .toList();
    }

    @Override
    public long count(LegacyProductGroupSearchCriteria criteria) {
        return listQueryDslRepository.count(criteria);
    }

    private Map<Long, List<LegacyProductCompositeResult>> groupProductsByProductGroupId(
            List<LegacyProductOptionQueryDto> productRows) {
        if (productRows == null || productRows.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<LegacyProductOptionQueryDto>> rowsByGroupId =
                productRows.stream()
                        .collect(
                                Collectors.groupingBy(LegacyProductOptionQueryDto::productGroupId));

        return rowsByGroupId.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> productCompositeMapper.toCompositeResults(e.getValue())));
    }

    private LegacyProductGroupCompositeResult toCompositeResult(
            LegacyProductGroupListQueryDto dto) {
        return new LegacyProductGroupCompositeResult(
                dto.productGroupId(),
                dto.productGroupName(),
                dto.sellerId(),
                dto.sellerName(),
                dto.brandId(),
                dto.brandName(),
                dto.categoryId(),
                dto.categoryPath(),
                dto.optionType(),
                dto.managementType(),
                dto.regularPrice(),
                dto.currentPrice(),
                dto.salePrice(),
                dto.directDiscountPrice(),
                dto.directDiscountRate(),
                dto.discountRate(),
                "Y".equals(dto.soldOutYn()),
                "Y".equals(dto.displayYn()),
                dto.productCondition(),
                dto.origin(),
                dto.styleCode(),
                dto.insertOperator(),
                dto.updateOperator(),
                dto.insertDate(),
                dto.updateDate(),
                buildMainImageList(dto.mainImageUrl()),
                null,
                null,
                null);
    }

    private List<LegacyProductGroupCompositeResult.ImageInfo> buildMainImageList(
            String mainImageUrl) {
        if (mainImageUrl == null || mainImageUrl.isBlank()) {
            return List.of();
        }
        return List.of(new LegacyProductGroupCompositeResult.ImageInfo("MAIN", mainImageUrl));
    }
}
