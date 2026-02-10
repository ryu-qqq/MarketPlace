package com.ryuqq.marketplace.adapter.out.persistence.category;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import java.time.Instant;

/**
 * CategoryJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CategoryJpaEntity 관련 객체들을 생성합니다.
 */
public final class CategoryJpaEntityFixtures {

    private CategoryJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "CAT001";
    public static final String DEFAULT_NAME_KO = "테스트 카테고리";
    public static final String DEFAULT_NAME_EN = "Test Category";
    public static final Long DEFAULT_PARENT_ID = null;
    public static final int DEFAULT_DEPTH = 1;
    public static final String DEFAULT_PATH = "/1";
    public static final int DEFAULT_SORT_ORDER = 1;
    public static final boolean DEFAULT_LEAF = true;
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_DEPARTMENT = "FASHION";
    public static final String DEFAULT_CATEGORY_GROUP = "CLOTHING";

    // ===== Entity Fixtures =====

    /** 활성 상태의 루트 카테고리 Entity 생성. */
    public static CategoryJpaEntity activeRootEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 루트 카테고리 Entity 생성. */
    public static CategoryJpaEntity activeRootEntity(Long id) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                "/" + id,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 활성 상태의 자식 카테고리 Entity 생성. */
    public static CategoryJpaEntity activeChildEntity(Long parentId) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                parentId,
                2,
                "/" + parentId + "/" + DEFAULT_ID,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 활성 상태 카테고리 Entity 생성. */
    public static CategoryJpaEntity activeEntityWithCode(String code) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 커스텀 이름을 가진 활성 상태 카테고리 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CategoryJpaEntity activeEntityWithName(String nameKo, String nameEn) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                nameKo,
                nameEn,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 비활성 상태 카테고리 Entity 생성. */
    public static CategoryJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                "/2",
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "INACTIVE",
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태 카테고리 Entity 생성. */
    public static CategoryJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                "/3",
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CategoryJpaEntity newEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 영문명이 없는 Entity 생성. */
    public static CategoryJpaEntity entityWithoutNameEn() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                null,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 리프가 아닌 Entity 생성 (자식이 있음). */
    public static CategoryJpaEntity nonLeafEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                false,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 깊이가 2인 Entity 생성. */
    public static CategoryJpaEntity depth2Entity(Long parentId) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                parentId,
                2,
                "/" + parentId + "/" + DEFAULT_ID,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 깊이가 3인 Entity 생성. */
    public static CategoryJpaEntity depth3Entity(Long parentId) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                parentId,
                3,
                "/" + parentId + "/" + DEFAULT_ID,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 다른 부문의 Entity 생성. */
    public static CategoryJpaEntity entityWithDepartment(String department) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                department,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 다른 카테고리 그룹의 Entity 생성. */
    public static CategoryJpaEntity entityWithCategoryGroup(String categoryGroup) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                categoryGroup,
                null,
                now,
                now,
                null);
    }

    /** 영문명이 없는 새 Entity 생성 (ID는 null). */
    public static CategoryJpaEntity newEntityWithoutNameEn() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                null,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static CategoryJpaEntity newInactiveEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "INACTIVE",
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 삭제된 상태의 새 Entity 생성 (ID는 null). */
    public static CategoryJpaEntity newDeletedEntity() {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                now);
    }

    /** 커스텀 코드를 가진 비활성 상태 카테고리 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CategoryJpaEntity inactiveEntityWithCode(String code) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                "INACTIVE",
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 삭제 상태 카테고리 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CategoryJpaEntity deletedEntityWithCode(String code) {
        Instant now = Instant.now();
        return CategoryJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_PARENT_ID,
                DEFAULT_DEPTH,
                DEFAULT_PATH,
                DEFAULT_SORT_ORDER,
                DEFAULT_LEAF,
                DEFAULT_STATUS,
                DEFAULT_DEPARTMENT,
                DEFAULT_CATEGORY_GROUP,
                null,
                now,
                now,
                now);
    }
}
