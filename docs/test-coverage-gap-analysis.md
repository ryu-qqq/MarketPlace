# 테스트 커버리지 갭 분석

- **브랜치**: `feature/product-catalog-structure`
- **기준 브랜치**: `main`
- **분석일**: 2026-02-10

---

## 요약

| 레이어 | 소스 파일 | 테스트 파일 | testFixtures | 커버리지 비율 |
|--------|----------|------------|-------------|-------------|
| Domain | 288 | 57 | 9 | ~20% |
| Application | 478 | 73 | 15 | ~15% |
| Adapter-Out (persistence) | 188 | 106 | 19 | ~56% |
| Adapter-Out (client) | 14 | 4 | 1 | ~29% |
| Adapter-In (rest-api) | 205 | 52 | 12 | ~25% |
| Integration Test | - | 27 | - | - |

---

## 1. DOMAIN 레이어

### 테스트 없음 (❌)

| 패키지 | 소스 | 주요 누락 대상 |
|--------|------|--------------|
| brandpreset | 8 | Aggregate(`BrandPreset`), ErrorCode, Exception, Id, SearchCriteria, SortKey, Status |
| categorypreset | 8 | Aggregate(`CategoryPreset`), ErrorCode, Exception, Id, SearchCriteria, SortKey, Status |
| brandmapping | 10 | Aggregate(`BrandMapping`), ErrorCode, Exception, Id, SearchCriteria, SortKey, Status |
| categorymapping | 10 | Aggregate(`CategoryMapping`), ErrorCode, Exception, Id, SearchCriteria, SortKey, Status |
| brand | 13 | Aggregate(`Brand`), VO(`BrandCode`, `BrandName`, `LogoUrl`), ErrorCode, Exception, Id |
| saleschannel | 12 | Aggregate(`SalesChannel`), VO(`ChannelName`), ErrorCode, Exception, Id, SearchCriteria |
| saleschannelbrand | 10 | Aggregate(`SalesChannelBrand`), ErrorCode, Exception, Id, SearchCriteria |
| saleschannelcategory | 11 | Aggregate(`SalesChannelCategory`), ErrorCode, Exception, Id, SearchCriteria |
| selleraddress | 12 | Aggregate(`SellerAddress`), VO(`AddressName`, `AddressType`), ErrorCode, Exception |
| sellerapplication | 13 | Aggregate(`SellerApplication`) 전체 |
| notice (신규) | 13 | Aggregate(`NoticeCategory`, `NoticeField`), VO 전체 |
| common | 21 | 공통 VO, QueryContext, PageRequest 등 |
| auth | 1 | `AuthToken` |

### 테스트 부족 (⚠️)

| 패키지 | 소스 | 테스트 | 비고 |
|--------|------|--------|------|
| category | 18 | 2 | Aggregate 테스트만 존재, VO/Exception/SearchCriteria 누락 |
| selleradmin | 21 | 1 | Aggregate 테스트만 존재 |

### 테스트 양호 (✅)

| 패키지 | 소스 | 테스트 |
|--------|------|--------|
| seller | 48 | 12 |
| shop | 13 | 9 |
| commoncode | 13 | 6 |
| commoncodetype | 16 | 6 |
| shippingpolicy | 15 | 7 |
| refundpolicy | 15 | 5 |

---

## 2. APPLICATION 레이어

### 테스트 없음 (❌)

| 패키지 | 소스 | 주요 누락 대상 |
|--------|------|--------------|
| brandpreset | 22 | Service(Register/Update/Delete/Search), Validator, Factory(Command/Query), Facade, Assembler, Manager |
| categorypreset | 22 | 동일 구조 |
| brand | 9 | ReadManager, QueryPort, CommandPort |
| category | 9 | ReadManager, QueryPort |
| saleschannel | 19 | Service 전체, Validator, Factory, Manager |
| saleschannelbrand | 16 | Service 전체, Validator, Factory, Manager |
| saleschannelcategory | 16 | Service 전체, Validator, Factory, Manager |
| selleraddress | 28 | Service(Register/Update/Delete/Search/GetMetadata), Validator, Factory, Manager, Assembler |
| brandmapping | 2 | CommandManager, CommandPort (Facade 내부 사용용, 낮은 우선순위) |
| categorymapping | 2 | CommandManager, CommandPort (Facade 내부 사용용, 낮은 우선순위) |
| common | 18 | TimeProvider, CommonSearchParams, StatusChangeContext 등 |

### 테스트 부족 (⚠️)

| 패키지 | 소스 | 테스트 | 비고 |
|--------|------|--------|------|
| selleradmin | 73 | 1 | ChangePasswordService만 테스트 존재. Apply/Approve/Reject/BulkApprove/BulkReject/Outbox 등 전체 누락 |
| sellerapplication | 27 | 1 | 대부분 누락 |

### 테스트 양호 (✅)

| 패키지 | 소스 | 테스트 |
|--------|------|--------|
| auth | 20 | 4 |
| seller | 84 | 19 |
| commoncode | 22 | 8 |
| commoncodetype | 22 | 10 |
| shippingpolicy | 24 | 11 |
| refundpolicy | 24 | 10 |
| shop | 19 | 9 |

---

## 3. ADAPTER-OUT 레이어

### persistence-mysql: 테스트 없음 (❌)

| 패키지 | 소스 | 주요 누락 대상 |
|--------|------|--------------|
| brandpreset | 8 | QueryAdapter, CommandAdapter, QueryDslRepository, Entity, Mapper, JpaRepository, ConditionBuilder |
| categorypreset | 8 | 동일 구조 |
| brandmapping | 4 | CommandAdapter, Entity, Mapper, JpaRepository |
| categorymapping | 4 | 동일 구조 |
| saleschannel | 7 | QueryAdapter, CommandAdapter, Repository, Entity, Mapper |
| saleschannelbrand | 7 | 동일 구조 |
| saleschannelcategory | 7 | 동일 구조 |

### client: 테스트 없음 (❌)

| 패키지 | 소스 | 주요 누락 대상 |
|--------|------|--------------|
| ses-client | 5 | SES 이메일 클라이언트 어댑터 전체 |

### 테스트 양호 (✅)

| 패키지 | 소스 | 테스트 |
|--------|------|--------|
| seller | 57 | 34 |
| selleradmin | 21 | 10 |
| authhub-client | 9 | 4 |
| brand | 6 | 5 |
| category | 6 | 5 |
| commoncode | 7 | 6 |
| commoncodetype | 7 | 6 |
| selleraddress | 7 | 5 |
| shop | 7 | 4 |
| composite | 17 | 7 |

---

## 4. ADAPTER-IN (REST API) 레이어

### 테스트 없음 (❌)

| 패키지 | 소스 | 주요 누락 대상 |
|--------|------|--------------|
| brandpreset | 13 | Controller(Command/Query), Mapper(Command/Query), DTO, ErrorMapper, Endpoints |
| categorypreset | 13 | 동일 구조 |
| saleschannel | 11 | Controller, Mapper, DTO, ErrorMapper |
| saleschannelbrand | 10 | 동일 구조 |
| saleschannelcategory | 10 | 동일 구조 |
| common | 11 | GlobalExceptionHandler, ErrorMapper 인프라 등 |

### 테스트 양호 (✅)

| 패키지 | 소스 | 테스트 |
|--------|------|--------|
| auth | 13 | 5 |
| selleradmin | 16 | 6 |
| sellerapplication | 12 | 5 |
| commoncode | 11 | 5 |
| commoncodetype | 11 | 5 |
| refundpolicy | 13 | 5 |
| seller | 13 | 4 |
| selleraddress | 14 | 4 |
| shippingpolicy | 12 | 4 |
| shop | 10 | 4 |
| brand | 6 | 2 |
| category | 6 | 2 |

---

## 5. INTEGRATION TEST (E2E)

### 있음 (✅)

auth, brand, category, commoncode, commoncodetype, refundpolicy, seller, selleraddress, selleradmin, shippingpolicy, shop

### 없음 (❌)

brandpreset, categorypreset, saleschannel, saleschannelbrand, saleschannelcategory, notice

---

## 우선순위

### P0 - 최근 작업 핵심 (전 레이어 테스트 0)

1. **brandpreset** - Domain Aggregate + Application Service/Validator/Factory/Facade + Persistence + REST API
2. **categorypreset** - 동일 구조
3. **brandmapping / categorymapping** - Domain Aggregate (Application은 CommandPort/Manager만 남음)

### P1 - 기존 도메인 (전 레이어 테스트 0)

4. **saleschannel / saleschannelbrand / saleschannelcategory** - 판매채널 도메인 전체
5. **brand** - Domain Aggregate + Application
6. **selleraddress** - Application 28개 소스 중 테스트 0
7. **notice** - 신규 도메인 전체

### P2 - 테스트 부족

8. **selleradmin** - Application 73개 중 테스트 1개
9. **sellerapplication** - Application 27개 중 테스트 1개
10. **category** - Domain 18개 중 테스트 2개

### P3 - 인프라/공통

11. **common** (Domain/Application) - 공통 VO, TimeProvider 등
12. **ses-client** - SES 이메일 클라이언트
13. **common** (REST API) - GlobalExceptionHandler 등
