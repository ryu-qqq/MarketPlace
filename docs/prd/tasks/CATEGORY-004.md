# CATEGORY-004: REST API Layer 구현

**Epic**: Catalog - Category 모듈
**Layer**: REST API Layer (Adapter-In)
**브랜치**: feature/CATEGORY-004-rest-api
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: CATEGORY-001 (Domain), CATEGORY-002 (Application), CATEGORY-003 (Persistence)

---

## 📝 목적

카테고리 REST API를 구현합니다.
- Admin API (관리자 전용): 쓰기 + 관리용 조회
- Public API (BFF/외부 서비스용): 읽기 전용, 캐싱 전제
- RESTful 설계 및 RFC 7807 에러 응답
- @Valid 기반 요청 검증

---

## 🎯 요구사항

### Admin API - Command (관리자 전용)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/admin/catalog/categories` | 카테고리 생성 |
| PATCH | `/api/v1/admin/catalog/categories/{id}` | 카테고리 수정 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/status` | 상태 변경 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/move` | 이동 |
| PATCH | `/api/v1/admin/catalog/categories/{id}/reorder` | 순서 변경 |
| DELETE | `/api/v1/admin/catalog/categories/{id}` | 삭제 (Soft Delete) |

### Admin API - Query (관리자 전용)

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/admin/catalog/categories/tree` | 트리 전체 조회 (상태 무관) |
| GET | `/api/v1/admin/catalog/categories/{id}` | 단일 조회 |
| GET | `/api/v1/admin/catalog/categories/search` | 검색 |

### Public API - Query (캐싱 전제)

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

### Request DTO

- [ ] **CreateCategoryApiRequest**
  - parentId (nullable)
  - code (@NotBlank, @Pattern: `^[A-Z][A-Z0-9_]{2,99}$`)
  - nameKo (@NotBlank, @Size max 255)
  - nameEn (@Size max 255)
  - sortOrder (@Min 0)
  - isListable, isVisible (boolean)
  - department (@NotNull)
  - productGroup (@NotNull)
  - genderScope, ageGroup
  - displayName, seoSlug (@Size max 255)
  - iconUrl (@Size max 500)

- [ ] **UpdateCategoryApiRequest**
  - nameKo (@NotBlank, @Size max 255)
  - nameEn (@Size max 255)
  - isListable, isVisible (boolean)
  - sortOrder (@Min 0)
  - displayName, seoSlug (@Size max 255)
  - iconUrl (@Size max 500)

- [ ] **ChangeCategoryStatusApiRequest**
  - status (@NotNull CategoryStatus)
  - replacementCategoryId (nullable)

- [ ] **MoveCategoryApiRequest**
  - newParentId (@NotNull)
  - newSortOrder (@Min 0)

### Response DTO

- [ ] **CategoryApiResponse**
  - id, code, nameKo, nameEn
  - parentId, depth, path, sortOrder, isLeaf
  - status, isVisible, isListable
  - department, productGroup, genderScope, ageGroup
  - displayName, seoSlug, iconUrl

- [ ] **CategoryTreeApiResponse**
  - roots: List<CategoryTreeNodeApiResponse>
  - CategoryTreeNodeApiResponse: category + children

- [ ] **CategoryPathApiResponse**
  - categoryId
  - ancestors: List<CategoryApiResponse>

### API Mapper

- [ ] **CategoryApiMapper** 구현
  - toCommand(request): Command DTO 변환
  - toApiResponse(response): API Response 변환
  - toTreeApiResponse(treeResponse): Tree Response 변환
  - toPathApiResponse(pathResponse): Path Response 변환

### Controller

- [ ] **CategoryAdminCommandController**
  - POST /api/v1/admin/catalog/categories (create)
  - PATCH /api/v1/admin/catalog/categories/{id} (update)
  - PATCH /api/v1/admin/catalog/categories/{id}/status (changeStatus)
  - PATCH /api/v1/admin/catalog/categories/{id}/move (move)
  - DELETE /api/v1/admin/catalog/categories/{id} (delete)

- [ ] **CategoryAdminQueryController**
  - GET /api/v1/admin/catalog/categories/tree
  - GET /api/v1/admin/catalog/categories/{id}
  - GET /api/v1/admin/catalog/categories/search

- [ ] **CategoryPublicQueryController**
  - GET /api/v1/catalog/categories/tree
  - GET /api/v1/catalog/categories/leaf
  - GET /api/v1/catalog/categories/{id}
  - GET /api/v1/catalog/categories/by-code/{code}
  - GET /api/v1/catalog/categories/{id}/path
  - GET /api/v1/catalog/categories/{id}/children
  - GET /api/v1/catalog/categories/search
  - GET /api/v1/catalog/categories/updated-since

### Error Handling (RFC 7807)

- [ ] **CategoryApiErrorMapper** 구현
  - CategoryNotFoundException → 404 NOT_FOUND
  - CategoryCodeDuplicateException → 409 CONFLICT
  - CategoryCycleDetectedException → 400 BAD_REQUEST
  - CategoryNotListableException → 400 BAD_REQUEST

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙
- [ ] **Lombok 금지** - Plain Java만 사용 (DTO 포함)
- [ ] **@Valid 필수** - 모든 Command Request에 검증
- [ ] **RESTful 설계** - HTTP 메서드 의미에 맞게 사용
- [ ] **일관된 응답 형식** - ApiResponse<T> 래퍼 사용
- [ ] **RFC 7807 에러 응답** - 표준 에러 형식

### HTTP 상태 코드
- 200 OK: 조회 성공, 수정 성공
- 201 Created: 생성 성공
- 204 No Content: 삭제 성공
- 400 Bad Request: 잘못된 요청 (Cycle 감지, 등록 불가 등)
- 404 Not Found: 리소스 미발견
- 409 Conflict: 충돌 (코드 중복)

### 테스트 규칙
- [ ] Controller 슬라이스 테스트 (@WebMvcTest + MockMvc)
- [ ] Request DTO 검증 테스트
- [ ] API 문서화 테스트 (REST Docs)
- [ ] ArchUnit 테스트로 아키텍처 규칙 검증
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Request DTO 구현 완료 (@Valid 검증 포함)
- [ ] 모든 Response DTO 구현 완료
- [ ] CategoryApiMapper 구현 완료
- [ ] CategoryAdminCommandController 구현 완료
- [ ] CategoryAdminQueryController 구현 완료
- [ ] CategoryPublicQueryController 구현 완료
- [ ] CategoryApiErrorMapper 구현 완료
- [ ] Controller 테스트 통과 (MockMvc)
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수 확인
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/category-module-design.md
- Plan: docs/prd/plans/CATEGORY-004-rest-api-plan.md (create-plan 후 생성)
- Domain: docs/prd/tasks/CATEGORY-001.md
- Application: docs/prd/tasks/CATEGORY-002.md
- Persistence: docs/prd/tasks/CATEGORY-003.md
- Jira: (sync-to-jira 후 추가)

---

## 📦 패키지 구조

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

---

## 📊 TDD 구현 우선순위

1. Request DTO 테스트 및 구현 (@Valid 검증)
2. Response DTO 테스트 및 구현
3. CategoryApiMapper 테스트 및 구현
4. CategoryApiErrorMapper 테스트 및 구현
5. CategoryAdminCommandController 테스트 및 구현
6. CategoryAdminQueryController 테스트 및 구현
7. CategoryPublicQueryController 테스트 및 구현
8. ArchUnit 테스트 작성
9. REST Docs 문서화

---

## 🔐 API 역할 구분

### Admin API
- **대상**: 내부 운영 관리자
- **인증**: ROLE_ADMIN 또는 동등 권한 필요 (게이트웨이에서 처리)
- **기능**: 쓰기(CRUD) + 관리용 조회 (전체 상태 포함)

### Public API
- **대상**: BFF, 외부 서비스, 클라이언트
- **인증**: 인증된 요청 (게이트웨이에서 처리)
- **기능**: 읽기 전용 (ACTIVE + visible만)
- **캐싱**: 캐싱 전제 설계

---

## 📝 API 응답 형식 예시

### 성공 응답
```json
{
  "success": true,
  "data": {
    "id": 1,
    "code": "FASHION",
    "nameKo": "패션",
    "nameEn": "Fashion",
    ...
  },
  "error": null
}
```

### 에러 응답 (RFC 7807)
```json
{
  "success": false,
  "data": null,
  "error": {
    "type": "CATEGORY_NOT_FOUND",
    "title": "카테고리를 찾을 수 없습니다",
    "status": 404,
    "detail": "Category not found: 999"
  }
}
```
