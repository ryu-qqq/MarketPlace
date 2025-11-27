# Catalog - Attribute 모듈 설계 v1

## 1. 개요

### 1.1 목적
내부 카탈로그에서 상품/옵션이 가져야 할 **스펙(속성)**을 정의하고, 카테고리별로 **"어떤 속성이 필수/선택인지"**를 선언적으로 관리한다.

### 1.2 배경
- 상품 등록 시 카테고리에 따라 필요한 속성이 다름
  - 예: 반팔 티셔츠 → COLOR, SIZE 필수
  - 예: 스니커즈 → COLOR, SIZE, GENDER 필수, MATERIAL 선택
- 외부몰/셀러 데이터 정규화의 기준이 필요
- 검색 필터(색상/사이즈/소재/성별) 스펙 제공 필요

### 1.3 범위

| 구분 | 포함 | 제외 (후속 단계) |
|------|------|-----------------|
| v1 | Attribute/AttributeValue/CategoryAttribute 도메인, Admin/Public API | product_attribute, seller_sku_attribute |
| v2 | - | 상품/옵션 저장 시 필수값 검증 로직 |
| v3 | - | external_attribute_mapping (외부 속성 매핑) |

### 1.4 전제 조건
- **인증/인가**: 게이트웨이에서 처리됨
- **아키텍처**: 헥사고날 아키텍처 (Ports & Adapters)
- **패턴**: CQRS, Long FK 전략
- **Aggregate 설계**: Attribute가 Root, AttributeValue는 내부 Entity

---

## 2. Domain Layer 설계

### 2.1 Bounded Context
```
catalog (카탈로그)
└── attribute (속성)
```

### 2.2 패키지 구조
```
domain/
└── catalog/
    └── attribute/
        ├── aggregate/
        │   └── attribute/
        │       ├── Attribute.java              # Aggregate Root
        │       └── AttributeValue.java         # 내부 Entity
        ├── entity/
        │   └── CategoryAttribute.java          # 독립 Entity (매핑 테이블)
        ├── vo/
        │   ├── AttributeId.java
        │   ├── AttributeCode.java              # 유니크 코드
        │   ├── AttributeName.java              # 다국어 이름 (ko, en)
        │   ├── ValueType.java                  # 값 타입 Enum
        │   ├── AppliesLevel.java               # 적용 레벨 Enum
        │   ├── AttributeValueId.java
        │   ├── AttributeValueCode.java         # 값 코드
        │   ├── AttributeValueName.java         # 다국어 값 이름
        │   ├── ValueMeta.java                  # 값 메타 정보 (hex, mm 등)
        │   ├── CategoryAttributeId.java
        │   ├── AttributeConstraint.java        # 제약 조건 (min, max count)
        │   └── GroupCode.java                  # UI 그룹 코드
        ├── event/
        │   ├── AttributeCreatedEvent.java
        │   ├── AttributeValueAddedEvent.java
        │   └── CategoryAttributeLinkedEvent.java
        └── exception/
            ├── AttributeNotFoundException.java
            ├── AttributeCodeDuplicateException.java
            ├── AttributeValueNotFoundException.java
            ├── AttributeValueCodeDuplicateException.java
            ├── CategoryAttributeNotFoundException.java
            ├── CategoryAttributeDuplicateException.java
            └── InvalidAppliesLevelException.java
```

### 2.3 Aggregate Root: Attribute

```java
/**
 * 속성 Aggregate Root
 *
 * 불변식:
 * - code는 시스템 전체에서 유니크
 * - valueType = ENUM이면 values 목록 필수
 * - values는 Attribute를 통해서만 추가/삭제
 * - 동일 attribute 내 valueCode 유니크
 */
public class Attribute {

    // === 식별자 ===
    private final AttributeId id;
    private final AttributeCode code;

    // === 이름 ===
    private AttributeName name;

    // === 타입 정보 ===
    private final ValueType valueType;        // 생성 후 변경 불가
    private AppliesLevel appliesLevel;

    // === 상태 ===
    private boolean isActive;

    // === 설명 ===
    private String description;

    // === 동시성 제어 ===
    private Long version;

    // === 값 목록 (내부 Entity 컬렉션) - ENUM 타입용 ===
    private final List<AttributeValue> values;

    // === 생성 메서드 ===

    /**
     * 새 속성 생성
     */
    public static Attribute create(
            AttributeCode code,
            AttributeName name,
            ValueType valueType,
            AppliesLevel appliesLevel,
            String description
    ) {
        return new Attribute(
                AttributeId.generate(),
                code,
                name,
                valueType,
                appliesLevel,
                true,
                description,
                new ArrayList<>()
        );
    }

    /**
     * 영속성에서 재구성
     */
    public static Attribute reconstitute(
            AttributeId id,
            AttributeCode code,
            AttributeName name,
            ValueType valueType,
            AppliesLevel appliesLevel,
            boolean isActive,
            String description,
            List<AttributeValue> values
    ) {
        return new Attribute(
                id, code, name, valueType, appliesLevel,
                isActive, description, new ArrayList<>(values)
        );
    }

    private Attribute(/* 모든 필드 */) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.valueType = valueType;
        this.appliesLevel = appliesLevel;
        this.isActive = isActive;
        this.description = description;
        this.values = values;
    }

    // === 도메인 행위 ===

    /**
     * 기본 정보 수정
     */
    public void update(
            AttributeName name,
            AppliesLevel appliesLevel,
            String description
    ) {
        this.name = name;
        this.appliesLevel = appliesLevel;
        this.description = description;
    }

    /**
     * 활성/비활성 전환
     *
     * ⚠️ 피드백 반영: ENUM 타입 Activation 시점 검증
     * - ENUM 타입인데 values가 없으면 활성화 불가
     * - 생성 시점이 아닌 활성화 시점에 검증 (Admin이 "생성 → 값 추가 → 활성화" 순서로 작업 가능)
     */
    public void activate() {
        validateCanActivate();
        this.isActive = true;
    }

    private void validateCanActivate() {
        if (isEnumType() && values.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot activate ENUM attribute without values: " + code.value()
            );
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    /**
     * ENUM 타입인지 확인
     */
    public boolean isEnumType() {
        return valueType == ValueType.ENUM;
    }

    /**
     * 특정 appliesLevel과 호환되는지 검증
     * - BOTH는 모든 레벨과 호환
     * - PRODUCT는 PRODUCT만 허용
     * - SKU는 SKU만 허용
     */
    public boolean isCompatibleWith(AppliesLevel targetLevel) {
        if (this.appliesLevel == AppliesLevel.BOTH) {
            return true;
        }
        return this.appliesLevel == targetLevel;
    }

    /**
     * CategoryAttribute에서 사용 가능한 appliesLevel 검증
     */
    public void validateAppliesLevel(AppliesLevel categoryLevel) {
        if (!isCompatibleWith(categoryLevel)) {
            throw new InvalidAppliesLevelException(
                    code, appliesLevel, categoryLevel
            );
        }
    }

    // === Value 관리 (Attribute를 통해서만 접근) ===

    /**
     * 값 추가 (ENUM 타입 전용)
     *
     * ⚠️ AttributeValue에 attributeId를 전달하지 않음
     * - Aggregate 내부 Entity는 부모 참조 불필요
     * - JPA 저장 시 Mapper에서 attributeId 주입
     */
    public AttributeValue addValue(
            AttributeValueCode valueCode,
            AttributeValueName valueName,
            int sortOrder,
            ValueMeta meta
    ) {
        validateEnumType();
        validateValueCodeNotDuplicate(valueCode);

        AttributeValue value = AttributeValue.create(
                valueCode,
                valueName,
                sortOrder,
                meta
        );
        this.values.add(value);
        return value;
    }

    private void validateEnumType() {
        if (!isEnumType()) {
            throw new IllegalStateException(
                    "Cannot add values to non-ENUM attribute: " + code.value()
            );
        }
    }

    private void validateValueCodeNotDuplicate(AttributeValueCode valueCode) {
        boolean exists = values.stream()
                .anyMatch(v -> v.code().equals(valueCode.value()));
        if (exists) {
            throw new AttributeValueCodeDuplicateException(id, valueCode);
        }
    }

    /**
     * 값 수정
     */
    public void updateValue(
            Long valueId,
            AttributeValueName valueName,
            int sortOrder,
            ValueMeta meta
    ) {
        findValue(valueId).update(valueName, sortOrder, meta);
    }

    /**
     * 값 활성/비활성
     */
    public void activateValue(Long valueId) {
        findValue(valueId).activate();
    }

    public void deactivateValue(Long valueId) {
        findValue(valueId).deactivate();
    }

    private AttributeValue findValue(Long valueId) {
        return values.stream()
                .filter(v -> v.id().equals(valueId))
                .findFirst()
                .orElseThrow(() -> new AttributeValueNotFoundException(id, valueId));
    }

    // === Getter (Law of Demeter 준수) ===

    public Long id() { return id.value(); }
    public String code() { return code.value(); }

    public String nameKo() { return name.ko(); }
    public String nameEn() { return name.en(); }

    public ValueType valueType() { return valueType; }
    public AppliesLevel appliesLevel() { return appliesLevel; }
    public boolean isActive() { return isActive; }
    public String description() { return description; }

    // 값 목록 - 불변 리스트로 반환 (내부 수정 방지)
    public List<AttributeValue> values() {
        return Collections.unmodifiableList(values);
    }

    public List<AttributeValue> activeValues() {
        return values.stream()
                .filter(AttributeValue::isActive)
                .toList();
    }

    public int valueCount() {
        return values.size();
    }

    public boolean hasValue(String valueCode) {
        return values.stream()
                .anyMatch(v -> v.code().equals(valueCode));
    }
}
```

### 2.4 내부 Entity: AttributeValue

```java
/**
 * 속성 값 (내부 Entity)
 *
 * Attribute Aggregate 내부에서만 관리됨.
 * ENUM 타입 Attribute의 선택 가능한 값 목록.
 * 독립적인 라이프사이클 없음 - Attribute를 통해서만 생성/수정.
 *
 * ⚠️ 설계 원칙: Aggregate 내부 Entity는 부모 FK를 가지지 않음
 * - AttributeValue는 항상 Attribute를 통해서만 접근됨
 * - attributeId는 JPA 매핑 시점에 Mapper에서 주입
 * - 도메인 모델의 순수성 유지
 */
public class AttributeValue {

    private final AttributeValueId id;
    // ❌ attributeId 제거 - Aggregate 내부 Entity는 부모 참조 불필요
    private final AttributeValueCode code;    // 값 코드 (WHITE, M 등)
    private AttributeValueName name;          // 다국어 이름
    private int sortOrder;                    // 정렬 순서
    private ValueMeta meta;                   // 메타 정보 (hex, mm 등)
    private boolean isActive;                 // 활성 여부

    // === 생성 메서드 ===

    static AttributeValue create(
            AttributeValueCode code,
            AttributeValueName name,
            int sortOrder,
            ValueMeta meta
    ) {
        return new AttributeValue(
                AttributeValueId.generate(),
                code,
                name,
                sortOrder,
                meta,
                true
        );
    }

    static AttributeValue reconstitute(
            AttributeValueId id,
            AttributeValueCode code,
            AttributeValueName name,
            int sortOrder,
            ValueMeta meta,
            boolean isActive
    ) {
        return new AttributeValue(id, code, name, sortOrder, meta, isActive);
    }

    private AttributeValue(
            AttributeValueId id,
            AttributeValueCode code,
            AttributeValueName name,
            int sortOrder,
            ValueMeta meta,
            boolean isActive
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sortOrder = sortOrder;
        this.meta = meta;
        this.isActive = isActive;
    }

    // === 도메인 행위 ===

    void update(AttributeValueName name, int sortOrder, ValueMeta meta) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.meta = meta;
    }

    void activate() {
        this.isActive = true;
    }

    void deactivate() {
        this.isActive = false;
    }

    // === Getter ===

    public Long id() { return id.value(); }
    // ❌ attributeId() 제거 - 도메인에서 불필요
    public String code() { return code.value(); }
    public String nameKo() { return name.ko(); }
    public String nameEn() { return name.en(); }
    public int sortOrder() { return sortOrder; }
    public String metaJson() { return meta != null ? meta.toJson() : null; }
    public boolean isActive() { return isActive; }

    // 색상용 헬퍼
    public String hexColor() {
        return meta != null ? meta.hex() : null;
    }

    // 사이즈용 헬퍼
    public Integer mmValue() {
        return meta != null ? meta.mm() : null;
    }
}
```

### 2.5 독립 Entity: CategoryAttribute

```java
/**
 * 카테고리-속성 매핑 (독립 Entity)
 *
 * 특정 카테고리에서 어떤 속성이 필수/선택인지 정의.
 * Category와 Attribute 사이의 다대다 관계를 표현.
 *
 * 불변식:
 * - category_id + attribute_id + applies_level 유니크
 * - appliesLevel은 Attribute의 appliesLevel과 호환되어야 함
 */
public class CategoryAttribute {

    private final CategoryAttributeId id;
    private final Long categoryId;            // Long FK (Category 참조)
    private final Long attributeId;           // Long FK (Attribute 참조)
    private AppliesLevel appliesLevel;        // 이 카테고리에서의 적용 레벨
    private boolean isRequired;               // 필수 여부
    private AttributeConstraint constraint;   // 제약 조건 (min/max count)
    private GroupCode groupCode;              // UI 그룹
    private int sortOrder;                    // 표시 순서
    private boolean isActive;                 // 활성 여부
    private Long version;                     // 동시성 제어

    // === 생성 메서드 ===

    public static CategoryAttribute create(
            Long categoryId,
            Long attributeId,
            AppliesLevel appliesLevel,
            boolean isRequired,
            AttributeConstraint constraint,
            GroupCode groupCode,
            int sortOrder
    ) {
        return new CategoryAttribute(
                CategoryAttributeId.generate(),
                categoryId,
                attributeId,
                appliesLevel,
                isRequired,
                constraint,
                groupCode,
                sortOrder,
                true
        );
    }

    public static CategoryAttribute reconstitute(
            CategoryAttributeId id,
            Long categoryId,
            Long attributeId,
            AppliesLevel appliesLevel,
            boolean isRequired,
            AttributeConstraint constraint,
            GroupCode groupCode,
            int sortOrder,
            boolean isActive
    ) {
        return new CategoryAttribute(
                id, categoryId, attributeId, appliesLevel,
                isRequired, constraint, groupCode, sortOrder, isActive
        );
    }

    private CategoryAttribute(/* 모든 필드 */) {
        this.id = id;
        this.categoryId = categoryId;
        this.attributeId = attributeId;
        this.appliesLevel = appliesLevel;
        this.isRequired = isRequired;
        this.constraint = constraint;
        this.groupCode = groupCode;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    // === 도메인 행위 ===

    public void update(
            boolean isRequired,
            AttributeConstraint constraint,
            GroupCode groupCode,
            int sortOrder
    ) {
        this.isRequired = isRequired;
        this.constraint = constraint;
        this.groupCode = groupCode;
        this.sortOrder = sortOrder;
    }

    public void changeAppliesLevel(AppliesLevel newLevel) {
        this.appliesLevel = newLevel;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 값 개수 검증
     */
    public boolean validateCount(int count) {
        if (constraint == null) {
            return true;
        }
        return constraint.isValidCount(count);
    }

    // === Getter ===

    public Long id() { return id.value(); }
    public Long categoryId() { return categoryId; }
    public Long attributeId() { return attributeId; }
    public AppliesLevel appliesLevel() { return appliesLevel; }
    public boolean isRequired() { return isRequired; }
    public Integer minCount() { return constraint != null ? constraint.minCount() : null; }
    public Integer maxCount() { return constraint != null ? constraint.maxCount() : null; }
    public String groupCode() { return groupCode != null ? groupCode.value() : null; }
    public int sortOrder() { return sortOrder; }
    public boolean isActive() { return isActive; }
}
```

### 2.6 Value Objects

#### AttributeId
```java
public record AttributeId(Long value) {
    public AttributeId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("AttributeId must be positive");
        }
    }

    public static AttributeId of(Long value) {
        return new AttributeId(value);
    }

    public static AttributeId generate() {
        return new AttributeId(null); // DB에서 생성
    }
}
```

#### AttributeCode
```java
public record AttributeCode(String value) {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{1,99}$");

    public AttributeCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AttributeCode cannot be blank");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "AttributeCode must start with uppercase letter, contain only A-Z, 0-9, _, length 2-100"
            );
        }
    }

    public static AttributeCode of(String value) {
        return new AttributeCode(value);
    }
}
```

#### AttributeName
```java
public record AttributeName(String ko, String en) {
    public AttributeName {
        if (ko == null || ko.isBlank()) {
            throw new IllegalArgumentException("nameKo is required");
        }
        if (ko.length() > 255) {
            throw new IllegalArgumentException("nameKo exceeds max length 255");
        }
        if (en != null && en.length() > 255) {
            throw new IllegalArgumentException("nameEn exceeds max length 255");
        }
    }

    public static AttributeName of(String ko, String en) {
        return new AttributeName(ko, en);
    }

    public static AttributeName ofKo(String ko) {
        return new AttributeName(ko, null);
    }
}
```

#### ValueType (Enum)
```java
/**
 * 속성 값 타입
 *
 * 값 타입별 저장 및 검증 정책:
 * - ENUM: AttributeValue 테이블에서 선택 (필수)
 * - TEXT: 자유 문자열 입력
 * - NUMBER: 숫자 입력 (정수/실수)
 * - BOOLEAN: true/false
 * - JSON: 복합 구조 (스키마 검증 선택)
 */
public enum ValueType {
    ENUM,       // 열거형 - AttributeValue 필수
    TEXT,       // 자유 문자열
    NUMBER,     // 숫자 (정수/실수)
    BOOLEAN,    // 참/거짓
    JSON;       // 복합 구조

    /**
     * AttributeValue 사용 여부
     */
    public boolean requiresValues() {
        return this == ENUM;
    }

    /**
     * 추천 값 목록 사용 가능 여부 (ENUM 외 타입)
     */
    public boolean supportsSuggestedValues() {
        return this == TEXT || this == NUMBER;
    }
}
```

#### AppliesLevel (Enum)
```java
/**
 * 속성 적용 레벨
 *
 * - PRODUCT: 상품 레벨 (상품 공통 속성)
 * - SKU: SKU 레벨 (옵션마다 다른 속성)
 * - BOTH: 둘 다 허용 (유연성 제공, 권장하지 않음)
 */
public enum AppliesLevel {
    PRODUCT,    // 상품 레벨
    SKU,        // SKU/옵션 레벨
    BOTH;       // 둘 다

    /**
     * 호환성 검증
     */
    public boolean isCompatibleWith(AppliesLevel target) {
        if (this == BOTH) {
            return true;
        }
        return this == target;
    }
}
```

#### AttributeValueId
```java
public record AttributeValueId(Long value) {
    public AttributeValueId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("AttributeValueId must be positive");
        }
    }

    public static AttributeValueId of(Long value) {
        return new AttributeValueId(value);
    }

    public static AttributeValueId generate() {
        return new AttributeValueId(null);
    }
}
```

#### AttributeValueCode
```java
public record AttributeValueCode(String value) {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9_]{1,100}$");

    public AttributeValueCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AttributeValueCode cannot be blank");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "AttributeValueCode must contain only A-Z, 0-9, _, length 1-100"
            );
        }
    }

    public static AttributeValueCode of(String value) {
        return new AttributeValueCode(value);
    }
}
```

#### AttributeValueName
```java
public record AttributeValueName(String ko, String en) {
    public AttributeValueName {
        // ko, en 중 최소 하나는 필수
        if ((ko == null || ko.isBlank()) && (en == null || en.isBlank())) {
            throw new IllegalArgumentException("At least one of valueKo or valueEn is required");
        }
        if (ko != null && ko.length() > 255) {
            throw new IllegalArgumentException("valueKo exceeds max length 255");
        }
        if (en != null && en.length() > 255) {
            throw new IllegalArgumentException("valueEn exceeds max length 255");
        }
    }

    public static AttributeValueName of(String ko, String en) {
        return new AttributeValueName(ko, en);
    }

    public static AttributeValueName ofKo(String ko) {
        return new AttributeValueName(ko, null);
    }
}
```

#### ValueMeta
```java
/**
 * 속성 값의 메타 정보
 *
 * 예시:
 * - 색상: { "hex": "#FFFFFF" }
 * - 사이즈: { "mm": 260 }
 * - 범위: { "min": 0, "max": 100 }
 */
public record ValueMeta(
        String hex,        // 색상 코드
        Integer mm,        // 밀리미터 (사이즈)
        Integer min,       // 최소값
        Integer max,       // 최대값
        String label       // 추가 라벨
) {
    public static ValueMeta empty() {
        return new ValueMeta(null, null, null, null, null);
    }

    public static ValueMeta ofHex(String hex) {
        return new ValueMeta(hex, null, null, null, null);
    }

    public static ValueMeta ofMm(Integer mm) {
        return new ValueMeta(null, mm, null, null, null);
    }

    public static ValueMeta ofRange(Integer min, Integer max) {
        return new ValueMeta(null, null, min, max, null);
    }

    public String toJson() {
        // 실제 구현에서는 Jackson ObjectMapper 사용
        StringBuilder sb = new StringBuilder("{");
        List<String> parts = new ArrayList<>();
        if (hex != null) parts.add("\"hex\":\"" + hex + "\"");
        if (mm != null) parts.add("\"mm\":" + mm);
        if (min != null) parts.add("\"min\":" + min);
        if (max != null) parts.add("\"max\":" + max);
        if (label != null) parts.add("\"label\":\"" + label + "\"");
        sb.append(String.join(",", parts));
        sb.append("}");
        return parts.isEmpty() ? null : sb.toString();
    }
}
```

#### AttributeConstraint
```java
/**
 * 카테고리별 속성 값 개수 제약
 *
 * 예: 색상 - 최소 1개, 최대 10개
 */
public record AttributeConstraint(
        Integer minCount,
        Integer maxCount
) {
    public AttributeConstraint {
        if (minCount != null && minCount < 0) {
            throw new IllegalArgumentException("minCount cannot be negative");
        }
        if (maxCount != null && maxCount < 0) {
            throw new IllegalArgumentException("maxCount cannot be negative");
        }
        if (minCount != null && maxCount != null && minCount > maxCount) {
            throw new IllegalArgumentException("minCount cannot be greater than maxCount");
        }
    }

    public static AttributeConstraint of(Integer minCount, Integer maxCount) {
        return new AttributeConstraint(minCount, maxCount);
    }

    public static AttributeConstraint required() {
        return new AttributeConstraint(1, null);
    }

    public static AttributeConstraint optional() {
        return new AttributeConstraint(null, null);
    }

    public boolean isValidCount(int count) {
        if (minCount != null && count < minCount) {
            return false;
        }
        if (maxCount != null && count > maxCount) {
            return false;
        }
        return true;
    }
}
```

#### GroupCode
```java
/**
 * UI 그룹 코드
 *
 * 상품 등록 화면에서 속성들을 그룹으로 묶어서 표시
 */
public record GroupCode(String value) {
    public static final String BASIC = "BASIC";
    public static final String DETAIL = "DETAIL";
    public static final String SIZE_INFO = "SIZE_INFO";
    public static final String MATERIAL = "MATERIAL";

    public GroupCode {
        if (value != null && value.length() > 100) {
            throw new IllegalArgumentException("GroupCode exceeds max length 100");
        }
    }

    public static GroupCode of(String value) {
        return value == null ? null : new GroupCode(value);
    }

    public static GroupCode basic() {
        return new GroupCode(BASIC);
    }

    public static GroupCode detail() {
        return new GroupCode(DETAIL);
    }

    public static GroupCode sizeInfo() {
        return new GroupCode(SIZE_INFO);
    }
}
```

### 2.7 Domain Events

```java
public record AttributeCreatedEvent(
        Long attributeId,
        String code,
        ValueType valueType,
        AppliesLevel appliesLevel,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record AttributeValueAddedEvent(
        Long attributeId,
        Long valueId,
        String valueCode,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record CategoryAttributeLinkedEvent(
        Long categoryAttributeId,
        Long categoryId,
        Long attributeId,
        AppliesLevel appliesLevel,
        boolean isRequired,
        LocalDateTime occurredAt
) implements DomainEvent {}
```

### 2.8 Domain Exceptions

```java
public class AttributeNotFoundException extends DomainException {
    public AttributeNotFoundException(Long attributeId) {
        super(AttributeErrorCode.ATTRIBUTE_NOT_FOUND,
              "Attribute not found: " + attributeId);
    }

    public AttributeNotFoundException(String code) {
        super(AttributeErrorCode.ATTRIBUTE_NOT_FOUND,
              "Attribute not found by code: " + code);
    }
}

public class AttributeCodeDuplicateException extends DomainException {
    public AttributeCodeDuplicateException(String code) {
        super(AttributeErrorCode.ATTRIBUTE_CODE_DUPLICATE,
              "Attribute code already exists: " + code);
    }
}

public class AttributeValueNotFoundException extends DomainException {
    public AttributeValueNotFoundException(AttributeId attributeId, Long valueId) {
        super(AttributeErrorCode.ATTRIBUTE_VALUE_NOT_FOUND,
              "Value " + valueId + " not found in attribute " + attributeId.value());
    }
}

public class AttributeValueCodeDuplicateException extends DomainException {
    public AttributeValueCodeDuplicateException(AttributeId attributeId, AttributeValueCode code) {
        super(AttributeErrorCode.ATTRIBUTE_VALUE_CODE_DUPLICATE,
              "Value code already exists: " + code.value() + " in attribute " + attributeId.value());
    }
}

public class CategoryAttributeNotFoundException extends DomainException {
    public CategoryAttributeNotFoundException(Long id) {
        super(AttributeErrorCode.CATEGORY_ATTRIBUTE_NOT_FOUND,
              "CategoryAttribute not found: " + id);
    }
}

public class CategoryAttributeDuplicateException extends DomainException {
    public CategoryAttributeDuplicateException(Long categoryId, Long attributeId, AppliesLevel level) {
        super(AttributeErrorCode.CATEGORY_ATTRIBUTE_DUPLICATE,
              "CategoryAttribute already exists: category=" + categoryId +
              ", attribute=" + attributeId + ", level=" + level);
    }
}

public class InvalidAppliesLevelException extends DomainException {
    public InvalidAppliesLevelException(
            AttributeCode attributeCode,
            AppliesLevel attributeLevel,
            AppliesLevel requestedLevel
    ) {
        super(AttributeErrorCode.INVALID_APPLIES_LEVEL,
              "Attribute " + attributeCode.value() + " (level=" + attributeLevel +
              ") is not compatible with requested level " + requestedLevel);
    }
}
```

---

## 3. Application Layer 설계

### 3.1 패키지 구조
```
application/
└── catalog/
    └── attribute/
        ├── assembler/
        │   ├── AttributeAssembler.java
        │   └── CategoryAttributeAssembler.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateAttributeCommand.java
        │   │   ├── UpdateAttributeCommand.java
        │   │   ├── AddAttributeValueCommand.java
        │   │   ├── UpdateAttributeValueCommand.java
        │   │   ├── CreateCategoryAttributeCommand.java
        │   │   └── UpdateCategoryAttributeCommand.java
        │   ├── query/
        │   │   ├── AttributeSearchQuery.java
        │   │   └── CategoryDefinitionQuery.java
        │   └── response/
        │       ├── AttributeResponse.java
        │       ├── AttributeDetailResponse.java
        │       ├── AttributeValueResponse.java
        │       ├── CategoryAttributeResponse.java
        │       └── CategoryDefinitionResponse.java
        ├── port/
        │   ├── in/
        │   │   ├── command/
        │   │   │   ├── CreateAttributeUseCase.java
        │   │   │   ├── UpdateAttributeUseCase.java
        │   │   │   ├── ManageAttributeValueUseCase.java
        │   │   │   └── ManageCategoryAttributeUseCase.java
        │   │   └── query/
        │   │       ├── GetAttributeUseCase.java
        │   │       ├── SearchAttributeUseCase.java
        │   │       └── GetCategoryDefinitionUseCase.java
        │   └── out/
        │       ├── command/
        │       │   ├── AttributePersistencePort.java
        │       │   └── CategoryAttributePersistencePort.java
        │       └── query/
        │           ├── AttributeQueryPort.java
        │           └── CategoryAttributeQueryPort.java
        └── service/
            ├── command/
            │   ├── CreateAttributeService.java
            │   ├── UpdateAttributeService.java
            │   ├── ManageAttributeValueService.java
            │   └── ManageCategoryAttributeService.java
            └── query/
                ├── GetAttributeService.java
                ├── SearchAttributeService.java
                └── GetCategoryDefinitionService.java
```

### 3.2 Port In - Command

```java
public interface CreateAttributeUseCase {
    AttributeResponse create(CreateAttributeCommand command);
}

public interface UpdateAttributeUseCase {
    AttributeResponse update(UpdateAttributeCommand command);
    void activate(Long attributeId);
    void deactivate(Long attributeId);
}

public interface ManageAttributeValueUseCase {
    AttributeValueResponse addValue(AddAttributeValueCommand command);
    AttributeValueResponse updateValue(UpdateAttributeValueCommand command);
    void activateValue(Long attributeId, Long valueId);
    void deactivateValue(Long attributeId, Long valueId);
}

public interface ManageCategoryAttributeUseCase {
    CategoryAttributeResponse create(CreateCategoryAttributeCommand command);
    CategoryAttributeResponse update(UpdateCategoryAttributeCommand command);

    /**
     * ⚠️ 피드백 반영: appliesLevel 변경 시 Attribute와의 호환성 검증
     * - CategoryAttribute의 appliesLevel 단독 변경 시에도 검증 필요
     */
    CategoryAttributeResponse changeAppliesLevel(Long categoryAttributeId, AppliesLevel newLevel);

    void activate(Long categoryAttributeId);
    void deactivate(Long categoryAttributeId);
}
```

### 3.3 Port In - Query

```java
public interface GetAttributeUseCase {
    AttributeDetailResponse getById(Long attributeId);
    AttributeDetailResponse getByCode(String code);
}

public interface SearchAttributeUseCase {
    Page<AttributeResponse> search(AttributeSearchQuery query, Pageable pageable);

    /**
     * ⚠️ 피드백 반영: findByValueType(null) 모호성 해결
     * - 전체 조회와 타입별 조회를 명시적으로 분리
     */
    List<AttributeResponse> findAllActive();                      // 전체 활성 속성 조회
    List<AttributeResponse> findByValueType(ValueType valueType); // 타입별 조회 (null 불허)
}

public interface GetCategoryDefinitionUseCase {
    /**
     * 카테고리별 속성 정의 조회 (상품 등록용)
     */
    CategoryDefinitionResponse getDefinition(Long categoryId);

    /**
     * 카테고리의 속성 매핑 목록 조회 (관리용)
     */
    List<CategoryAttributeResponse> getAttributesByCategoryId(Long categoryId);
}
```

### 3.4 Port Out - Command

```java
public interface AttributePersistencePort {
    void persist(Attribute attribute);
    boolean existsByCode(String code);
}

public interface CategoryAttributePersistencePort {
    void persist(CategoryAttribute categoryAttribute);
    void delete(Long categoryAttributeId);
    boolean exists(Long categoryId, Long attributeId, AppliesLevel level);
}
```

### 3.5 Port Out - Query

```java
public interface AttributeQueryPort {
    Optional<Attribute> findById(Long attributeId);
    Optional<Attribute> findByCode(String code);
    Page<Attribute> search(AttributeSearchQuery query, Pageable pageable);

    /**
     * ⚠️ 피드백 반영: 전체 조회와 타입별 조회 분리
     */
    List<Attribute> findAllActive();                          // 전체 활성 속성 조회
    List<Attribute> findByValueType(ValueType valueType);     // 타입별 조회

    List<Attribute> findByIds(List<Long> attributeIds);
}

public interface CategoryAttributeQueryPort {
    Optional<CategoryAttribute> findById(Long categoryAttributeId);
    List<CategoryAttribute> findByCategoryId(Long categoryId);
    List<CategoryAttributeProjection> findDefinitionByCategoryId(Long categoryId);
}

// 카테고리 정의 조회용 Projection
public record CategoryAttributeProjection(
        Long categoryAttributeId,
        Long attributeId,
        String attributeCode,
        String attributeNameKo,
        String attributeNameEn,
        String valueType,
        String appliesLevel,
        boolean isRequired,
        Integer minCount,
        Integer maxCount,
        String groupCode,
        int sortOrder
) {}
```

### 3.6 Command DTO

```java
public record CreateAttributeCommand(
        String code,
        String nameKo,
        String nameEn,
        ValueType valueType,
        AppliesLevel appliesLevel,
        String description
) {}

public record UpdateAttributeCommand(
        Long attributeId,
        String nameKo,
        String nameEn,
        AppliesLevel appliesLevel,
        String description
) {}

public record AddAttributeValueCommand(
        Long attributeId,
        String code,
        String valueKo,
        String valueEn,
        int sortOrder,
        String hex,
        Integer mm,
        Integer min,
        Integer max,
        String label
) {}

public record UpdateAttributeValueCommand(
        Long attributeId,
        Long valueId,
        String valueKo,
        String valueEn,
        int sortOrder,
        String hex,
        Integer mm,
        Integer min,
        Integer max,
        String label
) {}

public record CreateCategoryAttributeCommand(
        Long categoryId,
        Long attributeId,
        AppliesLevel appliesLevel,
        boolean isRequired,
        Integer minCount,
        Integer maxCount,
        String groupCode,
        int sortOrder
) {}

public record UpdateCategoryAttributeCommand(
        Long categoryAttributeId,
        boolean isRequired,
        Integer minCount,
        Integer maxCount,
        String groupCode,
        int sortOrder
) {}
```

### 3.7 Response DTO

```java
public record AttributeResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        ValueType valueType,
        AppliesLevel appliesLevel,
        boolean isActive,
        int valueCount
) {}

public record AttributeDetailResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        ValueType valueType,
        AppliesLevel appliesLevel,
        boolean isActive,
        String description,
        List<AttributeValueResponse> values
) {}

public record AttributeValueResponse(
        Long id,
        Long attributeId,
        String code,
        String valueKo,
        String valueEn,
        int sortOrder,
        String metaJson,
        boolean isActive
) {}

public record CategoryAttributeResponse(
        Long id,
        Long categoryId,
        Long attributeId,
        String attributeCode,
        String attributeNameKo,
        ValueType valueType,
        AppliesLevel appliesLevel,
        boolean isRequired,
        Integer minCount,
        Integer maxCount,
        String groupCode,
        int sortOrder,
        boolean isActive
) {}

public record CategoryDefinitionResponse(
        CategoryInfo category,
        List<AttributeDefinition> attributes
) {
    public record CategoryInfo(
            Long id,
            String code,
            String nameKo
    ) {}

    public record AttributeDefinition(
            Long attributeId,
            String attributeCode,
            String nameKo,
            String nameEn,
            ValueType valueType,
            AppliesLevel appliesLevel,
            boolean isRequired,
            Integer minCount,
            Integer maxCount,
            String groupCode,
            int sortOrder,
            List<AttributeValueResponse> values  // ENUM 타입인 경우
    ) {}
}
```

### 3.8 Service 구현 예시

#### CreateAttributeService
```java
@Service
@RequiredArgsConstructor
public class CreateAttributeService implements CreateAttributeUseCase {

    private final AttributePersistencePort persistencePort;
    private final AttributeAssembler assembler;

    @Override
    @Transactional
    public AttributeResponse create(CreateAttributeCommand command) {
        // 1. 유니크 제약 검증
        if (persistencePort.existsByCode(command.code())) {
            throw new AttributeCodeDuplicateException(command.code());
        }

        // 2. 속성 생성
        Attribute attribute = Attribute.create(
                AttributeCode.of(command.code()),
                AttributeName.of(command.nameKo(), command.nameEn()),
                command.valueType(),
                command.appliesLevel(),
                command.description()
        );

        // 3. 저장
        persistencePort.persist(attribute);

        return assembler.toResponse(attribute);
    }
}
```

#### ManageCategoryAttributeService
```java
@Service
@RequiredArgsConstructor
public class ManageCategoryAttributeService implements ManageCategoryAttributeUseCase {

    private final CategoryAttributePersistencePort persistencePort;
    private final AttributeQueryPort attributeQueryPort;
    private final CategoryAttributeAssembler assembler;

    @Override
    @Transactional
    public CategoryAttributeResponse create(CreateCategoryAttributeCommand command) {
        // 1. Attribute 조회 및 appliesLevel 호환성 검증
        Attribute attribute = attributeQueryPort.findById(command.attributeId())
                .orElseThrow(() -> new AttributeNotFoundException(command.attributeId()));

        attribute.validateAppliesLevel(command.appliesLevel());

        // 2. 중복 검증
        if (persistencePort.exists(command.categoryId(), command.attributeId(), command.appliesLevel())) {
            throw new CategoryAttributeDuplicateException(
                    command.categoryId(), command.attributeId(), command.appliesLevel()
            );
        }

        // 3. CategoryAttribute 생성
        CategoryAttribute categoryAttribute = CategoryAttribute.create(
                command.categoryId(),
                command.attributeId(),
                command.appliesLevel(),
                command.isRequired(),
                AttributeConstraint.of(command.minCount(), command.maxCount()),
                GroupCode.of(command.groupCode()),
                command.sortOrder()
        );

        // 4. 저장
        persistencePort.persist(categoryAttribute);

        return assembler.toResponse(categoryAttribute, attribute);
    }

    @Override
    @Transactional
    public CategoryAttributeResponse update(UpdateCategoryAttributeCommand command) {
        // 구현...
    }

    /**
     * ⚠️ 피드백 반영: appliesLevel 변경 시 Attribute와의 호환성 검증
     *
     * 플로우:
     * 1. CategoryAttribute 조회
     * 2. Attribute 조회
     * 3. Attribute.validateAppliesLevel(newLevel) 호출
     * 4. CategoryAttribute.changeAppliesLevel(newLevel) 호출
     * 5. 저장
     */
    @Override
    @Transactional
    public CategoryAttributeResponse changeAppliesLevel(Long categoryAttributeId, AppliesLevel newLevel) {
        // 1. CategoryAttribute 조회
        CategoryAttribute categoryAttribute = categoryAttributeQueryPort.findById(categoryAttributeId)
                .orElseThrow(() -> new CategoryAttributeNotFoundException(categoryAttributeId));

        // 2. Attribute 조회
        Attribute attribute = attributeQueryPort.findById(categoryAttribute.attributeId())
                .orElseThrow(() -> new AttributeNotFoundException(categoryAttribute.attributeId()));

        // 3. appliesLevel 호환성 검증 (핵심!)
        attribute.validateAppliesLevel(newLevel);

        // 4. appliesLevel 변경
        categoryAttribute.changeAppliesLevel(newLevel);

        // 5. 저장
        persistencePort.persist(categoryAttribute);

        return assembler.toResponse(categoryAttribute, attribute);
    }

    @Override
    @Transactional
    public void activate(Long categoryAttributeId) {
        // 구현...
    }

    @Override
    @Transactional
    public void deactivate(Long categoryAttributeId) {
        // 구현...
    }
}
```

#### GetCategoryDefinitionService
```java
@Service
@RequiredArgsConstructor
public class GetCategoryDefinitionService implements GetCategoryDefinitionUseCase {

    private final CategoryAttributeQueryPort categoryAttributeQueryPort;
    private final AttributeQueryPort attributeQueryPort;
    private final CategoryQueryPort categoryQueryPort;  // Category 모듈 참조
    private final CategoryAttributeAssembler assembler;

    @Override
    public CategoryDefinitionResponse getDefinition(Long categoryId) {
        // 1. 카테고리 정보 조회
        Category category = categoryQueryPort.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        // 2. 카테고리별 속성 정의 조회
        List<CategoryAttributeProjection> projections =
                categoryAttributeQueryPort.findDefinitionByCategoryId(categoryId);

        // 3. ENUM 타입 속성의 값 목록 조회
        List<Long> enumAttributeIds = projections.stream()
                .filter(p -> "ENUM".equals(p.valueType()))
                .map(CategoryAttributeProjection::attributeId)
                .toList();

        Map<Long, List<AttributeValue>> valuesMap = new HashMap<>();
        if (!enumAttributeIds.isEmpty()) {
            List<Attribute> enumAttributes = attributeQueryPort.findByIds(enumAttributeIds);
            for (Attribute attr : enumAttributes) {
                valuesMap.put(attr.id(), attr.activeValues());
            }
        }

        // 4. 응답 조립
        return assembler.toDefinitionResponse(category, projections, valuesMap);
    }

    @Override
    public List<CategoryAttributeResponse> getAttributesByCategoryId(Long categoryId) {
        // 구현...
    }
}
```

---

## 4. Persistence Layer 설계

### 4.1 패키지 구조
```
persistence-mysql/
└── catalog/
    └── attribute/
        ├── adapter/
        │   ├── AttributeCommandAdapter.java
        │   ├── AttributeQueryAdapter.java
        │   ├── CategoryAttributeCommandAdapter.java
        │   └── CategoryAttributeQueryAdapter.java
        ├── entity/
        │   ├── AttributeJpaEntity.java
        │   ├── AttributeValueJpaEntity.java
        │   └── CategoryAttributeJpaEntity.java
        ├── mapper/
        │   ├── AttributeJpaEntityMapper.java
        │   └── CategoryAttributeJpaEntityMapper.java
        └── repository/
            ├── AttributeJpaRepository.java
            ├── AttributeValueJpaRepository.java
            ├── CategoryAttributeJpaRepository.java
            └── AttributeQueryDslRepository.java
```

### 4.2 DB 스키마

```sql
-- 속성 정의 테이블
CREATE TABLE attribute (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(100) NOT NULL,
    name_ko         VARCHAR(255) NOT NULL,
    name_en         VARCHAR(255) NULL,
    value_type      VARCHAR(20) NOT NULL,     -- ENUM, TEXT, NUMBER, BOOLEAN, JSON
    applies_level   VARCHAR(20) NOT NULL,     -- PRODUCT, SKU, BOTH
    description     VARCHAR(500) NULL,
    is_active       TINYINT(1) NOT NULL DEFAULT 1,

    -- 동시성 제어 (낙관적 락)
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0,

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_attribute_code (code),
    KEY idx_attribute_value_type (value_type),
    KEY idx_attribute_applies_level (applies_level),
    KEY idx_attribute_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 속성 값 테이블 (ENUM 타입용)
CREATE TABLE attribute_value (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    attribute_id    BIGINT UNSIGNED NOT NULL,
    code            VARCHAR(100) NOT NULL,    -- WHITE, M, MEN 등
    value_ko        VARCHAR(255) NULL,
    value_en        VARCHAR(255) NULL,
    sort_order      INT NOT NULL DEFAULT 0,
    meta_json       JSON NULL,                -- RGB, mm, 범위 등 추가 정보
    is_active       TINYINT(1) NOT NULL DEFAULT 1,

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_attribute_value (attribute_id, code),
    KEY idx_attribute_value_attr (attribute_id),
    KEY idx_attribute_value_active (is_active),
    KEY idx_attribute_value_sort (attribute_id, sort_order),

    CONSTRAINT fk_attribute_value_attribute
        FOREIGN KEY (attribute_id) REFERENCES attribute(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 카테고리별 속성 매핑 테이블
CREATE TABLE category_attribute (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id     BIGINT UNSIGNED NOT NULL,
    attribute_id    BIGINT UNSIGNED NOT NULL,
    applies_level   VARCHAR(20) NOT NULL,     -- PRODUCT, SKU, BOTH
    is_required     TINYINT(1) NOT NULL DEFAULT 0,
    min_count       INT NULL,                 -- 최소 값 개수
    max_count       INT NULL,                 -- 최대 값 개수
    group_code      VARCHAR(100) NULL,        -- UI 그룹 (BASIC, DETAIL, SIZE_INFO 등)
    sort_order      INT NOT NULL DEFAULT 0,
    is_active       TINYINT(1) NOT NULL DEFAULT 1,

    -- 동시성 제어 (낙관적 락)
    version         BIGINT UNSIGNED NOT NULL DEFAULT 0,

    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP,

    -- category_id + attribute_id + applies_level 유니크
    UNIQUE KEY uk_category_attribute (category_id, attribute_id, applies_level),
    KEY idx_category_attribute_cat (category_id),
    KEY idx_category_attribute_attr (attribute_id),
    KEY idx_category_attribute_group (category_id, group_code),
    KEY idx_category_attribute_sort (category_id, sort_order),

    CONSTRAINT fk_category_attribute_category
        FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT fk_category_attribute_attribute
        FOREIGN KEY (attribute_id) REFERENCES attribute(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 4.3 JPA Entity

#### AttributeJpaEntity
```java
@Entity
@Table(name = "attribute")
public class AttributeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "name_ko", nullable = false)
    private String nameKo;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "value_type", nullable = false, length = 20)
    private String valueType;

    @Column(name = "applies_level", nullable = false, length = 20)
    private String appliesLevel;

    @Column(length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA 연관관계 없음 (Long FK 전략)

    protected AttributeJpaEntity() {}

    // Getter only
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getNameKo() { return nameKo; }
    public String getNameEn() { return nameEn; }
    public String getValueType() { return valueType; }
    public String getAppliesLevel() { return appliesLevel; }
    public String getDescription() { return description; }
    public Boolean getIsActive() { return isActive; }
    public Long getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // 정적 팩토리
    public static AttributeJpaEntity from(Attribute domain) {
        AttributeJpaEntity entity = new AttributeJpaEntity();
        entity.id = domain.id();
        entity.code = domain.code();
        entity.nameKo = domain.nameKo();
        entity.nameEn = domain.nameEn();
        entity.valueType = domain.valueType().name();
        entity.appliesLevel = domain.appliesLevel().name();
        entity.description = domain.description();
        entity.isActive = domain.isActive();
        return entity;
    }
}
```

#### AttributeValueJpaEntity
```java
@Entity
@Table(name = "attribute_value")
public class AttributeValueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;           // Long FK

    @Column(nullable = false, length = 100)
    private String code;

    @Column(name = "value_ko")
    private String valueKo;

    @Column(name = "value_en")
    private String valueEn;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "meta_json", columnDefinition = "JSON")
    private String metaJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AttributeValueJpaEntity() {}

    // Getter only
    public Long getId() { return id; }
    public Long getAttributeId() { return attributeId; }
    public String getCode() { return code; }
    public String getValueKo() { return valueKo; }
    public String getValueEn() { return valueEn; }
    public Integer getSortOrder() { return sortOrder; }
    public String getMetaJson() { return metaJson; }
    public Boolean getIsActive() { return isActive; }

    /**
     * ⚠️ attributeId를 외부에서 주입받음
     * - AttributeValue 도메인 객체는 attributeId를 가지지 않음
     * - Aggregate Root(Attribute)의 id를 사용
     */
    public static AttributeValueJpaEntity from(Long attributeId, AttributeValue value) {
        AttributeValueJpaEntity entity = new AttributeValueJpaEntity();
        entity.id = value.id();
        entity.attributeId = attributeId;  // 외부에서 주입
        entity.code = value.code();
        entity.valueKo = value.nameKo();
        entity.valueEn = value.nameEn();
        entity.sortOrder = value.sortOrder();
        entity.metaJson = value.metaJson();
        entity.isActive = value.isActive();
        return entity;
    }
}
```

#### CategoryAttributeJpaEntity
```java
@Entity
@Table(name = "category_attribute")
public class CategoryAttributeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;            // Long FK

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;           // Long FK

    @Column(name = "applies_level", nullable = false, length = 20)
    private String appliesLevel;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "min_count")
    private Integer minCount;

    @Column(name = "max_count")
    private Integer maxCount;

    @Column(name = "group_code", length = 100)
    private String groupCode;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CategoryAttributeJpaEntity() {}

    // Getter only
    public Long getId() { return id; }
    public Long getCategoryId() { return categoryId; }
    public Long getAttributeId() { return attributeId; }
    public String getAppliesLevel() { return appliesLevel; }
    public Boolean getIsRequired() { return isRequired; }
    public Integer getMinCount() { return minCount; }
    public Integer getMaxCount() { return maxCount; }
    public String getGroupCode() { return groupCode; }
    public Integer getSortOrder() { return sortOrder; }
    public Boolean getIsActive() { return isActive; }

    public static CategoryAttributeJpaEntity from(CategoryAttribute domain) {
        CategoryAttributeJpaEntity entity = new CategoryAttributeJpaEntity();
        entity.id = domain.id();
        entity.categoryId = domain.categoryId();
        entity.attributeId = domain.attributeId();
        entity.appliesLevel = domain.appliesLevel().name();
        entity.isRequired = domain.isRequired();
        entity.minCount = domain.minCount();
        entity.maxCount = domain.maxCount();
        entity.groupCode = domain.groupCode();
        entity.sortOrder = domain.sortOrder();
        entity.isActive = domain.isActive();
        return entity;
    }
}
```

### 4.4 Command Adapter

```java
@Component
@RequiredArgsConstructor
public class AttributeCommandAdapter implements AttributePersistencePort {

    private final AttributeJpaRepository attributeJpaRepository;
    private final AttributeValueJpaRepository valueJpaRepository;
    private final AttributeJpaEntityMapper mapper;

    /**
     * Attribute Persist 전략: 완전 재작성 (Full Rewrite) + Soft Delete
     *
     * ⚠️ 피드백 반영: Upsert 전략을 "완전 재작성"으로 명확히 정의
     *
     * 원칙:
     * - 물리 삭제(DELETE) 없음
     * - Value 변경 시: 기존 values 전체 soft delete → 현재 values 새로 insert
     * - Long FK 컨벤션 준수 (JPA Cascade 미사용)
     * - AttributeValue는 attributeId를 가지지 않으므로 Mapper에서 주입
     *
     * 완전 재작성 전략 선택 이유:
     * - 구현이 단순하고 버그 가능성 낮음
     * - AttributeValueId가 외부에서 참조될 일이 거의 없음 (attributeId + code로 조회)
     * - Diff 기반 업데이트 대비 복잡도 낮음
     */
    @Override
    public void persist(Attribute attribute) {
        // 1. Attribute 저장 (신규 생성이면 ID 자동 생성)
        AttributeJpaEntity attributeEntity = mapper.toEntity(attribute);
        attributeJpaRepository.save(attributeEntity);

        Long generatedId = attributeEntity.getId();

        // 2. Values 저장 (ENUM 타입인 경우만)
        if (attribute.isEnumType()) {
            // 2-1. 기존 values 전체 soft delete (완전 재작성 전략)
            valueJpaRepository.deactivateByAttributeId(generatedId);

            // 2-2. 현재 values 새로 insert (attributeId 주입)
            if (!attribute.values().isEmpty()) {
                List<AttributeValueJpaEntity> valueEntities = attribute.values().stream()
                        .filter(AttributeValue::isActive)  // 활성 상태인 것만
                        .map(v -> AttributeValueJpaEntity.from(generatedId, v))  // attributeId 주입
                        .toList();
                valueJpaRepository.saveAll(valueEntities);
            }
        }
    }

    @Override
    public boolean existsByCode(String code) {
        return attributeJpaRepository.existsByCode(code);
    }
}

/**
 * AttributeValueJpaRepository에 추가할 메서드
 */
public interface AttributeValueJpaRepository extends JpaRepository<AttributeValueJpaEntity, Long> {

    /**
     * 완전 재작성 전략: 기존 values 전체 soft delete
     */
    @Modifying
    @Query("UPDATE AttributeValueJpaEntity v SET v.isActive = false WHERE v.attributeId = :attributeId")
    void deactivateByAttributeId(@Param("attributeId") Long attributeId);
}
```

### 4.5 QueryDSL Repository

```java
@Repository
@RequiredArgsConstructor
public class AttributeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private static final QAttributeJpaEntity attribute = QAttributeJpaEntity.attributeJpaEntity;
    private static final QAttributeValueJpaEntity value = QAttributeValueJpaEntity.attributeValueJpaEntity;
    private static final QCategoryAttributeJpaEntity categoryAttribute = QCategoryAttributeJpaEntity.categoryAttributeJpaEntity;

    public Page<AttributeJpaEntity> search(AttributeSearchQuery query, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (query.keyword() != null && !query.keyword().isBlank()) {
            builder.and(
                    attribute.code.containsIgnoreCase(query.keyword())
                            .or(attribute.nameKo.containsIgnoreCase(query.keyword()))
                            .or(attribute.nameEn.containsIgnoreCase(query.keyword()))
            );
        }
        if (query.valueType() != null) {
            builder.and(attribute.valueType.eq(query.valueType().name()));
        }
        if (query.appliesLevel() != null) {
            builder.and(attribute.appliesLevel.eq(query.appliesLevel().name()));
        }
        if (query.isActive() != null) {
            builder.and(attribute.isActive.eq(query.isActive()));
        }

        List<AttributeJpaEntity> content = queryFactory
                .selectFrom(attribute)
                .where(builder)
                .orderBy(attribute.code.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(attribute.count())
                .from(attribute)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    public List<AttributeValueJpaEntity> findValuesByAttributeId(Long attributeId) {
        return queryFactory
                .selectFrom(value)
                .where(value.attributeId.eq(attributeId))
                .orderBy(value.sortOrder.asc())
                .fetch();
    }

    public List<AttributeValueJpaEntity> findActiveValuesByAttributeId(Long attributeId) {
        return queryFactory
                .selectFrom(value)
                .where(
                        value.attributeId.eq(attributeId),
                        value.isActive.eq(true)
                )
                .orderBy(value.sortOrder.asc())
                .fetch();
    }

    /**
     * 카테고리별 속성 정의 조회 (상품 등록용)
     */
    public List<CategoryAttributeProjection> findDefinitionByCategoryId(Long categoryId) {
        return queryFactory
                .select(Projections.constructor(CategoryAttributeProjection.class,
                        categoryAttribute.id,
                        attribute.id,
                        attribute.code,
                        attribute.nameKo,
                        attribute.nameEn,
                        attribute.valueType,
                        categoryAttribute.appliesLevel,
                        categoryAttribute.isRequired,
                        categoryAttribute.minCount,
                        categoryAttribute.maxCount,
                        categoryAttribute.groupCode,
                        categoryAttribute.sortOrder
                ))
                .from(categoryAttribute)
                .join(attribute).on(categoryAttribute.attributeId.eq(attribute.id))
                .where(
                        categoryAttribute.categoryId.eq(categoryId),
                        categoryAttribute.isActive.eq(true),
                        attribute.isActive.eq(true)
                )
                .orderBy(categoryAttribute.sortOrder.asc())
                .fetch();
    }
}
```

---

## 5. REST API Layer 설계

### 5.1 패키지 구조
```
adapter-in/rest-api/
└── catalog/
    └── attribute/
        ├── controller/
        │   ├── AttributeAdminController.java
        │   ├── AttributeValueAdminController.java
        │   ├── CategoryAttributeAdminController.java
        │   └── AttributePublicController.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateAttributeApiRequest.java
        │   │   ├── UpdateAttributeApiRequest.java
        │   │   ├── AddAttributeValueApiRequest.java
        │   │   ├── UpdateAttributeValueApiRequest.java
        │   │   ├── CreateCategoryAttributeApiRequest.java
        │   │   └── UpdateCategoryAttributeApiRequest.java
        │   ├── query/
        │   │   └── AttributeSearchApiRequest.java
        │   └── response/
        │       ├── AttributeApiResponse.java
        │       ├── AttributeDetailApiResponse.java
        │       ├── AttributeValueApiResponse.java
        │       ├── CategoryAttributeApiResponse.java
        │       └── CategoryDefinitionApiResponse.java
        ├── mapper/
        │   └── AttributeApiMapper.java
        └── error/
            └── AttributeApiErrorMapper.java
```

### 5.2 API 역할 구분

> **Admin API**는 내부 운영용이며, 인증/인가에서 ROLE_ADMIN을 요구한다.
> 쓰기 기능 + 관리용 조회를 제공한다.
>
> **Public API**는 BFF/외부 서비스에서 참조하는 속성 정의 조회용이며, 쓰기 기능은 제공하지 않는다.
> 읽기 전용이며, `is_active=true`인 데이터만 노출한다.

### 5.3 API 명세

#### Admin API - Attribute 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/attributes` | 목록 조회 (페이징) |
| GET | `/api/v1/admin/catalog/attributes/search` | 검색 |
| GET | `/api/v1/admin/catalog/attributes/{id}` | 상세 조회 |
| POST | `/api/v1/admin/catalog/attributes` | 생성 |
| PATCH | `/api/v1/admin/catalog/attributes/{id}` | 수정 |
| PATCH | `/api/v1/admin/catalog/attributes/{id}/activation` | 활성/비활성 |

#### Admin API - AttributeValue 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/attributes/{attributeId}/values` | 값 목록 조회 |
| POST | `/api/v1/admin/catalog/attributes/{attributeId}/values` | 값 추가 |
| PATCH | `/api/v1/admin/catalog/attributes/values/{valueId}` | 값 수정 |
| PATCH | `/api/v1/admin/catalog/attributes/values/{valueId}/activation` | 값 활성/비활성 |

#### Admin API - CategoryAttribute 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/categories/{categoryId}/attributes` | 카테고리별 속성 목록 |
| POST | `/api/v1/admin/catalog/categories/{categoryId}/attributes` | 속성 매핑 추가 |
| PATCH | `/api/v1/admin/catalog/categories/attributes/{id}` | 매핑 수정 |
| PATCH | `/api/v1/admin/catalog/categories/attributes/{id}/activation` | 매핑 활성/비활성 |

#### Public API (조회 전용)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/catalog/attributes` | 속성 목록 조회 |
| GET | `/api/v1/catalog/attributes/{attributeId}` | 속성 상세 조회 |
| GET | `/api/v1/catalog/attributes/{attributeId}/values` | 속성 값 목록 조회 |
| GET | `/api/v1/catalog/categories/{categoryId}/definition` | 카테고리별 속성 정의 (핵심) |

### 5.4 Request DTO

```java
public record CreateAttributeApiRequest(
        @NotBlank(message = "코드는 필수입니다")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,99}$",
                 message = "코드는 대문자로 시작, A-Z/0-9/_ 조합, 2-100자")
        String code,

        @NotBlank(message = "한글 이름은 필수입니다")
        @Size(max = 255)
        String nameKo,

        @Size(max = 255)
        String nameEn,

        @NotNull(message = "값 타입은 필수입니다")
        ValueType valueType,

        @NotNull(message = "적용 레벨은 필수입니다")
        AppliesLevel appliesLevel,

        @Size(max = 500)
        String description
) {}

public record AddAttributeValueApiRequest(
        @NotBlank(message = "값 코드는 필수입니다")
        @Pattern(regexp = "^[A-Z0-9_]{1,100}$")
        String code,

        @Size(max = 255)
        String valueKo,

        @Size(max = 255)
        String valueEn,

        @Min(0)
        int sortOrder,

        // 메타 정보
        String hex,
        Integer mm,
        Integer min,
        Integer max,
        String label
) {}

public record CreateCategoryAttributeApiRequest(
        @NotNull(message = "속성 ID는 필수입니다")
        Long attributeId,

        @NotNull(message = "적용 레벨은 필수입니다")
        AppliesLevel appliesLevel,

        boolean isRequired,

        @Min(0)
        Integer minCount,

        @Min(0)
        Integer maxCount,

        @Size(max = 100)
        String groupCode,

        @Min(0)
        int sortOrder
) {}
```

### 5.5 Response DTO

```java
public record AttributeApiResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        String valueType,
        String appliesLevel,
        boolean isActive,
        int valueCount
) {}

public record AttributeDetailApiResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        String valueType,
        String appliesLevel,
        boolean isActive,
        String description,
        List<AttributeValueApiResponse> values
) {}

public record AttributeValueApiResponse(
        Long id,
        Long attributeId,
        String code,
        String valueKo,
        String valueEn,
        int sortOrder,
        Object meta,        // JSON 파싱된 메타 정보
        boolean isActive
) {}

public record CategoryAttributeApiResponse(
        Long id,
        Long categoryId,
        Long attributeId,
        String attributeCode,
        String attributeNameKo,
        String valueType,
        String appliesLevel,
        boolean isRequired,
        Integer minCount,
        Integer maxCount,
        String groupCode,
        int sortOrder,
        boolean isActive
) {}

public record CategoryDefinitionApiResponse(
        CategoryInfo category,
        List<AttributeDefinition> attributes
) {
    public record CategoryInfo(
            Long id,
            String code,
            String nameKo
    ) {}

    public record AttributeDefinition(
            Long attributeId,
            String attributeCode,
            String nameKo,
            String nameEn,
            String valueType,
            String appliesLevel,
            boolean isRequired,
            Integer minCount,
            Integer maxCount,
            String groupCode,
            int sortOrder,
            List<AttributeValueApiResponse> values
    ) {}
}
```

### 5.6 Controller 예시

#### AttributePublicController (핵심 - 카테고리 정의 조회)
```java
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class AttributePublicController {

    private final GetAttributeUseCase getAttributeUseCase;
    private final SearchAttributeUseCase searchAttributeUseCase;
    private final GetCategoryDefinitionUseCase getCategoryDefinitionUseCase;
    private final AttributeApiMapper mapper;

    /**
     * 카테고리별 속성 정의 조회 (핵심 API)
     *
     * 용도:
     * - 상품 등록/수정 화면에서 "어떤 필드를 보여줄지"
     * - n8n에서 "이 카테고리 상품이면 어떤 정보를 채워야 하는지"
     */
    @GetMapping("/categories/{categoryId}/definition")
    public ResponseEntity<ApiResponse<CategoryDefinitionApiResponse>> getCategoryDefinition(
            @PathVariable Long categoryId
    ) {
        CategoryDefinitionResponse response = getCategoryDefinitionUseCase.getDefinition(categoryId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    /**
     * ⚠️ 피드백 반영: findByValueType(null) 모호성 해결
     * - 명시적으로 findAllActive()와 findByValueType() 분리
     */
    @GetMapping("/attributes")
    public ResponseEntity<ApiResponse<List<AttributeApiResponse>>> getAttributes(
            @RequestParam(required = false) ValueType valueType
    ) {
        // is_active = true만 조회
        List<AttributeResponse> list = (valueType != null)
                ? searchAttributeUseCase.findByValueType(valueType)
                : searchAttributeUseCase.findAllActive();  // ✅ 명시적 전체 조회

        return ResponseEntity.ok(
                ApiResponse.success(list.stream().map(mapper::toApiResponse).toList())
        );
    }

    @GetMapping("/attributes/{attributeId}")
    public ResponseEntity<ApiResponse<AttributeDetailApiResponse>> getAttribute(
            @PathVariable Long attributeId
    ) {
        AttributeDetailResponse response = getAttributeUseCase.getById(attributeId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toDetailApiResponse(response)));
    }

    @GetMapping("/attributes/{attributeId}/values")
    public ResponseEntity<ApiResponse<List<AttributeValueApiResponse>>> getAttributeValues(
            @PathVariable Long attributeId
    ) {
        AttributeDetailResponse response = getAttributeUseCase.getById(attributeId);
        return ResponseEntity.ok(
                ApiResponse.success(response.values().stream()
                        .filter(AttributeValueResponse::isActive)
                        .map(mapper::toApiResponse)
                        .toList())
        );
    }
}
```

### 5.7 Error Mapper

```java
@Component
public class AttributeApiErrorMapper implements ApiErrorMapper {

    @Override
    public boolean supports(DomainException exception) {
        return exception instanceof AttributeNotFoundException
                || exception instanceof AttributeCodeDuplicateException
                || exception instanceof AttributeValueNotFoundException
                || exception instanceof AttributeValueCodeDuplicateException
                || exception instanceof CategoryAttributeNotFoundException
                || exception instanceof CategoryAttributeDuplicateException
                || exception instanceof InvalidAppliesLevelException;
    }

    @Override
    public ErrorInfo map(DomainException exception) {
        return switch (exception) {
            case AttributeNotFoundException e -> ErrorInfo.of(
                    "ATTRIBUTE_NOT_FOUND",
                    "속성을 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case AttributeCodeDuplicateException e -> ErrorInfo.of(
                    "ATTRIBUTE_CODE_DUPLICATE",
                    "이미 존재하는 속성 코드입니다",
                    HttpStatus.CONFLICT
            );
            case AttributeValueNotFoundException e -> ErrorInfo.of(
                    "ATTRIBUTE_VALUE_NOT_FOUND",
                    "속성 값을 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case AttributeValueCodeDuplicateException e -> ErrorInfo.of(
                    "ATTRIBUTE_VALUE_CODE_DUPLICATE",
                    "이미 존재하는 값 코드입니다",
                    HttpStatus.CONFLICT
            );
            case CategoryAttributeNotFoundException e -> ErrorInfo.of(
                    "CATEGORY_ATTRIBUTE_NOT_FOUND",
                    "카테고리 속성 매핑을 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case CategoryAttributeDuplicateException e -> ErrorInfo.of(
                    "CATEGORY_ATTRIBUTE_DUPLICATE",
                    "이미 존재하는 카테고리 속성 매핑입니다",
                    HttpStatus.CONFLICT
            );
            case InvalidAppliesLevelException e -> ErrorInfo.of(
                    "INVALID_APPLIES_LEVEL",
                    "호환되지 않는 적용 레벨입니다",
                    HttpStatus.BAD_REQUEST
            );
            default -> ErrorInfo.of(
                    "ATTRIBUTE_ERROR",
                    exception.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        };
    }
}
```

---

## 6. 도메인 규칙 정리

### 6.1 Attribute 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| code 유니크 | 시스템 전체 유니크 | Service + DB |
| valueType 불변 | 생성 후 변경 불가 | Domain |
| ENUM 타입이면 값 필수 | values 목록 있어야 함 | Domain |
| Soft Delete | is_active = false로 비활성화 | Persistence |

### 6.2 AttributeValue 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| attribute_id + code 유니크 | 같은 속성 내 코드 중복 불가 | Domain + DB |
| sort_order로 정렬 | 필터/옵션 UI 표시 순서 | Query |
| ENUM 타입만 사용 | TEXT, NUMBER 등은 선택적 | Domain |
| Soft Delete | is_active = false로 비활성화 | Persistence |

### 6.3 CategoryAttribute 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| category_id + attribute_id + applies_level 유니크 | 중복 매핑 방지 | Service + DB |
| appliesLevel 호환성 | Attribute.appliesLevel과 호환되어야 함 | Domain |
| is_required = true이면 필수 | 상품/옵션 등록 시 검증 | (v2에서 구현) |
| Soft Delete | is_active = false로 비활성화 | Persistence |

### 6.4 appliesLevel 호환성 규칙

| Attribute.appliesLevel | CategoryAttribute 허용 값 |
|------------------------|--------------------------|
| PRODUCT | PRODUCT만 가능 |
| SKU | SKU만 가능 |
| BOTH | PRODUCT, SKU, BOTH 모두 가능 |

### 6.5 valueType별 AttributeValue 사용 정책

| valueType | AttributeValue 생성 | 용도 | 예시 |
|-----------|-------------------|------|------|
| **ENUM** | 필수 | 선택 가능한 값 목록 | COLOR → WHITE, BLACK, NAVY |
| **TEXT** | 선택적 | 추천 입력 값 (자동완성) | 소재 상세 설명 |
| **NUMBER** | 선택적 | 범위 힌트 (min/max) | 굽 높이 (0~10cm) |
| **BOOLEAN** | 불필요 | true/false 고정 | 방수 여부 |
| **JSON** | 선택적 | 스키마 템플릿 | 사이즈 스펙 테이블 |

### 6.6 AttributeValue 정렬 정책

| 속성 타입 | 정렬 기준 | 예시 |
|----------|----------|------|
| COLOR | 색상 계열 (밝은색 → 어두운색) | WHITE → BEIGE → ... → NAVY → BLACK |
| SIZE (의류) | S → M → L → XL → XXL | 의류 사이즈 순서 |
| SIZE (신발) | 숫자 오름차순 | 220 → 230 → 240 → ... → 300 |
| GENDER | 남성 → 여성 → 유니섹스 | MEN → WOMEN → UNISEX |

> `sort_order` 필드로 관리하며, Admin API에서 순서 조정 가능

### 6.7 카테고리 상속 정책 (v1)

> v1에서는 **카테고리 상속 없음** - 각 카테고리별로 독립적으로 속성 매핑
>
> 후속 단계에서 상위 카테고리 속성 상속 기능 검토 예정

### 6.8 동시성 및 트랜잭션 정책

#### 낙관적 락 (Optimistic Locking)
- `attribute`, `category_attribute` 테이블에 `version` 컬럼
- 동시 수정 시 **409 Conflict** 반환

#### 트랜잭션 경계
- Attribute + AttributeValue는 하나의 Aggregate로 **단일 트랜잭션**에서 처리
- CategoryAttribute는 독립 Entity로 별도 트랜잭션 가능
- 외부 API 호출 금지 (`@Transactional` 내)

---

## 7. 구현 우선순위 (TDD 사이클)

### Phase 1: Domain Layer
1. VO 테스트 및 구현 (AttributeCode, ValueType, AppliesLevel, ...)
2. AttributeValue Entity 테스트 및 구현
3. Attribute Aggregate Root 테스트 및 구현
4. CategoryAttribute Entity 테스트 및 구현
5. Exception 테스트 및 구현

### Phase 2: Persistence Layer
6. JPA Entity 테스트 및 구현
7. JPA Repository 테스트 및 구현
8. QueryDSL Repository 테스트 및 구현
9. Mapper 테스트 및 구현
10. Adapter 테스트 및 구현

### Phase 3: Application Layer
11. Assembler 테스트 및 구현
12. Command Service 테스트 및 구현
13. Query Service 테스트 및 구현 (GetCategoryDefinition 핵심)

### Phase 4: REST API Layer
14. API Request/Response DTO
15. API Mapper 테스트 및 구현
16. Controller 테스트 및 구현
17. Error Mapper 구현

### Phase 5: Integration Test
18. E2E 테스트 (TestRestTemplate)

---

## 8. 향후 연계 (미리 상상)

### v2: 상품/옵션 검증 연동
```
product_attribute / seller_sku_attribute 테이블
→ CategoryAttribute 정의 기반 필수값 검증
→ ENUM 타입이면 AttributeValue 존재 여부 검증
```

### v3: 외부 속성 매핑
```
external_attribute_mapping
external_attribute_value_mapping
→ 외부몰 속성명/값 → 내부 Attribute/AttributeValue 매핑
→ n8n 파이프라인에서 자동 정규화
```

---

## 9. 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| v1 | 2025-11-27 | 초안 작성 - 헥사고날 아키텍처 기반 설계, 컨벤션 적용 (Long FK, Soft Delete, version), appliesLevel 호환성 규칙, valueType별 정책 명시 |
| v1.1 | 2025-11-27 | **피드백 반영** - ① AttributeValue에서 attributeId 필드 제거 (Aggregate 내부 Entity 원칙), ② findAllActive() UseCase 분리 (null 의미론 제거), ③ ENUM Activation 시점 검증 추가, ④ CategoryAttribute.appliesLevel 변경 시 검증 추가, ⑤ Upsert 전략을 "완전 재작성"으로 명확화 |
