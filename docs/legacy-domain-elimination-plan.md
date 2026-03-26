# 레거시 도메인 제거 계획

## 1. 현재 문제

`domain/legacy/` 패키지에 레거시 전용 도메인 객체들이 존재한다.

```
domain/legacy/
  productgroup/      ← LegacyProductGroup (aggregate)
  productnotice/     ← LegacyProductNotice (aggregate)
  productdelivery/   ← LegacyProductDelivery (aggregate)
  productdescription/← LegacyProductGroupDescription, LegacyDescriptionImage (aggregate)
  product/           ← LegacyProduct (aggregate)
  productimage/      ← LegacyProductImage (aggregate)
  optiongroup/       ← LegacyOptionGroup
  optiondetail/      ← LegacyOptionDetail
  commoncode/        ← LegacyCommonCode
```

**이것들이 왜 불필요한가:**

Phase A에서 레거시 API의 Controller → Mapper가 **표준 커맨드**로 변환하도록 바꿨다.
표준 커맨드를 받았으면 **표준 도메인**을 태워야 한다.
Port 구현체만 레거시(luxurydb 저장)이면 된다.

```
잘못된 흐름 (현재):
  표준 Command → 레거시 Domain → 레거시 Entity → luxurydb
                    ↑
              왜 여기서 레거시 도메인을 거치나?

올바른 흐름:
  표준 Command → 표준 Coordinator → 표준 Domain
    → Port 구현체 (레거시)
      → adapter-out Mapper: 표준 Domain → 레거시 Entity → luxurydb
```

## 2. 레거시 도메인이 현재 사용되는 곳

### 2.1 Command 흐름 (쓰기)

현재 레거시 Command 서비스들이 레거시 Coordinator를 호출하고,
Coordinator가 레거시 도메인 객체를 조작한 후 레거시 Port로 저장한다.

```
LegacyProductRegistrationCoordinator
  → LegacyProductGroup.forNew() ← 레거시 도메인 생성
  → LegacyProductGroupCommandPort.persist(LegacyProductGroup) ← 레거시 도메인 전달
  → adapter-out: LegacyProductGroup → Entity → luxurydb
```

**해결:** 표준 Coordinator를 호출하고, Port 구현체만 레거시로 교체.

```
표준 FullProductGroupRegistrationCoordinator
  → ProductGroup.forNew() ← 표준 도메인 생성
  → ProductGroupCommandPort.persist(ProductGroup) ← 표준 Port
  → 레거시 구현체: ProductGroup → LegacyProductGroupEntity → luxurydb
```

### 2.2 Query 흐름 (읽기)

현재 레거시 조회 어댑터가 luxurydb에서 읽어서 레거시 도메인 객체를 만들고,
application 레이어가 이를 표준 결과 DTO로 변환한다.

```
LegacyProductGroupQueryAdapter
  → luxurydb 조회 → LegacyProductGroup (레거시 도메인) 복원
  → application: LegacyProductGroup → 표준 DTO로 변환
```

**해결:** adapter-out에서 luxurydb 조회 → 바로 표준 결과 DTO로 변환.
셀러에서 이미 이 패턴을 적용했다:

```
LegacySellerCompositionQueryAdapter
  → luxurydb 조회 → SellerAdminCompositeResult (표준 DTO) 직접 반환
  → 레거시 도메인 객체 없음!
```

### 2.3 Conversion 흐름 (동기화)

`legacyconversion` 패키지가 luxurydb → 신 스키마 동기화를 위해 레거시 도메인을 사용한다.

```
LegacyConversionCoordinator
  → LegacyProductGroupReadFacade: luxurydb 조회 → LegacyProductGroupCompositeResult
  → LegacyToInternalBundleFactory: 레거시 결과 → 표준 커맨드 변환
  → 표준 Coordinator 호출
```

이 흐름에서는 `LegacyProductGroupCompositeResult`(application DTO)를 사용하며,
레거시 도메인 객체가 아닌 **flat DTO**를 통해 변환한다.
따라서 Conversion 흐름은 레거시 도메인에 의존하지 않는다.
단, `LegacyProductGroupReadFacade`가 내부적으로 레거시 도메인을 거쳐서 DTO를 만드는데,
이것도 adapter-out에서 바로 flat DTO를 반환하도록 바꿀 수 있다.

## 3. 제거 전략

### Phase 1: Command 흐름 전환 (Description/Image/Notice 완료, ProductGroup 남음)

**이미 완료:**
- Notice: UseCase → 표준 커맨드 → Manager → Port → adapter-out (luxurydb)
- Description: UseCase → 표준 커맨드 → Manager → Port (adapter-out 구현체 미완)
- Image: UseCase → 표준 커맨드 → Manager → Port (adapter-out 구현체 미완)

**남은 작업:**
- ProductGroup 등록/수정: 레거시 Coordinator 대신 표준 Coordinator 호출
- Product(SKU): 표준 커맨드 흐름으로 전환

**핵심 변경:**
레거시 서비스가 표준 Coordinator를 호출하되,
Coordinator가 사용하는 Port의 **구현체만 레거시**인 구조.

```java
// 표준 Port 인터페이스
public interface ProductGroupCommandPort {
    Long persist(ProductGroup productGroup);
}

// 레거시 구현체 (luxurydb 저장)
@Component
@ConditionalOnProperty(name = "persistence.product.adapter", havingValue = "legacy")
public class LegacyProductGroupCommandAdapter implements ProductGroupCommandPort {
    // ProductGroup (표준 도메인) → LegacyProductGroupEntity → luxurydb
}

// 표준 구현체 (새 스키마 저장)
@Component
@ConditionalOnProperty(name = "persistence.product.adapter", havingValue = "standard", matchIfMissing = true)
public class JpaProductGroupCommandAdapter implements ProductGroupCommandPort {
    // ProductGroup (표준 도메인) → ProductGroupJpaEntity → 새 스키마
}
```

### Phase 2: Query 흐름 전환

레거시 조회 어댑터가 레거시 도메인을 복원하는 대신,
표준 결과 DTO나 표준 도메인 객체를 직접 반환하도록 변경.

**변경 전:**
```
LegacyProductGroupQueryAdapter
  → LegacyProductGroupEntity → LegacyProductGroup (레거시 도메인)
```

**변경 후:**
```
LegacyProductGroupQueryAdapter
  → LegacyProductGroupEntity → ProductGroup (표준 도메인)
  또는
  → QueryDsl Projection → 표준 Result DTO 직접 반환
```

### Phase 3: domain/legacy/ 삭제

Phase 1, 2 완료 후 레거시 도메인 객체를 참조하는 곳이 0이 되면 전체 삭제.

```
삭제 대상:
  domain/legacy/productgroup/
  domain/legacy/productnotice/
  domain/legacy/productdelivery/
  domain/legacy/productdescription/
  domain/legacy/product/
  domain/legacy/productimage/
  domain/legacy/optiongroup/
  domain/legacy/optiondetail/

유지 (별도 판단):
  domain/legacy/commoncode/  ← LegacyCommonCode (legacycommoncode 패키지에서 사용)
```

## 4. 현재 상태별 매트릭스

| 도메인 | Command 흐름 | Query 흐름 | 레거시 도메인 의존 | adapter-out 구현 |
|--------|-------------|-----------|-----------------|----------------|
| **Seller** | ✅ 표준 커맨드 | ✅ 표준 DTO 직접 반환 | ❌ 없음 | ✅ 완성 |
| **Notice** | ✅ 표준 커맨드 → Port | - | ❌ 없음 | ✅ 완성 |
| **Description** | ✅ 표준 커맨드 → Port | ⚠️ 레거시 도메인 경유 | ⚠️ Coordinator/ReadManager | ❌ update() 미완 |
| **Image** | ✅ 표준 커맨드 → Port | ⚠️ 레거시 도메인 경유 | ⚠️ Coordinator/ReadManager | ❌ update() 미완 |
| **ProductGroup** | ❌ 레거시 커맨드 + Coordinator | ❌ 레거시 도메인 복원 | ❌ 전면 의존 | ❌ 미착수 |
| **Product/SKU** | ❌ 레거시 커맨드 + Coordinator | ❌ 레거시 도메인 복원 | ❌ 전면 의존 | ❌ 미착수 |
| **Shipment** | ✅ 표준 CommonCode | - | ❌ 없음 | N/A |
| **Session** | ✅ 표준 커맨드 | - | ❌ 없음 | N/A |

## 5. adapter-out Description/Image 구현 방향

### Description adapter-out

```java
@Component
public class LegacyProductDescriptionCommandAdapter implements LegacyProductDescriptionCommandPort {

    @Override
    public void update(UpdateProductGroupDescriptionCommand command) {
        // command.productGroupId() → PK
        // command.content() → detail_description 컬럼
        // → LegacyProductGroupDetailDescriptionEntity.create(pgId, content)
        // → repository.save(entity)
    }
}
```

단순하다. `productGroupId + content` → flat 컬럼 1개 저장.

### Image adapter-out

```java
@Component
public class LegacyProductImageCommandAdapter implements LegacyProductImageCommandPort {

    @Override
    public void update(UpdateProductGroupImagesCommand command) {
        // 1. 기존 이미지 전체 soft delete (delete_yn = 'Y')
        // 2. command.images() 순회
        //    → ImageCommand(imageType, originUrl, sortOrder)
        //    → LegacyProductGroupImageEntity.create(pgId, type, originUrl, originUrl, sortOrder, 'N')
        //    → repository.saveAll(entities)
    }
}
```

기존 이미지 삭제 후 새로 저장하는 replace-all 패턴.

## 6. 장기 목표

```
최종 구조:

adapter-in/rest-api-legacy → 표준 커맨드/결과 사용
  → application (표준 UseCase/Coordinator/Domain)
    → Port 구현체만 레거시 (luxurydb)

domain/legacy/ 완전 삭제
application/legacy/ → UseCase + Service만 남음 (표준 Coordinator 호출 + ConversionOutbox)

전환 시:
  persistence.product.adapter=standard 설정 변경
  → Port 구현체가 표준으로 교체
  → 코드 변경 없음
```

## 7. 주의사항

### LegacyConversionOutbox

레거시 UseCase 서비스에서 `ConversionOutboxCommandManager.createIfNoPending()`를 호출한다.
이건 luxurydb 변경 → 신 스키마 동기화를 위한 것이며,
레거시 API에서 저장할 때마다 생성해야 한다.
표준 Coordinator에는 이 로직이 없으므로, 레거시 Service에서 별도로 호출한다.

### 표준 Port vs 레거시 Port

두 가지 선택지:
1. **표준 Port를 레거시가 구현** — `ProductGroupCommandPort`를 `LegacyProductGroupCommandAdapter`가 구현
2. **별도 레거시 Port** — `LegacyProductGroupCommandPort`를 따로 두고 레거시 서비스에서 호출

셀러에서는 2번 패턴(별도 Port)을 사용했다.
하지만 장기적으로는 1번(표준 Port를 ConditionalOnProperty로 교체)이 깔끔하다.

**현실적 제약:** 표준 도메인과 레거시 스키마 사이에 필드 차이가 크면
표준 Port를 그대로 구현하기 어렵다. 이 경우 별도 Port가 현실적이다.
