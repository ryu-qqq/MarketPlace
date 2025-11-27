# BRAND-001: Domain Layer 구현

**Epic**: Catalog - Brand 모듈
**Layer**: Domain Layer
**브랜치**: feature/BRAND-001-domain
**Jira URL**: (sync-to-jira 후 추가)

---

## 📝 목적

표준 브랜드(Canonical Brand)를 관리하고, 다양한 출처에서 들어오는 브랜드 텍스트를 내부 표준 브랜드로 정규화(Normalization)하는 도메인 모델을 구현한다.

---

## 🎯 요구사항

### Aggregate Root: Brand

- [ ] Brand Aggregate 설계
  - BrandId (식별자)
  - BrandCode (유니크 코드)
  - CanonicalName (표준 이름, 유니크)
  - BrandName (다국어 이름: ko, en, shortName)
  - Country (국가)
  - Department (부문 Enum)
  - isLuxury (럭셔리 여부)
  - BrandStatus (상태 Enum)
  - BrandMeta (메타 정보)
  - DataQuality (데이터 품질)
  - aliases (BrandAlias 컬렉션)
  - version (낙관적 락)

### 내부 Entity: BrandAlias

- [ ] BrandAlias Entity 설계
  - BrandAliasId (식별자)
  - brandId (소속 Brand, Long FK)
  - AliasName (원문 + 정규화된 별칭)
  - AliasSource (출처 정보)
  - Confidence (매칭 신뢰도)
  - AliasStatus (검수 상태)

### Value Objects (13개)

- [ ] BrandId: Long 래퍼, 양수 검증
- [ ] BrandCode: `^[A-Z][A-Z0-9_]{1,99}$` 패턴 검증
- [ ] CanonicalName: 비어있지 않음, 최대 255자
- [ ] BrandName: ko/en 중 최소 하나 필수, 각 최대 255자
- [ ] Country: 유효한 국가 코드 (KR, US, FR, IT 등)
- [ ] Department: Enum (FASHION 등)
- [ ] BrandStatus: Enum (ACTIVE, INACTIVE, BLOCKED)
- [ ] BrandMeta: officialWebsite, logoUrl, description
- [ ] DataQuality: level (UNKNOWN, LOW, MID, HIGH), score (0-100)
- [ ] BrandAliasId: Long 래퍼
- [ ] AliasName: 원문 + 자동 정규화 (소문자, 특수문자 제거)
- [ ] AliasSource: sourceType, sellerId, mallCode
- [ ] AliasSourceType: Enum (SELLER, EXTERNAL_MALL, LEGACY, MANUAL, SYSTEM)
- [ ] Confidence: 0.0 ~ 1.0 범위
- [ ] AliasStatus: Enum (AUTO_SUGGESTED, PENDING_REVIEW, CONFIRMED, REJECTED)

### 도메인 행위

- [ ] Brand.create(): 새 브랜드 생성
- [ ] Brand.reconstitute(): 영속성에서 재구성
- [ ] Brand.canMapProduct(): 상품 매핑 가능 여부 (ACTIVE만)
- [ ] Brand.validateProductMapping(): 매핑 불가 시 예외
- [ ] Brand.update(): 기본 정보 수정
- [ ] Brand.updateMeta(): 메타 정보 수정
- [ ] Brand.changeStatus(): 상태 변경
- [ ] Brand.updateDataQuality(): 데이터 품질 업데이트
- [ ] Brand.addAlias(): 별칭 추가 (중복 검증 포함)
- [ ] Brand.confirmAlias(): 별칭 확정
- [ ] Brand.rejectAlias(): 별칭 거부
- [ ] Brand.updateAliasConfidence(): 별칭 신뢰도 업데이트
- [ ] Brand.removeAlias(): 별칭 삭제

### 불변식 (Invariants)

- [ ] code는 시스템 전체에서 유니크
- [ ] canonicalName은 시스템 전체에서 유니크
- [ ] status가 BLOCKED이면 신규 상품 매핑 불가
- [ ] alias는 Brand를 통해서만 추가/삭제
- [ ] 동일 scope(brand_id + normalized_alias + mall_code + seller_id)에 중복 alias 불가

### Domain Events (5개)

- [ ] BrandCreatedEvent: brandId, code, canonicalName, occurredAt
- [ ] BrandUpdatedEvent: brandId, occurredAt
- [ ] BrandStatusChangedEvent: brandId, oldStatus, newStatus, occurredAt
- [ ] BrandAliasAddedEvent: brandId, aliasId, originalAlias, normalizedAlias, sourceType, occurredAt
- [ ] BrandAliasConfirmedEvent: brandId, aliasId, normalizedAlias, occurredAt

### Domain Exceptions (6개)

- [ ] BrandNotFoundException: 브랜드 조회 실패
- [ ] BrandCodeDuplicateException: 코드 중복
- [ ] CanonicalNameDuplicateException: 표준 이름 중복
- [ ] BrandBlockedException: 차단된 브랜드 사용 시도
- [ ] BrandAliasNotFoundException: 별칭 조회 실패
- [ ] BrandAliasDuplicateException: 별칭 중복

---

## 📦 패키지 구조

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
        │   ├── BrandCode.java
        │   ├── CanonicalName.java
        │   ├── BrandName.java
        │   ├── Country.java
        │   ├── Department.java             # Enum
        │   ├── BrandStatus.java            # Enum
        │   ├── BrandMeta.java
        │   ├── DataQuality.java
        │   ├── BrandAliasId.java
        │   ├── AliasName.java
        │   ├── AliasSource.java
        │   ├── AliasSourceType.java        # Enum
        │   ├── Confidence.java
        │   └── AliasStatus.java            # Enum
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

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙

- [ ] **Lombok 금지**: Plain Java record/class 사용
- [ ] **Law of Demeter 준수**: Getter 체이닝 금지
  - ❌ `brand.getName().getKo()`
  - ✅ `brand.nameKo()`
- [ ] **Tell Don't Ask 패턴**: 외부에서 판단하지 않고 도메인이 결정
  - ❌ `if (brand.getStatus() == ACTIVE) { ... }`
  - ✅ `brand.validateProductMapping()`
- [ ] **불변 컬렉션 반환**: `Collections.unmodifiableList()` 사용
- [ ] **JPA 어노테이션 금지**: Domain Layer는 순수 Java
  - ❌ `@Entity`, `@Id`, `@Column` 등 JPA 어노테이션
  - ✅ Plain Java class/record
- [ ] **외부 의존성 금지**: Spring, JPA, 외부 라이브러리 의존 금지
  - ❌ `import org.springframework.*`
  - ❌ `import jakarta.persistence.*`
  - ✅ `import java.*` (표준 라이브러리만 허용)

### 테스트 규칙

- [ ] ArchUnit 테스트 필수 (Domain Purity)
- [ ] TestFixture 사용 필수
- [ ] 모든 VO에 대한 단위 테스트
- [ ] Aggregate 비즈니스 규칙 테스트
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Value Objects 구현 및 테스트 완료
- [ ] Brand Aggregate Root 구현 및 테스트 완료
- [ ] BrandAlias 내부 Entity 구현 및 테스트 완료
- [ ] Domain Events 구현 완료
- [ ] Domain Exceptions 구현 완료
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/brand-module-design.md
- Plan: docs/prd/plans/BRAND-001-domain-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📐 TDD 순서 가이드

PRD 섹션 7의 구현 우선순위를 따름:

### Phase 1: Value Objects
1. BrandId, BrandCode, CanonicalName
2. BrandName, Country, Department (Enum), BrandStatus (Enum)
3. BrandMeta, DataQuality
4. BrandAliasId, AliasName (핵심 - 자동 정규화)
5. AliasSource, AliasSourceType (Enum), Confidence, AliasStatus (Enum)

### Phase 2: Domain Exceptions
6. 모든 Exception 클래스

### Phase 3: BrandAlias Entity
7. BrandAlias 내부 Entity (create, reconstitute, 도메인 행위)

### Phase 4: Brand Aggregate Root
8. Brand Aggregate (create, reconstitute, 모든 도메인 행위)

### Phase 5: Domain Events
9. 모든 Domain Event 클래스
