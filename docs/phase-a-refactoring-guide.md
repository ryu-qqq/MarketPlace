# Phase A 레거시 레이어 격리 리팩토링 가이드

## 목적

레거시 API가 application 레이어의 표준 UseCase를 통하되, adapter-out에서 레거시 스키마를 조회하는 구조로 전환한다.
이 가이드를 따르면 모든 도메인에 동일한 패턴을 적용할 수 있다.

---

## 아키텍처 패턴

```
adapter-in/rest-api-legacy (레거시 요청 포맷 ↔ 표준 객체 변환)
  Controller
    → LegacyAuthContextHolder.getSellerId()          ← JWT claims에서 추출
    → LegacyXxxUseCase.execute(sellerId, ...)         ← port/in (표준 결과 반환)
    → LegacyXxxApiMapper.toResponse(standardResult)   ← 표준 결과 → 레거시 응답 변환

application/legacyXxx (레거시 개념은 "어디서 데이터를 가져오느냐"만 다름)
  LegacyXxxUseCase (port/in)                         ← 인터페이스, 표준 결과 타입 반환
  LegacyXxxService                                   ← 구현체
    → LegacyXxxReadManager / LegacyXxxCommandManager  ← 매니저
      → LegacyXxxQueryPort / LegacyXxxCommandPort     ← port/out 인터페이스

adapter-out/persistence-mysql-legacy (레거시 테이블 ↔ 표준 객체 변환)
  LegacyXxxAdapter implements LegacyXxxQueryPort
    → luxurydb 레거시 테이블 조회
    → 표준 결과 객체로 변환하여 반환
```

---

## 핵심 원칙

### 1. Controller는 반드시 UseCase(port/in)를 통해 호출
```java
// ✅ 올바름
private final LegacyGetCurrentSellerUseCase useCase;
SellerAdminCompositeResult result = useCase.execute(sellerId);

// ❌ 금지 — Manager 직접 호출
private final SellerCompositionReadManager manager;
SellerAdminCompositeResult result = manager.getAdminComposite(sellerId);
```

### 2. application 레이어에 레거시 전용 DTO 없음 — 표준 결과 타입 사용
```java
// ✅ 올바름 — 표준 결과 타입 반환
public interface LegacyGetCurrentSellerUseCase {
    SellerAdminCompositeResult execute(long sellerId);
}

// ❌ 금지 — 레거시 전용 DTO 반환
public interface LegacyGetCurrentSellerUseCase {
    LegacySellerResult execute(String authTenantId);
}
```

### 3. 레거시 개념은 adapter-out에만 존재 — 표준 객체로 변환하여 반환
```java
// adapter-out/persistence-mysql-legacy
@Component
public class LegacySellerCompositionQueryAdapter implements LegacySellerCompositionQueryPort {
    // luxurydb 레거시 테이블 조회
    // → SellerAdminCompositeResult (표준 객체)로 변환하여 반환
}
```

### 4. adapter-in의 Mapper가 표준 → 레거시 응답 포맷 변환
```java
// adapter-in/rest-api-legacy
public LegacySellerResponse toSellerResponse(SellerAdminCompositeResult result) {
    return new LegacySellerResponse(
            result.seller().id(),
            result.seller().sellerName(),
            result.businessInfo().registrationNumber());
}
```

### 5. 인증은 LegacyAuthContextHolder 사용
```java
// JWT claims에서 추출된 sellerId 사용
long sellerId = LegacyAuthContextHolder.getSellerId();
String email = LegacyAuthContextHolder.getEmail();
```

---

## 미래 전환 시나리오

```
현재 (레거시 스키마):
  LegacyXxxReadManager
    → LegacyXxxQueryPort
    → luxurydb 직접 조회 → 표준 결과 객체

전환 시 (새 스키마):
  LegacyXxxReadManager
    → LegacyIdResolver (legacyId → newId 매핑 테이블 조회)
    → 표준 ReadManager/QueryPort 호출 (새 스키마)
    → 표준 결과 객체

* UseCase, Controller, Mapper는 변경 없음
* Manager 내부 구현만 교체
```

---

## 도메인별 작업 체크리스트

각 도메인에 대해 아래 순서로 작업한다.

### Phase A-1: adapter-in/rest-api-legacy 변경

- [ ] Controller: `LegacyAuthContextHolder.getSellerId()` 사용
- [ ] Controller: `LegacyXxxUseCase`(port/in) 호출 (Manager 직접 호출 금지)
- [ ] Controller: `@PreAuthorize("isAuthenticated()")` 사용 (AuthHub `@RequirePermission` 제거)
- [ ] Mapper: 표준 결과 객체 → 레거시 응답 DTO 변환
- [ ] Endpoints 클래스: 변경 없음 (유지)
- [ ] ErrorMapper: 변경 없음 (유지)
- [ ] Request DTO: 변경 없음 (유지)
- [ ] Response DTO: 변경 없음 (유지)

### Phase A-2: application/legacyXxx 변경

- [ ] UseCase(port/in): 입력을 sellerId(long) 등으로 변경, 반환을 표준 결과 타입으로 변경
- [ ] Port(port/out): `LegacyXxxQueryPort` / `LegacyXxxCommandPort` 인터페이스 생성
- [ ] Manager: `LegacyXxxReadManager` / `LegacyXxxCommandManager` 생성, port/out 의존
- [ ] Service: Manager 호출로 변경 (표준 Manager 직접 호출 금지)
- [ ] 레거시 전용 DTO: 삭제 (표준 결과 타입 사용)

### Phase B: adapter-out/persistence-mysql-legacy 구현 (별도 작업)

- [ ] Adapter: `LegacyXxxQueryPort` 구현체 — luxurydb 조회 → 표준 결과 객체 반환
- [ ] QueryDsl Repository: 레거시 테이블 조인 쿼리
- [ ] Mapper: 레거시 QueryDto → 표준 결과 객체 변환

---

## 도메인별 현황 및 매핑

### 리팩토링 제외 (유지)
| 도메인 | 사유 |
|--------|------|
| auth | 인증 체계 자체가 다름 (HS256 JWT + BCrypt). 레거시 서버 제거 시 함께 제거 |
| qna | 아직 미구현. 새로 개발할 예정 |

### 리팩토링 대상

| 도메인 | 레거시 UseCase | 표준 결과 타입 | 난이도 |
|--------|--------------|--------------|--------|
| seller | LegacyGetCurrentSellerUseCase | SellerAdminCompositeResult | ✅ 완료 |
| shipment | LegacyGetShipmentCompanyCodesUseCase | 표준 ShipmentCompanyCode 결과 | ⭐ |
| session | LegacyGetPresignedUrlUseCase | 표준 PresignedUrl 결과 | ⭐ |
| description | LegacyProductUpdateDescriptionUseCase | 표준 커맨드 사용 | ⭐ |
| image | LegacyProductUpdateImagesUseCase | 표준 커맨드 사용 | ⭐ |
| notice | LegacyProductUpdateNoticeUseCase | 표준 커맨드 사용 | ⭐ |
| order | 3개 UseCase (조회/목록/수정) | 표준 Order 결과 | ⭐⭐ |
| productgroup | 6개 UseCase (등록/수정/진열/품절/조회/목록) | 표준 ProductGroup 결과 | ⭐⭐⭐ |

### Seller 완료 예시 (참고용)

```
adapter-in/rest-api-legacy/
  seller/controller/LegacySellerController.java        ← UseCase 호출 + LegacyAuthContextHolder
  seller/mapper/LegacySellerQueryApiMapper.java        ← SellerAdminCompositeResult → LegacySellerResponse
  seller/dto/response/LegacySellerResponse.java        ← 유지 (레거시 응답 포맷)

application/legacyseller/
  port/in/LegacyGetCurrentSellerUseCase.java           ← execute(long sellerId) : SellerAdminCompositeResult
  port/out/LegacySellerCompositionQueryPort.java       ← findAdminCompositeById(long sellerId)
  manager/LegacySellerCompositionReadManager.java      ← port/out 호출 + 예외 처리
  service/LegacyGetCurrentSellerService.java           ← Manager 위임

삭제:
  application/legacyseller/dto/response/LegacySellerResult.java  ← 레거시 전용 DTO 제거
```

---

## 인증 구조 (공통)

레거시 JWT 인증은 `common/security/` 패키지에서 처리한다.

```
common/security/
  LegacyAuthContext.java              ← record(sellerId, email, roleType)
  LegacyAuthContextHolder.java        ← ThreadLocal 홀더 (getSellerId, getEmail, getRoleType)
  LegacyJwtAuthenticationFilter.java  ← JWT 서명 검증 → claims 추출 → context 세팅 (DB 조회 없음)
```

**인증 흐름:**
```
1. POST /auth/authentication → email/password 로그인 → JWT 발급 (claims: email, sellerId, role)
2. 이후 요청 → Authorization: Bearer <token>
   → Filter: HS256 서명 검증 → claims에서 email, sellerId, role 추출
   → LegacyAuthContextHolder에 세팅 (DB 조회 없음)
   → Controller: LegacyAuthContextHolder.getSellerId()
```
