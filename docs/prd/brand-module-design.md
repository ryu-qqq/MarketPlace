# Catalog - Brand 모듈 설계 v2

## 1. 개요

### 1.1 목적
입점형 커머스에서 **표준 브랜드(Canonical Brand)**를 관리하고, 다양한 출처에서 들어오는 브랜드 텍스트를 내부 표준 브랜드로 **정규화(Normalization)**하는 기반을 제공한다.

### 1.2 배경
- 셀러/외부몰/레거시에서 들어오는 브랜드 텍스트가 다양함
  - 예: `나이키`, `NIKE`, `N I K E`, `나이키(정품)`, `NIKE KOREA` → 모두 **NIKE**
- 검색/필터, 기획전, 정산, 통계, 노출 정책 등 **모든 축의 기준**이 브랜드
- 브랜드 정규화 없이는 데이터 일관성 유지 불가

### 1.3 범위

| 구분 | 포함 | 제외 (후속 단계) |
|------|------|-----------------|
| v1 | Brand/BrandAlias 도메인, Admin/Public API | LLM 기반 자동 매칭 |
| v2 | - | n8n/LLM 기반 Brand 매칭 파이프라인 |
| v3 | - | 브랜드 병합(merge) 툴 |

### 1.4 전제 조건
- **인증/인가**: 게이트웨이에서 처리됨
- **아키텍처**: 헥사고날 아키텍처 (Ports & Adapters)
- **패턴**: CQRS, Long FK 전략
- **Aggregate 설계**: Brand가 Root, BrandAlias는 내부 Entity

---

## 2. Domain Layer 설계

### 2.1 Bounded Context
```
catalog (카탈로그)
└── brand (브랜드)
```

### 2.2 패키지 구조
```
domain/
└── catalog/
    └── brand/
        ├── aggregate/
        │   └── brand/
        │       ├── Brand.java              # Aggregate Root
        │       └── BrandAlias.java         # 내부 Entity
        ├── vo/
        │   ├── BrandId.java
        │   ├── BrandCode.java              # 유니크 코드
        │   ├── CanonicalName.java          # 표준 이름 (유니크)
        │   ├── BrandName.java              # 다국어 이름 (ko, en, short)
        │   ├── Country.java                # 국가
        │   ├── Department.java             # 부문 Enum (공유)
        │   ├── BrandStatus.java            # 상태 Enum
        │   ├── BrandMeta.java              # 메타 정보 (website, logo, description)
        │   ├── DataQuality.java            # 데이터 품질 (level, score)
        │   ├── BrandAliasId.java
        │   ├── AliasName.java              # 별칭 (원문 + 정규화)
        │   ├── AliasSource.java            # 출처 (type, sellerId, mallCode)
        │   ├── AliasSourceType.java        # 출처 타입 Enum
        │   ├── Confidence.java             # 신뢰도 (0.0~1.0)
        │   └── AliasStatus.java            # 별칭 상태 Enum
        ├── event/
        │   ├── BrandCreatedEvent.java
        │   ├── BrandUpdatedEvent.java
        │   ├── BrandStatusChangedEvent.java
        │   ├── BrandAliasAddedEvent.java
        │   └── BrandAliasConfirmedEvent.java
        └── exception/
            ├── BrandNotFoundException.java
            ├── BrandCodeDuplicateException.java
            ├── CanonicalNameDuplicateException.java
            ├── BrandBlockedException.java
            ├── BrandAliasNotFoundException.java
            └── BrandAliasDuplicateException.java
```

### 2.3 Aggregate Root: Brand

```java
/**
 * 브랜드 Aggregate Root
 *
 * 불변식:
 * - code는 시스템 전체에서 유니크
 * - canonicalName은 시스템 전체에서 유니크
 * - status가 BLOCKED이면 신규 상품 매핑 불가
 * - alias는 Brand를 통해서만 추가/삭제
 * - 동일 scope(brand_id + normalized_alias + mall_code + seller_id)에 중복 alias 불가
 */
public class Brand {

    // === 식별자 ===
    private final BrandId id;
    private final BrandCode code;
    private final CanonicalName canonicalName;

    // === 이름 ===
    private BrandName name;

    // === 분류 ===
    private Country country;
    private Department department;
    private boolean isLuxury;

    // === 상태 ===
    private BrandStatus status;

    // === 동시성 제어 ===
    private Long version;                     // 낙관적 락 (@Version)

    // === 메타 정보 ===
    private BrandMeta meta;

    // === 데이터 품질 ===
    private DataQuality dataQuality;

    // === 별칭 (내부 Entity 컬렉션) ===
    private final List<BrandAlias> aliases;

    // === 생성 메서드 ===

    /**
     * 새 브랜드 생성
     */
    public static Brand create(
            BrandCode code,
            CanonicalName canonicalName,
            BrandName name,
            Country country,
            Department department,
            boolean isLuxury
    ) {
        return new Brand(
                BrandId.generate(),
                code,
                canonicalName,
                name,
                country,
                department,
                isLuxury,
                BrandStatus.ACTIVE,
                BrandMeta.empty(),
                DataQuality.unknown(),
                new ArrayList<>()
        );
    }

    /**
     * 영속성에서 재구성
     */
    public static Brand reconstitute(
            BrandId id,
            BrandCode code,
            CanonicalName canonicalName,
            BrandName name,
            Country country,
            Department department,
            boolean isLuxury,
            BrandStatus status,
            BrandMeta meta,
            DataQuality dataQuality,
            List<BrandAlias> aliases
    ) {
        return new Brand(
                id, code, canonicalName, name, country, department,
                isLuxury, status, meta, dataQuality, new ArrayList<>(aliases)
        );
    }

    private Brand(/* 모든 필드 */) {
        // 생성자는 private
        this.id = id;
        this.code = code;
        this.canonicalName = canonicalName;
        this.name = name;
        this.country = country;
        this.department = department;
        this.isLuxury = isLuxury;
        this.status = status;
        this.meta = meta;
        this.dataQuality = dataQuality;
        this.aliases = aliases;
    }

    // === 도메인 행위 ===

    /**
     * 상품 매핑 가능 여부 확인
     * Tell Don't Ask: 외부에서 판단하지 않고 도메인이 결정
     */
    public boolean canMapProduct() {
        return status == BrandStatus.ACTIVE;
    }

    /**
     * 상품 매핑 가능 여부 검증 (실패 시 예외)
     */
    public void validateProductMapping() {
        if (!canMapProduct()) {
            throw new BrandBlockedException(id);
        }
    }

    /**
     * 기본 정보 수정
     */
    public void update(
            BrandName name,
            Country country,
            Department department,
            boolean isLuxury
    ) {
        this.name = name;
        this.country = country;
        this.department = department;
        this.isLuxury = isLuxury;
    }

    /**
     * 메타 정보 수정
     * - create() 후 메타 정보 설정 시 사용
     * - 기본 정보와 분리하여 의미 있는 단위로 관리
     */
    public void updateMeta(BrandMeta meta) {
        this.meta = meta;
    }

    /**
     * 상태 변경
     */
    public void changeStatus(BrandStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }
        this.status = newStatus;
    }

    /**
     * 데이터 품질 업데이트
     */
    public void updateDataQuality(DataQuality dataQuality) {
        this.dataQuality = dataQuality;
    }

    // === Alias 관리 (Brand를 통해서만 접근) ===

    /**
     * 별칭 추가
     */
    public BrandAlias addAlias(
            AliasName aliasName,
            AliasSource source,
            Confidence confidence,
            AliasStatus aliasStatus
    ) {
        // 중복 검증
        validateAliasNotDuplicate(aliasName, source);

        BrandAlias alias = BrandAlias.create(
                this.id.value(),  // BrandId → Long 변환
                aliasName,
                source,
                confidence,
                aliasStatus
        );
        this.aliases.add(alias);
        return alias;
    }

    /**
     * 별칭 중복 검증
     */
    private void validateAliasNotDuplicate(AliasName aliasName, AliasSource source) {
        boolean exists = aliases.stream()
                .anyMatch(a -> a.isDuplicateOf(aliasName, source));
        if (exists) {
            throw new BrandAliasDuplicateException(id, aliasName);
        }
    }

    /**
     * 별칭 상태 확정 (AUTO_SUGGESTED → CONFIRMED)
     */
    public void confirmAlias(Long aliasId) {
        findAlias(aliasId).confirm();
    }

    /**
     * 별칭 거부 (AUTO_SUGGESTED → REJECTED)
     */
    public void rejectAlias(Long aliasId) {
        findAlias(aliasId).reject();
    }

    /**
     * 별칭 신뢰도 업데이트
     */
    public void updateAliasConfidence(Long aliasId, Confidence confidence) {
        findAlias(aliasId).updateConfidence(confidence);
    }

    /**
     * 별칭 삭제
     */
    public void removeAlias(Long aliasId) {
        BrandAlias alias = findAlias(aliasId);
        this.aliases.remove(alias);
    }

    private BrandAlias findAlias(Long aliasId) {
        return aliases.stream()
                .filter(a -> a.id().equals(aliasId))
                .findFirst()
                .orElseThrow(() -> new BrandAliasNotFoundException(id, aliasId));
    }

    // === Getter (Law of Demeter 준수) ===

    public Long id() { return id.value(); }
    public String code() { return code.value(); }
    public String canonicalName() { return canonicalName.value(); }

    // 이름 관련 - Getter 체이닝 방지
    public String nameKo() { return name.ko(); }
    public String nameEn() { return name.en(); }
    public String shortName() { return name.shortName(); }

    // 분류 관련
    public String country() { return country != null ? country.value() : null; }
    public Department department() { return department; }
    public boolean isLuxury() { return isLuxury; }

    // 상태
    public BrandStatus status() { return status; }
    public boolean isActive() { return status == BrandStatus.ACTIVE; }
    public boolean isBlocked() { return status == BrandStatus.BLOCKED; }

    // 메타 - Getter 체이닝 방지
    public String officialWebsite() { return meta.officialWebsite(); }
    public String logoUrl() { return meta.logoUrl(); }
    public String description() { return meta.description(); }

    // 데이터 품질 - Getter 체이닝 방지
    public String dataQualityLevel() { return dataQuality.level(); }
    public double dataQualityScore() { return dataQuality.score(); }

    // 별칭 - 불변 리스트로 반환 (내부 수정 방지)
    public List<BrandAlias> aliases() {
        return Collections.unmodifiableList(aliases);
    }

    public int aliasCount() {
        return aliases.size();
    }

    public boolean hasAlias(String normalizedAlias) {
        return aliases.stream()
                .anyMatch(a -> a.normalizedAlias().equals(normalizedAlias));
    }
}
```

### 2.4 내부 Entity: BrandAlias

```java
/**
 * 브랜드 별칭 (내부 Entity)
 *
 * Brand Aggregate 내부에서만 관리됨.
 * 독립적인 라이프사이클 없음 - Brand를 통해서만 생성/삭제.
 */
public class BrandAlias {

    private final BrandAliasId id;
    private final Long brandId;              // 소속 Brand (Long FK)
    private final AliasName aliasName;       // 원문 + 정규화된 별칭
    private final AliasSource source;        // 출처 정보
    private Confidence confidence;           // 매칭 신뢰도
    private AliasStatus status;              // 검수 상태

    // === 생성 메서드 ===

    static BrandAlias create(
            Long brandId,
            AliasName aliasName,
            AliasSource source,
            Confidence confidence,
            AliasStatus status
    ) {
        return new BrandAlias(
                BrandAliasId.generate(),
                brandId,
                aliasName,
                source,
                confidence,
                status
        );
    }

    static BrandAlias reconstitute(
            BrandAliasId id,
            Long brandId,
            AliasName aliasName,
            AliasSource source,
            Confidence confidence,
            AliasStatus status
    ) {
        return new BrandAlias(id, brandId, aliasName, source, confidence, status);
    }

    private BrandAlias(/* 모든 필드 */) {
        this.id = id;
        this.brandId = brandId;
        this.aliasName = aliasName;
        this.source = source;
        this.confidence = confidence;
        this.status = status;
    }

    // === 도메인 행위 ===

    /**
     * 확정 (검수 완료)
     */
    void confirm() {
        if (this.status == AliasStatus.REJECTED) {
            throw new IllegalStateException("Rejected alias cannot be confirmed");
        }
        this.status = AliasStatus.CONFIRMED;
    }

    /**
     * 거부
     */
    void reject() {
        this.status = AliasStatus.REJECTED;
    }

    /**
     * 신뢰도 업데이트
     */
    void updateConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    /**
     * 중복 여부 확인 (같은 scope인지)
     */
    boolean isDuplicateOf(AliasName otherAliasName, AliasSource otherSource) {
        return this.aliasName.normalizedEquals(otherAliasName)
                && this.source.sameScope(otherSource);
    }

    // === Getter ===

    public Long id() { return id.value(); }
    public Long brandId() { return brandId; }
    public String originalAlias() { return aliasName.original(); }
    public String normalizedAlias() { return aliasName.normalized(); }
    public AliasSourceType sourceType() { return source.sourceType(); }
    public Long sellerId() { return source.sellerId(); }
    public String mallCode() { return source.mallCode(); }
    public double confidence() { return confidence.value(); }
    public AliasStatus status() { return status; }
    public boolean isConfirmed() { return status == AliasStatus.CONFIRMED; }
    public boolean isRejected() { return status == AliasStatus.REJECTED; }
}
```

### 2.5 Value Objects

#### BrandId
```java
public record BrandId(Long value) {
    public BrandId {
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("BrandId must be positive");
        }
    }

    public static BrandId of(Long value) {
        return new BrandId(value);
    }

    public static BrandId generate() {
        return new BrandId(null); // DB에서 생성
    }
}
```

#### BrandCode
```java
public record BrandCode(String value) {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{1,99}$");

    public BrandCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BrandCode cannot be blank");
        }
        if (!CODE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "BrandCode must start with uppercase letter, contain only A-Z, 0-9, _, length 2-100"
            );
        }
    }

    public static BrandCode of(String value) {
        return new BrandCode(value);
    }
}
```

#### CanonicalName
```java
public record CanonicalName(String value) {
    public CanonicalName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CanonicalName cannot be blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("CanonicalName exceeds max length 255");
        }
    }

    public static CanonicalName of(String value) {
        return new CanonicalName(value);
    }
}
```

#### BrandName
```java
public record BrandName(
        String ko,
        String en,
        String shortName
) {
    public BrandName {
        // ko, en 중 최소 하나는 필수
        if ((ko == null || ko.isBlank()) && (en == null || en.isBlank())) {
            throw new IllegalArgumentException("At least one of nameKo or nameEn is required");
        }
        if (ko != null && ko.length() > 255) {
            throw new IllegalArgumentException("nameKo exceeds max length 255");
        }
        if (en != null && en.length() > 255) {
            throw new IllegalArgumentException("nameEn exceeds max length 255");
        }
        if (shortName != null && shortName.length() > 100) {
            throw new IllegalArgumentException("shortName exceeds max length 100");
        }
    }

    public static BrandName of(String ko, String en, String shortName) {
        return new BrandName(ko, en, shortName);
    }

    public static BrandName ofKo(String ko) {
        return new BrandName(ko, null, null);
    }

    public static BrandName ofEn(String en) {
        return new BrandName(null, en, null);
    }
}
```

#### Country
```java
public record Country(String value) {
    private static final Set<String> VALID_COUNTRIES = Set.of(
            "KR", "US", "FR", "IT", "GB", "DE", "JP", "CN", "ES", "SE", "CH", "OTHER"
    );

    public Country {
        if (value != null && !value.isBlank()) {
            String upper = value.toUpperCase();
            if (!VALID_COUNTRIES.contains(upper)) {
                throw new IllegalArgumentException("Invalid country code: " + value);
            }
        }
    }

    public static Country of(String value) {
        return value == null || value.isBlank() ? null : new Country(value.toUpperCase());
    }

    public static Country korea() { return new Country("KR"); }
    public static Country usa() { return new Country("US"); }
    public static Country france() { return new Country("FR"); }
    public static Country italy() { return new Country("IT"); }
}
```

#### BrandStatus (Enum)
```java
public enum BrandStatus {
    ACTIVE,      // 정상 사용
    INACTIVE,    // 비활성
    BLOCKED;     // 차단 (사용 금지)

    public boolean isUsable() {
        return this == ACTIVE;
    }

    public boolean isBlocked() {
        return this == BLOCKED;
    }
}
```

#### BrandMeta
```java
public record BrandMeta(
        String officialWebsite,
        String logoUrl,
        String description
) {
    public BrandMeta {
        if (officialWebsite != null && officialWebsite.length() > 500) {
            throw new IllegalArgumentException("officialWebsite exceeds max length 500");
        }
        if (logoUrl != null && logoUrl.length() > 500) {
            throw new IllegalArgumentException("logoUrl exceeds max length 500");
        }
    }

    public static BrandMeta of(String officialWebsite, String logoUrl, String description) {
        return new BrandMeta(officialWebsite, logoUrl, description);
    }

    public static BrandMeta empty() {
        return new BrandMeta(null, null, null);
    }
}
```

#### DataQuality
```java
public record DataQuality(
        String level,
        double score
) {
    private static final Set<String> VALID_LEVELS = Set.of("UNKNOWN", "LOW", "MID", "HIGH");

    public DataQuality {
        if (level == null) {
            level = "UNKNOWN";
        }
        if (!VALID_LEVELS.contains(level)) {
            throw new IllegalArgumentException("Invalid data quality level: " + level);
        }
        if (score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        }
    }

    public static DataQuality of(String level, double score) {
        return new DataQuality(level, score);
    }

    public static DataQuality unknown() {
        return new DataQuality("UNKNOWN", 0.0);
    }

    public static DataQuality high(double score) {
        return new DataQuality("HIGH", score);
    }
}
```

#### AliasName (핵심 - 자동 정규화)
```java
public record AliasName(
        String original,
        String normalized
) {
    /**
     * Compact Constructor - 정규화 자동 수행
     * 불변식: normalized는 항상 original의 정규화된 버전
     */
    public AliasName {
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("Alias name cannot be blank");
        }
        if (original.length() > 255) {
            throw new IllegalArgumentException("Alias name exceeds max length 255");
        }
        // 정규화 자동 수행
        normalized = normalize(original);
    }

    /**
     * 원문만 받아서 생성 (정규화는 자동)
     */
    public static AliasName of(String original) {
        return new AliasName(original, null); // compact constructor에서 normalized 생성
    }

    /**
     * 영속성에서 재구성 (이미 정규화된 값 사용)
     */
    public static AliasName reconstitute(String original, String normalized) {
        // 검증: 저장된 normalized가 현재 로직과 일치하는지
        String recalculated = normalize(original);
        if (!recalculated.equals(normalized)) {
            // 로직 변경 시 재정규화 필요 - 로그 남기고 새 값 사용
            normalized = recalculated;
        }
        return new AliasName(original, normalized);
    }

    /**
     * 정규화 로직
     * - 소문자 변환
     * - 공백, 특수문자 제거
     * - 한글/영문/숫자만 유지
     *
     * 악센트/다이아크리틱 문자 처리 정책:
     * - 악센트 문자는 제거하여 기본 라틴 문자로 매칭 (예: HERMÈS → herms, L'OCCITANE → loccitane)
     * - 1단계에서는 a-z/0-9/한글만 허용
     * - 필요 시 후속 버전에서 유니코드 라틴 확장 블록을 허용하도록 정규식 확장 예정
     */
    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase()
                .replaceAll("[^a-z0-9가-힣]", ""); // 영문, 숫자, 한글만 유지 (악센트 제거)
    }

    /**
     * 정규화된 값 기준 동등 비교
     */
    public boolean normalizedEquals(AliasName other) {
        return this.normalized.equals(other.normalized);
    }
}
```

#### AliasSource
```java
public record AliasSource(
        AliasSourceType sourceType,
        Long sellerId,
        String mallCode
) {
    public AliasSource {
        if (sourceType == null) {
            sourceType = AliasSourceType.MANUAL;
        }
        // SELLER 타입이면 sellerId 필수
        if (sourceType == AliasSourceType.SELLER && sellerId == null) {
            throw new IllegalArgumentException("sellerId is required for SELLER source");
        }
        // EXTERNAL_MALL 타입이면 mallCode 필수
        if (sourceType == AliasSourceType.EXTERNAL_MALL && (mallCode == null || mallCode.isBlank())) {
            throw new IllegalArgumentException("mallCode is required for EXTERNAL_MALL source");
        }
    }

    public static AliasSource manual() {
        return new AliasSource(AliasSourceType.MANUAL, null, null);
    }

    public static AliasSource seller(Long sellerId) {
        return new AliasSource(AliasSourceType.SELLER, sellerId, null);
    }

    public static AliasSource externalMall(String mallCode) {
        return new AliasSource(AliasSourceType.EXTERNAL_MALL, null, mallCode);
    }

    public static AliasSource legacy() {
        return new AliasSource(AliasSourceType.LEGACY, null, null);
    }

    public static AliasSource system() {
        return new AliasSource(AliasSourceType.SYSTEM, null, null);
    }

    /**
     * 같은 scope인지 확인 (중복 체크용)
     */
    public boolean sameScope(AliasSource other) {
        return Objects.equals(this.sellerId, other.sellerId)
                && Objects.equals(this.mallCode, other.mallCode);
    }
}
```

#### AliasSourceType (Enum)
```java
public enum AliasSourceType {
    SELLER,         // 셀러가 입력
    EXTERNAL_MALL,  // 외부몰에서 수집
    LEGACY,         // 레거시 데이터
    MANUAL,         // 운영자가 수동 입력
    SYSTEM          // 시스템 자동 생성
}
```

#### Confidence
```java
public record Confidence(double value) {
    public Confidence {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
    }

    public static Confidence of(double value) {
        return new Confidence(value);
    }

    public static Confidence full() {
        return new Confidence(1.0);
    }

    public static Confidence high() {
        return new Confidence(0.9);
    }

    public static Confidence medium() {
        return new Confidence(0.7);
    }

    public static Confidence low() {
        return new Confidence(0.5);
    }
}
```

#### AliasStatus (Enum)
```java
public enum AliasStatus {
    AUTO_SUGGESTED,   // 자동 제안 (검수 필요)
    PENDING_REVIEW,   // 검수 대기
    CONFIRMED,        // 확정
    REJECTED;         // 거부

    public boolean needsReview() {
        return this == AUTO_SUGGESTED || this == PENDING_REVIEW;
    }

    public boolean isConfirmed() {
        return this == CONFIRMED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}
```

### 2.6 Domain Events

```java
public record BrandCreatedEvent(
        Long brandId,
        String code,
        String canonicalName,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record BrandStatusChangedEvent(
        Long brandId,
        BrandStatus oldStatus,
        BrandStatus newStatus,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record BrandAliasAddedEvent(
        Long brandId,
        Long aliasId,
        String originalAlias,
        String normalizedAlias,
        AliasSourceType sourceType,
        LocalDateTime occurredAt
) implements DomainEvent {}

public record BrandAliasConfirmedEvent(
        Long brandId,
        Long aliasId,
        String normalizedAlias,
        LocalDateTime occurredAt
) implements DomainEvent {}
```

### 2.7 Domain Exceptions

```java
public class BrandNotFoundException extends DomainException {
    public BrandNotFoundException(Long brandId) {
        super(BrandErrorCode.BRAND_NOT_FOUND, "Brand not found: " + brandId);
    }

    public BrandNotFoundException(String code) {
        super(BrandErrorCode.BRAND_NOT_FOUND, "Brand not found by code: " + code);
    }
}

public class BrandCodeDuplicateException extends DomainException {
    public BrandCodeDuplicateException(String code) {
        super(BrandErrorCode.BRAND_CODE_DUPLICATE, "Brand code already exists: " + code);
    }
}

public class CanonicalNameDuplicateException extends DomainException {
    public CanonicalNameDuplicateException(String canonicalName) {
        super(BrandErrorCode.CANONICAL_NAME_DUPLICATE,
              "Canonical name already exists: " + canonicalName);
    }
}

public class BrandBlockedException extends DomainException {
    public BrandBlockedException(BrandId brandId) {
        super(BrandErrorCode.BRAND_BLOCKED,
              "Brand is blocked and cannot be used: " + brandId.value());
    }
}

public class BrandAliasNotFoundException extends DomainException {
    public BrandAliasNotFoundException(BrandId brandId, Long aliasId) {
        super(BrandErrorCode.BRAND_ALIAS_NOT_FOUND,
              "Alias " + aliasId + " not found in brand " + brandId.value());
    }
}

public class BrandAliasDuplicateException extends DomainException {
    public BrandAliasDuplicateException(BrandId brandId, AliasName aliasName) {
        super(BrandErrorCode.BRAND_ALIAS_DUPLICATE,
              "Alias already exists: " + aliasName.original() + " in brand " + brandId.value());
    }
}
```

---

## 3. Application Layer 설계

### 3.1 패키지 구조
```
application/
└── catalog/
    └── brand/
        ├── assembler/
        │   └── BrandAssembler.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateBrandCommand.java
        │   │   ├── UpdateBrandCommand.java
        │   │   ├── ChangeBrandStatusCommand.java
        │   │   ├── AddBrandAliasCommand.java
        │   │   ├── ConfirmBrandAliasCommand.java
        │   │   └── UpdateAliasConfidenceCommand.java
        │   ├── query/
        │   │   ├── BrandSearchQuery.java
        │   │   └── ResolveAliasQuery.java
        │   └── response/
        │       ├── BrandResponse.java
        │       ├── BrandDetailResponse.java
        │       ├── BrandAliasResponse.java
        │       ├── BrandSimpleResponse.java
        │       └── AliasMatchResponse.java
        ├── port/
        │   ├── in/
        │   │   ├── command/
        │   │   │   ├── CreateBrandUseCase.java
        │   │   │   ├── UpdateBrandUseCase.java
        │   │   │   ├── ChangeBrandStatusUseCase.java
        │   │   │   ├── AddBrandAliasUseCase.java
        │   │   │   ├── ConfirmBrandAliasUseCase.java
        │   │   │   └── RemoveBrandAliasUseCase.java
        │   │   └── query/
        │   │       ├── GetBrandUseCase.java
        │   │       ├── SearchBrandUseCase.java
        │   │       ├── GetBrandAliasesUseCase.java
        │   │       └── ResolveAliasUseCase.java
        │   └── out/
        │       ├── command/
        │       │   └── BrandPersistencePort.java
        │       └── query/
        │           ├── BrandQueryPort.java
        │           └── BrandAliasQueryPort.java
        └── service/
            ├── command/
            │   ├── CreateBrandService.java
            │   ├── UpdateBrandService.java
            │   ├── ChangeBrandStatusService.java
            │   ├── AddBrandAliasService.java
            │   ├── ConfirmBrandAliasService.java
            │   └── RemoveBrandAliasService.java
            └── query/
                ├── GetBrandService.java
                ├── SearchBrandService.java
                ├── GetBrandAliasesService.java
                └── ResolveAliasService.java
```

### 3.2 Port In - Command

```java
public interface CreateBrandUseCase {
    BrandResponse create(CreateBrandCommand command);
}

public interface UpdateBrandUseCase {
    BrandResponse update(UpdateBrandCommand command);
}

public interface ChangeBrandStatusUseCase {
    void changeStatus(ChangeBrandStatusCommand command);
}

public interface AddBrandAliasUseCase {
    BrandAliasResponse addAlias(AddBrandAliasCommand command);
}

public interface ConfirmBrandAliasUseCase {
    void confirm(ConfirmBrandAliasCommand command);
    void reject(ConfirmBrandAliasCommand command);
}

public interface RemoveBrandAliasUseCase {
    void remove(Long brandId, Long aliasId);
}
```

### 3.3 Port In - Query

```java
public interface GetBrandUseCase {
    BrandDetailResponse getById(Long brandId);
    BrandDetailResponse getByCode(String code);
}

public interface SearchBrandUseCase {
    Page<BrandResponse> search(BrandSearchQuery query, Pageable pageable);
    List<BrandSimpleResponse> getSimpleList(BrandSearchQuery query);
}

public interface GetBrandAliasesUseCase {
    List<BrandAliasResponse> getAliases(Long brandId);
    List<BrandAliasResponse> searchAliases(String keyword);
}

public interface ResolveAliasUseCase {
    AliasMatchResponse resolveByAlias(String aliasName);
}
```

### 3.4 Port Out - Command

```java
public interface BrandPersistencePort {
    void persist(Brand brand);
    void delete(Long brandId);
    boolean existsByCode(String code);
    boolean existsByCanonicalName(String canonicalName);
}
```

### 3.5 Port Out - Query

```java
public interface BrandQueryPort {
    Optional<Brand> findById(Long brandId);
    Optional<Brand> findByCode(String code);
    Page<Brand> search(BrandSearchQuery query, Pageable pageable);
    List<Brand> findByIds(List<Long> brandIds);
    List<Brand> findAll(BrandSearchQuery query);
}

public interface BrandAliasQueryPort {
    /**
     * 정규화된 alias로 브랜드 후보 조회 (매칭용)
     */
    List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias);

    /**
     * alias 키워드 검색 (관리용)
     */
    List<BrandAliasProjection> searchByKeyword(String keyword);

    /**
     * 특정 브랜드의 alias 목록
     */
    List<BrandAliasProjection> findByBrandId(Long brandId);
}

// 조회 전용 Projection (QueryDSL용)
public record AliasMatchResult(
        Long brandId,
        String brandCode,
        String canonicalName,
        String nameKo,
        double confidence
) {}

public record BrandAliasProjection(
        Long aliasId,
        Long brandId,
        String originalAlias,
        String normalizedAlias,
        String sourceType,
        Long sellerId,
        String mallCode,
        double confidence,
        String status
) {}
```

### 3.6 Command DTO

```java
public record CreateBrandCommand(
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        Department department,
        boolean isLuxury,
        String officialWebsite,
        String logoUrl,
        String description
) {}

public record UpdateBrandCommand(
        Long brandId,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        Department department,
        boolean isLuxury,
        String officialWebsite,
        String logoUrl,
        String description
) {}

public record ChangeBrandStatusCommand(
        Long brandId,
        BrandStatus newStatus
) {}

public record AddBrandAliasCommand(
        Long brandId,
        String aliasName,
        AliasSourceType sourceType,
        Long sellerId,
        String mallCode,
        double confidence,
        AliasStatus status
) {}

public record ConfirmBrandAliasCommand(
        Long brandId,
        Long aliasId
) {}

public record UpdateAliasConfidenceCommand(
        Long brandId,
        Long aliasId,
        double confidence
) {}
```

### 3.7 Query DTO

```java
public record BrandSearchQuery(
        String keyword,
        BrandStatus status,
        Boolean isLuxury,
        Department department,
        String country
) {}

public record ResolveAliasQuery(
        String aliasName
) {}
```

### 3.8 Response DTO

```java
public record BrandResponse(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        String logoUrl
) {}

public record BrandDetailResponse(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        Department department,
        boolean isLuxury,
        BrandStatus status,
        String officialWebsite,
        String logoUrl,
        String description,
        String dataQualityLevel,
        double dataQualityScore,
        int aliasCount,
        List<BrandAliasResponse> aliases
) {}

public record BrandSimpleResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn
) {}

public record BrandAliasResponse(
        Long id,
        Long brandId,
        String originalAlias,
        String normalizedAlias,
        AliasSourceType sourceType,
        Long sellerId,
        String mallCode,
        double confidence,
        AliasStatus status
) {}

public record AliasMatchResponse(
        List<AliasMatch> matches
) {
    public record AliasMatch(
            Long brandId,
            String code,
            String canonicalName,
            String nameKo,
            double confidence
    ) {}
}
```

### 3.9 Service 구현 예시

#### CreateBrandService
```java
@Service
@RequiredArgsConstructor
public class CreateBrandService implements CreateBrandUseCase {

    private final BrandPersistencePort persistencePort;
    private final BrandAssembler assembler;

    @Override
    @Transactional
    public BrandResponse create(CreateBrandCommand command) {
        // 1. 유니크 제약 검증
        if (persistencePort.existsByCode(command.code())) {
            throw new BrandCodeDuplicateException(command.code());
        }
        if (persistencePort.existsByCanonicalName(command.canonicalName())) {
            throw new CanonicalNameDuplicateException(command.canonicalName());
        }

        // 2. 브랜드 생성
        Brand brand = Brand.create(
                BrandCode.of(command.code()),
                CanonicalName.of(command.canonicalName()),
                BrandName.of(command.nameKo(), command.nameEn(), command.shortName()),
                Country.of(command.country()),
                command.department(),
                command.isLuxury()
        );

        // 3. 메타 정보 설정 (updateMeta로 분리)
        brand.updateMeta(
                BrandMeta.of(command.officialWebsite(), command.logoUrl(), command.description())
        );

        // 4. 저장
        persistencePort.persist(brand);

        return assembler.toResponse(brand);
    }
}
```

#### AddBrandAliasService
```java
@Service
@RequiredArgsConstructor
public class AddBrandAliasService implements AddBrandAliasUseCase {

    private final BrandPersistencePort persistencePort;
    private final BrandQueryPort queryPort;
    private final BrandAssembler assembler;

    @Override
    @Transactional
    public BrandAliasResponse addAlias(AddBrandAliasCommand command) {
        // 1. 브랜드 조회
        Brand brand = queryPort.findById(command.brandId())
                .orElseThrow(() -> new BrandNotFoundException(command.brandId()));

        // 2. Alias 생성 (Brand를 통해서만!)
        AliasSource source = createSource(command);
        BrandAlias alias = brand.addAlias(
                AliasName.of(command.aliasName()),
                source,
                Confidence.of(command.confidence()),
                command.status()
        );

        // 3. 저장 (Brand 전체 저장 - Aggregate 단위)
        persistencePort.persist(brand);

        return assembler.toAliasResponse(alias);
    }

    private AliasSource createSource(AddBrandAliasCommand command) {
        return switch (command.sourceType()) {
            case SELLER -> AliasSource.seller(command.sellerId());
            case EXTERNAL_MALL -> AliasSource.externalMall(command.mallCode());
            case LEGACY -> AliasSource.legacy();
            case SYSTEM -> AliasSource.system();
            case MANUAL -> AliasSource.manual();
        };
    }
}
```

#### ResolveAliasService
```java
@Service
@RequiredArgsConstructor
public class ResolveAliasService implements ResolveAliasUseCase {

    private final BrandAliasQueryPort aliasQueryPort;

    @Override
    public AliasMatchResponse resolveByAlias(String aliasName) {
        // 1. 정규화
        String normalized = AliasName.of(aliasName).normalized();

        // 2. 매칭 조회 (REJECTED 제외)
        List<AliasMatchResult> results = aliasQueryPort.findByNormalizedAlias(normalized);

        // 3. 응답 변환
        List<AliasMatchResponse.AliasMatch> matches = results.stream()
                .map(r -> new AliasMatchResponse.AliasMatch(
                        r.brandId(),
                        r.brandCode(),
                        r.canonicalName(),
                        r.nameKo(),
                        r.confidence()
                ))
                .toList();

        return new AliasMatchResponse(matches);
    }
}
```

---

## 4. Persistence Layer 설계

### 4.1 패키지 구조
```
persistence-mysql/
└── catalog/
    └── brand/
        ├── adapter/
        │   ├── BrandCommandAdapter.java
        │   ├── BrandQueryAdapter.java
        │   └── BrandAliasQueryAdapter.java
        ├── entity/
        │   ├── BrandJpaEntity.java
        │   └── BrandAliasJpaEntity.java
        ├── mapper/
        │   └── BrandJpaEntityMapper.java
        └── repository/
            ├── BrandJpaRepository.java
            ├── BrandAliasJpaRepository.java
            └── BrandQueryDslRepository.java
```

### 4.2 DB 스키마

```sql
CREATE TABLE brand (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code                VARCHAR(100) NOT NULL,
    canonical_name      VARCHAR(255) NOT NULL,
    name_ko             VARCHAR(255) NULL,
    name_en             VARCHAR(255) NULL,
    short_name          VARCHAR(100) NULL,
    country             VARCHAR(10) NULL,
    department          VARCHAR(50) NOT NULL DEFAULT 'FASHION',
    is_luxury           TINYINT(1) NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    official_website    VARCHAR(500) NULL,
    logo_url            VARCHAR(500) NULL,
    description         TEXT NULL,

    data_quality_level  VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN',
    data_quality_score  DECIMAL(5,2) NOT NULL DEFAULT 0.0,

    -- 동시성 제어 (낙관적 락)
    version             BIGINT UNSIGNED NOT NULL DEFAULT 0,

    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_brand_code (code),
    UNIQUE KEY uk_brand_canonical_name (canonical_name),
    KEY idx_brand_status (status),
    KEY idx_brand_department (department),
    KEY idx_brand_luxury (is_luxury),
    KEY idx_brand_updated (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE brand_alias (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    brand_id            BIGINT UNSIGNED NOT NULL,
    alias_name          VARCHAR(255) NOT NULL,
    normalized_alias    VARCHAR(255) NOT NULL,
    source_type         VARCHAR(50) NOT NULL DEFAULT 'MANUAL',
    seller_id           BIGINT UNSIGNED NOT NULL DEFAULT 0,
    mall_code           VARCHAR(50) NOT NULL DEFAULT 'GLOBAL',
    confidence          DECIMAL(5,4) NOT NULL DEFAULT 1.0,
    status              VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',

    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,

    -- 유니크 제약 (같은 브랜드 + 정규화된 alias + scope)
    UNIQUE KEY uk_brand_alias_scope (brand_id, normalized_alias, mall_code, seller_id),
    KEY idx_brand_alias_normalized (normalized_alias),
    KEY idx_brand_alias_brand (brand_id),
    KEY idx_brand_alias_source (source_type, mall_code, seller_id),
    KEY idx_brand_alias_status (status),

    CONSTRAINT fk_brand_alias_brand
        FOREIGN KEY (brand_id) REFERENCES brand(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 4.3 JPA Entity

#### BrandJpaEntity
```java
@Entity
@Table(name = "brand")
public class BrandJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "canonical_name", nullable = false, unique = true)
    private String canonicalName;

    @Column(name = "name_ko")
    private String nameKo;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "short_name", length = 100)
    private String shortName;

    @Column(length = 10)
    private String country;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(name = "is_luxury", nullable = false)
    private Boolean isLuxury;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "official_website", length = 500)
    private String officialWebsite;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "data_quality_level", nullable = false, length = 50)
    private String dataQualityLevel;

    @Column(name = "data_quality_score", nullable = false)
    private Double dataQualityScore;

    // 동시성 제어 (낙관적 락)
    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // JPA 연관관계 없음 (Long FK 전략)
    // aliases는 별도 쿼리로 조회

    protected BrandJpaEntity() {}

    // Getter only
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getCanonicalName() { return canonicalName; }
    public String getNameKo() { return nameKo; }
    public String getNameEn() { return nameEn; }
    public String getShortName() { return shortName; }
    public String getCountry() { return country; }
    public String getDepartment() { return department; }
    public Boolean getIsLuxury() { return isLuxury; }
    public String getStatus() { return status; }
    public String getOfficialWebsite() { return officialWebsite; }
    public String getLogoUrl() { return logoUrl; }
    public String getDescription() { return description; }
    public String getDataQualityLevel() { return dataQualityLevel; }
    public Double getDataQualityScore() { return dataQualityScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // 정적 팩토리
    public static BrandJpaEntity from(Brand domain) {
        BrandJpaEntity entity = new BrandJpaEntity();
        entity.id = domain.id();
        entity.code = domain.code();
        entity.canonicalName = domain.canonicalName();
        entity.nameKo = domain.nameKo();
        entity.nameEn = domain.nameEn();
        entity.shortName = domain.shortName();
        entity.country = domain.country();
        entity.department = domain.department().name();
        entity.isLuxury = domain.isLuxury();
        entity.status = domain.status().name();
        entity.officialWebsite = domain.officialWebsite();
        entity.logoUrl = domain.logoUrl();
        entity.description = domain.description();
        entity.dataQualityLevel = domain.dataQualityLevel();
        entity.dataQualityScore = domain.dataQualityScore();
        return entity;
    }
}
```

#### BrandAliasJpaEntity
```java
@Entity
@Table(name = "brand_alias")
public class BrandAliasJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;           // Long FK (관계 어노테이션 없음)

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @Column(name = "normalized_alias", nullable = false)
    private String normalizedAlias;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "mall_code", nullable = false, length = 50)
    private String mallCode;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected BrandAliasJpaEntity() {}

    // Getter only
    public Long getId() { return id; }
    public Long getBrandId() { return brandId; }
    public String getAliasName() { return aliasName; }
    public String getNormalizedAlias() { return normalizedAlias; }
    public String getSourceType() { return sourceType; }
    public Long getSellerId() { return sellerId; }
    public String getMallCode() { return mallCode; }
    public Double getConfidence() { return confidence; }
    public String getStatus() { return status; }

    public static BrandAliasJpaEntity from(BrandAlias alias) {
        BrandAliasJpaEntity entity = new BrandAliasJpaEntity();
        entity.id = alias.id();
        entity.brandId = alias.brandId();
        entity.aliasName = alias.originalAlias();
        entity.normalizedAlias = alias.normalizedAlias();
        entity.sourceType = alias.sourceType().name();
        entity.sellerId = alias.sellerId() != null ? alias.sellerId() : 0L;
        entity.mallCode = alias.mallCode() != null ? alias.mallCode() : "GLOBAL";
        entity.confidence = alias.confidence();
        entity.status = alias.status().name();
        return entity;
    }
}
```

### 4.4 QueryDSL Repository

```java
@Repository
@RequiredArgsConstructor
public class BrandQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private static final QBrandJpaEntity brand = QBrandJpaEntity.brandJpaEntity;
    private static final QBrandAliasJpaEntity alias = QBrandAliasJpaEntity.brandAliasJpaEntity;

    public Page<BrandJpaEntity> search(BrandSearchQuery query, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (query.keyword() != null && !query.keyword().isBlank()) {
            builder.and(
                    brand.canonicalName.containsIgnoreCase(query.keyword())
                            .or(brand.nameKo.containsIgnoreCase(query.keyword()))
                            .or(brand.nameEn.containsIgnoreCase(query.keyword()))
                            .or(brand.code.containsIgnoreCase(query.keyword()))
            );
        }
        if (query.status() != null) {
            builder.and(brand.status.eq(query.status().name()));
        }
        if (query.isLuxury() != null) {
            builder.and(brand.isLuxury.eq(query.isLuxury()));
        }
        if (query.department() != null) {
            builder.and(brand.department.eq(query.department().name()));
        }
        if (query.country() != null) {
            builder.and(brand.country.eq(query.country()));
        }

        List<BrandJpaEntity> content = queryFactory
                .selectFrom(brand)
                .where(builder)
                .orderBy(brand.canonicalName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(brand.count())
                .from(brand)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    public List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias) {
        return queryFactory
                .select(Projections.constructor(AliasMatchResult.class,
                        brand.id,
                        brand.code,
                        brand.canonicalName,
                        brand.nameKo,
                        alias.confidence
                ))
                .from(alias)
                .join(brand).on(alias.brandId.eq(brand.id))
                .where(
                        alias.normalizedAlias.eq(normalizedAlias),
                        alias.status.ne(AliasStatus.REJECTED.name())
                )
                .orderBy(alias.confidence.desc())
                .fetch();
    }

    public List<BrandAliasJpaEntity> findAliasesByBrandId(Long brandId) {
        return queryFactory
                .selectFrom(alias)
                .where(alias.brandId.eq(brandId))
                .orderBy(alias.status.asc(), alias.aliasName.asc())
                .fetch();
    }

    public List<BrandAliasJpaEntity> searchAliasesByKeyword(String keyword) {
        return queryFactory
                .selectFrom(alias)
                .where(
                        alias.aliasName.containsIgnoreCase(keyword)
                                .or(alias.normalizedAlias.containsIgnoreCase(keyword))
                )
                .orderBy(alias.aliasName.asc())
                .limit(100)
                .fetch();
    }
}
```

### 4.5 Command Adapter

```java
@Component
@RequiredArgsConstructor
public class BrandCommandAdapter implements BrandPersistencePort {

    private final BrandJpaRepository brandJpaRepository;
    private final BrandAliasJpaRepository aliasJpaRepository;
    private final BrandJpaEntityMapper mapper;

    /**
     * Alias Persist 전략: Soft Delete + UPSERT
     *
     * 원칙:
     * - 물리 삭제(DELETE) 없음 - alias 제거 시 status = REJECTED로 상태 변경
     * - aliasId 영구 보존 (안정성)
     * - 이력 추적 가능
     * - Long FK 컨벤션 준수 (JPA Cascade 미사용)
     *
     * 구현:
     * - Domain에서 상태 관리: brand.rejectAlias(aliasId) → status = REJECTED
     * - Persistence는 단순 UPSERT: saveAll()이 id 유무에 따라 INSERT/UPDATE 수행
     */
    @Override
    public void persist(Brand brand) {
        // 1. Brand 저장
        BrandJpaEntity brandEntity = mapper.toEntity(brand);
        brandJpaRepository.save(brandEntity);

        // 2. Aliases 저장 (Soft Delete 전략)
        // - 새 alias: INSERT (id == null)
        // - 기존 alias: UPDATE (상태 변경 포함)
        // - 삭제된 alias: status = REJECTED로 이미 도메인에서 처리됨
        // - 물리 DELETE 없음!
        List<BrandAliasJpaEntity> aliasEntities = brand.aliases().stream()
                .map(mapper::toAliasEntity)
                .toList();
        aliasJpaRepository.saveAll(aliasEntities);
    }

    /**
     * 브랜드 삭제 (Soft Delete)
     * - 물리 삭제 없음
     * - status = INACTIVE로 변경하여 처리
     * - 실제 삭제는 Service 레이어에서 changeStatus로 처리 권장
     */
    @Override
    public void delete(Long brandId) {
        // Soft Delete: 물리 삭제 대신 상태 변경으로 처리
        // 실제 구현에서는 Service 레이어의 changeStatus(INACTIVE) 사용 권장
        brandJpaRepository.findById(brandId)
                .ifPresent(entity -> {
                    // status를 INACTIVE로 변경 (물리 삭제 없음)
                    // 별도의 updateStatus 메서드 또는 쿼리 사용
                });
    }

    @Override
    public boolean existsByCode(String code) {
        return brandJpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByCanonicalName(String canonicalName) {
        return brandJpaRepository.existsByCanonicalName(canonicalName);
    }
}
```

### 4.6 Query Adapter

```java
@Component
@RequiredArgsConstructor
public class BrandQueryAdapter implements BrandQueryPort {

    private final BrandJpaRepository jpaRepository;
    private final BrandQueryDslRepository queryDslRepository;
    private final BrandJpaEntityMapper mapper;

    @Override
    public Optional<Brand> findById(Long brandId) {
        return jpaRepository.findById(brandId)
                .map(entity -> {
                    List<BrandAliasJpaEntity> aliases =
                            queryDslRepository.findAliasesByBrandId(brandId);
                    return mapper.toDomain(entity, aliases);
                });
    }

    @Override
    public Optional<Brand> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(entity -> {
                    List<BrandAliasJpaEntity> aliases =
                            queryDslRepository.findAliasesByBrandId(entity.getId());
                    return mapper.toDomain(entity, aliases);
                });
    }

    @Override
    public Page<Brand> search(BrandSearchQuery query, Pageable pageable) {
        Page<BrandJpaEntity> page = queryDslRepository.search(query, pageable);
        return page.map(entity -> mapper.toDomainWithoutAliases(entity));
    }

    @Override
    public List<Brand> findByIds(List<Long> brandIds) {
        return jpaRepository.findAllById(brandIds).stream()
                .map(mapper::toDomainWithoutAliases)
                .toList();
    }

    @Override
    public List<Brand> findAll(BrandSearchQuery query) {
        return queryDslRepository.search(query, Pageable.unpaged())
                .getContent()
                .stream()
                .map(mapper::toDomainWithoutAliases)
                .toList();
    }
}

@Component
@RequiredArgsConstructor
public class BrandAliasQueryAdapter implements BrandAliasQueryPort {

    private final BrandQueryDslRepository queryDslRepository;

    @Override
    public List<AliasMatchResult> findByNormalizedAlias(String normalizedAlias) {
        return queryDslRepository.findByNormalizedAlias(normalizedAlias);
    }

    @Override
    public List<BrandAliasProjection> searchByKeyword(String keyword) {
        return queryDslRepository.searchAliasesByKeyword(keyword).stream()
                .map(this::toProjection)
                .toList();
    }

    @Override
    public List<BrandAliasProjection> findByBrandId(Long brandId) {
        return queryDslRepository.findAliasesByBrandId(brandId).stream()
                .map(this::toProjection)
                .toList();
    }

    private BrandAliasProjection toProjection(BrandAliasJpaEntity entity) {
        return new BrandAliasProjection(
                entity.getId(),
                entity.getBrandId(),
                entity.getAliasName(),
                entity.getNormalizedAlias(),
                entity.getSourceType(),
                entity.getSellerId(),
                entity.getMallCode(),
                entity.getConfidence(),
                entity.getStatus()
        );
    }
}
```

---

## 5. REST API Layer 설계

### 5.1 패키지 구조
```
adapter-in/rest-api/
└── catalog/
    └── brand/
        ├── controller/
        │   ├── BrandAdminCommandController.java
        │   ├── BrandAdminQueryController.java
        │   ├── BrandAliasAdminController.java
        │   └── BrandPublicQueryController.java
        ├── dto/
        │   ├── command/
        │   │   ├── CreateBrandApiRequest.java
        │   │   ├── UpdateBrandApiRequest.java
        │   │   ├── ChangeBrandStatusApiRequest.java
        │   │   ├── AddBrandAliasApiRequest.java
        │   │   └── UpdateAliasApiRequest.java
        │   ├── query/
        │   │   └── BrandSearchApiRequest.java
        │   └── response/
        │       ├── BrandApiResponse.java
        │       ├── BrandDetailApiResponse.java
        │       ├── BrandSimpleApiResponse.java
        │       ├── BrandAliasApiResponse.java
        │       └── AliasMatchApiResponse.java
        ├── mapper/
        │   └── BrandApiMapper.java
        └── error/
            └── BrandApiErrorMapper.java
```

### 5.2 API 역할 구분

> **Admin API**는 내부 운영용이며, 인증/인가에서 ROLE_ADMIN을 요구한다.
> 쓰기 기능 + 관리용 조회를 제공한다.
>
> **Public API**는 BFF/외부 서비스에서 참조하는 브랜드 조회용이며, 쓰기 기능은 제공하지 않는다.
> 읽기 전용이며, 캐싱을 전제로 설계되었다. `status=ACTIVE`인 브랜드만 노출한다.

### 5.3 API 명세

#### Admin API - Brand 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/brands` | 목록 조회 (페이징) |
| GET | `/api/v1/admin/catalog/brands/search` | 검색 |
| GET | `/api/v1/admin/catalog/brands/{id}` | 상세 조회 |
| POST | `/api/v1/admin/catalog/brands` | 생성 |
| PATCH | `/api/v1/admin/catalog/brands/{id}` | 수정 |
| PATCH | `/api/v1/admin/catalog/brands/{id}/status` | 상태 변경 |
| DELETE | `/api/v1/admin/catalog/brands/{id}` | 삭제 (Soft) |

#### Admin API - BrandAlias 관리

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/brands/{brandId}/aliases` | 별칭 목록 |
| POST | `/api/v1/admin/catalog/brands/{brandId}/aliases` | 별칭 추가 |
| PATCH | `/api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}` | 별칭 수정 |
| PATCH | `/api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}/confirm` | 별칭 확정 |
| PATCH | `/api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}/reject` | 별칭 거부 |
| DELETE | `/api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}` | 별칭 삭제 |
| GET | `/api/v1/admin/catalog/brands/aliases/search` | 별칭 전역 검색 |

#### Public API (조회 전용, 캐싱 전제)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/catalog/brands/{id}` | 상세 조회 |
| GET | `/api/v1/catalog/brands/by-code/{code}` | 코드로 조회 |
| GET | `/api/v1/catalog/brands/search` | 검색 |
| GET | `/api/v1/catalog/brands/simple-list` | 간단 목록 (셀렉트박스용) |
| GET | `/api/v1/catalog/brands/resolve-by-alias` | 별칭으로 브랜드 조회 |

### 5.4 Request DTO

```java
public record CreateBrandApiRequest(
        @NotBlank(message = "코드는 필수입니다")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,99}$",
                 message = "코드는 대문자로 시작, A-Z/0-9/_ 조합, 2-100자")
        String code,

        @NotBlank(message = "표준 이름은 필수입니다")
        @Size(max = 255)
        String canonicalName,

        @Size(max = 255)
        String nameKo,

        @Size(max = 255)
        String nameEn,

        @Size(max = 100)
        String shortName,

        @Size(max = 10)
        String country,

        @NotNull(message = "부문은 필수입니다")
        Department department,

        boolean isLuxury,

        @Size(max = 500)
        String officialWebsite,

        @Size(max = 500)
        String logoUrl,

        String description
) {}

public record UpdateBrandApiRequest(
        @Size(max = 255)
        String nameKo,

        @Size(max = 255)
        String nameEn,

        @Size(max = 100)
        String shortName,

        @Size(max = 10)
        String country,

        Department department,

        Boolean isLuxury,

        @Size(max = 500)
        String officialWebsite,

        @Size(max = 500)
        String logoUrl,

        String description
) {}

public record ChangeBrandStatusApiRequest(
        @NotNull(message = "상태는 필수입니다")
        BrandStatus status
) {}

public record AddBrandAliasApiRequest(
        @NotBlank(message = "별칭은 필수입니다")
        @Size(max = 255)
        String aliasName,

        @NotNull(message = "출처 타입은 필수입니다")
        AliasSourceType sourceType,

        Long sellerId,

        @Size(max = 50)
        String mallCode,

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "1.0")
        Double confidence,

        AliasStatus status
) {}

public record UpdateAliasApiRequest(
        @DecimalMin(value = "0.0")
        @DecimalMax(value = "1.0")
        Double confidence,

        AliasStatus status
) {}
```

### 5.5 Response DTO

```java
public record BrandApiResponse(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        String department,
        boolean isLuxury,
        String status,
        String logoUrl
) {}

public record BrandDetailApiResponse(
        Long id,
        String code,
        String canonicalName,
        String nameKo,
        String nameEn,
        String shortName,
        String country,
        String department,
        boolean isLuxury,
        String status,
        String officialWebsite,
        String logoUrl,
        String description,
        String dataQualityLevel,
        double dataQualityScore,
        int aliasCount,
        List<BrandAliasApiResponse> aliases
) {}

public record BrandSimpleApiResponse(
        Long id,
        String code,
        String nameKo,
        String nameEn
) {}

public record BrandAliasApiResponse(
        Long id,
        Long brandId,
        String originalAlias,
        String normalizedAlias,
        String sourceType,
        Long sellerId,
        String mallCode,
        double confidence,
        String status
) {}

public record AliasMatchApiResponse(
        List<AliasMatch> matches
) {
    public record AliasMatch(
            Long brandId,
            String code,
            String canonicalName,
            String nameKo,
            double confidence
    ) {}
}
```

### 5.6 Controller 예시

#### BrandAdminCommandController
```java
@RestController
@RequestMapping("/api/v1/admin/catalog/brands")
@RequiredArgsConstructor
public class BrandAdminCommandController {

    private final CreateBrandUseCase createBrandUseCase;
    private final UpdateBrandUseCase updateBrandUseCase;
    private final ChangeBrandStatusUseCase changeBrandStatusUseCase;
    private final BrandApiMapper mapper;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandApiResponse>> create(
            @Valid @RequestBody CreateBrandApiRequest request
    ) {
        CreateBrandCommand command = mapper.toCommand(request);
        BrandResponse response = createBrandUseCase.create(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @PatchMapping("/{brandId}")
    public ResponseEntity<ApiResponse<BrandApiResponse>> update(
            @PathVariable Long brandId,
            @Valid @RequestBody UpdateBrandApiRequest request
    ) {
        UpdateBrandCommand command = mapper.toCommand(brandId, request);
        BrandResponse response = updateBrandUseCase.update(command);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }

    @PatchMapping("/{brandId}/status")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long brandId,
            @Valid @RequestBody ChangeBrandStatusApiRequest request
    ) {
        ChangeBrandStatusCommand command = new ChangeBrandStatusCommand(brandId, request.status());
        changeBrandStatusUseCase.changeStatus(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<Void> delete(@PathVariable Long brandId) {
        changeBrandStatusUseCase.changeStatus(
                new ChangeBrandStatusCommand(brandId, BrandStatus.INACTIVE)
        );
        return ResponseEntity.noContent().build();
    }
}
```

#### BrandPublicQueryController
```java
@RestController
@RequestMapping("/api/v1/catalog/brands")
@RequiredArgsConstructor
public class BrandPublicQueryController {

    private final GetBrandUseCase getBrandUseCase;
    private final SearchBrandUseCase searchBrandUseCase;
    private final ResolveAliasUseCase resolveAliasUseCase;
    private final BrandApiMapper mapper;

    @GetMapping("/{brandId}")
    public ResponseEntity<ApiResponse<BrandDetailApiResponse>> getById(
            @PathVariable Long brandId
    ) {
        BrandDetailResponse response = getBrandUseCase.getById(brandId);
        return ResponseEntity.ok(ApiResponse.success(mapper.toDetailApiResponse(response)));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<ApiResponse<BrandDetailApiResponse>> getByCode(
            @PathVariable String code
    ) {
        BrandDetailResponse response = getBrandUseCase.getByCode(code);
        return ResponseEntity.ok(ApiResponse.success(mapper.toDetailApiResponse(response)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BrandApiResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isLuxury,
            @RequestParam(required = false) Department department,
            Pageable pageable
    ) {
        BrandSearchQuery query = new BrandSearchQuery(
                keyword, BrandStatus.ACTIVE, isLuxury, department, null
        );
        Page<BrandResponse> page = searchBrandUseCase.search(query, pageable);
        return ResponseEntity.ok(
                ApiResponse.success(page.map(mapper::toApiResponse))
        );
    }

    @GetMapping("/simple-list")
    public ResponseEntity<ApiResponse<List<BrandSimpleApiResponse>>> getSimpleList(
            @RequestParam(required = false) Boolean isLuxury,
            @RequestParam(required = false) Department department
    ) {
        BrandSearchQuery query = new BrandSearchQuery(
                null, BrandStatus.ACTIVE, isLuxury, department, null
        );
        List<BrandSimpleResponse> list = searchBrandUseCase.getSimpleList(query);
        return ResponseEntity.ok(
                ApiResponse.success(list.stream().map(mapper::toSimpleApiResponse).toList())
        );
    }

    @GetMapping("/resolve-by-alias")
    public ResponseEntity<ApiResponse<AliasMatchApiResponse>> resolveByAlias(
            @RequestParam String aliasName
    ) {
        AliasMatchResponse response = resolveAliasUseCase.resolveByAlias(aliasName);
        return ResponseEntity.ok(ApiResponse.success(mapper.toApiResponse(response)));
    }
}
```

### 5.7 Error Mapper

```java
@Component
public class BrandApiErrorMapper implements ApiErrorMapper {

    @Override
    public boolean supports(DomainException exception) {
        return exception instanceof BrandNotFoundException
                || exception instanceof BrandCodeDuplicateException
                || exception instanceof CanonicalNameDuplicateException
                || exception instanceof BrandBlockedException
                || exception instanceof BrandAliasNotFoundException
                || exception instanceof BrandAliasDuplicateException;
    }

    @Override
    public ErrorInfo map(DomainException exception) {
        return switch (exception) {
            case BrandNotFoundException e -> ErrorInfo.of(
                    "BRAND_NOT_FOUND",
                    "브랜드를 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case BrandCodeDuplicateException e -> ErrorInfo.of(
                    "BRAND_CODE_DUPLICATE",
                    "이미 존재하는 브랜드 코드입니다",
                    HttpStatus.CONFLICT
            );
            case CanonicalNameDuplicateException e -> ErrorInfo.of(
                    "CANONICAL_NAME_DUPLICATE",
                    "이미 존재하는 표준 이름입니다",
                    HttpStatus.CONFLICT
            );
            case BrandBlockedException e -> ErrorInfo.of(
                    "BRAND_BLOCKED",
                    "차단된 브랜드입니다",
                    HttpStatus.FORBIDDEN
            );
            case BrandAliasNotFoundException e -> ErrorInfo.of(
                    "BRAND_ALIAS_NOT_FOUND",
                    "브랜드 별칭을 찾을 수 없습니다",
                    HttpStatus.NOT_FOUND
            );
            case BrandAliasDuplicateException e -> ErrorInfo.of(
                    "BRAND_ALIAS_DUPLICATE",
                    "이미 존재하는 별칭입니다",
                    HttpStatus.CONFLICT
            );
            default -> ErrorInfo.of(
                    "BRAND_ERROR",
                    exception.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        };
    }
}
```

---

## 6. 도메인 규칙 정리

### 6.1 Brand 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| code 유니크 | 시스템 전체 유니크 | Service + DB |
| canonicalName 유니크 | 시스템 전체 유니크 | Service + DB |
| ACTIVE만 매핑 가능 | status=ACTIVE인 브랜드만 상품 매핑 | Domain |
| BLOCKED는 신규 불가 | 기존 데이터 유지, 신규 매핑 금지 | Domain |

### 6.2 BrandAlias 규칙

| 규칙 | 설명 | 검증 위치 |
|------|------|----------|
| 정규화 자동 수행 | AliasName 생성 시 normalized 자동 계산 | VO |
| scope 내 유니크 | brand_id + normalized + mall_code + seller_id | Domain + DB |
| REJECTED 제외 | 매칭 시 REJECTED 상태 제외 | Query |
| Brand 통해서만 관리 | 독립 생성/삭제 불가 | Aggregate |

### 6.3 정규화 로직

```
입력: "N I K E (정품)"
→ 소문자 변환: "n i k e (정품)"
→ 특수문자/공백 제거: "nike정품"
→ 결과: "nike정품"

입력: "HERMÈS"
→ 소문자 변환: "hermès"
→ 악센트/특수문자 제거: "herms"
→ 결과: "herms"
```

### 6.4 DataQuality vs Confidence 역할

| 개념 | 대상 | 용도 | 예시 |
|------|------|------|------|
| **DataQuality** | Brand 자체 | 브랜드 메타데이터 품질 지표 | 로고 없음 → LOW, 모든 정보 완비 → HIGH |
| **Confidence** | BrandAlias → Brand 연결 | alias 매칭 신뢰도 | 수동 입력 → 1.0, LLM 추론 → 0.7 |

- `DataQuality`는 운영자가 수동/자동으로 관리하는 "브랜드 데이터 품질 지표"
- `Confidence`는 alias 매칭에서만 사용하는 "alias → brand link 신뢰도"

### 6.5 resolve-by-alias API 역할

> `resolve-by-alias`는 **"최종 매칭 결정"이 아니라, 후보 리스트 + confidence를 제공하는 API**다.
> 최종 선택/검수는 상위 레이어(n8n/LLM/운영툴)에서 수행한다.

**활용 시나리오**:
1. 셀러 상품 인입 시 브랜드 텍스트 → resolve-by-alias → 후보 리스트 제공
2. n8n/LLM이 confidence 기반 자동 매칭 또는 운영자 검수 요청
3. 운영자가 confirm/reject → 학습 데이터로 활용

### 6.6 동시성 및 트랜잭션 정책

#### 낙관적 락 (Optimistic Locking)
- `version` 컬럼 + `@Version` 어노테이션으로 동시성 제어
- alias 검수(confirm/reject) 시 동시 변경 발생 시 **409 Conflict** 반환
- 클라이언트는 "다시 로드하고 재시도" 유도

#### 트랜잭션 경계
- Brand + BrandAlias는 하나의 Aggregate로 **단일 트랜잭션**에서 처리
- 외부 API 호출 금지 (`@Transactional` 내)

---

## 7. 구현 우선순위 (TDD 사이클)

### Phase 1: Domain Layer
1. VO 테스트 및 구현 (BrandCode, CanonicalName, AliasName, ...)
2. BrandAlias Entity 테스트 및 구현
3. Brand Aggregate Root 테스트 및 구현
4. Exception 테스트 및 구현

### Phase 2: Persistence Layer
5. Entity 테스트 및 구현
6. JPA Repository 테스트 및 구현
7. QueryDSL Repository 테스트 및 구현
8. Mapper 테스트 및 구현
9. Adapter 테스트 및 구현

### Phase 3: Application Layer
10. Assembler 테스트 및 구현
11. Command Service 테스트 및 구현
12. Query Service 테스트 및 구현

### Phase 4: REST API Layer
13. API Request/Response DTO
14. API Mapper 테스트 및 구현
15. Controller 테스트 및 구현
16. Error Mapper 구현

### Phase 5: Integration Test
17. E2E 테스트 (TestRestTemplate)

---

## 8. 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| v1 | - | 초안 작성 |
| v2 | 2025-11-27 | 헥사고날 아키텍처 기반 재설계, Aggregate 경계 명확화, 코딩 컨벤션 적용 |
| v2.1 | 2025-11-27 | 피드백 반영: update/updateMeta 분리, BrandId→Long 타입 정리, 악센트 정규화 정책 명시, Soft Delete 전략 적용, version/@Version 추가, Admin/Public API 역할 명시, DataQuality/Confidence 역할 구분, resolve-by-alias 설명 추가 |
