package com.ryuqq.marketplace.domain.category.aggregate;

import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.event.CategoryCreatedEvent;
import com.ryuqq.marketplace.domain.category.event.CategoryStatusChangedEvent;
import com.ryuqq.marketplace.domain.category.event.CategoryUpdatedEvent;
import com.ryuqq.marketplace.domain.category.vo.AgeGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryMeta;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.CategoryVisibility;
import com.ryuqq.marketplace.domain.category.vo.GenderScope;
import com.ryuqq.marketplace.domain.category.vo.ProductGroup;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Category Aggregate Root
 *
 * <p><strong>핵심 원칙</strong>:</p>
 * <ul>
 *   <li>Plain Java - Lombok 금지</li>
 *   <li>Law of Demeter 준수 - Getter 체이닝 금지</li>
 *   <li>Tell Don't Ask - 도메인이 스스로 판단</li>
 *   <li>불변성 - 상태 변경은 비즈니스 메서드로만</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class Category {

    private final CategoryId id;
    private final CategoryCode code;
    private CategoryName name;
    private final Long parentId;
    private final CategoryDepth depth;
    private final CategoryPath path;
    private SortOrder sortOrder;
    private boolean isLeaf;
    private CategoryStatus status;
    private CategoryVisibility visibility;
    private Department department;
    private ProductGroup productGroup;
    private GenderScope genderScope;
    private AgeGroup ageGroup;
    private CategoryMeta meta;
    private final long version;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    // Private Constructor (정적 팩토리 메서드로만 생성)
    private Category(
            CategoryId id,
            CategoryCode code,
            CategoryName name,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean isLeaf,
            CategoryStatus status,
            CategoryVisibility visibility,
            Department department,
            ProductGroup productGroup,
            GenderScope genderScope,
            AgeGroup ageGroup,
            CategoryMeta meta,
            long version) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.parentId = parentId;
        this.depth = depth;
        this.path = path;
        this.sortOrder = sortOrder;
        this.isLeaf = isLeaf;
        this.status = status;
        this.visibility = visibility;
        this.department = department;
        this.productGroup = productGroup;
        this.genderScope = genderScope;
        this.ageGroup = ageGroup;
        this.meta = meta;
        this.version = version;
    }

    /**
     * 루트 카테고리 생성 (신규)
     *
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param department 부서 구분
     * @param productGroup 상품 그룹
     * @return Category
     */
    public static Category createRoot(
            CategoryCode code,
            CategoryName name,
            Department department,
            ProductGroup productGroup) {

        var category = new Category(
                CategoryId.forNew(),
                code,
                name,
                null,
                CategoryDepth.of(0),
                null, // path는 persist 후 설정
                SortOrder.defaultOrder(),
                true,
                CategoryStatus.ACTIVE,
                CategoryVisibility.visible(),
                department,
                productGroup,
                GenderScope.NONE,
                AgeGroup.NONE,
                CategoryMeta.empty(),
                0L
        );

        category.registerEvent(new CategoryCreatedEvent(null, code.value(), name.displayName()));
        return category;
    }

    /**
     * 하위 카테고리 생성 (신규)
     *
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param parent 부모 카테고리
     * @return Category
     */
    public static Category createChild(
            CategoryCode code,
            CategoryName name,
            Category parent) {

        CategoryDepth childDepth = parent.depth.increment();

        var category = new Category(
                CategoryId.forNew(),
                code,
                name,
                parent.idValue(),
                childDepth,
                null, // path는 persist 후 설정
                SortOrder.defaultOrder(),
                true,
                CategoryStatus.ACTIVE,
                CategoryVisibility.visible(),
                parent.department,
                parent.productGroup,
                parent.genderScope,
                parent.ageGroup,
                CategoryMeta.empty(),
                0L
        );

        category.registerEvent(new CategoryCreatedEvent(null, code.value(), name.displayName()));
        return category;
    }

    /**
     * 재구성 (DB에서 로드)
     *
     * @param id 카테고리 ID
     * @param code 카테고리 코드
     * @param name 카테고리 이름
     * @param parentId 부모 카테고리 ID
     * @param depth 깊이
     * @param path 경로
     * @param sortOrder 정렬 순서
     * @param isLeaf 리프 여부
     * @param status 상태
     * @param visibility 표시 설정
     * @param department 부서 구분
     * @param productGroup 상품 그룹
     * @param genderScope 성별 구분
     * @param ageGroup 연령대 구분
     * @param meta 메타데이터
     * @param version 버전
     * @return Category
     */
    public static Category reconstitute(
            CategoryId id,
            CategoryCode code,
            CategoryName name,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean isLeaf,
            CategoryStatus status,
            CategoryVisibility visibility,
            Department department,
            ProductGroup productGroup,
            GenderScope genderScope,
            AgeGroup ageGroup,
            CategoryMeta meta,
            long version) {

        return new Category(
                id, code, name, parentId, depth, path, sortOrder,
                isLeaf, status, visibility, department, productGroup,
                genderScope, ageGroup, meta, version
        );
    }

    // ========== Domain Behaviors ==========

    /**
     * 카테고리 이름 변경
     *
     * @param newName 새 이름
     */
    public void updateName(CategoryName newName) {
        this.name = newName;
        registerEvent(new CategoryUpdatedEvent(idValue()));
    }

    /**
     * 정렬 순서 변경
     *
     * @param newSortOrder 새 정렬 순서
     */
    public void updateSortOrder(SortOrder newSortOrder) {
        this.sortOrder = newSortOrder;
    }

    /**
     * 표시 설정 변경
     *
     * @param newVisibility 새 표시 설정
     */
    public void updateVisibility(CategoryVisibility newVisibility) {
        this.visibility = newVisibility;
        registerEvent(new CategoryUpdatedEvent(idValue()));
    }

    /**
     * 메타데이터 변경
     *
     * @param newMeta 새 메타데이터
     */
    public void updateMeta(CategoryMeta newMeta) {
        this.meta = newMeta;
    }

    /**
     * 비즈니스 정보 변경
     *
     * @param department 부서 구분
     * @param productGroup 상품 그룹
     * @param genderScope 성별 구분
     * @param ageGroup 연령대 구분
     */
    public void updateBusinessInfo(
            Department department,
            ProductGroup productGroup,
            GenderScope genderScope,
            AgeGroup ageGroup) {
        this.department = department;
        this.productGroup = productGroup;
        this.genderScope = genderScope;
        this.ageGroup = ageGroup;
        registerEvent(new CategoryUpdatedEvent(idValue()));
    }

    /**
     * 상태 변경
     *
     * @param newStatus 새 상태
     */
    public void changeStatus(CategoryStatus newStatus) {
        CategoryStatus oldStatus = this.status;
        this.status = newStatus;
        registerEvent(new CategoryStatusChangedEvent(idValue(), oldStatus.name(), newStatus.name()));
    }

    /**
     * 리프 카테고리로 표시
     */
    public void markAsLeaf() {
        this.isLeaf = true;
    }

    /**
     * 리프 카테고리가 아님을 표시
     */
    public void markAsNotLeaf() {
        this.isLeaf = false;
    }

    // ========== Query Methods (Law of Demeter 준수) ==========

    /**
     * 카테고리 ID 반환
     *
     * @return CategoryId
     */
    public CategoryId id() {
        return id;
    }

    /**
     * 카테고리 ID 값 반환
     *
     * @return ID 값 (Long)
     */
    public Long idValue() {
        return id.value();
    }

    /**
     * 카테고리 코드 값 반환
     *
     * @return 코드 값
     */
    public String codeValue() {
        return code.value();
    }

    /**
     * 한국어 이름 반환
     *
     * @return 한국어 이름
     */
    public String nameKo() {
        return name.ko();
    }

    /**
     * 영어 이름 반환
     *
     * @return 영어 이름
     */
    public String nameEn() {
        return name.en();
    }

    /**
     * 표시용 이름 반환
     *
     * @return 표시용 이름
     */
    public String displayName() {
        return name.displayName();
    }

    /**
     * 부모 카테고리 ID 반환
     *
     * @return 부모 ID (null 가능)
     */
    public Long parentIdValue() {
        return parentId;
    }

    /**
     * 깊이 값 반환
     *
     * @return 깊이
     */
    public int depthValue() {
        return depth.value();
    }

    /**
     * 경로 값 반환
     *
     * @return 경로 (null 가능)
     */
    public String pathValue() {
        return path != null ? path.value() : null;
    }

    /**
     * 정렬 순서 값 반환
     *
     * @return 정렬 순서
     */
    public int sortOrderValue() {
        return sortOrder.value();
    }

    /**
     * 리프 카테고리 여부
     *
     * @return 리프이면 true
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * 루트 카테고리 여부
     *
     * @return 루트이면 true
     */
    public boolean isRoot() {
        return parentId == null;
    }

    /**
     * 카테고리 상태 반환
     *
     * @return CategoryStatus
     */
    public CategoryStatus status() {
        return status;
    }

    /**
     * 활성 상태 여부
     *
     * @return 활성이면 true
     */
    public boolean isActive() {
        return status.isUsable();
    }

    /**
     * 표시 가능 여부
     *
     * @return 표시 가능하면 true
     */
    public boolean isVisible() {
        return visibility.isVisible();
    }

    /**
     * 상품 등록 가능 여부
     *
     * @return 등록 가능하면 true
     */
    public boolean isListable() {
        return visibility.isListable();
    }

    /**
     * 부서 구분 반환
     *
     * @return Department
     */
    public Department department() {
        return department;
    }

    /**
     * 상품 그룹 반환
     *
     * @return ProductGroup
     */
    public ProductGroup productGroup() {
        return productGroup;
    }

    /**
     * 성별 구분 반환
     *
     * @return GenderScope
     */
    public GenderScope genderScope() {
        return genderScope;
    }

    /**
     * 연령대 구분 반환
     *
     * @return AgeGroup
     */
    public AgeGroup ageGroup() {
        return ageGroup;
    }

    /**
     * 메타 표시용 이름 반환
     *
     * @return 표시용 이름
     */
    public String metaDisplayName() {
        return meta.displayName();
    }

    /**
     * SEO 슬러그 반환
     *
     * @return SEO 슬러그
     */
    public String seoSlug() {
        return meta.seoSlug();
    }

    /**
     * 아이콘 URL 반환
     *
     * @return 아이콘 URL
     */
    public String iconUrl() {
        return meta.iconUrl();
    }

    /**
     * 버전 반환
     *
     * @return 버전
     */
    public long version() {
        return version;
    }

    // ========== Event Handling ==========

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * 도메인 이벤트 목록 반환
     *
     * @return 도메인 이벤트 (불변 리스트)
     */
    public List<DomainEvent> domainEvents() {
        return List.copyOf(domainEvents);
    }

    /**
     * 도메인 이벤트 초기화
     */
    public void clearEvents() {
        domainEvents.clear();
    }
}
