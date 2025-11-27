# BRAND-004: REST API Layer 구현

**Epic**: Catalog - Brand 모듈
**Layer**: REST API Layer (Adapter-In)
**브랜치**: feature/BRAND-004-rest-api
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: BRAND-001, BRAND-002, BRAND-003

---

## 📝 목적

Brand 모듈의 RESTful API를 구현한다. Admin API(내부 운영용)와 Public API(외부 서비스용)를 분리하여 제공하고, Request/Response DTO, Validation, Error Handling을 포함한다.

---

## 🎯 요구사항

### Admin API - Brand 관리 (7개 엔드포인트)

- [ ] `GET /api/v1/admin/catalog/brands` - 목록 조회 (페이징)
- [ ] `GET /api/v1/admin/catalog/brands/search` - 검색
- [ ] `GET /api/v1/admin/catalog/brands/{id}` - 상세 조회
- [ ] `POST /api/v1/admin/catalog/brands` - 생성
- [ ] `PATCH /api/v1/admin/catalog/brands/{id}` - 수정
- [ ] `PATCH /api/v1/admin/catalog/brands/{id}/status` - 상태 변경
- [ ] `DELETE /api/v1/admin/catalog/brands/{id}` - 삭제 (Soft)

### Admin API - BrandAlias 관리 (7개 엔드포인트)

- [ ] `GET /api/v1/admin/catalog/brands/{brandId}/aliases` - 별칭 목록
- [ ] `POST /api/v1/admin/catalog/brands/{brandId}/aliases` - 별칭 추가
- [ ] `PATCH /api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}` - 별칭 수정
- [ ] `PATCH /api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}/confirm` - 별칭 확정
- [ ] `PATCH /api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}/reject` - 별칭 거부
- [ ] `DELETE /api/v1/admin/catalog/brands/{brandId}/aliases/{aliasId}` - 별칭 삭제
- [ ] `GET /api/v1/admin/catalog/brands/aliases/search` - 별칭 전역 검색

### Public API (5개 엔드포인트, 조회 전용)

- [ ] `GET /api/v1/catalog/brands/{id}` - 상세 조회
- [ ] `GET /api/v1/catalog/brands/by-code/{code}` - 코드로 조회
- [ ] `GET /api/v1/catalog/brands/search` - 검색 (status=ACTIVE만)
- [ ] `GET /api/v1/catalog/brands/simple-list` - 간단 목록 (셀렉트박스용)
- [ ] `GET /api/v1/catalog/brands/resolve-by-alias` - 별칭으로 브랜드 조회

### Controller (4개)

- [ ] BrandAdminCommandController
  - POST, PATCH, DELETE 엔드포인트
  - `@Valid` 적용
  - ResponseEntity<ApiResponse<T>> 반환

- [ ] BrandAdminQueryController
  - Admin용 GET 엔드포인트
  - Pageable 파라미터 처리

- [ ] BrandAliasAdminController
  - Alias 관련 모든 Admin 엔드포인트

- [ ] BrandPublicQueryController
  - Public GET 엔드포인트
  - 캐싱 전제 설계 (status=ACTIVE만 노출)

### Request DTO (5개)

- [ ] CreateBrandApiRequest
  - `@NotBlank` code (패턴 검증)
  - `@NotBlank` canonicalName
  - `@Size(max = 255)` nameKo, nameEn
  - `@NotNull` department
  - `@Size(max = 500)` officialWebsite, logoUrl

- [ ] UpdateBrandApiRequest
  - 모든 필드 Optional (Partial Update)

- [ ] ChangeBrandStatusApiRequest
  - `@NotNull` status

- [ ] AddBrandAliasApiRequest
  - `@NotBlank` aliasName
  - `@NotNull` sourceType
  - `@DecimalMin("0.0") @DecimalMax("1.0")` confidence

- [ ] UpdateAliasApiRequest
  - confidence, status 수정용

### Response DTO (5개)

- [ ] BrandApiResponse: 기본 브랜드 정보
- [ ] BrandDetailApiResponse: 상세 정보 + aliases
- [ ] BrandSimpleApiResponse: 셀렉트박스용 최소 정보
- [ ] BrandAliasApiResponse: 별칭 정보
- [ ] AliasMatchApiResponse: 별칭 매칭 결과 (후보 리스트)

### API Mapper (1개)

- [ ] BrandApiMapper
  - Request DTO → Command 변환
  - Application Response → API Response 변환

### Error Mapper (1개)

- [ ] BrandApiErrorMapper (implements ApiErrorMapper)
  - `supports(DomainException)`: Brand 관련 예외 판별
  - `map(DomainException)`: ErrorInfo 반환
    - BrandNotFoundException → 404 NOT_FOUND
    - BrandCodeDuplicateException → 409 CONFLICT
    - CanonicalNameDuplicateException → 409 CONFLICT
    - BrandBlockedException → 403 FORBIDDEN
    - BrandAliasNotFoundException → 404 NOT_FOUND
    - BrandAliasDuplicateException → 409 CONFLICT

---

## 📦 패키지 구조

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

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙

- [ ] **Lombok 금지**: Plain Java record 사용 (DTO)
- [ ] **RESTful 설계 준수**:
  - 명사 복수형 리소스명 (`/brands`, `/aliases`)
  - 적절한 HTTP Method (GET, POST, PATCH, DELETE)
  - 적절한 Status Code (200, 201, 204, 400, 404, 409)
- [ ] **@Valid 필수**: Request DTO 검증

### API 역할 구분

- [ ] **Admin API**: ROLE_ADMIN 요구, 쓰기 + 관리용 조회
- [ ] **Public API**: 읽기 전용, status=ACTIVE만 노출, 캐싱 전제

### 테스트 규칙

- [ ] ArchUnit 테스트 필수 (Controller 의존성)
- [ ] MockMvc 테스트 (Controller 단위 테스트)
- [ ] TestFixture 사용 필수
- [ ] Request Validation 테스트
- [ ] Error Handling 테스트
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Request DTO 구현 및 Validation 테스트 완료
- [ ] 모든 Response DTO 구현 완료
- [ ] BrandApiMapper 구현 및 테스트 완료
- [ ] BrandApiErrorMapper 구현 및 테스트 완료
- [ ] BrandAdminCommandController 구현 및 테스트 완료
- [ ] BrandAdminQueryController 구현 및 테스트 완료
- [ ] BrandAliasAdminController 구현 및 테스트 완료
- [ ] BrandPublicQueryController 구현 및 테스트 완료
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/brand-module-design.md
- Plan: docs/prd/plans/BRAND-004-rest-api-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📐 TDD 순서 가이드

PRD 섹션 7의 구현 우선순위를 따름:

### Phase 1: Request DTOs
1. CreateBrandApiRequest 테스트 및 구현
2. UpdateBrandApiRequest 테스트 및 구현
3. ChangeBrandStatusApiRequest 테스트 및 구현
4. AddBrandAliasApiRequest 테스트 및 구현
5. UpdateAliasApiRequest 테스트 및 구현

### Phase 2: Response DTOs
6. BrandApiResponse 구현
7. BrandDetailApiResponse 구현
8. BrandSimpleApiResponse 구현
9. BrandAliasApiResponse 구현
10. AliasMatchApiResponse 구현

### Phase 3: Mapper
11. BrandApiMapper 테스트 및 구현

### Phase 4: Error Mapper
12. BrandApiErrorMapper 테스트 및 구현

### Phase 5: Admin Controllers
13. BrandAdminCommandController 테스트 및 구현
14. BrandAdminQueryController 테스트 및 구현
15. BrandAliasAdminController 테스트 및 구현

### Phase 6: Public Controller
16. BrandPublicQueryController 테스트 및 구현

---

## 📝 API 응답 형식 예시

### 성공 응답
```json
{
  "success": true,
  "data": {
    "id": 1,
    "code": "NIKE",
    "canonicalName": "Nike",
    "nameKo": "나이키",
    "nameEn": "Nike",
    "shortName": "NIKE",
    "country": "US",
    "department": "FASHION",
    "isLuxury": false,
    "status": "ACTIVE",
    "logoUrl": "https://example.com/nike-logo.png"
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
    "type": "BRAND_NOT_FOUND",
    "title": "브랜드를 찾을 수 없습니다",
    "status": 404,
    "detail": "Brand not found: 999"
  }
}
```

### HTTP 상태 코드별 에러 응답 예시

#### 409 Conflict - 코드 중복
```json
{
  "success": false,
  "data": null,
  "error": {
    "type": "BRAND_CODE_DUPLICATE",
    "title": "브랜드 코드가 중복됩니다",
    "status": 409,
    "detail": "Brand code already exists: NIKE"
  }
}
```

#### 409 Conflict - 표준 이름 중복
```json
{
  "success": false,
  "data": null,
  "error": {
    "type": "CANONICAL_NAME_DUPLICATE",
    "title": "표준 브랜드명이 중복됩니다",
    "status": 409,
    "detail": "Canonical name already exists: Nike"
  }
}
```

#### 403 Forbidden - 차단된 브랜드
```json
{
  "success": false,
  "data": null,
  "error": {
    "type": "BRAND_BLOCKED",
    "title": "차단된 브랜드입니다",
    "status": 403,
    "detail": "Brand is blocked and cannot be used for product mapping: 123"
  }
}
```

#### 400 Bad Request - 검증 실패
```json
{
  "success": false,
  "data": null,
  "error": {
    "type": "VALIDATION_ERROR",
    "title": "요청 데이터가 유효하지 않습니다",
    "status": 400,
    "detail": "code: 브랜드 코드는 대문자로 시작해야 합니다"
  }
}
```
