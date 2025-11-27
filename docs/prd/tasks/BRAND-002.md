# BRAND-002: Application Layer 구현

**Epic**: Catalog - Brand 모듈
**Layer**: Application Layer
**브랜치**: feature/BRAND-002-application
**Jira URL**: (sync-to-jira 후 추가)
**선행 작업**: BRAND-001 (Domain Layer)

---

## 📝 목적

Brand 도메인에 대한 Command/Query Use Case를 구현하고, Port In/Out 인터페이스를 정의하여 헥사고날 아키텍처의 Application Layer를 완성한다.

---

## 🎯 요구사항

### Port In - Command Use Cases (6개)

- [ ] CreateBrandUseCase
  - Input: CreateBrandCommand
  - Output: BrandResponse
  - 검증: code/canonicalName 유니크 체크

- [ ] UpdateBrandUseCase
  - Input: UpdateBrandCommand
  - Output: BrandResponse

- [ ] ChangeBrandStatusUseCase
  - Input: ChangeBrandStatusCommand
  - Output: void

- [ ] AddBrandAliasUseCase
  - Input: AddBrandAliasCommand
  - Output: BrandAliasResponse
  - 검증: 중복 alias 체크

- [ ] ConfirmBrandAliasUseCase
  - Methods: confirm(), reject()
  - Input: ConfirmBrandAliasCommand
  - Output: void

- [ ] RemoveBrandAliasUseCase
  - Input: brandId, aliasId
  - Output: void

### Port In - Query Use Cases (4개)

- [ ] GetBrandUseCase
  - Methods: getById(), getByCode()
  - Output: BrandDetailResponse

- [ ] SearchBrandUseCase
  - Methods: search(), getSimpleList()
  - Input: BrandSearchQuery, Pageable
  - Output: Page<BrandResponse>, List<BrandSimpleResponse>

- [ ] GetBrandAliasesUseCase
  - Methods: getAliases(), searchAliases()
  - Output: List<BrandAliasResponse>

- [ ] ResolveAliasUseCase
  - Method: resolveByAlias()
  - Input: aliasName (String)
  - Output: AliasMatchResponse (후보 리스트 + confidence)

### Port Out - Command (1개)

- [ ] BrandPersistencePort
  - Methods: persist(), delete(), existsByCode(), existsByCanonicalName()

### Port Out - Query (2개)

- [ ] BrandQueryPort
  - Methods: findById(), findByCode(), search(), findByIds(), findAll()

- [ ] BrandAliasQueryPort
  - Methods: findByNormalizedAlias(), searchByKeyword(), findByBrandId()
  - Projection DTOs: AliasMatchResult, BrandAliasProjection

### Command DTOs (6개)

- [ ] CreateBrandCommand: code, canonicalName, nameKo, nameEn, shortName, country, department, isLuxury, officialWebsite, logoUrl, description
- [ ] UpdateBrandCommand: brandId, nameKo, nameEn, shortName, country, department, isLuxury, officialWebsite, logoUrl, description
- [ ] ChangeBrandStatusCommand: brandId, newStatus
- [ ] AddBrandAliasCommand: brandId, aliasName, sourceType, sellerId, mallCode, confidence, status
- [ ] ConfirmBrandAliasCommand: brandId, aliasId
- [ ] UpdateAliasConfidenceCommand: brandId, aliasId, confidence

### Query DTOs (2개)

- [ ] BrandSearchQuery: keyword, status, isLuxury, department, country
- [ ] ResolveAliasQuery: aliasName

### Response DTOs (5개)

- [ ] BrandResponse: id, code, canonicalName, nameKo, nameEn, shortName, country, department, isLuxury, status, logoUrl
- [ ] BrandDetailResponse: 위 + officialWebsite, description, dataQualityLevel, dataQualityScore, aliasCount, aliases
- [ ] BrandSimpleResponse: id, code, nameKo, nameEn
- [ ] BrandAliasResponse: id, brandId, originalAlias, normalizedAlias, sourceType, sellerId, mallCode, confidence, status
- [ ] AliasMatchResponse: matches (List<AliasMatch>)

### Service 구현 (10개)

#### Command Services (6개)
- [ ] CreateBrandService: 유니크 검증 → 브랜드 생성 → 메타 설정 → 저장
- [ ] UpdateBrandService: 브랜드 조회 → 수정 → 저장
- [ ] ChangeBrandStatusService: 브랜드 조회 → 상태 변경 → 저장
- [ ] AddBrandAliasService: 브랜드 조회 → alias 생성 (Brand 통해서) → 저장
- [ ] ConfirmBrandAliasService: 브랜드 조회 → confirm/reject → 저장
- [ ] RemoveBrandAliasService: 브랜드 조회 → alias 제거 → 저장

#### Query Services (4개)
- [ ] GetBrandService: 단건 조회 (by id, by code)
- [ ] SearchBrandService: 목록 검색, 간단 목록
- [ ] GetBrandAliasesService: 특정 브랜드 alias 목록, 전역 검색
- [ ] ResolveAliasService: 정규화 → 매칭 조회 → 후보 리스트 반환

### Assembler (1개)

- [ ] BrandAssembler
  - toResponse(Brand): BrandResponse
  - toDetailResponse(Brand): BrandDetailResponse
  - toSimpleResponse(Brand): BrandSimpleResponse
  - toAliasResponse(BrandAlias): BrandAliasResponse

---

## 📦 패키지 구조

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

---

## ⚠️ 제약사항

### Zero-Tolerance 규칙

- [ ] **Lombok 금지**: Plain Java record 사용 (DTO)
- [ ] **@Transactional 경계 준수**:
  - Command Service에만 `@Transactional` 적용
  - `@Transactional` 내 외부 API 호출 절대 금지
- [ ] **CQRS 분리**: Command/Query Service 명확히 분리
- [ ] **Assembler 사용 필수**: Domain → Response 변환 시

### Spring 프록시 제약사항

- [ ] Private 메서드에 `@Transactional` 금지
- [ ] Final 클래스/메서드에 `@Transactional` 금지
- [ ] 같은 클래스 내부 호출(`this.method()`)에서 `@Transactional` 작동 안함

### 테스트 규칙

- [ ] ArchUnit 테스트 필수 (Port/Service 의존성)
- [ ] TestFixture 사용 필수
- [ ] Service 단위 테스트 (Mock Port)
- [ ] Assembler 단위 테스트
- [ ] 테스트 커버리지 > 80%

---

## ✅ 완료 조건

- [ ] 모든 Port 인터페이스 정의 완료
- [ ] 모든 Command/Query DTO 구현 완료
- [ ] 모든 Response DTO 구현 완료
- [ ] BrandAssembler 구현 및 테스트 완료
- [ ] 모든 Command Service 구현 및 테스트 완료
- [ ] 모든 Query Service 구현 및 테스트 완료
- [ ] ArchUnit 테스트 통과
- [ ] Zero-Tolerance 규칙 준수
- [ ] 코드 리뷰 승인
- [ ] PR 머지 완료

---

## 🔗 관련 문서

- PRD: docs/prd/brand-module-design.md
- Plan: docs/prd/plans/BRAND-002-application-plan.md (create-plan 후 생성)
- Jira: (sync-to-jira 후 추가)

---

## 📐 TDD 순서 가이드

PRD 섹션 7의 구현 우선순위를 따름:

### Phase 1: Assembler
1. BrandAssembler 테스트 및 구현

### Phase 2: DTOs
2. Command DTOs (record)
3. Query DTOs (record)
4. Response DTOs (record)

### Phase 3: Ports
5. Port In - Command Use Cases
6. Port In - Query Use Cases
7. Port Out - Command Port
8. Port Out - Query Ports

### Phase 4: Command Services
9. CreateBrandService
10. UpdateBrandService
11. ChangeBrandStatusService
12. AddBrandAliasService
13. ConfirmBrandAliasService
14. RemoveBrandAliasService

### Phase 5: Query Services
15. GetBrandService
16. SearchBrandService
17. GetBrandAliasesService
18. ResolveAliasService
