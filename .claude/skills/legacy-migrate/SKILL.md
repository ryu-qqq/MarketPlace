---
name: legacy-migrate
description: 레거시 API 전체 마이그레이션 워크플로우. 레거시 서버(setof-commerce) 분석 → MarketPlace 내부 코드 생성. 6단계 자동화 파이프라인.
disable-model-invocation: true
---

# /legacy-migrate

레거시 API를 새 아키텍처로 **완전 마이그레이션**하는 자동화 워크플로우입니다.

## 레거시 프로젝트 정보

| 항목 | 값 |
|------|-----|
| **레거시 프로젝트 경로** | `/Users/ryu-qqq/Documents/ryu-qqq/setof-commerce` |
| **Admin API 모듈** | `bootstrap/bootstrap-legacy-web-api-admin` |
| **Web API 모듈** | `bootstrap/bootstrap-legacy-web-api` |
| **패키지 루트** | `com.connectly.partnerAdmin.module` |

### 분석 대상 컨트롤러 위치

```
/Users/ryu-qqq/Documents/ryu-qqq/setof-commerce/
  bootstrap/
    bootstrap-legacy-web-api-admin/src/main/java/com/connectly/partnerAdmin/module/
      {domain}/controller/{Domain}Controller.java
    bootstrap-legacy-web-api/src/main/java/com/connectly/partnerAdmin/module/
      {domain}/controller/{Domain}Controller.java
```

### 기존 분석 문서 위치

이미 `/legacy-flow`로 분석된 문서가 있으면 재활용합니다:

```
/Users/ryu-qqq/Documents/ryu-qqq/setof-commerce/claudedocs/
  legacy-endpoints/{prefix}/{module}_endpoints.md
  legacy-flows/{prefix}/{Controller}_{method}.md
```

---

## 🚨 의존성 방향 원칙 (절대 준수)

> **Domain과 Application은 독립적인 핵심이다. 절대로 레거시에 맞추지 않는다.**

```text
[레거시 요청] → Adapter-In(Mapper) → Application UseCase → Domain
                                                              ↓
[레거시 DB] ← Adapter-Out(Mapper) ← Domain 객체 반환 ← Domain
```

### 인바운드 (외부 → 내부)
- 레거시 v1 Controller나 외부 요청이 들어오면, **Adapter-In의 Mapper가 변환**하여 Application UseCase 형태에 맞춘다
- Application의 UseCase 시그니처를 레거시 요청 형태에 맞추는 것은 **금지**

### 아웃바운드 (내부 → 외부)
- Domain 객체를 그대로 반환하면, **Adapter-Out의 Mapper/Repository가 자기 DB 구조에 맞춰 저장**한다
- Domain 객체의 필드나 구조를 레거시 DB 스키마에 맞추는 것은 **금지**

### 위반 사례 (하지 말 것)

```java
// ❌ Application UseCase를 레거시 필터에 맞춤
public interface FetchProductGroupsUseCase {
    Page<ProductGroupDetailResponse> execute(ProductGroupFilter filter, Pageable pageable);
    // ProductGroupFilter는 레거시 DTO → 절대 금지
}

// ✅ Application UseCase는 내부 SearchCondition 사용
public interface GetProductGroupsUseCase {
    SliceResult<ProductGroupSummary> execute(ProductGroupSearchCondition condition);
    // 내부 도메인 기준 DTO
}

// ❌ Domain 객체에 레거시 DB 컬럼명 반영
public record ProductGroup(Yn soldOutYn, Yn displayYn) {}

// ✅ Domain 객체는 내부 규칙
public record ProductGroup(boolean soldOut, boolean displayed) {}
```

### 변환 책임 위치

| 레이어 | 변환 방향 | 예시 |
|--------|----------|------|
| **Adapter-In Controller/Mapper** | 레거시 Request → 내부 Command/Condition | `ProductGroupFilter` → `ProductGroupSearchCondition` |
| **Adapter-In Controller/Mapper** | 내부 Result → 레거시 Response | `ProductGroupSummary` → `ProductGroupDetailResponse` |
| **Adapter-Out Mapper** | Domain → 레거시 DB Entity | `ProductGroup` → `LegacyProductGroupEntity` |
| **Application/Domain** | 변환 없음, 내부 규칙만 사용 | 레거시 DTO import **금지** |

---

## 사용법

```bash
/legacy-migrate admin:product           # Admin API product 모듈 전체 마이그레이션
/legacy-migrate web:product             # Web API product 모듈 전체 마이그레이션
/legacy-migrate admin:brand --step 3    # 3단계(legacy-convert)부터 재개
```

## 입력

- `$ARGUMENTS[0]`: 대상 모듈 (예: `admin:product`, `web:product`)
- `$ARGUMENTS[1]`: (선택) `--step N` - N단계부터 시작

---

## 워크플로우 (6단계 순차 실행)

다음 순서로 각 **Agent를 순차적으로 호출**하세요.
각 단계 완료 후 결과를 확인하고, 다음 Agent에 필요한 정보를 전달합니다.

### Step 1: 엔드포인트 분석

```
Agent: legacy-endpoints-analyzer
분석 대상: /Users/ryu-qqq/Documents/ryu-qqq/setof-commerce의 레거시 Controller
입력: $ARGUMENTS[0]의 모듈 부분 (예: admin:product)
출력: claudedocs/legacy-endpoints/{prefix}/{module}_endpoints.md
```

**확인사항**: Query/Command 엔드포인트 목록 추출

> 기존 분석 문서가 `/Users/ryu-qqq/Documents/ryu-qqq/setof-commerce/claudedocs/legacy-endpoints/`에 있으면 재활용

---

### Step 2: API 흐름 분석

```
Agent: legacy-flow-analyzer
분석 대상: /Users/ryu-qqq/Documents/ryu-qqq/setof-commerce의 레거시 서비스/레포지토리
입력: Step 1에서 추출한 각 엔드포인트
      예: admin:ProductController.fetchProductGroups
출력: claudedocs/legacy-flows/{prefix}/{Controller}_{method}.md
```

**확인사항**: Request/Response 구조, 호출 스택, QueryDSL 쿼리

> 기존 분석 문서가 `/Users/ryu-qqq/Documents/ryu-qqq/setof-commerce/claudedocs/legacy-flows/`에 있으면 재활용

---

### Step 3: DTO 변환

```
Agent: legacy-dto-converter
입력: Step 2의 분석 결과 기반
출력 위치: MarketPlace 프로젝트 (현재 프로젝트)
  adapter-in/rest-api-{admin|web}/.../dto/request/, response/
```

**⚠️ 의존성 원칙 적용**:
- 레거시 DTO를 그대로 복사하지 않는다
- 레거시 `Yn` enum → `boolean` 변환
- 레거시 `SearchKeyword` → 내부 검색 조건으로 재설계
- record 타입, @Schema/@Parameter 어노테이션, Validation

---

### Step 4: Persistence Layer 생성

```
Agent: legacy-query-generator
입력: Step 2의 QueryDSL 분석 결과 기반
출력 위치: MarketPlace 프로젝트 (현재 프로젝트)
  - domain/.../query/ (SearchCondition)
  - application/.../dto/response/ (Result DTO)
  - adapter-out/persistence-mysql/.../repository/ 또는
    adapter-out/persistence-mysql-legacy/.../composite/
```

**⚠️ 의존성 원칙 적용**:
- SearchCondition은 Domain 레이어 기준으로 설계
- 레거시 Filter 필드명/구조를 그대로 가져오지 않는다
- QueryDSL은 market 스키마 기준 (또는 필요 시 legacy 스키마)

---

### Step 5: Application Layer 생성

```
Agent: legacy-service-generator
입력: Step 4의 Persistence Layer 기반
출력 위치: MarketPlace 프로젝트 (현재 프로젝트)
  application/.../
    - port/in/, port/out/
    - service/, manager/, assembler/
```

**⚠️ 의존성 원칙 적용**:
- UseCase 시그니처는 내부 기준 (레거시 Request/Response import 금지)
- Port 인터페이스는 Domain 기준

---

### Step 6: Controller 생성

```
Agent: legacy-controller-generator
입력: Step 3 DTO + Step 5 UseCase
출력 위치: MarketPlace 프로젝트 (현재 프로젝트)
  adapter-in/rest-api-{admin|web}/.../v1/{domain}/
    - controller/, mapper/
```

**⚠️ 의존성 원칙 적용**:
- Controller/Mapper가 레거시 ↔ 내부 변환 책임
- 레거시 Request → 내부 Command/Condition 변환은 Mapper에서
- 내부 Result → 레거시 Response 변환은 Mapper에서

---

## 실행 지침

1. **순차 실행**: 각 Agent 완료 후 다음 Agent 호출
2. **결과 전달**: 이전 Agent의 출력을 다음 Agent에 전달
3. **레거시 분석은 setof-commerce**, 코드 생성은 **MarketPlace (현재 프로젝트)**
4. **에러 처리**: 실패 시 해당 단계에서 중단하고 사용자에게 보고
5. **의존성 방향 검증**: 매 단계 완료 시 Domain/Application에 레거시 import가 없는지 확인
6. **진행 상황 보고**: 각 단계 완료 시 결과 요약 출력

## 진행 상황 보고 형식

```
✅ Step 1/6: 엔드포인트 분석 완료 (setof-commerce 분석)
   - Query: 5개, Command: 3개
   - 출력: claudedocs/legacy-endpoints/admin/product_endpoints.md

🔄 Step 2/6: API 흐름 분석 중... (setof-commerce 분석)
   - 대상: ProductController.fetchProductGroups
   - 기존 분석 문서 발견 → 재활용

✅ Step 3/6: DTO 변환 완료 (MarketPlace 코드 생성)
   - 의존성 방향 검증: ✅ Domain/Application에 레거시 import 없음
```

## 마이그레이션 완료 후

```
✅ 마이그레이션 완료: admin:product

📁 생성된 파일 (MarketPlace 프로젝트):
- DTO: 2개 (adapter-in)
- Repository: 2개 (adapter-out)
- Service: 5개 (application)
- Controller: 2개 (adapter-in)

🔍 의존성 방향 검증:
- Domain 레이어 레거시 import: 0개 ✅
- Application 레이어 레거시 import: 0개 ✅
- Adapter 레이어 변환 Mapper: 2개 ✅

🔗 다음 단계:
- 테스트 작성: /test-repository, /test-api
- 코드 리뷰: /review
```
