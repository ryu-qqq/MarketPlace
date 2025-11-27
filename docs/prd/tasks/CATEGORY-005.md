# CATEGORY-005: Integration Test 구현

**Epic**: Catalog - Category 모듈
**Layer**: Integration Test
**브랜치**: feature/CATEGORY-005-integration
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: CATEGORY-001 ~ CATEGORY-004

---

## 📝 목적

카테고리 모듈의 E2E 통합 테스트를 구현합니다.
- 전체 플로우 검증 (API → Application → Persistence → DB)
- 비즈니스 시나리오 기반 테스트
- TestRestTemplate 기반 실제 HTTP 호출 테스트
- Flyway 마이그레이션 및 @Sql 데이터 준비

---

## 🎯 요구사항

### E2E 시나리오

#### 시나리오 1: 카테고리 트리 CRUD 전체 플로우

- [ ] **TC-001: 루트 카테고리 생성**
  - POST /api/v1/admin/catalog/categories
  - 응답 검증: 201 Created, id/code/depth(0)/path 확인
  - DB 검증: category 테이블에 레코드 생성 확인

- [ ] **TC-002: 자식 카테고리 생성**
  - POST /api/v1/admin/catalog/categories (parentId 지정)
  - 응답 검증: depth = parent.depth + 1, path = parent.path + "/" + code
  - DB 검증: 부모 isLeaf = false 업데이트 확인

- [ ] **TC-003: 카테고리 트리 조회**
  - GET /api/v1/catalog/categories/tree
  - 응답 검증: 트리 구조, 정렬 순서 확인
  - ACTIVE + visible 카테고리만 반환 검증

- [ ] **TC-004: 카테고리 수정**
  - PATCH /api/v1/admin/catalog/categories/{id}
  - 응답 검증: 200 OK, 변경된 필드 확인
  - DB 검증: updated_at 갱신 확인

- [ ] **TC-005: 카테고리 상태 변경**
  - PATCH /api/v1/admin/catalog/categories/{id}/status
  - 응답 검증: 200 OK
  - 트리 조회 검증: INACTIVE 카테고리 미포함 확인

- [ ] **TC-006: 카테고리 삭제 (Soft Delete)**
  - DELETE /api/v1/admin/catalog/categories/{id}
  - 응답 검증: 204 No Content
  - DB 검증: status = DEPRECATED 확인

#### 시나리오 2: 카테고리 이동

- [ ] **TC-007: 카테고리 이동 성공**
  - PATCH /api/v1/admin/catalog/categories/{id}/move
  - 응답 검증: 200 OK
  - DB 검증: 이동된 카테고리 + 하위 노드 path/depth 재계산 확인
  - DB 검증: 기존 부모 isLeaf = true (자식 없으면)
  - DB 검증: 새 부모 isLeaf = false

- [ ] **TC-008: Cycle 이동 방지**
  - 자기 자신의 하위로 이동 시도
  - 응답 검증: 400 Bad Request, CATEGORY_CYCLE_DETECTED

#### 시나리오 3: 코드 유니크 검증

- [ ] **TC-009: 중복 코드 생성 시도**
  - POST /api/v1/admin/catalog/categories (기존 코드와 동일)
  - 응답 검증: 409 Conflict, CATEGORY_CODE_DUPLICATE

#### 시나리오 4: 검색 및 필터링

- [ ] **TC-010: 키워드 검색**
  - GET /api/v1/catalog/categories/search?keyword=패션
  - 응답 검증: nameKo/nameEn/code/displayName 중 일치하는 결과

- [ ] **TC-011: Leaf 카테고리 필터링**
  - GET /api/v1/catalog/categories/leaf
  - 응답 검증: isLeaf = true, isListable = true, status = ACTIVE만 반환

- [ ] **TC-012: 비즈니스 분류 필터링**
  - GET /api/v1/catalog/categories/leaf?department=FASHION&productGroup=APPAREL
  - 응답 검증: 해당 분류에 속하는 카테고리만 반환

#### 시나리오 5: 경로 및 증분 조회

- [ ] **TC-013: 경로 조회 (Breadcrumb)**
  - GET /api/v1/catalog/categories/{id}/path
  - 응답 검증: 루트부터 현재까지 ancestors 배열

- [ ] **TC-014: 증분 조회**
  - GET /api/v1/catalog/categories/updated-since?since=2025-01-01T00:00:00
  - 응답 검증: since 이후 변경된 카테고리만 반환
  - 정렬 검증: updated_at ASC

#### 시나리오 6: 동시성 및 낙관적 락

- [ ] **TC-015: 낙관적 락 충돌**
  - 동일 카테고리 동시 수정 시도
  - 응답 검증: 409 Conflict (OptimisticLockException)

### 테스트 데이터 준비

- [ ] **Flyway 테스트 마이그레이션**
  - 테스트용 스키마 생성 (V999__test_data.sql)

- [ ] **@Sql 데이터 스크립트**
  - 기본 카테고리 트리 데이터
  - 다양한 상태/노출 조합 데이터

### TestFixture

- [ ] **CategoryTestFixture** 구현
  - createRootCategory(): 루트 카테고리 생성
  - createChildCategory(parentId): 자식 카테고리 생성
  - createCategoryTree(): 전체 트리 생성
  - createRequest(): API Request 생성

---

## ⚠️ 제약사항

### 테스트 규칙
- [ ] **MockMvc 금지** - TestRestTemplate 사용 필수
- [ ] **실제 HTTP 호출** - 전체 스택 통합 테스트
- [ ] **Flyway 마이그레이션** - 테스트 DB 스키마 관리
- [ ] **@Sql 데이터 준비** - 테스트 데이터 격리
- [ ] **TestFixture 사용** - 중복 코드 제거

### 테스트 격리
- [ ] 각 테스트 메서드 독립 실행 보장
- [ ] @Transactional 롤백 또는 @DirtiesContext 사용
- [ ] 테스트 순서 의존성 제거

### 성능 고려
- [ ] 테스트 실행 시간 최소화
- [ ] 불필요한 데이터 로드 방지
- [ ] 병렬 테스트 실행 가능하도록 설계

---

## ✅ 완료 조건

- [ ] 모든 E2E 시나리오 테스트 구현 완료
- [ ] Flyway 테스트 마이그레이션 작성 완료
- [ ] @Sql 데이터 스크립트 작성 완료
- [ ] TestFixture 구현 완료
- [ ] 통합 테스트 통과 (TestRestTemplate)
- [ ] 테스트 격리 확인
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/category-module-design.md
- Plan: docs/prd/plans/CATEGORY-005-integration-plan.md (create-plan 후 생성)
- Domain: docs/prd/tasks/CATEGORY-001.md
- Application: docs/prd/tasks/CATEGORY-002.md
- Persistence: docs/prd/tasks/CATEGORY-003.md
- REST API: docs/prd/tasks/CATEGORY-004.md
- Jira: (sync-to-jira 후 추가)

---

## 📦 패키지 구조

```
integration-test/
└── catalog/
    └── category/
        ├── CategoryIntegrationTest.java      # 메인 통합 테스트
        ├── CategoryTreeIntegrationTest.java  # 트리 관련 테스트
        ├── CategorySearchIntegrationTest.java # 검색 관련 테스트
        └── CategoryConcurrencyTest.java      # 동시성 테스트

test-fixtures/
└── catalog/
    └── category/
        └── CategoryTestFixture.java

test-resources/
└── sql/
    └── catalog/
        └── category/
            ├── category-tree-data.sql
            └── category-search-data.sql
```

---

## 📊 테스트 우선순위

1. 기본 CRUD 테스트 (TC-001 ~ TC-006)
2. 이동 및 Cycle 방지 테스트 (TC-007 ~ TC-008)
3. 코드 유니크 검증 테스트 (TC-009)
4. 검색 및 필터링 테스트 (TC-010 ~ TC-012)
5. 경로 및 증분 조회 테스트 (TC-013 ~ TC-014)
6. 동시성 테스트 (TC-015)

---

## 🧪 테스트 코드 예시

### TestRestTemplate 기반 E2E 테스트
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/catalog/category/category-tree-data.sql")
class CategoryIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void 루트_카테고리_생성() {
        // given
        var request = CategoryTestFixture.createRootCategoryRequest("FASHION", "패션");

        // when
        var response = restTemplate.postForEntity(
                "/api/v1/admin/catalog/categories",
                request,
                ApiResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getData().getDepth()).isEqualTo(0);
        assertThat(response.getBody().getData().getPath()).isEqualTo("/FASHION");
    }

    @Test
    void 자식_카테고리_생성시_부모_isLeaf_false_업데이트() {
        // given
        Long parentId = createRootCategory("FASHION");
        var request = CategoryTestFixture.createChildCategoryRequest(parentId, "APPAREL", "의류");

        // when
        restTemplate.postForEntity(
                "/api/v1/admin/catalog/categories",
                request,
                ApiResponse.class
        );

        // then
        var parent = findCategoryById(parentId);
        assertThat(parent.isLeaf()).isFalse();
    }

    @Test
    void Cycle_이동_방지() {
        // given
        Long parentId = createRootCategory("FASHION");
        Long childId = createChildCategory(parentId, "APPAREL");
        var moveRequest = new MoveCategoryApiRequest(childId, 0); // 자식 아래로 이동 시도

        // when
        var response = restTemplate.exchange(
                "/api/v1/admin/catalog/categories/{id}/move",
                HttpMethod.PATCH,
                new HttpEntity<>(moveRequest),
                ApiResponse.class,
                parentId
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError().getType()).isEqualTo("CATEGORY_CYCLE_DETECTED");
    }
}
```

---

## 📝 테스트 데이터 예시

### category-tree-data.sql
```sql
-- 루트 카테고리
INSERT INTO category (id, code, name_ko, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group)
VALUES (1, 'FASHION', '패션', NULL, 0, '/FASHION', 0, false, 'ACTIVE', true, true, 'FASHION', 'ETC');

-- 1단계 자식
INSERT INTO category (id, code, name_ko, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group)
VALUES (2, 'APPAREL', '의류', 1, 1, '/FASHION/APPAREL', 0, false, 'ACTIVE', true, true, 'FASHION', 'APPAREL');

INSERT INTO category (id, code, name_ko, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group)
VALUES (3, 'SHOES', '신발', 1, 1, '/FASHION/SHOES', 1, true, 'ACTIVE', true, true, 'FASHION', 'SHOES');

-- 2단계 자식
INSERT INTO category (id, code, name_ko, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group)
VALUES (4, 'TOPS', '상의', 2, 2, '/FASHION/APPAREL/TOPS', 0, true, 'ACTIVE', true, true, 'FASHION', 'APPAREL');

INSERT INTO category (id, code, name_ko, parent_id, depth, path, sort_order, is_leaf, status, is_visible, is_listable, department, product_group)
VALUES (5, 'BOTTOMS', '하의', 2, 2, '/FASHION/APPAREL/BOTTOMS', 1, true, 'ACTIVE', true, true, 'FASHION', 'APPAREL');
```
