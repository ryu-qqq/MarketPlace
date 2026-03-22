# 레거시 레이어 격리 리팩토링 계획

## 1. 문제

현재 레거시 개념이 adapter-in부터 adapter-out까지 **전 레이어를 관통**하고 있습니다.

```
현재 (레거시가 전 레이어에 침투):

rest-api-legacy (adapter-in)
  → LegacyRegisterProductGroupCommand           ← 레거시 DTO
  → LegacyProductGroupFullRegisterUseCase        ← 레거시 UseCase
  → LegacyProductRegistrationCoordinator         ← 레거시 Coordinator
  → LegacyProductGroupCommandPort                ← 레거시 Port
  → LegacyProductGroupCommandAdapter             ← 레거시 Adapter
  → luxurydb
```

**문제점:**
- application 레이어에 legacy 패키지만 **125+ 파일**
- Step 2(신 스키마 전환) 시 application 레이어까지 전면 수정 필요
- 동일 기능의 UseCase가 **표준/레거시 두 벌** 존재 (중복)
- 레거시 제거 시 삭제 범위가 너무 넓음

## 2. 목표 구조

```
목표 (레거시는 양쪽 어댑터에만 존재):

rest-api-legacy (adapter-in) ← 레거시 포맷 변환
  → LegacyCreateProductGroupRequest
  → Mapper: legacy request → RegisterProductGroupCommand (표준 커맨드)

application ← 레거시 개념 없음
  → RegisterProductGroupFullUseCase (표준, 이미 존재)
  → FullProductGroupRegistrationCoordinator (표준, 이미 존재)
  → ProductGroupCommandPort (표준, 이미 존재)

adapter-out/persistence-mysql-legacy ← 레거시 스키마 변환
  → LegacyProductGroupCommandAdapter
  → 표준 도메인 객체 → legacy 스키마 변환 → luxurydb 저장
```

**핵심 원칙:**
- `rest-api-legacy`: legacy 요청 포맷 → 표준 도메인 커맨드 변환
- `application`: 표준 UseCase/Coordinator만 사용 (레거시 개념 없음)
- `persistence-mysql-legacy`: 표준 Port 구현체가 luxurydb에 저장

## 3. 현재 상태 분석

### 좋은 소식: 표준 UseCase가 이미 전부 존재합니다

| 기능 | 레거시 UseCase | 표준 UseCase | 상태 |
|------|-------------|-------------|------|
| 상품그룹 등록 | LegacyProductGroupFullRegisterUseCase | RegisterProductGroupFullUseCase | ✅ 양쪽 존재 |
| 상품그룹 수정 | LegacyProductGroupFullUpdateUseCase | UpdateProductGroupFullUseCase | ✅ 양쪽 존재 |
| 상품그룹 조회 | LegacyProductQueryUseCase | GetProductGroupUseCase | ✅ 양쪽 존재 |
| 상품그룹 목록 | LegacySearchProductGroupByOffsetUseCase | SearchProductGroupByOffsetUseCase | ✅ 양쪽 존재 |
| 재고 수정 | LegacyProductUpdateStockUseCase | UpdateProductStockUseCase | ✅ 양쪽 존재 |
| 이미지 수정 | LegacyProductUpdateImagesUseCase | (productgroupimage 패키지) | ✅ 양쪽 존재 |
| 고시정보 수정 | LegacyProductUpdateNoticeUseCase | (productnotice 패키지) | ✅ 양쪽 존재 |
| 상세설명 수정 | LegacyProductUpdateDescriptionUseCase | (productgroupdescription 패키지) | ✅ 양쪽 존재 |
| 주문 조회 | LegacyOrderQueryUseCase | GetOrderDetailUseCase | ✅ 양쪽 존재 |
| 주문 수정 | LegacyOrderUpdateUseCase | (order 패키지) | ✅ 양쪽 존재 |
| 셀러 조회 | LegacyGetCurrentSellerUseCase | **이미 표준 Manager 사용** | ✅ 통합 완료 |

### legacyconversion이 이미 증명한 패턴

`legacyconversion` 패키지가 이미 **표준 Coordinator를 직접 호출**하는 구조입니다:

```
LegacyConversionCoordinator.convert(outbox)
  → luxurydb에서 조회 (LegacyProductGroupReadFacade)
  → 표준 커맨드로 변환 (LegacyToInternalBundleFactory)
  → 표준 Coordinator 호출 (FullProductGroupRegistrationCoordinator)
```

이것이 **리팩토링 후 rest-api-legacy가 해야 할 패턴과 동일**합니다.

## 4. 리팩토링 계획

### Phase A: rest-api-legacy 매퍼 변경 (adapter-in)

**현재:**
```java
// LegacyProductGroupCommandController.java
@PostMapping
public ResponseEntity<?> registerProductGroupFull(
        @RequestBody LegacyCreateProductGroupRequest request) {

    LegacyRegisterProductGroupCommand command = mapper.toCommand(request);  // 레거시 커맨드
    legacyRegisterUseCase.execute(command);  // 레거시 UseCase 호출
}
```

**변경 후:**
```java
// LegacyProductGroupCommandController.java
@PostMapping
public ResponseEntity<?> registerProductGroupFull(
        @RequestBody LegacyCreateProductGroupRequest request) {

    RegisterProductGroupCommand command = legacyMapper.toStandardCommand(request);  // 표준 커맨드로 변환
    registerUseCase.execute(command);  // 표준 UseCase 호출
}
```

**작업 내용:**
- `LegacyInboundApiMapper` 수정: legacy request → 표준 `RegisterProductGroupCommand` 변환
- Controller에서 표준 UseCase 주입
- 응답 변환: 표준 result → legacy response 포맷 매핑

### Phase B: persistence-mysql-legacy 어댑터 변경 (adapter-out)

**현재:**
```java
// LegacyProductGroupCommandAdapter.java
public class LegacyProductGroupCommandAdapter implements LegacyProductGroupCommandPort {
    // LegacyProductGroupEntity를 직접 생성하여 luxurydb에 저장
}
```

**변경 후:**
```java
// LegacyProductGroupCommandAdapter.java
public class LegacyProductGroupCommandAdapter implements ProductGroupCommandPort {  // 표준 Port 구현!
    // 표준 도메인 객체를 받아서 → LegacyProductGroupEntity로 변환 → luxurydb에 저장
}
```

**작업 내용:**
- 기존 `LegacyXxxCommandPort` 인터페이스 제거
- 표준 `ProductGroupCommandPort` 등을 구현
- 내부에서 도메인 객체 → legacy 엔티티 변환 로직 구현
- Profile/ConditionalOnProperty로 표준 어댑터와 교체 가능

### Phase C: application legacy 패키지 제거

Phase A, B 완료 후:
- `application/legacy/*` 삭제 (125+ 파일)
- `application/legacyconversion/*`은 유지 (Outbox 변환은 여전히 필요)
- `application/legacyauth/*`은 유지 (인증 체계가 다르므로)

### Phase D: 어댑터 교체 가능 구조 확인

```yaml
# application.yml
persistence:
  product:
    adapter: legacy    # legacy | standard
  order:
    adapter: legacy    # legacy | standard
```

```java
// Profile 기반 어댑터 교체
@Component
@ConditionalOnProperty(name = "persistence.product.adapter", havingValue = "legacy")
public class LegacyProductGroupCommandAdapter implements ProductGroupCommandPort { ... }

@Component
@ConditionalOnProperty(name = "persistence.product.adapter", havingValue = "standard")
public class JpaProductGroupCommandAdapter implements ProductGroupCommandPort { ... }
```

이 구조가 되면 **Step 2(신 스키마 전환)는 설정 변경만으로 완료**됩니다.

## 5. 도메인별 상세 작업

### 5.1 상품그룹 (productgroup)

| 변경 대상 | 현재 | 변경 후 |
|----------|------|---------|
| Controller | `LegacyProductGroupFullRegisterUseCase` 호출 | `RegisterProductGroupFullUseCase` 호출 |
| Mapper | legacy request → `LegacyRegisterProductGroupCommand` | legacy request → `RegisterProductGroupCommand` |
| 응답 Mapper | 없음 (레거시 result 직접 반환) | 표준 result → legacy response 변환 |
| application | `LegacyProductRegistrationCoordinator` | 삭제 (표준 `FullProductGroupRegistrationCoordinator` 사용) |
| adapter-out | `LegacyProductGroupCommandPort` 구현 | `ProductGroupCommandPort` 구현 (luxurydb 저장) |

### 5.2 주문 (order)

| 변경 대상 | 현재 | 변경 후 |
|----------|------|---------|
| Controller | `LegacyOrderQueryUseCase` 호출 | `GetOrderDetailUseCase` 호출 |
| Mapper | legacy params → `LegacyOrderSearchParams` | legacy params → 표준 query params |
| 응답 Mapper | 없음 | 표준 result → legacy response 변환 |
| application | `LegacyOrderReadManager` | 삭제 (표준 `OrderReadManager` 사용) |
| adapter-out | `LegacyOrderQueryPort` 구현 | `OrderQueryPort` 구현 (luxurydb 조회) |

### 5.3 인증 (auth) — 유지

레거시 인증은 HS256 JWT + BCrypt로, 표준 인증(AuthHub SDK)과 완전히 다릅니다.
`legacyauth` 패키지는 **그대로 유지**합니다.
레거시 서버가 제거되는 시점에 함께 제거됩니다.

### 5.4 셀러 (seller) — 이미 완료

`legacyseller`는 이미 표준 `SellerCompositionReadManager`를 사용 중입니다.
리팩토링 시 이 UseCase만 제거하고, Controller에서 직접 표준 UseCase를 호출하면 됩니다.

### 5.5 변환 (conversion) — 유지

`legacyconversion`은 Outbox 기반 비동기 변환입니다.
luxurydb → 신 스키마 동기화가 필요한 동안 유지됩니다.
Step 2 Phase 2 (Dual Write) 시점에 비활성화됩니다.

## 6. 리팩토링 후 패키지 구조

```
application/
  ├── productgroup/           ← 표준 (레거시 + 신 API 모두 이 UseCase 사용)
  ├── product/                ← 표준
  ├── order/                  ← 표준
  ├── seller/                 ← 표준
  ├── productgroupimage/      ← 표준
  ├── productnotice/          ← 표준
  ├── productgroupdescription/← 표준
  ├── legacyauth/             ← 유지 (인증 체계 자체가 다름)
  ├── legacyconversion/       ← 유지 (Outbox 동기화)
  ├── legacycommoncode/       ← 유지 (택배사 코드 등)
  └── legacy/ ← 삭제 (125 파일)
      legacyseller/ ← 삭제 (3 파일)
      legacyshipment/ ← 삭제 (4 파일)

adapter-in/rest-api-legacy/
  └── 변경: 표준 UseCase 호출 + legacy ↔ 표준 매퍼

adapter-out/persistence-mysql-legacy/
  └── 변경: 표준 Port 구현 (도메인 → legacy 스키마 변환)
```

**삭제: ~132 파일, 유지: ~73 파일, 신규: 매퍼 ~10 파일**

## 7. 리팩토링 순서

```
Phase A (adapter-in):
  1. 상품그룹 Controller → 표준 UseCase 호출로 변경
  2. Mapper: legacy request → 표준 Command 변환
  3. 응답 Mapper: 표준 Result → legacy Response 변환
  4. 주문, 셀러도 동일하게 변경

Phase B (adapter-out):
  1. LegacyProductGroupCommandAdapter → ProductGroupCommandPort 구현으로 변경
  2. LegacyProductGroupQueryAdapter → ProductGroupQueryPort 구현으로 변경
  3. 주문도 동일하게 변경
  4. Profile 기반 어댑터 교체 설정 추가

Phase C (application 정리):
  1. application/legacy/* 삭제 (Phase A, B 완료 후)
  2. application/legacyseller/* 삭제
  3. application/legacyshipment/* 삭제
  4. 테스트 코드 정리

Phase D (검증):
  1. 전체 빌드 확인
  2. 레거시 API 엔드포인트 동작 검증
  3. Shadow 테스트 케이스 실행
```

## 8. 주의 사항

### 8.1 legacyconversion과의 관계

현재 `legacyconversion`이 이미 **표준 Coordinator를 호출**하고 있으므로,
리팩토링 후에도 `legacyconversion`은 영향 없이 동작합니다.

### 8.2 Outbox 생성 로직

현재 레거시 UseCase에서 `LegacyConversionOutboxCommandManager`를 호출하여 Outbox를 생성합니다.
리팩토링 후에는 **표준 UseCase에서 Outbox를 생성하지 않으므로**,
`persistence-mysql-legacy` 어댑터 내부에서 Outbox 생성을 처리하거나,
AOP/이벤트 리스너로 분리해야 합니다.

### 8.3 ID 매핑

레거시 API 응답에는 legacy ID를 반환해야 합니다.
표준 UseCase가 반환하는 new ID를 legacy ID로 역변환하는 로직이 필요합니다.
현재 ID 매핑 테이블이 존재하므로, 응답 매퍼에서 처리합니다.

### 8.4 테스트 영향

- `rest-api-legacy` 테스트: 매퍼 변경에 따라 수정 필요
- `application/legacy` 테스트: 삭제
- `persistence-mysql-legacy` 테스트: Port 인터페이스 변경에 따라 수정

## 9. 이 리팩토링이 Step 2에 미치는 영향

| Step 2 Phase | 리팩토링 전 | 리팩토링 후 |
|-------------|-----------|-----------|
| Phase 1 (읽기 전환) | application 레이어 수정 필요 | **adapter-out 설정만 변경** |
| Phase 2 (Dual Write) | application에 Dual Write 로직 추가 | **adapter-out에만 Dual Write 로직** |
| Phase 3 (쓰기 전환) | application에서 legacy 제거 | **adapter-out 설정만 변경** |
| 최종 정리 | 125+ 파일 삭제 + application 수정 | **persistence-mysql-legacy 모듈만 삭제** |
