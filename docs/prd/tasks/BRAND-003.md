# BRAND-003: Persistence Layer 구현

**Epic**: Catalog - Brand 모듈
**Layer**: Persistence Layer (MySQL)
**브랜치**: feature/BRAND-003-persistence
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: BRAND-001 (Domain Layer), BRAND-002 (Application Layer)

---

## 📝 목적

Brand 도메인의 영속성 계층을 구현한다. JPA Entity, Repository, Adapter를 통해 Application Layer의 Port Out 인터페이스를 구현하고, Long FK 전략과 QueryDSL을 활용한 효율적인 데이터 접근을 제공한다.

---

## 🎯 요구사항

### DB 스키마 (Flyway 마이그레이션)

- [ ] brand 테이블 생성
  ```sql
  - id (BIGINT UNSIGNED, PK, AUTO_INCREMENT)
  - code (VARCHAR(100), UNIQUE, NOT NULL)
  - canonical_name (VARCHAR(255), UNIQUE, NOT NULL)
  - name_ko, name_en (VARCHAR(255))
  - short_name (VARCHAR(100))
  - country (VARCHAR(10))
  - department (VARCHAR(50), DEFAULT 'FASHION')
  - is_luxury (TINYINT(1), DEFAULT 0)
  - status (VARCHAR(20), DEFAULT 'ACTIVE')
  - official_website, logo_url (VARCHAR(500))
  - description (TEXT)
  - data_quality_level (VARCHAR(50), DEFAULT 'UNKNOWN')
  - data_quality_score (DECIMAL(5,2), DEFAULT 0.0)
  - version (BIGINT UNSIGNED, DEFAULT 0) - 낙관적 락
  - created_at, updated_at (DATETIME)
  ```

- [ ] brand_alias 테이블 생성
  ```sql
  - id (BIGINT UNSIGNED, PK, AUTO_INCREMENT)
  - brand_id (BIGINT UNSIGNED, FK → brand.id)
  - alias_name (VARCHAR(255), NOT NULL)
  - normalized_alias (VARCHAR(255), NOT NULL)
  - source_type (VARCHAR(50), DEFAULT 'MANUAL')
  - seller_id (BIGINT UNSIGNED, DEFAULT 0)
  - mall_code (VARCHAR(50), DEFAULT 'GLOBAL')
  - confidence (DECIMAL(5,4), DEFAULT 1.0)
  - status (VARCHAR(30), DEFAULT 'CONFIRMED')
  - created_at, updated_at (DATETIME)
  - UNIQUE (brand_id, normalized_alias, mall_code, seller_id)
  ```

### JPA Entity (2개)

- [ ] BrandJpaEntity
  - `@Entity`, `@Table(name = "brand")`
  - `@Id`, `@GeneratedValue(strategy = IDENTITY)`
  - `@Version` for 낙관적 락
  - **JPA 관계 어노테이션 없음** (Long FK 전략)
  - 정적 팩토리: `from(Brand domain)`
  - Getter only, protected 기본 생성자

- [ ] BrandAliasJpaEntity
  - `@Entity`, `@Table(name = "brand_alias")`
  - `brandId` (Long) - **관계 어노테이션 없음**
  - 정적 팩토리: `from(BrandAlias alias)`

### Repository (3개)

- [ ] BrandJpaRepository (JpaRepository 상속)
  - `findByCode(String code)`
  - `existsByCode(String code)`
  - `existsByCanonicalName(String canonicalName)`

- [ ] BrandAliasJpaRepository (JpaRepository 상속)
  - `findByBrandId(Long brandId)`
  - `deleteByBrandId(Long brandId)`

- [ ] BrandQueryDslRepository (QueryDSL 사용)
  - `search(BrandSearchQuery, Pageable)` → Page<BrandJpaEntity>
  - `findByNormalizedAlias(String)` → List<AliasMatchResult> (DTO Projection)
  - `findAliasesByBrandId(Long)` → List<BrandAliasJpaEntity>
  - `searchAliasesByKeyword(String)` → List<BrandAliasJpaEntity>

### Mapper (1개)

- [ ] BrandJpaEntityMapper
  - `toEntity(Brand)` → BrandJpaEntity
  - `toAliasEntity(BrandAlias)` → BrandAliasJpaEntity
  - `toDomain(BrandJpaEntity, List<BrandAliasJpaEntity>)` → Brand
  - `toDomainWithoutAliases(BrandJpaEntity)` → Brand

### Adapter (3개)

- [ ] BrandCommandAdapter (implements BrandPersistencePort)
  - `persist(Brand)`: Brand + Aliases 저장
    - **Soft Delete 전략**: 물리 삭제 없음, status = REJECTED로 상태 변경
    - **UPSERT 방식**: saveAll()이 id 유무에 따라 INSERT/UPDATE
  - `delete(Long brandId)`: Soft Delete (status → INACTIVE)
  - `existsByCode(String code)`
  - `existsByCanonicalName(String canonicalName)`

- [ ] BrandQueryAdapter (implements BrandQueryPort)
  - `findById(Long brandId)`: Entity + Aliases 조회 → Domain 변환
  - `findByCode(String code)`
  - `search(BrandSearchQuery, Pageable)`
  - `findByIds(List<Long> brandIds)`
  - `findAll(BrandSearchQuery query)`

- [ ] BrandAliasQueryAdapter (implements BrandAliasQueryPort)
  - `findByNormalizedAlias(String)`: 매칭용 (REJECTED 제외)
  - `searchByKeyword(String)`: 관리용 검색
  - `findByBrandId(Long brandId)`: 특정 브랜드 aliases

### QueryDSL DTO Projection

- [ ] AliasMatchResult (Port Out에 정의됨)
  - brandId, brandCode, canonicalName, nameKo, confidence
  - `Projections.constructor()` 사용

- [ ] BrandAliasProjection (Port Out에 정의됨)
  - aliasId, brandId, originalAlias, normalizedAlias, sourceType, sellerId, mallCode, confidence, status

---

## 📦 패키지 구조

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

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙

- [ ] **Lombok 금지**: Plain Java 클래스 사용
- [ ] **Long FK 전략**: JPA 관계 어노테이션 (@OneToMany, @ManyToOne) 금지
  - ❌ `@ManyToOne private Brand brand`
  - ✅ `private Long brandId`
- [ ] **Soft Delete 전략**: 물리 삭제(DELETE) 금지, 상태 변경으로 처리
- [ ] **QueryDSL DTO Projection**: 조회 시 Entity 대신 Projection 사용

### 동시성 제어

- [ ] `@Version` 필드로 낙관적 락 적용
- [ ] Alias 검수 시 동시 변경 → OptimisticLockingFailureException → 409 Conflict

### 테스트 규칙

- [ ] ArchUnit 테스트 필수 (Adapter 의존성)
- [ ] @DataJpaTest 사용 (Repository 테스트)
- [ ] TestFixture 사용 필수
- [ ] QueryDSL 쿼리 통합 테스트
- [ ] Mapper 단위 테스트
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] Flyway 마이그레이션 스크립트 완료 (brand, brand_alias 테이블)
- [ ] JPA Entity 구현 및 테스트 완료
- [ ] JPA Repository 구현 및 테스트 완료
- [ ] QueryDSL Repository 구현 및 테스트 완료
- [ ] Mapper 구현 및 테스트 완료
- [ ] Command Adapter 구현 및 테스트 완료
- [ ] Query Adapter 구현 및 테스트 완료
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/brand-module-design.md
- Plan: docs/prd/plans/BRAND-003-persistence-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📐 TDD 순서 가이드

PRD 섹션 7의 구현 우선순위를 따름:

### Phase 1: Flyway Migration
1. brand 테이블 마이그레이션
2. brand_alias 테이블 마이그레이션

### Phase 2: JPA Entity
3. BrandJpaEntity 테스트 및 구현
4. BrandAliasJpaEntity 테스트 및 구현

### Phase 3: JPA Repository
5. BrandJpaRepository 테스트 및 구현
6. BrandAliasJpaRepository 테스트 및 구현

### Phase 4: QueryDSL Repository
7. BrandQueryDslRepository 테스트 및 구현
   - search() 메서드
   - findByNormalizedAlias() 메서드 (DTO Projection)
   - findAliasesByBrandId() 메서드
   - searchAliasesByKeyword() 메서드

### Phase 5: Mapper
8. BrandJpaEntityMapper 테스트 및 구현

### Phase 6: Adapters
9. BrandCommandAdapter 테스트 및 구현
10. BrandQueryAdapter 테스트 및 구현
11. BrandAliasQueryAdapter 테스트 및 구현
