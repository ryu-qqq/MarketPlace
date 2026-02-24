# InboundProduct 파이프라인 리팩토링 설계서

> 작성일: 2026-02-23
> 상태: Draft
> 상위 문서: [product-integration-migration-plan.md](./product-integration-migration-plan.md)
> 관련 브랜치: feature/product-integration

---

## 1. 현재 상태 분석

### 1.1 ReceiveInboundProductService 현재 구조

```java
// application/.../inboundproduct/service/command/ReceiveInboundProductService.java
@Service
public class ReceiveInboundProductService implements ReceiveInboundProductUseCase {

    public InboundProductConversionResult execute(ReceiveInboundProductCommand command) {
        Optional<InboundProduct> existing =
            readManager.findByInboundSourceIdAndProductCode(
                command.inboundSourceId(), command.externalProductCode());

        if (existing.isPresent()) {
            return updateCoordinator.update(existing.get(), command);  // ← 업데이트 경로
        }
        return registerCoordinator.register(command);                  // ← 등록 경로
    }
}
```

**현재 문제**: 하나의 서비스가 등록과 업데이트를 모두 처리하고 있음.

### 1.2 등록 흐름 (InboundProductRegisterCoordinator)

```
RegisterCoordinator.register(command)
  ① factory.create(command)                     → InboundProduct 도메인 생성
  ② mappingResolver.resolveMappingAndApply()     → 브랜드/카테고리 매핑
  ③ conversionCoordinator.convert()              → 내부 ProductGroup 전체 등록 (동기)
  ④ commandManager.persist(newProduct)           → InboundProduct 저장
  ⑤ return ConversionResult                     → PK 반환
```

### 1.3 변환 흐름 (InboundProductConversionCoordinator → FullProductGroupRegistrationCoordinator)

```
FullProductGroupRegistrationCoordinator.register(bundle)
  ① ProductGroup persist                → productGroupId 생성
  ② Images persist                      → 이미지 저장
  ③ SellerOptionGroups persist           → optionValueIds 생성
  ④ Description persist                 → 설명 저장
  ⑤ Notice persist                      → 고시정보 저장
  ⑥ Products persist                    → productId 생성 (③ optionValueIds 의존)
  ⑦ IntelligenceOutbox persist          → AI 분석 아웃박스 (이미 비동기 패턴)
```

### 1.4 업데이트 흐름 (InboundProductUpdateCoordinator)

```
UpdateCoordinator.update(existingProduct, command)
  ① factory.toUpdateData(command)                → 업데이트 데이터 생성
  ② existingProduct.detectChanges(updateData)    → 변경 감지
  ③ existingProduct.applyUpdate(updateData, now) → 데이터 갱신
  ④ 브랜드/카테고리 변경 시 재매핑
  ⑤ performUpdateConversion()                    → ProductGroup 수정
  ⑥ commandManager.persist(existingProduct)      → 저장
  ⑦ return ConversionResult
```

---

## 2. 설계 결정 과정

### 2.1 비동기 분리 검토

#### 검토 배경

크롤링으로 대량 호출이 들어올 경우, 매핑 → 변환 → 내부 상품 등록이 한 트랜잭션에서
동기로 처리되면 응답이 느려질 수 있다는 우려.

#### 검토한 선택지

**선택지 A: 수신(RECEIVED)만 동기, 나머지 전부 비동기**

```
동기: InboundProduct 저장 (RECEIVED) → 즉시 응답
비동기: 매핑 → 변환 → ProductGroup 전체 등록
```

- 기각 이유: 레거시 컨트롤러(LegacyProductCommandController)가 등록 즉시
  productGroupId + productId를 응답으로 반환해야 함.
  비동기로 바꾸면 이 값이 null이 되어 외부 OMS(사방넷, 셀릭)가 에러 발생.

**선택지 B: 호출자에 따라 동기/비동기 선택**

```
크롤링 호출: 비동기 (RECEIVED만 저장, 빠른 응답)
레거시 호출: 동기 유지 (매핑+변환까지 완료 후 응답)
```

- 기각 이유: 동일 UseCase에 두 가지 실행 경로가 공존하면 복잡도 증가.
  테스트/유지보수 부담.

**선택지 C: 매핑까지 동기, 변환(ProductGroup 등록)만 비동기**

```
동기: InboundProduct 저장 + 매핑 (가벼움)
비동기: ProductGroup + 부속 데이터 등록 (무거움)
```

- 기각 이유: 여전히 productGroupId가 즉시 필요한 문제 해결 안 됨.

**선택지 D: ProductGroup + Products만 동기, 부속 데이터(Images, Description, Notice)는 아웃박스**

```
동기: InboundProduct + 매핑 + ProductGroup + Options + Products → PK 반환
아웃박스: Images, Description, Notice → 스케줄러
```

- 기각 이유: Images, Description, Notice 저장은 전부 단순 DB INSERT 몇 건.
  외부 API 호출이 아니라 같은 RDS 내부 쓰기이므로 ProductGroup + Options + Products
  저장과 속도 차이가 거의 없음. 분리할 실익 대비 복잡도(아웃박스 테이블, 스케줄러,
  상태 관리)가 과도함.

#### 최종 결론

**FullProductGroupRegistrationCoordinator.register()는 현재 구조 유지.**

- 7~8개 테이블 INSERT가 전부 같은 RDS, 같은 트랜잭션 안에서 실행
- 단순 DB 쓰기 작업이므로 각각의 저장 시간 차이가 미미함
- IntelligenceOutbox는 이미 아웃박스 패턴으로 분리되어 있어 AI 분석은 비동기 처리 중
- 진짜 병목이 될 수 있는 건 "등록 자체"가 아니라 "대량 동시 호출 시 동시성" → 이건 별도 대응

### 2.2 ReceiveInboundProductService 등록 전용 전환

#### 결정 근거

세토프 마이그레이션 계획(product-integration-migration-plan.md Phase 0)에서
세토프 기존 상품을 InboundProduct에 선 적재하면:

```
세토프 기존 상품 (크롤링 포함)
  → Phase 0에서 InboundProduct에 LEGACY_IMPORTED로 선 적재됨
  → 이미 InboundProduct에 존재
  → 외부 OMS(사방넷, 셀릭)가 기존 상품에 대해 호출할 때
     → externalProductCode로 조회하면 이미 있음
     → 업데이트 경로로 가야 함
  → ReceiveInboundProductService가 아니라 별도 업데이트 서비스에서 처리

완전 신규 상품
  → InboundProduct에 존재하지 않음
  → ReceiveInboundProductService에서 등록
  → 이 경우에만 register() 호출
```

**외부 OMS가 기존 세토프 PK로 호출하는 경우:**

- 사방넷/셀릭은 세토프 PK를 이미 들고 있음
- PUT /{id}, PATCH /{id}/stock 등 **수정 엔드포인트**를 호출
- POST (등록)는 사방넷만 주 78건/7일 — 완전 신규 상품에만 해당
- 기존 상품에 대해 POST를 다시 호출하는 것은 중복 등록이지 업데이트가 아님

따라서 **ReceiveInboundProductService는 신규 등록만 담당**하는 것이 도메인 의미에 맞음.
기존 상품 업데이트는 레거시 컨트롤러의 개별 수정 엔드포인트에서 별도 처리.

---

## 3. 리팩토링 설계

### 3.1 ReceiveInboundProductService 변경

```
변경 전:
  execute(command)
    ├─ 기존 있으면 → updateCoordinator.update()  (제거)
    └─ 없으면 → registerCoordinator.register()

변경 후:
  execute(command)
    └─ 등록만 → registerCoordinator.register()
    * 이미 존재하는 경우 → 예외 또는 별도 응답 (중복 수신 방어)
```

#### 중복 수신 처리 전략

```
선택지 A: 예외 발생
  - 이미 등록된 externalProductCode로 다시 등록 요청이 오면 예외
  - 엄격한 멱등성 보장

선택지 B: 기존 결과 반환 (멱등성)
  - 이미 등록되어 있으면 기존 InboundProduct의 현재 상태 반환
  - 외부 시스템의 재시도에 안전

선택지 C: 업데이트 위임
  - 이미 존재하면 UpdateCoordinator로 위임 (현재와 동일)
  - 다만 이 경우 "등록 전용"이라는 의미가 훼손됨
```

**권장: 선택지 B** — 크롤링이나 외부 시스템은 재시도가 빈번하므로 멱등성이 중요.

### 3.2 변경 범위

#### 변경 대상 파일

| 파일 | 변경 내용 |
|------|----------|
| `ReceiveInboundProductService` | 업데이트 분기 제거, 등록 전용 + 중복 수신 방어 |
| `ReceiveInboundProductUseCase` | 인터페이스 JavaDoc 업데이트 (등록 전용 명시) |
| `InboundProductRegisterCoordinator` | 변경 없음 (현재 등록 로직 유지) |
| `InboundProductUpdateCoordinator` | 제거 대상 검토 (아래 참고) |

#### InboundProductUpdateCoordinator 처리 방안

현재 `InboundProductUpdateCoordinator`는 ReceiveInboundProductService에서만 호출됨.
등록 전용으로 전환하면 이 코디네이터의 호출부가 사라짐.

```
선택지 A: 즉시 삭제
  - 호출부 없으면 바로 삭제
  - 깔끔하지만, 향후 업데이트 경로 재구현 시 참고 코드 소실

선택지 B: 별도 UseCase로 분리
  - UpdateInboundProductUseCase로 독립
  - Phase 1 (레거시 라우팅) 에서 활용 가능
  - 변경 감지 + 재매핑 + 변환 업데이트 로직 보존

선택지 C: 일단 유지, 호출부만 제거
  - ReceiveInboundProductService에서 참조 제거
  - 코드 자체는 남겨두고 Phase 1에서 활용 결정
```

**권장: 선택지 C** — Phase 1에서 레거시 라우팅 구현 시 업데이트 경로가 필요할 수 있으므로
코드를 보존하되, ReceiveInboundProductService에서 참조만 제거.

### 3.3 RetryPendingMappingService 영향

현재 `RetryPendingMappingService`가 `ReceiveInboundProductUseCase.execute()`를 재호출하는 방식:

```java
// RetryPendingMappingService.java
for (InboundProduct product : pendingProducts) {
    ReceiveInboundProductCommand retryCommand = toRetryCommand(product);
    receiveInboundProductUseCase.execute(retryCommand);  // ← 기존 상품을 다시 execute()
}
```

**문제**: ReceiveInboundProductService를 등록 전용으로 바꾸면,
이미 존재하는 InboundProduct를 다시 execute()하면 "중복 수신"으로 처리됨.

**해결**: RetryPendingMappingService는 별도 경로로 변경 필요.
- InboundProduct를 직접 조회 → 매핑 재시도 → 변환 시도 → persist
- ReceiveInboundProductUseCase를 호출하지 않고 MappingResolver + ConversionCoordinator 직접 사용

```
변경 후 RetryPendingMappingService:
  ① findPendingMappingProducts(batchSize)
  ② 각 product에 대해:
     mappingResolver.resolveMappingAndApply(product, now)
     if (fullyMapped) → conversionCoordinator.convert(product, now)
     commandManager.persist(product)
```

### 3.4 변경 후 전체 흐름

```
[신규 등록 - 크롤링 / 외부 OMS POST]
InboundProductCommandController  또는  LegacyProductCommandController
  → ReceiveInboundProductService.execute(command)
    → 중복 체크 (이미 존재하면 기존 결과 반환)
    → RegisterCoordinator.register(command)
      → InboundProduct 생성 (RECEIVED)
      → 매핑 (MAPPED / PENDING_MAPPING)
      → 매핑 성공 시 → FullProductGroupRegistrationCoordinator.register()
      → InboundProduct persist
      → ConversionResult 반환

[기존 상품 업데이트 - 외부 OMS PUT/PATCH]
LegacyProductCommandController의 개별 수정 엔드포인트
  → 각 UseCase (Price, Stock, Option, Images, Description 등)
  → Phase 1에서 InboundProduct 라우팅 추가

[매핑 실패 재처리 - 스케줄러]
RetryPendingMappingService.execute()
  → PENDING_MAPPING 상품 조회
  → 매핑 재시도 → 변환 시도 → persist
  → ReceiveInboundProductUseCase 호출하지 않음 (직접 처리)
```

---

## 4. 향후 Phase와의 관계

| Phase | ReceiveInboundProductService 관련 |
|-------|-----------------------------------|
| Phase 0 (선 적재) | 세토프 상품 → InboundProduct 벌크 적재. ReceiveInboundProductService와 무관 (SQL 스크립트) |
| Phase 1 (라우팅) | 레거시 컨트롤러에서 InboundProduct 상태 기반 분기 추가. 업데이트는 별도 경로 |
| **현재 작업** | ReceiveInboundProductService 등록 전용 전환 + RetryPendingMappingService 수정 |
| Phase 3 (배치 전환) | LEGACY_IMPORTED → CONVERTED 배치 처리. ReceiveInboundProductService와 무관 |
| Phase 4 (전송) | ExternalProduct 전송. ReceiveInboundProductService와 무관 |

---

## 5. 비동기 분리를 하지 않는 이유 (의사결정 기록)

### 결론: FullProductGroupRegistrationCoordinator 동기 유지

#### 분석 내역

`FullProductGroupRegistrationCoordinator.register()` 내부 7단계를 분석한 결과:

| 단계 | 작업 | 성격 | 소요 |
|------|------|------|------|
| ① ProductGroup persist | DB INSERT 1건 | 동기 DB | ~1ms |
| ② Images persist | DB INSERT N건 (보통 1~5장) | 동기 DB | ~1-3ms |
| ③ SellerOptionGroups persist | DB INSERT N건 | 동기 DB | ~1-3ms |
| ④ Description persist | DB INSERT 1건 (LONGTEXT) | 동기 DB | ~1-2ms |
| ⑤ Notice persist | DB INSERT 1건 | 동기 DB | ~1ms |
| ⑥ Products persist | DB INSERT N건 (SKU 수) | 동기 DB | ~1-3ms |
| ⑦ IntelligenceOutbox persist | DB INSERT 1건 | 동기 DB | ~1ms |

**전체 예상: ~10-15ms** (같은 RDS, 같은 트랜잭션)

- 외부 API 호출 없음
- 전부 같은 DB 인스턴스 내 INSERT
- Images, Description, Notice를 분리해도 절약되는 시간: ~3-5ms
- 아웃박스 도입 비용: 테이블 설계, 스케줄러 구현, 상태 관리, 실패 처리, 모니터링
- **비용 대비 효과가 맞지 않음**

#### 진짜 병목이 될 수 있는 지점

1. **대량 동시 호출**: 크롤링으로 수백 건이 동시에 들어올 때 DB 커넥션 풀 경합
   → 해결: 커넥션 풀 튜닝, 배치 처리, Rate Limiting
2. **AI 분석 파이프라인**: IntelligenceOutbox 이후 처리
   → 이미 아웃박스 패턴으로 비동기 처리 중
3. **세토프 배치 전환**: LEGACY_IMPORTED → CONVERTED 대량 변환
   → Phase 3에서 배치 스케줄러로 별도 처리

이 세 가지는 ReceiveInboundProductService 비동기 분리와 무관하며, 각각 별도 대응이 필요함.

---

## 6. 레거시 컨트롤러 업데이트 경로 재설계

### 6.1 현재 LegacyProductCommandController 구조

```java
@RestController
public class LegacyProductCommandController {

    // 의존성
    private final ReceiveInboundProductUseCase receiveInboundProductUseCase;  // 등록용
    private final LegacyProductCommandUseCase legacyProductCommandUseCase;    // 수정용 (갓 인터페이스)
    private final LegacyProductQueryUseCase legacyProductQueryUseCase;        // 조회용

    // POST (등록)      → ReceiveInboundProductUseCase (인바운드 파이프라인)
    // PUT /{id}        → LegacyProductCommandUseCase.updateFull()
    // PUT /notice      → LegacyProductCommandUseCase.updateNotice()
    // PUT /images      → LegacyProductCommandUseCase.updateImages()
    // PUT /description → LegacyProductCommandUseCase.updateDescription()
    // PUT /option      → LegacyProductCommandUseCase.updateOptions()
    // PATCH /price     → LegacyProductCommandUseCase.updatePrice()
    // PATCH /display-yn → LegacyProductCommandUseCase.updateDisplayStatus()
    // PATCH /out-stock → LegacyProductCommandUseCase.markOutOfStock()
    // PATCH /stock     → LegacyProductCommandUseCase.updateStock()
}
```

### 6.2 현재 LegacyProductCommandUseCase/Service 문제점

```java
// 갓 인터페이스 — 9개 메서드
public interface LegacyProductCommandUseCase {
    void updateFull(long setofProductGroupId, ProductGroupUpdateBundle bundle);
    void updateNotice(long setofProductGroupId, UpdateProductNoticeCommand command);
    void updateImages(long setofProductGroupId, UpdateProductGroupImagesCommand command);
    void updateDescription(long setofProductGroupId, UpdateProductGroupDescriptionCommand command);
    void updateOptions(long setofProductGroupId, ...);
    void updatePrice(long setofProductGroupId, int regularPrice, int currentPrice);
    void updateDisplayStatus(long setofProductGroupId, String displayYn);
    void markOutOfStock(long setofProductGroupId);
    void updateStock(List<UpdateProductStockCommand> commands);
}

// 갓 서비스 — 12개 의존성
public class LegacyProductCommandService implements LegacyProductCommandUseCase {
    private final LegacyProductIdResolver idResolver;
    private final FullProductGroupUpdateCoordinator fullProductGroupUpdateCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final ProductCommandCoordinator productCommandCoordinator;
    private final ProductReadManager productReadManager;
    private final ProductCommandManager productCommandManager;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    // ...
}
```

**문제:**

1. **ISP 위반**: 9개 메서드를 가진 하나의 인터페이스.
   컨트롤러가 이 중 하나만 써도 나머지 8개에 의존.
2. **SRP 위반**: 하나의 서비스가 전체수정, 이미지수정, 가격수정, 재고수정 등
   성격이 다른 10가지 오퍼레이션을 모두 처리.
3. **의존성 폭발**: 12개 의존성 — 각 메서드가 사용하는 의존성이 다른데 하나의 클래스에 전부 주입.
4. **인바운드 비대칭**: 등록은 InboundProduct를 거치는데, 수정은 InboundProduct를 완전히 우회.
   세토프 PK → 내부 ID 해석만 하고 바로 내부 상품을 직접 수정함.
   InboundProduct의 상태(rawPayloadJson, 매핑 정보 등)가 수정 시 갱신되지 않음.

### 6.3 PUT /{id} (updateProductGroup) — 전체 수정의 특수성

#### 현재 흐름

```
LegacyProductCommandController.updateProductGroup()
  → LegacyProductCommandService.updateFull(setofProductGroupId, bundle)
    → idResolver.resolve(setofProductGroupId) → internalProductGroupId
    → replaceProductGroupId(bundle, internalId) → PK 치환
    → fullProductGroupUpdateCoordinator.update(resolvedBundle) → 내부 상품 직접 수정
```

#### 문제점

- InboundProduct를 전혀 거치지 않음
- 외부 OMS가 상품 전체를 수정해도 InboundProduct의 상태가 반영 안 됨
- rawPayloadJson이 최초 등록 시점 데이터로 고정됨
- 매핑 정보(브랜드/카테고리)가 변경되어도 InboundProduct에 반영 안 됨

#### InboundProduct를 거쳐야 하는가?

**결론: PUT /{id} 전체 수정은 InboundProduct를 거칠 필요가 없다.**

이유:

1. **PUT /{id}는 이미 CONVERTED된 상품에 대한 수정**
   - 외부 OMS가 PUT /{id}를 호출한다는 건 이미 등록이 완료된 상품
   - InboundProduct는 "외부 → 내부" 변환 과정의 중간 상태를 관리하는 것
   - 이미 CONVERTED된 상품의 수정은 내부 상품 직접 수정이 맞음

2. **InboundProduct는 수신(Receive)의 상태 관리가 핵심 역할**
   - RECEIVED → PENDING_MAPPING → MAPPED → CONVERTED: 등록 파이프라인
   - 수정은 이 파이프라인의 범위 밖

3. **rawPayloadJson 갱신은 별도로 고려 가능**
   - 수정 시 rawPayloadJson을 업데이트하고 싶다면 별도 이벤트/훅으로 처리
   - 하지만 이것도 현 단계에서는 불필요 (세토프 마이그레이션 후 rawPayloadJson 자체가 무의미)

### 6.4 LegacyProductCommandUseCase 분리 검토

#### 검토한 선택지

**선택지 A: UseCase 개별 분리**

```
현재 LegacyProductCommandUseCase (9개 메서드)
  → 분리:
    LegacyUpdateFullUseCase
    LegacyUpdateNoticeUseCase
    LegacyUpdateImagesUseCase
    LegacyUpdateDescriptionUseCase
    LegacyUpdateOptionsUseCase
    LegacyUpdatePriceUseCase
    LegacyUpdateDisplayStatusUseCase
    LegacyMarkOutOfStockUseCase
    LegacyUpdateStockUseCase
```

- 장점: ISP 준수, 각 UseCase가 단일 책임
- 단점: 9개 인터페이스 + 9개 서비스 = 18개 파일 생성.
  레거시 호환 레이어에 과도한 투자.

**선택지 B: 기능 그룹별 분리**

```
LegacyProductGroupCommandUseCase    — updateFull, updateDisplayStatus
LegacyProductDetailCommandUseCase   — updateNotice, updateImages, updateDescription
LegacyProductOptionCommandUseCase   — updateOptions
LegacyProductStockCommandUseCase    — updatePrice, markOutOfStock, updateStock
```

- 장점: 논리적 그룹핑, 의존성 분산
- 단점: 그룹 기준이 자의적, 여전히 레거시 레이어에 투자

**선택지 C: 현재 구조 유지, 서비스 내부만 정리**

```
LegacyProductCommandUseCase 인터페이스는 유지
LegacyProductCommandService 내부에서:
  - 각 메서드가 해당 Coordinator/Manager만 사용하도록 정리
  - 불필요한 공통 의존성 제거
```

- 장점: 변경 최소화, 레거시 레이어는 어차피 임시
- 단점: 갓 인터페이스/서비스 유지

**선택지 D: 컨트롤러에서 직접 개별 UseCase 호출**

```
LegacyProductCommandController:
  - updateProductGroup() → 기존 내부 UseCase (FullProductGroupUpdateUseCase) 직접 호출
  - updateNotice()       → UpdateProductNoticeUseCase 직접 호출
  - updatePrice()        → UpdateProductPriceUseCase 직접 호출
  - ...
  - 세토프 PK → 내부 ID 변환만 컨트롤러/매퍼에서 처리
```

- 장점: LegacyProductCommandUseCase/Service 자체가 불필요해짐.
  기존 OMS 내부 UseCase를 재사용. 레거시 레이어는 "PK 변환 + 요청 포맷 변환"만 담당.
- 단점: 컨트롤러에 ID 변환 로직이 노출됨 (매퍼로 캡슐화 가능)

#### 최종 결론

**선택지 D 권장 — 레거시 컨트롤러를 "PK 변환 + 포맷 변환 어댑터"로 재정의.**

이유:

1. **LegacyProductCommandService가 하는 일의 본질**

   현재 LegacyProductCommandService의 각 메서드가 하는 일:
   ```
   ① idResolver.resolve(setofPK) → internalId
   ② 기존 Command에 internalId를 바인딩
   ③ 기존 Coordinator에 위임
   ```
   이건 "서비스 로직"이 아니라 **어댑터 로직**. PK 변환은 Adapter-In 레이어의 책임.

2. **이미 존재하는 내부 UseCase/Coordinator 재사용**

   OMS 내부에는 이미 각 도메인별 Coordinator가 있음:
   - FullProductGroupUpdateCoordinator
   - ImageCommandCoordinator
   - DescriptionCommandCoordinator
   - ProductNoticeCommandCoordinator
   - SellerOptionCommandCoordinator
   - ProductCommandCoordinator

   LegacyProductCommandService는 이것들을 단순 위임하고 있을 뿐.
   Adapter-In에서 PK 변환 후 직접 호출하면 중간 레이어가 불필요.

3. **레거시 레이어는 임시**

   세토프 마이그레이션 완료 후 레거시 컨트롤러 자체가 제거됨.
   이 임시 레이어에 UseCase 인터페이스를 여러 개 만드는 건 과투자.

### 6.5 InboundProductUpdateCoordinator 리팩토링

#### 현재 문제

```java
@Component
public class InboundProductUpdateCoordinator {
    private final InboundProductCommandFactory factory;
    private final InboundProductConversionFactory conversionFactory;  // ← 소스타입별 파서 호출
    private final InboundProductCommandManager commandManager;
    private final InboundProductMappingResolver mappingResolver;
    private final FullProductGroupUpdateCoordinator updateCoordinator;
    private final ExternalSourceReadManager externalSourceReadManager; // ← 외부소스 조회

    public InboundProductConversionResult update(InboundProduct existingProduct, ...) {
        // ...
        performUpdateConversion(existingProduct, now);
        // ...
    }

    private void performUpdateConversion(InboundProduct product, Instant now) {
        ExternalSource source = externalSourceReadManager.getById(product.inboundSourceId());
        ExternalSourceType sourceType = source.type();
        conversionFactory.toUpdateCommand(product, sourceType)  // ← 항상 Optional.empty() 반환
            .ifPresent(updateCoordinator::update);
        product.markConverted(product.internalProductGroupId(), now);
    }
}
```

**문제:**

1. **performUpdateConversion()이 실질적으로 아무것도 안 함**
   - `LegacyPayloadParser.toUpdateBundle()` → `return Optional.empty()`
   - `CrawlingPayloadParser.toUpdateBundle()` → `return Optional.empty()`
   - 즉, 모든 파서가 업데이트 번들을 반환하지 않음
   - ExternalSource 조회 + sourceType 결정까지 하고 결국 아무것도 안 하고 CONVERTED만 찍음

2. **ExternalSourceReadManager 의존이 불필요한 상태**
   - 소스타입을 조회해봤자 toUpdateBundle()이 빈 Optional
   - 불필요한 DB 조회 + 불필요한 의존성

3. **InboundProduct 자체의 업데이트가 핵심인데 ProductGroup 업데이트에만 초점**
   - `applyUpdate(updateData, now)` → InboundProduct 도메인 갱신 (OK)
   - `performUpdateConversion()` → 내부 ProductGroup 갱신 (미구현)
   - 정작 중요한 "InboundProduct가 제대로 갱신되었는지"에 대한 검증/완성도가 부족

#### 리팩토링 방향

InboundProductUpdateCoordinator는 **InboundProduct 도메인 갱신에만 집중**하도록 정리.

```
리팩토링 후:
  InboundProductUpdateCoordinator.update(existingProduct, command)
    ① 변경 감지 (detectChanges)
    ② 변경 없으면 → NO_CHANGE 반환
    ③ InboundProduct 데이터 갱신 (applyUpdate)
    ④ 브랜드/카테고리 변경 시 → 재매핑
    ⑤ persist
    ⑥ 결과 반환

제거:
    - ExternalSourceReadManager 의존 제거
    - InboundProductConversionFactory 의존 제거
    - performUpdateConversion() 메서드 제거
    - FullProductGroupUpdateCoordinator 의존 제거

핵심 변화:
    InboundProduct 갱신과 내부 ProductGroup 갱신을 분리.
    InboundProductUpdateCoordinator는 InboundProduct만 책임짐.
    내부 ProductGroup 수정은 레거시 컨트롤러에서 직접 기존 Coordinator 호출.
```

#### 이유

InboundProduct와 내부 ProductGroup은 서로 다른 Aggregate:
- InboundProduct: 외부 수신 상태 관리 (매핑, 변환 추적)
- ProductGroup: 내부 상품 데이터 (이미지, 옵션, 가격 등)

하나의 코디네이터가 두 Aggregate를 동시에 수정하면:
- 트랜잭션 경계가 모호
- 실패 시 어느 쪽이 롤백되는지 불명확
- 각각의 변경 이유가 다름 (InboundProduct는 외부 수신 때문, ProductGroup은 비즈니스 로직 때문)

---

## 7. 변경 후 최종 아키텍처

### 7.1 등록 경로 (변경 없음)

```
POST /api/v1/legacy/product/group (외부 OMS)
POST /api/v1/admin/inbound-products (크롤링)
  │
  ▼
ReceiveInboundProductService (등록 전용)
  → InboundProductRegisterCoordinator
    → InboundProduct 생성 + 매핑
    → FullProductGroupRegistrationCoordinator (전체 등록)
  → ConversionResult 반환 (productGroupId + productIds)
```

### 7.2 수정 경로 (변경)

```
PUT /api/v1/legacy/product/group/{id} (외부 OMS)
  │
  ▼
LegacyProductCommandController
  → LegacyProductCommandApiMapper: 세토프 PK → 내부 ID 변환 + 요청 포맷 변환
  → 기존 내부 Coordinator 직접 호출:
    ├─ FullProductGroupUpdateCoordinator (전체 수정)
    ├─ ImageCommandCoordinator (이미지 수정)
    ├─ DescriptionCommandCoordinator (설명 수정)
    ├─ ProductNoticeCommandCoordinator (고시정보 수정)
    ├─ SellerOptionCommandCoordinator (옵션 수정)
    └─ ProductCommandCoordinator (상품/재고 수정)

LegacyProductCommandUseCase/Service → 제거
```

### 7.3 매핑 재처리 경로 (변경)

```
스케줄러 (주기적 실행)
  │
  ▼
RetryPendingMappingService
  → InboundProduct(PENDING_MAPPING) 조회
  → MappingResolver 직접 호출 (재매핑)
  → ConversionCoordinator 직접 호출 (변환)
  → persist
  (ReceiveInboundProductUseCase를 호출하지 않음)
```

### 7.4 InboundProductCommandController (크롤링용)

```
현재:
  receiveInboundProduct()   → ReceiveInboundProductUseCase
  updatePrice()             → UpdateInboundProductPriceUseCase
  updateStock()             → UpdateInboundProductStockUseCase
  updateImages()            → UpdateInboundProductImagesUseCase
  updateDescription()       → UpdateInboundProductDescriptionUseCase
  updateProducts()          → InboundProductIdResolver → UpdateProductsUseCase

변경:
  상품 전체 수정 미지원 (크롤링 서버는 내부 컨트롤 가능)
  부분 수정 UseCase들은 현재 구조 유지
```

---

## 8. 작업 순서

| 순서 | 작업 | 영향 범위 |
|------|------|----------|
| 1 | ReceiveInboundProductService → 등록 전용 전환 + 중복 수신 방어 | application 레이어 |
| 2 | RetryPendingMappingService → 직접 처리 방식으로 변경 | application 레이어 |
| 3 | InboundProductUpdateCoordinator → InboundProduct 전용으로 정리 | application 레이어 |
| 4 | LegacyProductCommandController → 내부 Coordinator 직접 호출로 전환 | adapter-in 레이어 |
| 5 | LegacyProductCommandUseCase/Service → 제거 | application + adapter-in 레이어 |

### 작업 간 의존관계

```
작업 1 (ReceiveInboundProductService) ← 독립
작업 2 (RetryPendingMappingService)   ← 작업 1 선행 필요
작업 3 (InboundProductUpdateCoordinator) ← 작업 1 선행 필요
작업 4 (LegacyProductCommandController) ← 독립 (작업 5와 동시 진행 가능)
작업 5 (LegacyProductCommandUseCase 제거) ← 작업 4 선행 필요
```
