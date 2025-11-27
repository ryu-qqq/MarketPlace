# CATEGORY-001: Domain Layer 구현

**Epic**: Catalog - Category 모듈
**Layer**: Domain Layer
**브랜치**: feature/CATEGORY-001-domain
**Jira URL**: (sync-to-jira 후 추가)

---

## 📝 목적

카테고리 도메인 모델을 설계하고 구현합니다.
- 트리 구조의 카테고리 Aggregate Root 설계
- 비즈니스 불변식 및 규칙 구현
- 도메인 이벤트 및 예외 정의

---

## 🎯 요구사항

### Aggregate Root: Category

- [ ] Category Aggregate Root 설계 및 구현
  - 식별자: CategoryId, CategoryCode
  - 트리 구조: parentId (Long FK), depth, path, sortOrder, isLeaf
  - 이름: CategoryName (다국어)
  - 상태/노출: CategoryStatus, isVisible, isListable
  - 비즈니스 분류: BusinessClassification
  - UX/SEO 메타: DisplayMeta
  - 동시성 제어: version (낙관적 락)

### Value Objects

- [ ] **CategoryId**: Long 타입 식별자 VO
  - positive 검증
  - of(), generate() 팩토리 메서드

- [ ] **CategoryCode**: 유니크 코드 VO
  - 패턴: `^[A-Z][A-Z0-9_]{2,99}$`
  - 대문자로 시작, A-Z/0-9/_ 조합, 3-100자

- [ ] **CategoryName**: 다국어 이름 VO
  - ko (필수, 최대 255자)
  - en (선택, 최대 255자)

- [ ] **CategoryPath**: 경로 VO
  - `/`로 시작
  - 최대 1000자
  - append(), startsWith(), segments() 메서드

- [ ] **CategoryDepth**: 깊이 VO
  - 0-10 범위 검증
  - root(), increment() 메서드

- [ ] **SortOrder**: 정렬 순서 VO
  - 0 이상 검증
  - defaultOrder() 메서드

- [ ] **CategoryStatus**: 상태 Enum
  - ACTIVE, INACTIVE, DEPRECATED
  - isUsable() 메서드

- [ ] **Department**: 부문 Enum
  - FASHION, BEAUTY, LIVING, DIGITAL, ETC
  - displayName() 메서드

- [ ] **ProductGroup**: 상품군 Enum
  - APPAREL, SHOES, BAG, ACCESSORY, UNDERWEAR, ETC
  - displayName() 메서드

- [ ] **GenderScope**: 성별 범위 Enum
  - NONE, MEN, WOMEN, UNISEX, KIDS

- [ ] **AgeGroup**: 연령 그룹 Enum
  - NONE, ADULT, KIDS, BABY

- [ ] **BusinessClassification**: 비즈니스 분류 VO
  - department, productGroup, genderScope, ageGroup 조합
  - defaults() 팩토리 메서드

- [ ] **DisplayMeta**: UX/SEO 메타 VO
  - displayName (선택, 최대 255자)
  - seoSlug (선택, 최대 255자)
  - iconUrl (선택, 최대 500자)
  - empty() 팩토리 메서드

### 비즈니스 규칙 (불변식)

- [ ] code는 시스템 전체에서 유니크
- [ ] depth = parent.depth + 1 (루트는 0)
- [ ] path = parent.path + "/" + code (루트는 "/" + code)
- [ ] isLeaf = 자식이 없으면 true
- [ ] 자식이 있는 노드는 isLeaf = false
- [ ] 상품 등록: isLeaf && isListable && status == ACTIVE인 경우만 가능
- [ ] 자기 자신의 하위로 이동 불가 (Cycle 방지)

### 도메인 행위

- [ ] createRoot(): 루트 카테고리 생성
- [ ] createChild(): 자식 카테고리 생성
- [ ] reconstitute(): 영속성에서 재구성
- [ ] canRegisterProduct(): 상품 등록 가능 여부 확인
- [ ] validateProductRegistration(): 상품 등록 가능 여부 검증 (예외)
- [ ] canMoveTo(): 이동 가능 여부 확인
- [ ] validateMoveTo(): 이동 가능 여부 검증 (예외)
- [ ] childAdded(): 자식 추가됨 → isLeaf = false
- [ ] lastChildRemoved(): 마지막 자식 삭제됨 → isLeaf = true
- [ ] changeStatus(): 상태 변경
- [ ] updateMeta(): 메타 정보 수정
- [ ] reorder(): 정렬 순서 변경
- [ ] recalculatePath(): 경로 재계산 (부모 이동 시)

### Domain Events

- [ ] **CategoryCreatedEvent**: 카테고리 생성 시
  - categoryId, code, parentId, occurredAt

- [ ] **CategoryUpdatedEvent**: 카테고리 수정 시
  - categoryId, occurredAt

- [ ] **CategoryMovedEvent**: 카테고리 이동 시
  - categoryId, oldParentId, newParentId, oldPath, newPath, occurredAt

- [ ] **CategoryStatusChangedEvent**: 상태 변경 시
  - categoryId, oldStatus, newStatus, replacementCategoryId, occurredAt

### Domain Exceptions

- [ ] **CategoryNotFoundException**: 카테고리 미발견
  - CategoryErrorCode.CATEGORY_NOT_FOUND

- [ ] **CategoryCodeDuplicateException**: 코드 중복
  - CategoryErrorCode.CATEGORY_CODE_DUPLICATE

- [ ] **CategoryCycleDetectedException**: Cycle 감지 (하위 이동 불가)
  - CategoryErrorCode.CATEGORY_CYCLE_DETECTED

- [ ] **CategoryNotLeafException**: Leaf 아님
  - CategoryErrorCode.CATEGORY_NOT_LEAF

- [ ] **CategoryNotListableException**: 등록 불가 카테고리
  - CategoryErrorCode.CATEGORY_NOT_LISTABLE

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **Lombok 금지** - Plain Java만 사용
- [ ] **Law of Demeter 준수** - Getter 체이닝 금지
  - ✅ `category.nameKo()` (Aggregate에서 직접 반환)
  - ❌ `category.name().ko()`
- [ ] **Tell Don't Ask** - 외부에서 판단하지 않고 도메인이 결정
  - ✅ `category.canRegisterProduct()`
  - ❌ `category.isLeaf() && category.isListable() && category.status() == ACTIVE`
- [ ] **JPA 어노테이션 금지** - Domain Layer는 순수 Java
- [ ] **외부 의존성 금지** - Spring, JPA 등 금지

### 테스트 규칙
- [ ] 모든 VO에 대한 단위 테스트 필수
- [ ] Aggregate Root 행위에 대한 단위 테스트 필수
- [ ] ArchUnit 테스트로 아키텍처 규칙 검증
- [ ] TestFixture 사용 필수
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Value Object 구현 완료
- [ ] Category Aggregate Root 구현 완료
- [ ] 모든 Domain Event 구현 완료
- [ ] 모든 Domain Exception 구현 완료
- [ ] 단위 테스트 통과 (Unit)
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수 확인
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/category-module-design.md
- Plan: docs/prd/plans/CATEGORY-001-domain-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📦 패키지 구조

```
domain/
└── catalog/
    └── category/
        ├── aggregate/
        │   └── category/
        │       └── Category.java              # Aggregate Root
        ├── vo/
        │   ├── CategoryId.java
        │   ├── CategoryCode.java
        │   ├── CategoryName.java
        │   ├── CategoryPath.java
        │   ├── CategoryDepth.java
        │   ├── SortOrder.java
        │   ├── CategoryStatus.java
        │   ├── Department.java
        │   ├── ProductGroup.java
        │   ├── GenderScope.java
        │   ├── AgeGroup.java
        │   ├── DisplayMeta.java
        │   └── BusinessClassification.java
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

---

## 📊 TDD 구현 우선순위

1. VO 테스트 및 구현 (CategoryCode, CategoryName, CategoryPath, CategoryDepth 등)
2. Enum 테스트 및 구현 (CategoryStatus, Department, ProductGroup 등)
3. 복합 VO 테스트 및 구현 (BusinessClassification, DisplayMeta)
4. Aggregate Root 테스트 및 구현 (Category)
5. Domain Event 테스트 및 구현
6. Domain Exception 테스트 및 구현
7. ArchUnit 테스트 작성
