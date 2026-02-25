package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupCommandDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.SetofProductGroupCommandPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SetofProductGroupCommandAdapter - 세토프 DB 상품그룹 커맨드 Adapter.
 *
 * <p>세토프 DB에 직접 UPDATE/INSERT하여 상품 데이터를 수정합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SetofProductGroupCommandAdapter implements SetofProductGroupCommandPort {

    private final LegacyProductGroupCommandDslRepository commandDslRepository;
    private final LegacyProductGroupImageJpaRepository imageJpaRepository;

    public SetofProductGroupCommandAdapter(
            LegacyProductGroupCommandDslRepository commandDslRepository,
            LegacyProductGroupImageJpaRepository imageJpaRepository) {
        this.commandDslRepository = commandDslRepository;
        this.imageJpaRepository = imageJpaRepository;
    }

    @Override
    public void updatePrice(
            long productGroupId, long regularPrice, long currentPrice, long salePrice) {
        commandDslRepository.updatePrice(productGroupId, regularPrice, currentPrice, salePrice);
    }

    @Override
    public void updateDisplayYn(long productGroupId, String displayYn) {
        commandDslRepository.updateDisplayYn(productGroupId, displayYn);
    }

    @Override
    public void updateSoldOutYn(long productGroupId, String soldOutYn) {
        commandDslRepository.updateSoldOutYn(productGroupId, soldOutYn);
    }

    @Override
    public void updateNotice(
            long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {
        commandDslRepository.updateNotice(
                productGroupId,
                material,
                color,
                size,
                maker,
                origin,
                washingMethod,
                yearMonthDay,
                assuranceStandard,
                asPhone);
    }

    @Override
    public void replaceImages(long productGroupId, List<ImageUpdateData> images) {
        commandDslRepository.softDeleteImagesByProductGroupId(productGroupId);

        List<LegacyProductGroupImageEntity> newEntities = new java.util.ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            ImageUpdateData img = images.get(i);
            newEntities.add(
                    LegacyProductGroupImageEntity.create(
                            null,
                            productGroupId,
                            img.imageType(),
                            img.imageUrl(),
                            img.imageUrl(),
                            i + 1L,
                            "N"));
        }

        imageJpaRepository.saveAll(newEntities);
    }

    @Override
    public void updateDetailDescription(long productGroupId, String imageUrl) {
        commandDslRepository.updateDetailDescription(productGroupId, imageUrl);
    }

    @Override
    public void updateStock(long productId, int quantity) {
        commandDslRepository.updateStock(productId, quantity);
    }

    @Override
    public void markProductSoldOut(long productId) {
        commandDslRepository.markProductSoldOut(productId);
    }
}
