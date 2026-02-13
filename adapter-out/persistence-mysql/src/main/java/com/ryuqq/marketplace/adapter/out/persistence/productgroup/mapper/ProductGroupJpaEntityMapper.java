package com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupImageId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** ProductGroup JPA Entity Mapper. */
@Component
public class ProductGroupJpaEntityMapper {

    public ProductGroupJpaEntity toEntity(ProductGroup domain) {
        return ProductGroupJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.brandIdValue(),
                domain.categoryIdValue(),
                domain.shippingPolicyIdValue(),
                domain.refundPolicyIdValue(),
                domain.productGroupNameValue(),
                domain.optionType().name(),
                domain.status().name(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public ProductGroupImageJpaEntity toImageEntity(ProductGroupImage image) {
        return ProductGroupImageJpaEntity.create(
                image.idValue(),
                image.productGroupIdValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.imageType().name(),
                image.sortOrder());
    }

    public SellerOptionGroupJpaEntity toOptionGroupEntity(SellerOptionGroup group) {
        return SellerOptionGroupJpaEntity.create(
                group.idValue(),
                group.productGroupIdValue(),
                group.optionGroupNameValue(),
                group.canonicalOptionGroupId() != null
                        ? group.canonicalOptionGroupId().value()
                        : null,
                group.sortOrder());
    }

    public SellerOptionValueJpaEntity toOptionValueEntity(SellerOptionValue value) {
        return SellerOptionValueJpaEntity.create(
                value.idValue(),
                value.sellerOptionGroupIdValue(),
                value.optionValueNameValue(),
                value.canonicalOptionValueId() != null
                        ? value.canonicalOptionValueId().value()
                        : null,
                value.sortOrder());
    }

    public ProductGroup toDomain(
            ProductGroupJpaEntity entity,
            List<ProductGroupImageJpaEntity> imageEntities,
            List<SellerOptionGroupJpaEntity> groupEntities,
            List<SellerOptionValueJpaEntity> valueEntities) {

        List<ProductGroupImage> images = imageEntities.stream().map(this::toImageDomain).toList();

        Map<Long, List<SellerOptionValueJpaEntity>> valuesByGroupId =
                valueEntities.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SellerOptionValueJpaEntity::getSellerOptionGroupId));

        List<SellerOptionGroup> optionGroups =
                groupEntities.stream()
                        .map(
                                groupEntity -> {
                                    List<SellerOptionValueJpaEntity> groupValues =
                                            valuesByGroupId.getOrDefault(
                                                    groupEntity.getId(), List.of());
                                    List<SellerOptionValue> values =
                                            groupValues.stream()
                                                    .map(this::toOptionValueDomain)
                                                    .toList();
                                    return toOptionGroupDomain(groupEntity, values);
                                })
                        .toList();

        ProductGroupId id =
                entity.getId() != null
                        ? ProductGroupId.of(entity.getId())
                        : ProductGroupId.forNew();

        return ProductGroup.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                BrandId.of(entity.getBrandId()),
                CategoryId.of(entity.getCategoryId()),
                ShippingPolicyId.of(entity.getShippingPolicyId()),
                RefundPolicyId.of(entity.getRefundPolicyId()),
                ProductGroupName.of(entity.getProductGroupName()),
                OptionType.valueOf(entity.getOptionType()),
                ProductGroupStatus.valueOf(entity.getStatus()),
                images,
                optionGroups,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ProductGroupImage toImageDomain(ProductGroupImageJpaEntity entity) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                ImageUrl.of(entity.getOriginUrl()),
                entity.getUploadedUrl() != null ? ImageUrl.of(entity.getUploadedUrl()) : null,
                ImageType.valueOf(entity.getImageType()),
                entity.getSortOrder());
    }

    private SellerOptionGroup toOptionGroupDomain(
            SellerOptionGroupJpaEntity entity, List<SellerOptionValue> values) {
        return SellerOptionGroup.reconstitute(
                SellerOptionGroupId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                OptionGroupName.of(entity.getOptionGroupName()),
                entity.getCanonicalOptionGroupId() != null
                        ? CanonicalOptionGroupId.of(entity.getCanonicalOptionGroupId())
                        : null,
                entity.getSortOrder(),
                values);
    }

    private SellerOptionValue toOptionValueDomain(SellerOptionValueJpaEntity entity) {
        return SellerOptionValue.reconstitute(
                SellerOptionValueId.of(entity.getId()),
                SellerOptionGroupId.of(entity.getSellerOptionGroupId()),
                OptionValueName.of(entity.getOptionValueName()),
                entity.getCanonicalOptionValueId() != null
                        ? CanonicalOptionValueId.of(entity.getCanonicalOptionValueId())
                        : null,
                entity.getSortOrder());
    }
}
