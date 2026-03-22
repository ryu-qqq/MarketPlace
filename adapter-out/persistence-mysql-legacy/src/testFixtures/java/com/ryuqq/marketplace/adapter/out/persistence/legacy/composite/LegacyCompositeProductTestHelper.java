package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.LegacyProductNoticeEntity;
import jakarta.persistence.EntityManager;

/**
 * Composite Product 통합 테스트용 데이터 셋업 헬퍼.
 *
 * <p>Seller, Brand, Category는 읽기 전용 엔티티(팩토리 메서드 없음)이므로 Native SQL로 삽입합니다.
 */
public final class LegacyCompositeProductTestHelper {

    private final EntityManager em;

    public LegacyCompositeProductTestHelper(EntityManager em) {
        this.em = em;
    }

    // ===== 읽기 전용 엔티티 (Native SQL) =====

    /** Seller 레코드를 Native SQL로 삽입합니다. */
    public void insertSeller(long sellerId, String sellerName) {
        em.createNativeQuery(
                        "INSERT INTO seller (seller_id, seller_name, commission_rate) VALUES (?, ?, ?)")
                .setParameter(1, sellerId)
                .setParameter(2, sellerName)
                .setParameter(3, 10.0)
                .executeUpdate();
    }

    /** Brand 레코드를 Native SQL로 삽입합니다. */
    public void insertBrand(long brandId, String brandName) {
        em.createNativeQuery(
                        "INSERT INTO brand (brand_id, brand_name, display_order, display_yn) VALUES (?, ?, ?, ?)")
                .setParameter(1, brandId)
                .setParameter(2, brandName)
                .setParameter(3, 1)
                .setParameter(4, "Y")
                .executeUpdate();
    }

    /** Category 레코드를 Native SQL로 삽입합니다. */
    public void insertCategory(long categoryId, String categoryName, String path) {
        em.createNativeQuery(
                        "INSERT INTO category (category_id, category_name, category_depth, parent_category_id, display_yn, path) VALUES (?, ?, ?, ?, ?, ?)")
                .setParameter(1, categoryId)
                .setParameter(2, categoryName)
                .setParameter(3, 1)
                .setParameter(4, 0L)
                .setParameter(5, "Y")
                .setParameter(6, path)
                .executeUpdate();
    }

    // ===== 팩토리 메서드 있는 엔티티 =====

    /** 기본 상품그룹을 persist하고 ID를 반환합니다. */
    public long persistProductGroup(long sellerId, long brandId, long categoryId) {
        return persistProductGroup(sellerId, brandId, categoryId, "테스트 상품그룹", "N", "Y");
    }

    /** 커스텀 상품그룹을 persist합니다. */
    public long persistProductGroup(
            long sellerId,
            long brandId,
            long categoryId,
            String name,
            String soldOutYn,
            String displayYn) {
        LegacyProductGroupEntity entity =
                LegacyProductGroupEntity.create(
                        null,
                        name,
                        sellerId,
                        brandId,
                        categoryId,
                        "SINGLE",
                        "SYSTEM",
                        50000L,
                        45000L,
                        soldOutYn,
                        displayYn,
                        "NEW",
                        "KR",
                        "STYLE001");
        em.persist(entity);
        em.flush();
        return entity.getId();
    }

    /** 상품을 persist하고 ID를 반환합니다. */
    public long persistProduct(long productGroupId, String soldOutYn) {
        LegacyProductEntity entity =
                LegacyProductEntity.create(productGroupId, soldOutYn, "Y", 0);
        em.persist(entity);
        em.flush();
        return entity.getId();
    }

    /** 상품 재고를 persist합니다. */
    public void persistProductStock(long productId, int stockQuantity) {
        em.persist(LegacyProductStockEntity.create(productId, stockQuantity));
        em.flush();
    }

    /** 옵션 그룹을 persist하고 ID를 반환합니다. */
    public long persistOptionGroup(String optionName) {
        LegacyOptionGroupEntity entity = LegacyOptionGroupEntity.create(0L, optionName);
        em.persist(entity);
        em.flush();
        return entity.getId();
    }

    /** 옵션 상세를 persist하고 ID를 반환합니다. */
    public long persistOptionDetail(long optionGroupId, String optionValue) {
        LegacyOptionDetailEntity entity =
                LegacyOptionDetailEntity.create(optionGroupId, optionValue);
        em.persist(entity);
        em.flush();
        return entity.getId();
    }

    /** 상품 옵션을 persist합니다. */
    public void persistProductOption(
            long productId, long optionGroupId, long optionDetailId) {
        em.persist(
                LegacyProductOptionEntity.create(productId, optionGroupId, optionDetailId, 0L));
        em.flush();
    }

    /** 배송 정보를 persist합니다. */
    public void persistDelivery(long productGroupId) {
        em.persist(
                LegacyProductDeliveryEntity.create(
                        productGroupId, "NATIONWIDE", 3000, 3, "택배", "CJ", 3000, "서울시 강남구"));
        em.flush();
    }

    /** 상세 설명을 persist합니다. */
    public void persistDescription(long productGroupId, String description) {
        em.persist(
                LegacyProductGroupDetailDescriptionEntity.create(productGroupId, description));
        em.flush();
    }

    /** 상품 고시를 persist합니다. */
    public void persistNotice(long productGroupId) {
        em.persist(
                LegacyProductNoticeEntity.create(
                        productGroupId,
                        "면100%",
                        "블랙",
                        "M",
                        "나이키",
                        "한국",
                        "세탁기 가능",
                        "2025-01",
                        "KC인증",
                        "02-1234-5678"));
        em.flush();
    }

    /** 이미지를 persist합니다. */
    public void persistImage(long productGroupId, String imageType, String imageUrl) {
        em.persist(
                LegacyProductGroupImageEntity.create(
                        null, productGroupId, imageType, imageUrl, imageUrl, 1L, "N"));
        em.flush();
    }

    /** flush + clear로 1차 캐시를 비웁니다. */
    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    /**
     * 기본 테스트 데이터 세트를 구성합니다.
     *
     * <p>Seller(10) + Brand(20) + Category(30) + ProductGroup + Product(옵션 2개) + Stock + Delivery
     * + Description + Notice + Image(MAIN, DETAIL)
     *
     * @return 생성된 상품그룹 ID
     */
    public long setupFullProductGroupData() {
        insertSeller(10L, "테스트 셀러");
        insertBrand(20L, "나이키");
        insertCategory(30L, "상의", "패션>의류>상의");

        long pgId = persistProductGroup(10L, 20L, 30L);
        long productId = persistProduct(pgId, "N");
        persistProductStock(productId, 10);

        long colorGroupId = persistOptionGroup("색상");
        long redDetailId = persistOptionDetail(colorGroupId, "빨강");
        long sizeGroupId = persistOptionGroup("사이즈");
        long mDetailId = persistOptionDetail(sizeGroupId, "M");

        persistProductOption(productId, colorGroupId, redDetailId);
        persistProductOption(productId, sizeGroupId, mDetailId);

        persistDelivery(pgId);
        persistDescription(pgId, "<p>상품 상세 설명</p>");
        persistNotice(pgId);
        persistImage(pgId, "MAIN", "https://cdn.example.com/main.jpg");
        persistImage(pgId, "DETAIL", "https://cdn.example.com/detail.jpg");

        flushAndClear();
        return pgId;
    }
}
