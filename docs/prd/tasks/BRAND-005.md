# BRAND-005: Integration Test 구현

**Epic**: Catalog - Brand 모듈
**Layer**: Integration Test (E2E)
**브랜치**: feature/BRAND-005-integration
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: BRAND-001, BRAND-002, BRAND-003, BRAND-004

---

## 📝 목적

Brand 모듈의 전체 레이어를 아우르는 E2E 통합 테스트를 구현한다. 실제 HTTP 요청을 통해 Controller → Service → Repository → Database 전체 플로우를 검증한다.

---

## 🎯 요구사항

### E2E 시나리오 - Brand CRUD

- [ ] **Brand 생성 → 조회 → 수정 → 삭제 전체 플로우**
  1. POST `/api/v1/admin/catalog/brands` - 브랜드 생성
  2. GET `/api/v1/admin/catalog/brands/{id}` - 생성된 브랜드 조회
  3. PATCH `/api/v1/admin/catalog/brands/{id}` - 브랜드 수정
  4. GET `/api/v1/admin/catalog/brands/{id}` - 수정 확인
  5. DELETE `/api/v1/admin/catalog/brands/{id}` - 브랜드 삭제 (Soft)
  6. GET `/api/v1/admin/catalog/brands/{id}` - status=INACTIVE 확인

- [ ] **검색 및 페이징 테스트**
  1. 여러 브랜드 생성 (다양한 department, isLuxury, status)
  2. GET `/api/v1/admin/catalog/brands?keyword=NIKE` - 키워드 검색
  3. GET `/api/v1/admin/catalog/brands?isLuxury=true` - 럭셔리 필터
  4. GET `/api/v1/admin/catalog/brands?page=0&size=10` - 페이징

### E2E 시나리오 - BrandAlias 관리

- [ ] **Alias 추가 → 확정 → 삭제 플로우**
  1. POST `/api/v1/admin/catalog/brands/{brandId}/aliases` - 별칭 추가
  2. GET `/api/v1/admin/catalog/brands/{brandId}/aliases` - 별칭 목록 확인
  3. PATCH `.../aliases/{aliasId}/confirm` - 별칭 확정
  4. GET `.../aliases` - status=CONFIRMED 확인
  5. DELETE `.../aliases/{aliasId}` - 별칭 삭제

- [ ] **resolve-by-alias 테스트**
  1. 브랜드 생성
  2. 다양한 alias 추가 (`NIKE`, `나이키`, `N I K E`)
  3. GET `/api/v1/catalog/brands/resolve-by-alias?aliasName=나이키` - 정규화 후 매칭
  4. 반환된 후보 리스트 검증

### E2E 시나리오 - 유효성 검증

- [ ] **code 중복 검증**
  1. 브랜드 생성 (code=NIKE)
  2. 동일 code로 재생성 시도
  3. 409 CONFLICT 응답 확인

- [ ] **canonicalName 중복 검증**
  1. 브랜드 생성 (canonicalName=Nike)
  2. 동일 canonicalName으로 재생성 시도
  3. 409 CONFLICT 응답 확인

- [ ] **alias 중복 검증 (scope 기준)**
  1. 브랜드에 alias 추가 (aliasName=NIKE, mallCode=GLOBAL)
  2. 동일 scope로 재추가 시도
  3. 409 CONFLICT 응답 확인

### E2E 시나리오 - 상태 관리

- [ ] **Brand 상태 변경 테스트**
  1. 브랜드 생성 (status=ACTIVE)
  2. PATCH `.../status` - BLOCKED로 변경
  3. GET 확인 - status=BLOCKED
  4. PATCH `.../status` - ACTIVE로 복원

- [ ] **BLOCKED 브랜드 제약 테스트**
  1. 브랜드 BLOCKED 상태로 변경
  2. (상품 매핑 시뮬레이션) - 403 FORBIDDEN 확인

### E2E 시나리오 - Public API

- [ ] **Public API는 ACTIVE만 노출 확인**
  1. ACTIVE, INACTIVE, BLOCKED 브랜드 각각 생성
  2. GET `/api/v1/catalog/brands/search` - ACTIVE만 노출 확인
  3. GET `/api/v1/catalog/brands/simple-list` - ACTIVE만 노출 확인

### E2E 시나리오 - 동시성 제어

- [ ] **낙관적 락 테스트**
  1. 브랜드 조회 (version=0)
  2. 동시에 두 클라이언트에서 수정 시도
  3. 하나는 성공, 하나는 409 CONFLICT 확인

### 테스트 데이터 관리

- [ ] Flyway 마이그레이션으로 스키마 준비
- [ ] `@Sql` 어노테이션으로 테스트 데이터 준비
- [ ] 각 테스트 후 데이터 정리 (Transactional Rollback)

---

## 📦 테스트 구조

```
integration-test/
└── catalog/
    └── brand/
        ├── BrandCrudIntegrationTest.java
        ├── BrandSearchIntegrationTest.java
        ├── BrandAliasIntegrationTest.java
        ├── BrandValidationIntegrationTest.java
        ├── BrandStatusIntegrationTest.java
        ├── BrandPublicApiIntegrationTest.java
        ├── BrandConcurrencyIntegrationTest.java
        └── fixture/
            └── BrandIntegrationTestFixture.java
```

---

## ⚠️ 제약사항

### 테스트 규칙

- [ ] **TestRestTemplate 필수** (MockMvc 금지)
- [ ] **Flyway 마이그레이션 사용** (테스트 DB 스키마)
- [ ] **@Sql로 테스트 데이터 준비**
- [ ] **실제 HTTP 요청** (localhost:random-port)
- [ ] **TestFixture 사용 필수**

### 테스트 환경

- [ ] `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)`
- [ ] 테스트용 application-test.yml 설정
- [ ] H2 또는 TestContainers (MySQL)

### 테스트 격리

- [ ] 각 테스트는 독립적으로 실행 가능
- [ ] 테스트 간 데이터 의존성 없음
- [ ] `@Transactional` 또는 명시적 정리

---

## ✅ 완료 조건

- [ ] Brand CRUD E2E 테스트 완료
- [ ] Brand 검색/페이징 E2E 테스트 완료
- [ ] BrandAlias E2E 테스트 완료
- [ ] 유효성 검증 E2E 테스트 완료
- [ ] 상태 관리 E2E 테스트 완료
- [ ] Public API E2E 테스트 완료
- [ ] 동시성 제어 E2E 테스트 완료
- [ ] 모든 E2E 테스트 통과
- [ ] 테스트 커버리지 > 80%
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/brand-module-design.md
- Plan: docs/prd/plans/BRAND-005-integration-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📐 TDD 순서 가이드

PRD 섹션 7의 구현 우선순위를 따름:

### Phase 1: 테스트 인프라 설정
1. 테스트 환경 설정 (application-test.yml)
2. TestFixture 준비
3. 테스트용 SQL 스크립트 준비

### Phase 2: Brand CRUD 테스트
4. BrandCrudIntegrationTest - 생성/조회/수정/삭제

### Phase 3: Brand 검색 테스트
5. BrandSearchIntegrationTest - 검색/필터/페이징

### Phase 4: BrandAlias 테스트
6. BrandAliasIntegrationTest - 추가/확정/거부/삭제/매칭

### Phase 5: 유효성 검증 테스트
7. BrandValidationIntegrationTest - 중복 검증, 형식 검증

### Phase 6: 상태 관리 테스트
8. BrandStatusIntegrationTest - 상태 변경, 제약 확인

### Phase 7: Public API 테스트
9. BrandPublicApiIntegrationTest - ACTIVE만 노출 확인

### Phase 8: 동시성 테스트
10. BrandConcurrencyIntegrationTest - 낙관적 락 테스트
