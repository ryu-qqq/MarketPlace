# CATEGORY-003: Persistence Layer 구현

**Epic**: Catalog - Category 모듈
**Layer**: Persistence Layer (MySQL)
**브랜치**: feature/CATEGORY-003-persistence
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: CATEGORY-001 (Domain), CATEGORY-002 (Application)

---

## 📝 목적

카테고리 데이터 저장소를 구현합니다.
- JPA Entity 설계 (Long FK 전략)
- JPA Repository 및 QueryDSL Repository 구현
- Adapter 구현 (Port Out 인터페이스 구현)
- Entity ↔ Domain Mapper 구현

---

## 🎯 요구사항

### DB 스키마

- [ ] **category 테이블** 생성 (Flyway 마이그레이션)
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
      status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
      is_visible      TINYINT(1) NOT NULL DEFAULT 1,
      is_listable     TINYINT(1) NOT NULL DEFAULT 1,
      department      VARCHAR(50) NOT NULL DEFAULT 'FASHION',
      product_group   VARCHAR(50) NOT NULL DEFAULT 'ETC',
      gender_scope    VARCHAR(20) NOT NULL DEFAULT 'NONE',
      age_group       VARCHAR(20) NOT NULL DEFAULT 'NONE',
      display_name    VARCHAR(255),
      seo_slug        VARCHAR(255),
      icon_url        VARCHAR(500),
      version         BIGINT UNSIGNED NOT NULL DEFAULT 0,
      created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

      UNIQUE KEY uk_category_code (code),
      KEY idx_category_parent (parent_id),
      KEY idx_category_parent_sort (parent_id, sort_order),
      KEY idx_category_status (status, is_visible),
      KEY idx_category_business (department, product_group, gender_scope),
      KEY idx_category_path (path(255)),
      KEY idx_category_updated (updated_at),

      CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
  );
  ```

### JPA Entity

- [ ] **CategoryJpaEntity** 구현
  - id (Long, @GeneratedValue IDENTITY)
  - code (String, unique)
  - nameKo, nameEn (String)
  - parentId (Long, FK - 관계 어노테이션 없음)
  - depth (Integer)
  - path (String, 최대 1000자)
  - sortOrder (Integer)
  - isLeaf (Boolean)
  - status (CategoryStatus, @Enumerated STRING)
  - isVisible, isListable (Boolean)
  - department (Department, @Enumerated STRING)
  - productGroup (ProductGroup, @Enumerated STRING)
  - genderScope (GenderScope, @Enumerated STRING)
  - ageGroup (AgeGroup, @Enumerated STRING)
  - displayName, seoSlug, iconUrl (String)
  - version (Long, @Version)
  - createdAt, updatedAt (LocalDateTime)
  - from(Category): 정적 팩토리 메서드

### JPA Repository (Command)

- [ ] **CategoryJpaRepository** (JpaRepository 상속)
  - existsByCode(String code): boolean
  - findByCode(String code): Optional<CategoryJpaEntity>

### QueryDSL Repository (Query)

- [ ] **CategoryQueryDslRepository** 구현
  - findByParentId(Long parentId): List<CategoryJpaEntity>
  - findAllActiveVisible(): List<CategoryJpaEntity>
  - findListableLeaves(CategorySearchQuery query): List<CategoryJpaEntity>
  - findDescendants(Long categoryId): List<CategoryJpaEntity>
  - search(String keyword): List<CategoryJpaEntity>
  - findUpdatedSince(LocalDateTime since): List<CategoryJpaEntity>
  - hasChildren(Long categoryId): boolean

### Entity Mapper

- [ ] **CategoryJpaEntityMapper** 구현
  - toDomain(CategoryJpaEntity): Category
  - toEntity(Category): CategoryJpaEntity

### Command Adapter

- [ ] **CategoryCommandAdapter** (CategoryPersistencePort 구현)
  - persist(Category): void
  - persistAll(List<Category>): void
  - delete(Long categoryId): void
  - existsByCode(String code): boolean

### Query Adapter

- [ ] **CategoryQueryAdapter** (CategoryQueryPort 구현)
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

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **Lombok 금지** - Plain Java만 사용 (Entity 포함)
- [ ] **Long FK 전략** - JPA 관계 어노테이션(@ManyToOne, @OneToMany 등) 금지
  - ✅ `private Long parentId;`
  - ❌ `@ManyToOne private Category parent;`
- [ ] **QueryDSL DTO Projection** - Entity 직접 반환 대신 필요한 필드만 조회
  - ✅ `Projections.constructor(CategoryDto.class, ...)`
  - ❌ `.selectFrom(category).fetch()` (Entity 직접 반환)
- [ ] **Entity Setter 금지** - Getter Only, 정적 팩토리 메서드만 사용
- [ ] **Soft Delete 전략** - 물리 삭제(DELETE) 금지, 상태 변경으로 처리
  - ✅ `status = DEPRECATED` 또는 `status = INACTIVE`로 변경
  - ❌ `DELETE FROM category WHERE id = ?`

### 인덱스 전략
- [ ] uk_category_code: 코드 유니크 제약
- [ ] idx_category_parent: 부모 ID로 자식 조회
- [ ] idx_category_parent_sort: 부모 + 정렬 순서 (자식 정렬 조회)
- [ ] idx_category_status: 상태 + 노출 여부 필터링
- [ ] idx_category_business: 비즈니스 분류 필터링
- [ ] idx_category_path: 경로 prefix 조회 (하위 노드)
- [ ] idx_category_updated: 증분 조회

### 테스트 규칙
- [ ] Repository 통합 테스트 (@DataJpaTest)
- [ ] QueryDSL 쿼리 테스트
- [ ] Mapper 단위 테스트
- [ ] Adapter 통합 테스트
- [ ] ArchUnit 테스트로 아키텍처 규칙 검증
- [ ] TestFixture 사용 필수
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] Flyway 마이그레이션 스크립트 작성 완료
- [ ] CategoryJpaEntity 구현 완료
- [ ] CategoryJpaRepository 구현 완료
- [ ] CategoryQueryDslRepository 구현 완료
- [ ] CategoryJpaEntityMapper 구현 완료
- [ ] CategoryCommandAdapter 구현 완료
- [ ] CategoryQueryAdapter 구현 완료
- [ ] 통합 테스트 통과 (Repository, Adapter)
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수 확인
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/category-module-design.md
- Plan: docs/prd/plans/CATEGORY-003-persistence-plan.md (create-plan 후 생성)
- Domain: docs/prd/tasks/CATEGORY-001.md
- Application: docs/prd/tasks/CATEGORY-002.md
- Jira: (sync-to-jira 후 추가)

---

## 📦 패키지 구조

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

---

## 📊 TDD 구현 우선순위

1. Flyway 마이그레이션 스크립트 작성
2. CategoryJpaEntity 테스트 및 구현
3. CategoryJpaRepository 테스트 및 구현
4. CategoryQueryDslRepository 테스트 및 구현
5. CategoryJpaEntityMapper 테스트 및 구현
6. CategoryCommandAdapter 테스트 및 구현
7. CategoryQueryAdapter 테스트 및 구현
8. ArchUnit 테스트 작성

---

## 🔍 QueryDSL 쿼리 예시

### findByParentId (DTO Projection 사용)
```java
// ✅ 권장: DTO Projection 사용
return queryFactory
        .select(Projections.constructor(
                CategoryQueryResult.class,
                category.id,
                category.code,
                category.nameKo,
                category.nameEn,
                category.parentId,
                category.depth,
                category.path,
                category.sortOrder,
                category.isLeaf,
                category.status
        ))
        .from(category)
        .where(parentIdEq(parentId))
        .orderBy(category.sortOrder.asc())
        .fetch();

// ❌ 금지: Entity 직접 반환
// return queryFactory.selectFrom(category).where(...).fetch();
```

### findDescendants (path LIKE 패턴)
```java
return queryFactory
        .select(Projections.constructor(
                CategoryQueryResult.class,
                category.id,
                category.code,
                category.nameKo,
                category.nameEn,
                category.parentId,
                category.depth,
                category.path,
                category.sortOrder,
                category.isLeaf,
                category.status
        ))
        .from(category)
        .where(
                category.path.startsWith(parentPath + "/"),
                category.id.ne(categoryId)
        )
        .orderBy(category.depth.asc())
        .fetch();
```

### search (이름/코드/표시명 검색)
```java
return queryFactory
        .select(Projections.constructor(
                CategoryQueryResult.class,
                category.id,
                category.code,
                category.nameKo,
                category.nameEn,
                category.parentId,
                category.depth,
                category.path,
                category.sortOrder,
                category.isLeaf,
                category.status
        ))
        .from(category)
        .where(
                category.nameKo.containsIgnoreCase(keyword)
                        .or(category.nameEn.containsIgnoreCase(keyword))
                        .or(category.code.containsIgnoreCase(keyword))
                        .or(category.displayName.containsIgnoreCase(keyword))
        )
        .orderBy(category.depth.asc(), category.sortOrder.asc())
        .fetch();
```

### QueryDSL DTO Projection Record
```java
// Port Out에 정의
public record CategoryQueryResult(
        Long id,
        String code,
        String nameKo,
        String nameEn,
        Long parentId,
        Integer depth,
        String path,
        Integer sortOrder,
        Boolean isLeaf,
        CategoryStatus status
) {}
```
