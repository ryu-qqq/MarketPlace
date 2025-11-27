package com.ryuqq.marketplace.adapter.out.persistence.category.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.marketplace.domain.brand.vo.Department;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.vo.AgeGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.GenderScope;
import com.ryuqq.marketplace.domain.category.vo.ProductGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;

/**
 * CategoryJpaEntity - Category Aggregate Root의 JPA Entity
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java 클래스</li>
 *   <li>Long FK 전략 - parentId는 Long 타입, JPA 관계 어노테이션 금지</li>
 *   <li>Setter 금지 - Getter Only, 정적 팩토리 메서드 사용</li>
 *   <li>protected 기본 생성자 - JPA 스펙 요구사항</li>
 * </ul>
 *
 * <p><strong>계층 구조 설계 (Path Enumeration 패턴)</strong>:</p>
 * <ul>
 *   <li>parent_id: 직접 부모 참조 (Long FK)</li>
 *   <li>path: 조상 경로 (예: "1/2/3") - 조상 조회 성능 최적화</li>
 *   <li>depth: 계층 깊이 (루트=0)</li>
 *   <li>is_leaf: 리프 노드 여부 (자식 없음)</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Entity
@Table(name = "category")
public class CategoryJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "name_ko", nullable = false, length = 255)
    private String nameKo;

    @Column(name = "name_en", length = 255)
    private String nameEn;

    /**
     * 부모 카테고리 ID (Long FK 전략)
     *
     * <p>루트 카테고리는 null입니다.
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 계층 깊이 (0부터 시작)
     *
     * <p>루트 카테고리는 0입니다.
     */
    @Column(name = "depth", nullable = false)
    private Integer depth;

    /**
     * Path Enumeration (경로 표현)
     *
     * <p>예: "1/2/3" → ID 1(루트) > ID 2 > ID 3
     */
    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    /**
     * 리프 노드 여부
     *
     * <p>자식 카테고리가 없으면 true입니다.
     */
    @Column(name = "is_leaf", nullable = false)
    private boolean isLeaf;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;

    @Column(name = "is_listable", nullable = false)
    private boolean isListable;

    @Column(name = "department", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Department department;

    @Column(name = "product_group", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ProductGroup productGroup;

    @Column(name = "gender_scope", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private GenderScope genderScope;

    @Column(name = "age_group", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "seo_slug", length = 255)
    private String seoSlug;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * 기본 생성자 (JPA 스펙 요구사항)
     */
    protected CategoryJpaEntity() {
        super();
    }

    private CategoryJpaEntity(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        Long parentId,
        Integer depth,
        String path,
        Integer sortOrder,
        boolean isLeaf,
        CategoryStatus status,
        boolean isVisible,
        boolean isListable,
        Department department,
        ProductGroup productGroup,
        GenderScope genderScope,
        AgeGroup ageGroup,
        String displayName,
        String seoSlug,
        String iconUrl,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.parentId = parentId;
        this.depth = depth;
        this.path = path;
        this.sortOrder = sortOrder;
        this.isLeaf = isLeaf;
        this.status = status;
        this.isVisible = isVisible;
        this.isListable = isListable;
        this.department = department;
        this.productGroup = productGroup;
        this.genderScope = genderScope;
        this.ageGroup = ageGroup;
        this.displayName = displayName;
        this.seoSlug = seoSlug;
        this.iconUrl = iconUrl;
        this.version = version;
    }

    /**
     * Category Aggregate에서 JPA Entity로 변환하는 정적 팩토리 메서드
     *
     * <p>새 카테고리 생성 시 path는 저장 후 업데이트됩니다.
     *
     * @param category 도메인 Aggregate
     * @return CategoryJpaEntity
     */
    public static CategoryJpaEntity from(Category category) {
        LocalDateTime now = LocalDateTime.now();

        // path 계산: 새 카테고리는 ID 할당 후 path 업데이트 필요
        String path = category.pathValue();
        if (path == null || path.isEmpty()) {
            // 임시 path (저장 후 실제 ID로 업데이트)
            path = category.idValue() != null ? String.valueOf(category.idValue()) : "0";
        }

        return new CategoryJpaEntity(
            category.idValue(),
            category.codeValue(),
            category.nameKo(),
            category.nameEn(),
            category.parentIdValue(),
            category.depthValue(),
            path,
            category.sortOrderValue(),
            category.isLeaf(),
            category.status(),
            category.isVisible(),
            category.isListable(),
            category.department(),
            category.productGroup(),
            category.genderScope(),
            category.ageGroup(),
            category.metaDisplayName(),
            category.seoSlug(),
            category.iconUrl(),
            category.version(),
            now,
            now
        );
    }

    // Getters only (No Setters)

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getNameKo() {
        return nameKo;
    }

    public String getNameEn() {
        return nameEn;
    }

    public Long getParentId() {
        return parentId;
    }

    public Integer getDepth() {
        return depth;
    }

    public String getPath() {
        return path;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public CategoryStatus getStatus() {
        return status;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isListable() {
        return isListable;
    }

    public Department getDepartment() {
        return department;
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public GenderScope getGenderScope() {
        return genderScope;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSeoSlug() {
        return seoSlug;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public Long getVersion() {
        return version;
    }
}
