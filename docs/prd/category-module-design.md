# Catalog - Category 모듈 설계 v2

## 1. 개요

### 1.1 목적
내부 카탈로그 시스템에서 상품이 귀속될 **기준 카테고리 트리**를 제공한다.

### 1.2 배경
- 입점형 커머스 특성상, 모든 상품은 **내부 카테고리(leaf)**를 기준으로 관리
- Brand/Attribute/외부몰 매핑/셀러 상품 매칭 모두가 Category를 **기준 축**으로 동작
- 본 문서는 **1단계(트리 + 메타 정보 + 조회/관리 API)** 설계

### 1.3 범위

| 구분 | 포함 | 제외 (후속 단계) |
|------|------|-----------------|
| 1단계 | Category 도메인, DB 스키마, Admin/Public API | Attribute, CategoryAttribute |
| 2단계 | - | Product, ProductSku, ProductAttribute |
| 3단계 | - | 외부몰 카테고리 매핑 |

### 1.4 전제 조건
- **인증/인가**: 게이트웨이에서 처리됨 (본 모듈은 인증된 요청만 수신)
- **아키텍처**: 헥사고날 아키텍처 (Ports & Adapters)
- **패턴**: CQRS (Command/Query 분리), Long FK 전략

---

## 2. Domain Layer 설계

### 2.1 Bounded Context
```
catalog (카탈로그)
└── category (카테고리)
```

### 2.2 패키지 구조
```
domain/
└── catalog/
    └── category/
        ├── aggregate/
        │   └── category/
        │       └── Category.java              # Aggregate Root
        ├── vo/
        │   ├── CategoryId.java                # 식별자 VO
        │   ├── CategoryCode.java              # 유니크 코드 VO
        │   ├── CategoryName.java              # 다국어 이름 VO
        │   ├── CategoryPath.java              # 경로 VO
        │   ├── CategoryDepth.java             # 깊이 VO
        │   ├── SortOrder.java                 # 정렬 순서 VO
        │   ├── CategoryStatus.java            # 상태 Enum
        │   ├── Department.java                # 부문 Enum
        │   ├── ProductGroup.java              # 상품군 Enum
        │   ├── GenderScope.java               # 성별 범위 Enum
        │   ├── AgeGroup.java                  # 연령 그룹 Enum
        │   ├── DisplayMeta.java               # UX/SEO 메타 VO
        │   └── BusinessClassification.java   # 비즈니스 분류 VO
        ├── event/
        │   ├── CategoryCreatedEvent.java
        │   ├── CategoryUpdatedEvent.java
        │   ├── CategoryMovedEvent.java
        │   └── CategoryStatusChangedEvent.java
        └── exception/
            ├── CategoryNotFoundException.java
            ├── CategoryCodeDuplicateException.java
            ├── CategoryCycleDetectedException.java
            ├── CategoryNotLeafException.java
            └── CategoryNotListableException.java
```

### 2.3 Aggregate Root: Category

```java
/**
 * 카테고리 Aggregate Root
 *
 * 불변식:
 * - code는 시스템 전체에서 유니크
 * - depth = parent.depth + 1 (루트는 0)
 * - path = parent.path + "/" + code (루트는 "/" + code)
 * - isLeaf = 자식이 없으면 true (파생 값이지만, 조회 성능을 위해 DB에 캐시함)
 * - 자식이 있는 노드는 isLeaf = false 이어야 한다
 * - 상품 등록은 isLeaf && isListable && status == ACTIVE인 경우만 가능
 * - 모든 자식 추가/삭제/이동은 반드시 도메인 서비스를 통해 발생해야 한다
 */
public class Category {

    // === 식별자 ===
    private final CategoryId id;
    private final CategoryCode code;

    // === 트리 구조 ===
    private final Long parentId;              // Long FK 전략 (JPA 관계 금지)
    private CategoryDepth depth;
    private CategoryPath path;
    private SortOrder sortOrder;
    private boolean isLeaf;                   // 파생 값 캐시 (자식이 없으면 true)

    // === 동시성 제어 ===
    private Long version;                     // 낙관적 락 (@Version)

    // === 이름 ===
    private CategoryName name;

    // === 상태/노출 ===
    private CategoryStatus status;
    private boolean isVisible;
    private boolean isListable;

    // === 비즈니스 분류 ===
    private BusinessClassification classification;

    // === UX/SEO 메타 ===
    private DisplayMeta displayMeta;

    // === 생성 메서드 ===

    /**
     * 새 카테고리 생성 (루트)
     */
    public static Category createRoot(
            CategoryCode code,
            CategoryName name,
            BusinessClassification classification
    ) {
        return new Category(
                CategoryId.generate(),
                code,
                null,                           // 루트는 parentId 없음
                CategoryDepth.root(),
                CategoryPath.of("/" + code.value()),
                SortOrder.defaultOrder(),
                true,                           // 초기에는 leaf
                name,
                CategoryStatus.ACTIVE,
                true,
                true,
                classification,
                DisplayMeta.empty()
        );
    }

    /**
     * 새 카테고리 생성 (자식)
     */
    public static Category createChild(
            CategoryCode code,
            CategoryName name,
            Long parentId,
            CategoryDepth parentDepth,
            CategoryPath parentPath,
            BusinessClassification classification
    ) {
        return new Category(
                CategoryId.generate(),
                code,
                parentId,
                parentDepth.increment(),
                parentPath.append(code),
                SortOrder.defaultOrder(),
                true,
                name,
                CategoryStatus.ACTIVE,
                true,
                true,
                classification,
                DisplayMeta.empty()
        );
    }

    /**
     * 영속성에서 재구성
     */
    public static Category reconstitute(
            CategoryId id,
            CategoryCode code,
            Long parentId,
            CategoryDepth depth,
            CategoryPath path,
            SortOrder sortOrder,
            boolean isLeaf,
            CategoryName name,
            CategoryStatus status,
            boolean isVisible,
            boolean isListable,
            BusinessClassification classification,
            DisplayMeta displayMeta
    ) {
        return new Category(
                id, code, parentId, depth, path, sortOrder, isLeaf,
                name, status, isVisible, isListable, classification, displayMeta
        );
    }

    private Category(/* 모든 필드 */) {
        // 생성자는 private
    }

    // === 도메인 행위 ===

    /**
     * 상품 등록 가능 여부 확인
     * Tell Don't Ask: 외부에서 판단하지 않고 도메인이 결정
     */
    public boolean canRegisterProduct() {
        return isLeaf && isListable && status == CategoryStatus.ACTIVE;
    }

    /**
     * 상품 등록 가능 여부 검증 (실패 시 예외)
     */
    public void validateProductRegistration() {
        if (!canRegisterProduct()) {
            throw new CategoryNotListableException(id);
        }
    }

    /**
     * 이동 가능 여부 확인
     * @param newParentPath 새 부모의 경로
     */
    public boolean canMoveTo(CategoryPath newParentPath) {
        // 자기 자신의 하위로 이동 불가 (Cycle 방지)
        return !newParentPath.startsWith(this.path);
    }

    /**
     * 이동 가능 여부 검증 (실패 시 예외)
     */
    public void validateMoveTo(CategoryPath newParentPath) {
        if (!canMoveTo(newParentPath)) {
            throw new CategoryCycleDetectedException(id, newParentPath);
        }
    }

    /**
     * 부모 변경 (이동)
     */
    public void moveTo(Long newParentId, CategoryDepth newParentDepth, CategoryPath newParentPath) {
        validateMoveTo(newParentPath);
        // parentId는 final이므로 새 인스턴스 생성 필요 → 별도 처리
    }

    /**
     * 자식 추가됨 → isLeaf = false
     */
    public void childAdded() {
        this.isLeaf = false;
    }

    /**
     * 마지막 자식 삭제됨 → isLeaf = true
     */
    public void lastChildRemoved() {
        this.isLeaf = true;
    }

    /**
     * 상태 변경
     */
    public void changeStatus(CategoryStatus newStatus, Long replacementCategoryId) {
        if (this.status == newStatus) {
            return;
        }

        // DEPRECATED로 변경 시 대체 카테고리 필요 (선택)
        this.status = newStatus;
    }

    /**
     * 메타 정보 수정
     */
    public void updateMeta(
            CategoryName name,
            boolean isVisible,
            boolean isListable,
            DisplayMeta displayMeta
    ) {
        this.name = name;
        this.isVisible = isVisible;
        this.isListable = isListable;
        this.displayMeta = displayMeta;
    }

    /**
     * 정렬 순서 변경
     */
    public void reorder(SortOrder newSortOrder) {
        this.sortOrder = newSortOrder;
    }

    /**
     * 경로 재계산 (부모 이동 시)
     */
    public void recalculatePath(CategoryPath newParentPath, CategoryDepth newParentDepth) {
        this.path = newParentPath.append(this.code);
        this.depth = newParentDepth.increment();
    }

    // === Getter (Law of Demeter 준수) ===

    public Long id() { return id.value(); }
    public String code() { return code.value(); }
    public Long parentId() { return parentId; }
    public int depth() { return depth.value(); }
    public String path() { return path.value(); }
    public int sortOrder() { return sortOrder.value(); }
    public boolean isLeaf() { return isLeaf; }
    public boolean isRoot() { return parentId == null; }

    // 이름 관련 - Getter 체이닝 방지
    public String nameKo() { return name.ko(); }
    public String nameEn() { return name.en(); }

    // 상태 관련
    public CategoryStatus status() { return status; }
    public boolean isVisible() { return isVisible; }
    public boolean isListable() { return isListable; }
    public boolean isActive() { return status == CategoryStatus.ACTIVE; }

    // 비즈니스 분류 - Getter 체이닝 방지
    public Department department() { return classification.department(); }
    public ProductGroup productGroup() { return classification.productGroup(); }
    public GenderScope genderScope() { return classification.genderScope(); }
    public AgeGroup ageGroup() { return classification.ageGroup(); }

    // 디스플레이 메타 - Getter 체이닝 방지
    public String displayName() { return displayMeta.displayName(); }
    public String seoSlug() { return displayMeta.seoSlug(); }
    public String iconUrl() { return displayMeta.iconUrl(); }
}
```

### 2.4 Value Objects

#### CategoryId
```java
public record CategoryId(Long value) {
    public CategoryId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CategoryId must be positive");
        }
    }

    public static CategoryId of(Long value) {
        return new CategoryId(value);
    }

    public static CategoryId generate() {
        // ID 생성 전략에 따라 구현 (DB 생성 시 null 허용 필요할 수 있음)
        return new CategoryId(null); // DB에서 생성
    }
}
```

#### CategoryCode
```java
public record CategoryCode(String value) {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{2,99}$");

    public CategoryCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryCode cannot be blank");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "CategoryCode must start with uppercase letter, contain only A-Z, 0-9, _, length 3-100"
            );
        }
    }

    public static CategoryCode of(String value) {
        return new CategoryCode(value);
    }
}
```

#### CategoryName
```java
public record CategoryName(
        String ko,
        String en
) {
    public CategoryName {
        if (ko == null || ko.isBlank()) {
            throw new IllegalArgumentException("Korean name is required");
        }
        if (ko.length() > 255) {
            throw new IllegalArgumentException("Korean name exceeds max length 255");
        }
        if (en != null && en.length() > 255) {
            throw new IllegalArgumentException("English name exceeds max length 255");
        }
    }

    public static CategoryName of(String ko, String en) {
        return new CategoryName(ko, en);
    }

    public static CategoryName koOnly(String ko) {
        return new CategoryName(ko, null);
    }
}
```

#### CategoryPath
```java
public record CategoryPath(String value) {
    private static final int MAX_LENGTH = 1000;

    public CategoryPath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryPath cannot be blank");
        }
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("CategoryPath must start with /");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("CategoryPath exceeds max length " + MAX_LENGTH);
        }
    }

    public static CategoryPath of(String value) {
        return new CategoryPath(value);
    }

    public CategoryPath append(CategoryCode code) {
        return new CategoryPath(this.value + "/" + code.value());
    }

    public boolean startsWith(CategoryPath other) {
        return this.value.startsWith(other.value);
    }

    public List<String> segments() {
        return Arrays.stream(value.split("/"))
                .filter(s -> !s.isBlank())
                .toList();
    }
}
```

#### CategoryDepth
```java
public record CategoryDepth(int value) {
    private static final int MAX_DEPTH = 10;

    public CategoryDepth {
        if (value < 0 || value > MAX_DEPTH) {
            throw new IllegalArgumentException("CategoryDepth must be 0-" + MAX_DEPTH);
        }
    }

    public static CategoryDepth of(int value) {
        return new CategoryDepth(value);
    }

    public static CategoryDepth root() {
        return new CategoryDepth(0);
    }

    public CategoryDepth increment() {
        return new CategoryDepth(value + 1);
    }
}
```

#### CategoryStatus (Enum)
```java
public enum CategoryStatus {
    ACTIVE,      // 정상 사용
    INACTIVE,    // 임시 중지
    DEPRECATED;  // 더 이상 사용 안 함

    public boolean isUsable() {
        return this == ACTIVE;
    }
}
```

#### Department (Enum)
```java
public enum Department {
    FASHION("패션"),
    BEAUTY("뷰티"),
    LIVING("리빙"),
    DIGITAL("디지털"),
    ETC("기타");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
```

#### ProductGroup (Enum)
```java
public enum ProductGroup {
    APPAREL("의류"),
    SHOES("신발"),
    BAG("가방"),
    ACCESSORY("악세서리"),
    UNDERWEAR("언더웨어"),
    ETC("기타");

    private final String displayName;

    ProductGroup(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
```

#### GenderScope (Enum)
```java
public enum GenderScope {
    NONE,
    MEN,
    WOMEN,
    UNISEX,
    KIDS
}
```

#### AgeGroup (Enum)
```java
public enum AgeGroup {
    NONE,
    ADULT,
    KIDS,
    BABY
}
```

#### BusinessClassification (VO)

**용도**: 기획전/리스트/정산/통계에서 카테고리를 비즈니스 관점으로 분류하는 데 사용

**예시 조합**:
| 카테고리 | Department | ProductGroup | GenderScope | AgeGroup |
|----------|------------|--------------|-------------|----------|
| 여성 원피스 | FASHION | APPAREL | WOMEN | ADULT |
| 남성 스니커즈 | FASHION | SHOES | MEN | ADULT |
| 키즈 가방 | FASHION | BAG | KIDS | KIDS |
| 립스틱 | BEAUTY | ETC | WOMEN | ADULT |

```java
public record BusinessClassification(
        Department department,
        ProductGroup productGroup,
        GenderScope genderScope,
        AgeGroup ageGroup
) {
    public BusinessClassification {
        if (department == null) {
            department = Department.FASHION;
        }
        if (productGroup == null) {
            productGroup = ProductGroup.ETC;
        }
        if (genderScope == null) {
            genderScope = GenderScope.NONE;
        }
        if (ageGroup == null) {
            ageGroup = AgeGroup.NONE;
        }
    }

    public static BusinessClassification of(
            Department department,
            ProductGroup productGroup,
            GenderScope genderScope,
            AgeGroup ageGroup
    ) {
        return new BusinessClassification(department, productGroup, genderScope, ageGroup);
    }

    public static BusinessClassification defaults() {
        return new BusinessClassification(
                Department.FASHION,
                ProductGroup.ETC,
                GenderScope.NONE,
                AgeGroup.NONE
        );
    }
}
```

#### DisplayMeta (VO)

**용도**: UI 노출 및 SEO 최적화용 메타 정보

**필드 설명**:
| 필드 | 필수 | 설명 | 예시 |
|------|------|------|------|
| displayName | 선택 | UI 노출용 이름 (name_ko 대신 사용) | "👗 여성 원피스" |
| seoSlug | 선택 | SEO URL용 슬러그 | "womens-dresses" |
| iconUrl | 선택 | 카테고리 아이콘 URL | "https://cdn.../dress.svg" |

> `displayName`은 `name_ko`와 다를 수 있음 (이모지 포함, 마케팅 문구 등)

```java
public record DisplayMeta(
        String displayName,
        String seoSlug,
        String iconUrl
) {
    public DisplayMeta {
        if (displayName != null && displayName.length() > 255) {
            throw new IllegalArgumentException("displayName exceeds max length 255");
        }
        if (seoSlug != null && seoSlug.length() > 255) {
            throw new IllegalArgumentException("seoSlug exceeds max length 255");
        }
        if (iconUrl != null && iconUrl.length() > 500) {
            throw new IllegalArgumentException("iconUrl exceeds max length 500");
        }
    }

    public static DisplayMeta of(String displayName, String seoSlug, String iconUrl) {
        return new DisplayMeta(displayName, seoSlug, iconUrl);
    }

    public static DisplayMeta empty() {
        return new DisplayMeta(null, null, null);
    }
}
```

### 2.5 Domain Events

```java
public record CategoryCreatedEvent(
        Long categoryId,
        String code,
        Long parentId,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record CategoryMovedEvent(
        Long categoryId,
        Long oldParentId,
        Long newParentId,
        String oldPath,
        String newPath,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record CategoryStatusChangedEvent(
        Long categoryId,
        CategoryStatus oldStatus,
        CategoryStatus newStatus,
        Long replacementCategoryId,
        LocalDateTime occurredAt
) implements DomainEvent {}
```

### 2.6 Domain Exceptions

```java
public class CategoryNotFoundException extends DomainException {
    public CategoryNotFoundException(Long categoryId) {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND,
              "Category not found: " + categoryId);
    }
}

public class CategoryCodeDuplicateException extends DomainException {
    public CategoryCodeDuplicateException(String code) {
        super(CategoryErrorCode.CATEGORY_CODE_DUPLICATE,
              "Category code already exists: " + code);
    }
}

public class CategoryCycleDetectedException extends DomainException {
    public CategoryCycleDetectedException(Long categoryId, CategoryPath targetPath) {
        super(CategoryErrorCode.CATEGORY_CYCLE_DETECTED,
              "Cannot move category " + categoryId + " under its own subtree: " + targetPath);
    }
}

public class CategoryNotListableException extends DomainException {
    public CategoryNotListableException(Long categoryId) {
        super(CategoryErrorCode.CATEGORY_NOT_LISTABLE,
              "Category is not listable for product registration: " + categoryId);
    }
}
```

---

## 3. Application Layer 설계

### 3.1 패키지 구조
```
application/
└── catalog/
    └── category/
        ├── assembler/
        │   └── CategoryAssembler.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateCategoryCommand.java
        │   │   ├── UpdateCategoryCommand.java
        │   │   ├── ChangeCategoryStatusCommand.java
        │   │   └── MoveCategoryCommand.java
        │   ├── query/
        │   │   ├── CategorySearchQuery.java
        │   │   └── CategoryTreeQuery.java
        │   └── response/
        │       ├── CategoryResponse.java
        │       ├── CategoryTreeResponse.java
        │       └── CategoryPathResponse.java
        ├── port/
        │   ├── in/
        │   │   ├── command/
        │   │   │   ├── CreateCategoryUseCase.java
        │   │   │   ├── UpdateCategoryUseCase.java
        │   │   │   ├── ChangeCategoryStatusUseCase.java
        │   │   │   └── MoveCategoryUseCase.java
        │   │   └── query/
        │   │       ├── GetCategoryUseCase.java
        │   │       ├── GetCategoryTreeUseCase.java
        │   │       ├── SearchCategoryUseCase.java
        │   │       └── GetCategoryPathUseCase.java
        │   └── out/
        │       ├── command/
        │       │   └── CategoryPersistencePort.java
        │       └── query/
        │           └── CategoryQueryPort.java
        └── service/
            ├── command/
            │   ├── CreateCategoryService.java
            │   ├── UpdateCategoryService.java
            │   ├── ChangeCategoryStatusService.java
            │   └── MoveCategoryService.java
            └── query/
                ├── GetCategoryService.java
                ├── GetCategoryTreeService.java
                ├── SearchCategoryService.java
                └── GetCategoryPathService.java
```

### 3.2 Port In - Command

#### CreateCategoryUseCase
```java
public interface CreateCategoryUseCase {
    CategoryResponse create(CreateCategoryCommand command);
}
```

#### MoveCategoryUseCase
```java
public interface MoveCategoryUseCase {
    void move(MoveCategoryCommand command);
}
```

### 3.3 Port In - Query

#### GetCategoryTreeUseCase
```java
public interface GetCategoryTreeUseCase {
    CategoryTreeResponse getTree(CategoryTreeQuery query);
}
```

#### GetCategoryPathUseCase
```java
public interface GetCategoryPathUseCase {
    CategoryPathResponse getPath(Long categoryId);
}
```

### 3.4 Port Out - Command

#### CategoryPersistencePort
```java
public interface CategoryPersistencePort {
    /**
     * 저장 (생성/수정 통합)
     */
    void persist(Category category);

    /**
     * 벌크 저장 (하위 노드 경로 업데이트 등)
     */
    void persistAll(List<Category> categories);

    /**
     * 삭제 (Soft Delete 권장)
     */
    void delete(Long categoryId);

    /**
     * 코드 중복 검사
     */
    boolean existsByCode(String code);
}
```

### 3.5 Port Out - Query

#### CategoryQueryPort
```java
public interface CategoryQueryPort {
    /**
     * ID로 조회
     */
    Optional<Category> findById(Long categoryId);

    /**
     * 코드로 조회
     */
    Optional<Category> findByCode(String code);

    /**
     * 부모 ID로 자식 조회
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 전체 트리 조회 (ACTIVE + is_visible)
     */
    List<Category> findAllActiveVisible();

    /**
     * 전체 트리 조회 (Admin용, 상태 무관)
     */
    List<Category> findAll();

    /**
     * Leaf 카테고리 조회 (상품 등록 가능)
     */
    List<Category> findListableLeaves(CategorySearchQuery query);

    /**
     * 하위 노드 전체 조회 (이동 시 사용)
     */
    List<Category> findDescendants(Long categoryId);

    /**
     * 검색 (이름/코드 키워드)
     */
    List<Category> search(String keyword);

    /**
     * 증분 조회 (updatedSince 이후 변경된 것)
     */
    List<Category> findUpdatedSince(LocalDateTime since);

    /**
     * 자식 존재 여부 확인
     */
    boolean hasChildren(Long categoryId);
}
```

### 3.6 Command DTO

```java
public record CreateCategoryCommand(
        Long parentId,                    // null이면 루트
        String code,
        String nameKo,
        String nameEn,
        int sortOrder,
        boolean isListable,
        boolean isVisible,
        Department department,
        ProductGroup productGroup,
        GenderScope genderScope,
        AgeGroup ageGroup,
        String displayName,
        String seoSlug,
        String iconUrl
) {}

public record UpdateCategoryCommand(
        Long categoryId,
        String nameKo,
        String nameEn,
        boolean isListable,
        boolean isVisible,
        int sortOrder,
        String displayName,
        String seoSlug,
        String iconUrl
) {}

public record ChangeCategoryStatusCommand(
        Long categoryId,
        CategoryStatus newStatus,
        Long replacementCategoryId      // DEPRECATED 시 대체 카테고리 (선택)
) {}

public record MoveCategoryCommand(
        Long categoryId,
        Long newParentId,
        int newSortOrder
) {}
```

### 3.7 Query DTO

```java
public record CategoryTreeQuery(
        boolean includeInactive,          // Admin용
        Department department,            // 필터 (선택)
        ProductGroup productGroup         // 필터 (선택)
) {}

public record CategorySearchQuery(
        String keyword,
        Department department,
        ProductGroup productGroup,
        GenderScope genderScope,
        boolean onlyLeaf,
        boolean onlyListable
) {}
```

### 3.8 Response DTO

```java
public record CategoryResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        Long parentId,
        int depth,
        String path,
        int sortOrder,
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
        String iconUrl
) {}

public record CategoryTreeResponse(
        List<CategoryTreeNode> roots
) {
    public record CategoryTreeNode(
            CategoryResponse category,
            List<CategoryTreeNode> children
    ) {}
}

public record CategoryPathResponse(
        Long categoryId,
        List<CategoryResponse> ancestors    // 루트부터 현재까지
) {}
```

### 3.9 Service 구현 예시

#### CreateCategoryService
```java
@Service
@RequiredArgsConstructor
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;
    private final CategoryAssembler assembler;

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryCommand command) {
        // 1. 코드 중복 검증
        if (persistencePort.existsByCode(command.code())) {
            throw new CategoryCodeDuplicateException(command.code());
        }

        // 2. 카테고리 생성
        Category category;
        if (command.parentId() == null) {
            // 루트 카테고리
            category = Category.createRoot(
                    CategoryCode.of(command.code()),
                    CategoryName.of(command.nameKo(), command.nameEn()),
                    assembler.toClassification(command)
            );
        } else {
            // 자식 카테고리
            Category parent = queryPort.findById(command.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(command.parentId()));

            category = Category.createChild(
                    CategoryCode.of(command.code()),
                    CategoryName.of(command.nameKo(), command.nameEn()),
                    parent.id(),
                    CategoryDepth.of(parent.depth()),
                    CategoryPath.of(parent.path()),
                    assembler.toClassification(command)
            );

            // 부모의 isLeaf 업데이트
            parent.childAdded();
            persistencePort.persist(parent);
        }

        // 3. 저장
        persistencePort.persist(category);

        return assembler.toResponse(category);
    }
}
```

#### MoveCategoryService
```java
@Service
@RequiredArgsConstructor
public class MoveCategoryService implements MoveCategoryUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;

    @Override
    @Transactional
    public void move(MoveCategoryCommand command) {
        // 1. 이동 대상 조회
        Category category = queryPort.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        Long oldParentId = category.parentId();

        // 2. 새 부모 조회 및 검증
        Category newParent = queryPort.findById(command.newParentId())
                .orElseThrow(() -> new CategoryNotFoundException(command.newParentId()));

        category.validateMoveTo(CategoryPath.of(newParent.path()));

        // 3. 하위 노드 전체 조회
        List<Category> descendants = queryPort.findDescendants(command.categoryId());

        // 4. 경로/깊이 재계산
        category.recalculatePath(
                CategoryPath.of(newParent.path()),
                CategoryDepth.of(newParent.depth())
        );

        for (Category descendant : descendants) {
            // 하위 노드도 경로 재계산 필요
            // (구현 생략 - 재귀적 처리)
        }

        // 5. 기존 부모의 isLeaf 업데이트
        if (oldParentId != null && !queryPort.hasChildren(oldParentId)) {
            Category oldParent = queryPort.findById(oldParentId).orElseThrow();
            oldParent.lastChildRemoved();
            persistencePort.persist(oldParent);
        }

        // 6. 새 부모의 isLeaf 업데이트
        newParent.childAdded();
        persistencePort.persist(newParent);

        // 7. 저장
        persistencePort.persist(category);
        persistencePort.persistAll(descendants);
    }
}
```

---

## 4. Persistence Layer 설계

### 4.1 패키지 구조
```
persistence-mysql/
└── catalog/
    └── category/
        ├── adapter/
        │   ├── CategoryCommandAdapter.java
        │   └── CategoryQueryAdapter.java
        ├── entity/
        │   └── CategoryJpaEntity.java
        ├── mapper/
        │   └── CategoryJpaEntityMapper.java
        └── repository/
            ├── CategoryJpaRepository.java
            └── CategoryQueryDslRepository.java
```

### 4.2 DB 스키마

```sql
CREATE TABLE category (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(100) NOT NULL,
    name_ko         VARCHAR(255) NOT NULL,
    name_en         VARCHAR(255),
    parent_id       BIGINT UNSIGNED NULL,
    depth           TINYINT UNSIGNED NOT NULL DEFAULT 0,
    path            VARCHAR(1000) NOT NULL,
    sort_order      INT NOT NULL DEFAULT 0,
    is_leaf         TINYINT(1) NOT NULL DEFAULT 1,

    -- 상태/노출
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_visible      TINYINT(1) NOT NULL DEFAULT 1,
    is_listable     TINYINT(1) NOT NULL DEFAULT 1,

    -- 비즈니스 분류 (VARCHAR로 변경 - 확장성)
    department      VARCHAR(50) NOT NULL DEFAULT 'FASHION',
    product_group   VARCHAR(50) NOT NULL DEFAULT 'ETC',
    gender_scope    VARCHAR(20) NOT NULL DEFAULT 'NONE',
    age_group       VARCHAR(20) NOT NULL DEFAULT 'NONE',

    -- UX/SEO
    display_name    VARCHAR(255),
    seo_slug        VARCHAR(255),
    icon_url        VARCHAR(500),

    -- 동시성 제어 (낙관적 락)
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0,

    -- Audit
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    -- Indexes
    UNIQUE KEY uk_category_code (code),
    KEY idx_category_parent (parent_id),
    KEY idx_category_parent_sort (parent_id, sort_order),  -- 자식 정렬 조회용
    KEY idx_category_status (status, is_visible),
    KEY idx_category_business (department, product_group, gender_scope),
    KEY idx_category_path (path(255)),
    KEY idx_category_updated (updated_at),

    -- FK (Long FK 전략이지만 참조 무결성 유지)
    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id) REFERENCES category(id)
);
```

### 4.3 JPA Entity

```java
@Entity
@Table(name = "category")
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "name_ko", nullable = false)
    private String nameKo;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "parent_id")
    private Long parentId;              // Long FK (관계 어노테이션 없음)

    @Column(nullable = false)
    private Integer depth;

    @Column(nullable = false, length = 1000)
    private String path;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_leaf", nullable = false)
    private Boolean isLeaf;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible;

    @Column(name = "is_listable", nullable = false)
    private Boolean isListable;

    @Column(nullable = false, length = 50)
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

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "seo_slug")
    private String seoSlug;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    // 동시성 제어 (낙관적 락)
    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Protected 생성자 (JPA용)
    protected CategoryJpaEntity() {}

    // 정적 팩토리 메서드
    public static CategoryJpaEntity from(Category domain) {
        CategoryJpaEntity entity = new CategoryJpaEntity();
        entity.id = domain.id();
        entity.code = domain.code();
        entity.nameKo = domain.nameKo();
        entity.nameEn = domain.nameEn();
        entity.parentId = domain.parentId();
        entity.depth = domain.depth();
        entity.path = domain.path();
        entity.sortOrder = domain.sortOrder();
        entity.isLeaf = domain.isLeaf();
        entity.status = domain.status();
        entity.isVisible = domain.isVisible();
        entity.isListable = domain.isListable();
        entity.department = domain.department();
        entity.productGroup = domain.productGroup();
        entity.genderScope = domain.genderScope();
        entity.ageGroup = domain.ageGroup();
        entity.displayName = domain.displayName();
        entity.seoSlug = domain.seoSlug();
        entity.iconUrl = domain.iconUrl();
        return entity;
    }

    // Getter only (Setter 금지)
    public Long getId() { return id; }
    public String getCode() { return code; }
    // ... 나머지 Getter
}
```

### 4.4 JPA Repository (Command)

```java
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByCode(String code);

    Optional<CategoryJpaEntity> findByCode(String code);
}
```

### 4.5 QueryDSL Repository (Query)

```java
@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private static final QCategoryJpaEntity category = QCategoryJpaEntity.categoryJpaEntity;

    public List<CategoryJpaEntity> findByParentId(Long parentId) {
        return queryFactory
                .selectFrom(category)
                .where(parentIdEq(parentId))
                .orderBy(category.sortOrder.asc())
                .fetch();
    }

    public List<CategoryJpaEntity> findAllActiveVisible() {
        return queryFactory
                .selectFrom(category)
                .where(
                        category.status.eq(CategoryStatus.ACTIVE),
                        category.isVisible.isTrue()
                )
                .orderBy(category.depth.asc(), category.sortOrder.asc())
                .fetch();
    }

    public List<CategoryJpaEntity> findListableLeaves(CategorySearchQuery query) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(category.isLeaf.isTrue());
        builder.and(category.isListable.isTrue());
        builder.and(category.status.eq(CategoryStatus.ACTIVE));
        builder.and(category.isVisible.isTrue());

        if (query.department() != null) {
            builder.and(category.department.eq(query.department()));
        }
        if (query.productGroup() != null) {
            builder.and(category.productGroup.eq(query.productGroup()));
        }
        if (query.genderScope() != null) {
            builder.and(category.genderScope.eq(query.genderScope()));
        }

        return queryFactory
                .selectFrom(category)
                .where(builder)
                .orderBy(category.path.asc())
                .fetch();
    }

    public List<CategoryJpaEntity> findDescendants(Long categoryId) {
        // path LIKE '/ROOT/PARENT/CHILD%' 패턴으로 조회
        CategoryJpaEntity parent = queryFactory
                .selectFrom(category)
                .where(category.id.eq(categoryId))
                .fetchOne();

        if (parent == null) {
            return List.of();
        }

        return queryFactory
                .selectFrom(category)
                .where(
                        category.path.startsWith(parent.getPath() + "/"),
                        category.id.ne(categoryId)
                )
                .orderBy(category.depth.asc())
                .fetch();
    }

    public List<CategoryJpaEntity> search(String keyword) {
        return queryFactory
                .selectFrom(category)
                .where(
                        category.nameKo.containsIgnoreCase(keyword)
                                .or(category.nameEn.containsIgnoreCase(keyword))
                                .or(category.code.containsIgnoreCase(keyword))
                                .or(category.displayName.containsIgnoreCase(keyword))
                )
                .orderBy(category.depth.asc(), category.sortOrder.asc())
                .fetch();
    }

    public List<CategoryJpaEntity> findUpdatedSince(LocalDateTime since) {
        return queryFactory
                .selectFrom(category)
                .where(category.updatedAt.after(since))
                .orderBy(category.updatedAt.asc())
                .fetch();
    }

    public boolean hasChildren(Long categoryId) {
        Integer count = queryFactory
                .selectOne()
                .from(category)
                .where(category.parentId.eq(categoryId))
                .fetchFirst();
        return count != null;
    }

    private BooleanExpression parentIdEq(Long parentId) {
        return parentId == null
                ? category.parentId.isNull()
                : category.parentId.eq(parentId);
    }
}
```

### 4.6 Command Adapter

```java
@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryPersistencePort {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryJpaEntityMapper mapper;

    @Override
    public void persist(Category category) {
        CategoryJpaEntity entity = mapper.toEntity(category);
        jpaRepository.save(entity);
    }

    @Override
    public void persistAll(List<Category> categories) {
        List<CategoryJpaEntity> entities = categories.stream()
                .map(mapper::toEntity)
                .toList();
        jpaRepository.saveAll(entities);
    }

    @Override
    public void delete(Long categoryId) {
        jpaRepository.deleteById(categoryId);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }
}
```

### 4.7 Query Adapter

```java
@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryQueryDslRepository queryDslRepository;
    private final CategoryJpaEntityMapper mapper;

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
        return jpaRepository.findAll().stream()
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
```

### 4.8 Entity Mapper

```java
@Component
public class CategoryJpaEntityMapper {

    public Category toDomain(CategoryJpaEntity entity) {
        return Category.reconstitute(
                CategoryId.of(entity.getId()),
                CategoryCode.of(entity.getCode()),
                entity.getParentId(),
                CategoryDepth.of(entity.getDepth()),
                CategoryPath.of(entity.getPath()),
                SortOrder.of(entity.getSortOrder()),
                entity.getIsLeaf(),
                CategoryName.of(entity.getNameKo(), entity.getNameEn()),
                entity.getStatus(),
                entity.getIsVisible(),
                entity.getIsListable(),
                BusinessClassification.of(
                        entity.getDepartment(),
                        entity.getProductGroup(),
                        entity.getGenderScope(),
                        entity.getAgeGroup()
                ),
                DisplayMeta.of(
                        entity.getDisplayName(),
                        entity.getSeoSlug(),
                        entity.getIconUrl()
                )
        );
    }

    public CategoryJpaEntity toEntity(Category domain) {
        return CategoryJpaEntity.from(domain);
    }
}
```

---

## 5. REST API Layer 설계

### 5.1 패키지 구조
```
adapter-in/rest-api/
└── catalog/
    └── category/
        ├── controller/
        │   ├── CategoryAdminCommandController.java
        │   ├── CategoryAdminQueryController.java
        │   └── CategoryPublicQueryController.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateCategoryApiRequest.java
        │   │   ├── UpdateCategoryApiRequest.java
        │   │   ├── ChangeCategoryStatusApiRequest.java
        │   │   └── MoveCategoryApiRequest.java
        │   ├── query/
        │   │   ├── CategorySearchApiRequest.java
        │   │   └── CategoryTreeApiRequest.java
        │   └── response/
        │       ├── CategoryApiResponse.java
        │       ├── CategoryTreeApiResponse.java
        │       └── CategoryPathApiResponse.java
        ├── mapper/
        │   └── CategoryApiMapper.java
        └── error/
            └── CategoryApiErrorMapper.java
```

### 5.2 API 역할 구분

> **Admin API**는 내부 운영용이며, 인증/인가에서 ROLE_ADMIN (혹은 그에 준하는 권한)을 요구한다.
> 쓰기 기능 + 관리용 조회를 제공한다.
>
> **Public API**는 BFF/외부 서비스에서 참조하는 기준 트리 조회용이며, 쓰기 기능은 제공하지 않는다.
> 읽기 전용이며, 캐싱을 전제로 설계되었다.

### 5.3 API 명세

#### Admin API (관리자 전용, 쓰기 + 관리용 조회)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/categories/tree` | 트리 전체 조회 |
| GET | `/api/v1/admin/catalog/categories/{id}` | 단일 조회 |
| GET | `/api/v1/admin/catalog/categories/search` | 검색 |
| POST | `/api/v1/admin/catalog/categories` | 생성 |
| PATCH | `/api/v1/admin/catalog/categories/{id}` | 수정 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/status` | 상태 변경 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/move` | 이동 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/reorder` | 순서 변경 |
| DELETE | `/api/v1/admin/catalog/categories/{id}` | 삭제 |

#### Public API (조회 전용, 캐싱 전제)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/catalog/categories/tree` | 트리 조회 (ACTIVE + visible만) |
| GET | `/api/v1/catalog/categories/leaf` | Leaf 카테고리 목록 |
| GET | `/api/v1/catalog/categories/{id}` | 단일 조회 |
| GET | `/api/v1/catalog/categories/by-code/{code}` | 코드로 조회 |
| GET | `/api/v1/catalog/categories/{id}/path` | 경로 조회 (breadcrumb) |
| GET | `/api/v1/catalog/categories/{id}/children` | 자식 조회 |
| GET | `/api/v1/catalog/categories/search` | 검색 |
| GET | `/api/v1/catalog/categories/updated-since` | 증분 조회 |

### 5.3 Request DTO

```java
public record CreateCategoryApiRequest(
        @Nullable Long parentId,

        @NotBlank(message = "코드는 필수입니다")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]{2,99}$",
                 message = "코드는 대문자로 시작, A-Z/0-9/_ 조합, 3-100자")
        String code,

        @NotBlank(message = "한글 이름은 필수입니다")
        @Size(max = 255, message = "한글 이름은 최대 255자")
        String nameKo,

        @Size(max = 255, message = "영문 이름은 최대 255자")
        String nameEn,

        @Min(value = 0, message = "정렬 순서는 0 이상")
        int sortOrder,

        boolean isListable,
        boolean isVisible,

        @NotNull(message = "부문은 필수입니다")
        Department department,

        @NotNull(message = "상품군은 필수입니다")
        ProductGroup productGroup,

        GenderScope genderScope,
        AgeGroup ageGroup,

        @Size(max = 255, message = "표시명은 최대 255자")
        String displayName,

        @Size(max = 255, message = "SEO 슬러그는 최대 255자")
        String seoSlug,

        @Size(max = 500, message = "아이콘 URL은 최대 500자")
        String iconUrl
) {}

public record UpdateCategoryApiRequest(
        @NotBlank(message = "한글 이름은 필수입니다")
        @Size(max = 255)
        String nameKo,

        @Size(max = 255)
        String nameEn,

        boolean isListable,
        boolean isVisible,

        @Min(0)
        int sortOrder,

        @Size(max = 255)
        String displayName,

        @Size(max = 255)
        String seoSlug,

        @Size(max = 500)
        String iconUrl
) {}

public record ChangeCategoryStatusApiRequest(
        @NotNull(message = "상태는 필수입니다")
        CategoryStatus status,

        Long replacementCategoryId
) {}

public record MoveCategoryApiRequest(
        @NotNull(message = "새 부모 ID는 필수입니다")
        Long newParentId,

        @Min(0)
        int newSortOrder
) {}
```

### 5.4 Response DTO

```java
public record CategoryApiResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        Long parentId,
        int depth,
        String path,
        int sortOrder,
        boolean isLeaf,
        String status,
        boolean isVisible,
        boolean isListable,
        String department,
        String productGroup,
        String genderScope,
        String ageGroup,
        String displayName,
        String seoSlug,
        String iconUrl
) {}

public record CategoryTreeApiResponse(
        List<CategoryTreeNodeApiResponse> roots
) {
    public record CategoryTreeNodeApiResponse(
            CategoryApiResponse category,
            List<CategoryTreeNodeApiResponse> children
    ) {}
}

public record CategoryPathApiResponse(
        Long categoryId,
        List<CategoryApiResponse> ancestors
) {}
```

### 5.5 Admin Command Controller

```java
@RestController
@RequestMapping("/api/v1/admin/catalog/categories")
@RequiredArgsConstructor
public class CategoryAdminCommandController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final ChangeCategoryStatusUseCase changeCategoryStatusUseCase;
    private final MoveCategoryUseCase moveCategoryUseCase;
    private final CategoryApiMapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryApiResponse>> create(
            @Valid @RequestBody CreateCategoryApiRequest request
    ) {
        CreateCategoryCommand command = mapper.toCommand(request);
        CategoryResponse response = createCategoryUseCase.create(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> update(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryApiRequest request
    ) {
        UpdateCategoryCommand command = mapper.toCommand(categoryId, request);
        CategoryResponse response = updateCategoryUseCase.update(command);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @PatchMapping("/{categoryId}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long categoryId,
            @Valid @RequestBody ChangeCategoryStatusApiRequest request
    ) {
        ChangeCategoryStatusCommand command = mapper.toCommand(categoryId, request);
        changeCategoryStatusUseCase.changeStatus(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{categoryId}/move")
    public ResponseEntity<ApiResponse<Void>> move(
            @PathVariable Long categoryId,
            @Valid @RequestBody MoveCategoryApiRequest request
    ) {
        MoveCategoryCommand command = mapper.toCommand(categoryId, request);
        moveCategoryUseCase.move(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Long categoryId) {
        // Soft Delete 권장 - status를 DEPRECATED로 변경
        changeCategoryStatusUseCase.changeStatus(
                new ChangeCategoryStatusCommand(categoryId, CategoryStatus.DEPRECATED, null)
        );
        return ResponseEntity.noContent().build();
    }
}
```

### 5.6 Public Query Controller

```java
@RestController
@RequestMapping("/api/v1/catalog/categories")
@RequiredArgsConstructor
public class CategoryPublicQueryController {

    private final GetCategoryTreeUseCase getCategoryTreeUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final GetCategoryPathUseCase getCategoryPathUseCase;
    private final SearchCategoryUseCase searchCategoryUseCase;
    private final CategoryApiMapper mapper;

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<CategoryTreeApiResponse>> getTree(
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) ProductGroup productGroup
    ) {
        CategoryTreeQuery query = new CategoryTreeQuery(false, department, productGroup);
        CategoryTreeResponse response = getCategoryTreeUseCase.getTree(query);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @GetMapping("/leaf")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> getLeaves(
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) ProductGroup productGroup,
            @RequestParam(required = false) GenderScope genderScope
    ) {
        CategorySearchQuery query = new CategorySearchQuery(
                null, department, productGroup, genderScope, true, true
        );
        List<CategoryResponse> responses = searchCategoryUseCase.searchLeaves(query);
        return ResponseEntity.ok(
                ApiResponse.success(responses.stream().map(mapper::toApiResponse).toList())
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> getById(
            @PathVariable Long categoryId
    ) {
        CategoryResponse response = getCategoryUseCase.getById(categoryId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> getByCode(
            @PathVariable String code
    ) {
        CategoryResponse response = getCategoryUseCase.getByCode(code);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @GetMapping("/{categoryId}/path")
    public ResponseEntity<ApiResponse<CategoryPathApiResponse>> getPath(
            @PathVariable Long categoryId
    ) {
        CategoryPathResponse response = getCategoryPathUseCase.getPath(categoryId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> search(
            @RequestParam String keyword
    ) {
        List<CategoryResponse> responses = searchCategoryUseCase.search(keyword);
        return ResponseEntity.ok(
                ApiResponse.success(responses.stream().map(mapper::toApiResponse).toList())
        );
    }

    @GetMapping("/updated-since")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> getUpdatedSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since
    ) {
        List<CategoryResponse> responses = searchCategoryUseCase.findUpdatedSince(since);
        return ResponseEntity.ok(
                ApiResponse.success(responses.stream().map(mapper::toApiResponse).toList())
        );
    }
}
```

### 5.7 Error Response (RFC 7807)

```java
@Component
public class CategoryApiErrorMapper implements ApiErrorMapper {

    @Override
    public boolean supports(DomainException exception) {
        return exception instanceof CategoryNotFoundException
                || exception instanceof CategoryCodeDuplicateException
                || exception instanceof CategoryCycleDetectedException
                || exception instanceof CategoryNotListableException;
    }

    @Override
    public ErrorInfo map(DomainException exception) {
        return switch (exception) {
            case CategoryNotFoundException e -> ErrorInfo.of(
                    "CATEGORY_NOT_FOUND",
                    "카테고리를 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case CategoryCodeDuplicateException e -> ErrorInfo.of(
                    "CATEGORY_CODE_DUPLICATE",
                    "이미 존재하는 카테고리 코드입니다",
                    HttpStatus.CONFLICT
            );
            case CategoryCycleDetectedException e -> ErrorInfo.of(
                    "CATEGORY_CYCLE_DETECTED",
                    "카테고리를 자기 자신의 하위로 이동할 수 없습니다",
                    HttpStatus.BAD_REQUEST
            );
            case CategoryNotListableException e -> ErrorInfo.of(
                    "CATEGORY_NOT_LISTABLE",
                    "해당 카테고리에는 상품을 등록할 수 없습니다",
                    HttpStatus.BAD_REQUEST
            );
            default -> ErrorInfo.of(
                    "CATEGORY_ERROR",
                    exception.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        };
    }
}
```

---

## 6. 도메인 규칙 정리

### 6.1 트리 무결성 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| 루트 노드 | `parentId = null`, `depth = 0` | Domain |
| 깊이 계산 | `depth = parent.depth + 1` | Domain |
| 경로 계산 | `path = parent.path + "/" + code` | Domain |
| Leaf 관리 | 자식 추가/삭제 시 자동 갱신 | Service |
| isLeaf 무결성 | 자식이 있는 노드는 `isLeaf = false` 이어야 함 | Domain/Service |
| Cycle 방지 | 자기 하위로 이동 불가 | Domain |
| 최대 깊이 | `depth <= 10` | VO |

### 6.2 상태/노출 규칙

| 상태 | 설명 | 제약 |
|------|------|------|
| ACTIVE | 정상 사용 | - |
| INACTIVE | 임시 중지 | 노출/등록 제한 |
| DEPRECATED | 사용 종료 | 신규 등록 불가, 기존 상품 유지 |

### 6.3 상품 등록 가능 조건

```java
canRegisterProduct() = isLeaf && isListable && status == ACTIVE
```

### 6.4 동시성 및 트랜잭션 정책

#### 낙관적 락 (Optimistic Locking)
- `version` 컬럼 + `@Version` 어노테이션으로 동시성 제어
- move / reorder 시 동일 parent 아래에서 동시 변경 발생 시 **409 Conflict** 반환
- 클라이언트는 "다시 로드하고 재시도" 유도

#### 트랜잭션 경계
- **Move 연산**: 상당히 많은 row update가 동반될 수 있음 (자기 + 모든 descendant)
- 하나의 트랜잭션으로 묶어서 **원자성 보장**
- 카테고리 트리 크기가 관리 가능한 수준이라 long-running 가능성은 낮음

```java
@Transactional  // 원자성 보장
public void move(MoveCategoryCommand command) {
    // 1. 이동 대상 + 하위 노드 전체 조회
    // 2. 경로/깊이 재계산
    // 3. 기존/새 부모의 isLeaf 업데이트
    // 4. 일괄 저장
}
```

### 6.5 증분 조회 설계

#### 현재 (v1)
- `since` 파라미터 기반 일괄 조회
- 인덱스: `idx_category_updated (updated_at)`

```
GET /api/v1/catalog/categories/updated-since?since=2025-01-01T00:00:00
```

#### 로드맵 (v2)
- 변경량이 하루 수천/수만이 될 경우, **커서 기반 페이징** 추가 예정
- `pageSize` + `lastId` 기반 증분 조회

```
GET /api/v1/catalog/categories/updated-since?since=...&pageSize=100&lastId=12345
```

> 1단계에서는 since 기반 일괄 조회만 제공하고, 이후 필요 시 커서/배치 단위의 페이징 증분 조회를 추가한다.

---

## 7. 구현 우선순위 (TDD 사이클)

### Phase 1: Domain Layer
1. VO 테스트 및 구현 (CategoryCode, CategoryName, CategoryPath, ...)
2. Aggregate Root 테스트 및 구현 (Category)
3. Exception 테스트 및 구현

### Phase 2: Persistence Layer
4. Entity 테스트 및 구현
5. JPA Repository 테스트 및 구현
6. QueryDSL Repository 테스트 및 구현
7. Mapper 테스트 및 구현
8. Adapter 테스트 및 구현

### Phase 3: Application Layer
9. Assembler 테스트 및 구현
10. Command Service 테스트 및 구현
11. Query Service 테스트 및 구현

### Phase 4: REST API Layer
12. API Request/Response DTO
13. API Mapper 테스트 및 구현
14. Controller 테스트 및 구현
15. Error Mapper 구현

### Phase 5: Integration Test
16. E2E 테스트 (TestRestTemplate)

---

## 8. 캐싱 전략 (선택)

### 8.1 캐싱 대상

| 데이터 | 캐시 키 | TTL | 무효화 조건 |
|--------|---------|-----|------------|
| 전체 트리 | `category:tree` | 1h | 생성/수정/삭제/이동 |
| 단일 카테고리 | `category:{id}` | 1h | 해당 카테고리 수정 |
| Leaf 목록 | `category:leaves:{filter}` | 1h | 생성/수정/삭제 |

### 8.2 무효화 전략
- 이벤트 기반 무효화 (CategoryCreatedEvent 등)
- 캐시 어노테이션 (`@CacheEvict`)

---

## 9. 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| v1 | - | 초안 작성 |
| v2 | 2025-11-27 | 헥사고날 아키텍처 기반 재설계, 코딩 컨벤션 적용 |
| v2.1 | 2025-11-27 | 피드백 반영: version 컬럼 추가, isLeaf 파생값 명시, API 역할 구분, BusinessClassification/DisplayMeta 예시, 복합 인덱스, 동시성 정책, 증분 조회 로드맵 |
