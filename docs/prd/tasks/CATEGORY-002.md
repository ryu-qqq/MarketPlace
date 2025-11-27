# CATEGORY-002: Application Layer 구현

**Epic**: Catalog - Category 모듈
**Layer**: Application Layer
**브랜치**: feature/CATEGORY-002-application
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: CATEGORY-001 (Domain Layer)

---

## 📝 목적

카테고리 비즈니스 로직을 구현합니다.
- CQRS 패턴 적용 (Command/Query 분리)
- UseCase 인터페이스 정의 및 Service 구현
- Port Out 인터페이스 정의 (Persistence Adapter용)
- Assembler를 통한 DTO 변환

---

## 🎯 요구사항

### Port In - Command UseCase

- [ ] **CreateCategoryUseCase**: 카테고리 생성
  - Input: CreateCategoryCommand
  - Output: CategoryResponse

- [ ] **UpdateCategoryUseCase**: 카테고리 수정
  - Input: UpdateCategoryCommand
  - Output: CategoryResponse

- [ ] **ChangeCategoryStatusUseCase**: 상태 변경
  - Input: ChangeCategoryStatusCommand
  - Output: void

- [ ] **MoveCategoryUseCase**: 카테고리 이동
  - Input: MoveCategoryCommand
  - Output: void

### Port In - Query UseCase

- [ ] **GetCategoryUseCase**: 단일 카테고리 조회
  - getById(Long categoryId): CategoryResponse
  - getByCode(String code): CategoryResponse

- [ ] **GetCategoryTreeUseCase**: 트리 조회
  - getTree(CategoryTreeQuery query): CategoryTreeResponse

- [ ] **SearchCategoryUseCase**: 검색
  - search(String keyword): List<CategoryResponse>
  - searchLeaves(CategorySearchQuery query): List<CategoryResponse>
  - findUpdatedSince(LocalDateTime since): List<CategoryResponse>

- [ ] **GetCategoryPathUseCase**: 경로 조회 (breadcrumb)
  - getPath(Long categoryId): CategoryPathResponse

### Port Out - Command

- [ ] **CategoryPersistencePort**: 저장 포트
  - persist(Category category): void
  - persistAll(List<Category> categories): void
  - delete(Long categoryId): void
  - existsByCode(String code): boolean

### Port Out - Query

- [ ] **CategoryQueryPort**: 조회 포트
  - findById(Long categoryId): Optional<Category>
  - findByCode(String code): Optional<Category>
  - findByParentId(Long parentId): List<Category>
  - findAllActiveVisible(): List<Category>
  - findAll(): List<Category>
  - findListableLeaves(CategorySearchQuery query): List<Category>
  - findDescendants(Long categoryId): List<Category>
  - search(String keyword): List<Category>
  - findUpdatedSince(LocalDateTime since): List<Category>
  - hasChildren(Long categoryId): boolean

### Command DTO

- [ ] **CreateCategoryCommand**
  - parentId (nullable, 루트면 null)
  - code, nameKo, nameEn
  - sortOrder, isListable, isVisible
  - department, productGroup, genderScope, ageGroup
  - displayName, seoSlug, iconUrl

- [ ] **UpdateCategoryCommand**
  - categoryId
  - nameKo, nameEn
  - isListable, isVisible, sortOrder
  - displayName, seoSlug, iconUrl

- [ ] **ChangeCategoryStatusCommand**
  - categoryId
  - newStatus (CategoryStatus)
  - replacementCategoryId (DEPRECATED 시 대체 카테고리, 선택)

- [ ] **MoveCategoryCommand**
  - categoryId
  - newParentId
  - newSortOrder

### Query DTO

- [ ] **CategoryTreeQuery**
  - includeInactive (Admin용)
  - department (필터, 선택)
  - productGroup (필터, 선택)

- [ ] **CategorySearchQuery**
  - keyword
  - department, productGroup, genderScope
  - onlyLeaf, onlyListable

### Response DTO

- [ ] **CategoryResponse**
  - id, code, nameKo, nameEn
  - parentId, depth, path, sortOrder, isLeaf
  - status, isVisible, isListable
  - department, productGroup, genderScope, ageGroup
  - displayName, seoSlug, iconUrl

- [ ] **CategoryTreeResponse**
  - roots: List<CategoryTreeNode>
  - CategoryTreeNode: category + children

- [ ] **CategoryPathResponse**
  - categoryId
  - ancestors: List<CategoryResponse> (루트부터 현재까지)

### Assembler

- [ ] **CategoryAssembler**: DTO 변환
  - toClassification(command): BusinessClassification
  - toResponse(category): CategoryResponse
  - toTreeResponse(categories): CategoryTreeResponse
  - toPathResponse(categoryId, ancestors): CategoryPathResponse

### Service 구현

- [ ] **CreateCategoryService**
  - 코드 중복 검증
  - 부모 조회 및 자식 생성
  - 부모 isLeaf 업데이트
  - 저장 및 응답 반환

- [ ] **UpdateCategoryService**
  - 카테고리 조회
  - 메타 정보 업데이트
  - 저장 및 응답 반환

- [ ] **ChangeCategoryStatusService**
  - 카테고리 조회
  - 상태 변경 (DEPRECATED 시 대체 카테고리 검증)
  - 저장

- [ ] **MoveCategoryService**
  - 이동 대상 조회
  - 새 부모 조회 및 Cycle 검증
  - 하위 노드 전체 조회
  - 경로/깊이 재계산
  - 기존/새 부모 isLeaf 업데이트
  - 일괄 저장

- [ ] **GetCategoryService**: 단일 조회 서비스
- [ ] **GetCategoryTreeService**: 트리 조회 서비스
- [ ] **SearchCategoryService**: 검색 서비스
- [ ] **GetCategoryPathService**: 경로 조회 서비스

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **Lombok 금지** - Plain Java만 사용
- [ ] **@Transactional 내 외부 API 호출 금지** - 트랜잭션 경계 엄수
- [ ] **CQRS 분리** - Command와 Query 명확히 분리
- [ ] **Port 패턴 준수** - UseCase(In), Persistence/Query Port(Out) 분리
- [ ] **Assembler 사용 필수** - Entity↔Domain, Domain↔DTO 변환

### Spring 프록시 제약사항
- [ ] `@Transactional` 메서드는 public이어야 함
- [ ] 같은 클래스 내부 호출 시 트랜잭션 미작동 주의
- [ ] final 클래스/메서드에서 @Transactional 미작동 주의

### 테스트 규칙
- [ ] UseCase 단위 테스트 (Mock 사용)
- [ ] Service 단위 테스트 (Port Mock)
- [ ] ArchUnit 테스트로 아키텍처 규칙 검증
- [ ] TestFixture 사용 필수
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Command UseCase 인터페이스 및 구현 완료
- [ ] 모든 Query UseCase 인터페이스 및 구현 완료
- [ ] Port Out 인터페이스 정의 완료
- [ ] 모든 Command/Query/Response DTO 구현 완료
- [ ] Assembler 구현 완료
- [ ] 단위 테스트 통과 (Unit)
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수 확인
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/category-module-design.md
- Plan: docs/prd/plans/CATEGORY-002-application-plan.md (create-plan 후 생성)
- Domain: docs/prd/tasks/CATEGORY-001.md
- Jira: (sync-to-jira 후 추가)

---

## 📦 패키지 구조

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

---

## 📊 TDD 구현 우선순위

1. Command/Query/Response DTO 테스트 및 구현
2. Port Out 인터페이스 정의 (CategoryPersistencePort, CategoryQueryPort)
3. Port In UseCase 인터페이스 정의
4. Assembler 테스트 및 구현
5. Command Service 테스트 및 구현 (CreateCategoryService 우선)
6. Query Service 테스트 및 구현
7. ArchUnit 테스트 작성
