---
name: test-coverage
description: 테스트 커버리지 갭 분석 → 자동 보완 → 검증을 한 사이클로 수행. 레이어별 소스 대비 테스트 누락을 분석하고, 우선순위 기반으로 테스트를 자동 생성/수정하고, 실행하여 결과를 리포트한다. 테스트 감사, 테스트 보완, 테스트 커버리지 분석, test audit, test fix 관련 요청에 사용.
context: fork
agent: test-coverage-agent
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-coverage

레이어별 테스트 커버리지 갭 분석 → 자동 보완 → 검증을 한 사이클로 수행하는 올인원 스킬.

기존 `test-audit` + `test-fix` + `test-fix-complete` 3개 스킬을 하나로 통합했다.
분석만 하고 끝나는 게 아니라, 발견된 갭을 즉시 보완하고 테스트를 실행하여 검증까지 완료한다.

## 사용법

```bash
/test-coverage domain seller                    # domain/seller 전체 사이클
/test-coverage application seller               # application/seller 전체 사이클
/test-coverage adapter-in selleradmin           # adapter-in/selleradmin 전체 사이클
/test-coverage adapter-out seller               # adapter-out/seller 전체 사이클

# 범위 옵션
/test-coverage domain --all                     # domain 레이어 전체
/test-coverage domain seller --high-only        # HIGH 갭만 처리

# 실행 제어
/test-coverage domain seller --audit-only       # 분석만 (보완 안 함)
/test-coverage domain seller --dry-run          # 분석 + 보완 계획만 (코드 생성 안 함)
/test-coverage domain seller --no-run           # 코드 생성까지만 (테스트 실행 안 함)
```

## 입력

- `$ARGUMENTS[0]`: 레이어 (`domain`, `application`, `adapter-out`, `adapter-in`)
- `$ARGUMENTS[1]`: 패키지명 또는 `--all`
- `$ARGUMENTS[2]`: (선택) `--high-only`, `--audit-only`, `--dry-run`, `--no-run`

---

## 실행 흐름

```
Phase 1: 갭 분석 (Audit)
    ↓
Phase 2: 자동 보완 (Fix) — HIGH → MEDIUM → LOW 순서
    ↓
Phase 3: 검증 및 리포트 (Verify)
```

`--audit-only` → Phase 1만 수행
`--dry-run` → Phase 1 + Phase 2 계획만 출력
`--no-run` → Phase 1 + Phase 2 수행 (테스트 실행 안 함)
기본 → 전체 수행

---

## Phase 1: 갭 분석 (Audit)

소스 코드 대비 테스트 코드의 커버리지 갭을 6가지 유형으로 분석한다.

### 1.1 소스 파일 스캔

레이어별 소스 파일 경로:

| 레이어 | 소스 경로 |
|--------|----------|
| domain | `domain/src/main/java/**/domain/{package}/` |
| application | `application/src/main/java/**/application/{package}/` |
| adapter-out | `adapter-out/persistence-mysql/src/main/java/**/{package}/` |
| adapter-in | `rest-api-admin/src/main/java/**/{package}/` 또는 `rest-api/src/main/java/**/{package}/` |

### 1.2 테스트 파일 매핑

소스 클래스별로 대응하는 테스트 파일이 존재하는지 확인한다.

| 소스 | 예상 테스트 위치 |
|------|-----------------|
| `Seller.java` (Aggregate) | `domain/src/test/java/**/SellerTest.java` |
| `RegisterSellerService.java` | `application/src/test/java/**/RegisterSellerServiceTest.java` |
| `SellerJpaAdapter.java` | `adapter-out/.../SellerJpaAdapterTest.java` |

### 1.3 갭 유형 분류

| 유형 | 코드 | 설명 | 우선순위 가중치 |
|------|------|------|---------------|
| 테스트 파일 없음 | `MISSING_TEST` | 소스에 대응하는 테스트 없음 | +3 |
| Fixture 없음 | `MISSING_FIXTURES` | testFixtures 없어 하드코딩 | +1 |
| 메서드 누락 | `MISSING_METHOD` | public 메서드 테스트 안 됨 | +2 |
| 엣지케이스 부족 | `MISSING_EDGE_CASE` | 예외/null/경계값 미검증 | +1 |
| 상태 전이 누락 | `MISSING_STATE_TRANSITION` | 상태 변경 시나리오 없음 | +2 |
| 컨벤션 위반 | `PATTERN_VIOLATION` | 프로젝트 테스트 패턴 불일치 | +1 |

### 1.4 우선순위 판정 (3축 복합)

최종 우선순위 = 갭 유형 가중치 + 클래스 역할 가중치 + 복잡도 가중치

**클래스 역할 가중치**:
| 역할 | 가중치 | 예시 |
|------|--------|------|
| Aggregate / Service | +3 | Seller, RegisterSellerService |
| Entity / Factory | +2 | SellerBusinessInfo, SellerFactory |
| VO / Assembler / Mapper | +1 | SellerId, SellerAssembler |

**복잡도 가중치**:
| public 메서드 수 | 가중치 |
|-----------------|--------|
| 5개 이상 | +3 |
| 3~4개 | +2 |
| 1~2개 | +1 |

**최종 우선순위 결정**:
| 합산 점수 | 우선순위 |
|----------|---------|
| 7점 이상 | HIGH |
| 4~6점 | MEDIUM |
| 1~3점 | LOW |

---

## Phase 2: 자동 보완 (Fix)

Phase 1에서 식별된 갭을 우선순위 순서(HIGH → MEDIUM → LOW)로 보완한다.

### 2.1 갭별 처리 방식

| 갭 유형 | 처리 방식 | 도구 |
|---------|----------|------|
| `MISSING_TEST` | 새 테스트 파일 생성 | Write |
| `MISSING_FIXTURES` | 새 Fixtures 파일 생성 (testFixtures) | Write |
| `MISSING_METHOD` | 기존 테스트에 메서드 추가 | Edit |
| `MISSING_EDGE_CASE` | 기존 테스트에 케이스 추가 | Edit |
| `MISSING_STATE_TRANSITION` | 기존 테스트에 전이 시나리오 추가 | Edit |
| `PATTERN_VIOLATION` | 기존 테스트 수정 (컨벤션 맞춤) | Edit |

### 2.2 보완 시 준수 사항

각 레이어의 테스트 컨벤션을 반드시 따른다. 기존 같은 레이어의 테스트 파일을 2~3개 읽어서 패턴을 파악한 후 생성한다.

**Domain 레이어**:
- Aggregate: 생성, 상태 전이, 비즈니스 규칙 검증
- VO: 생성 검증, 동등성, 유효성 검사
- testFixtures 패턴: `{Domain}Fixtures.createDefault()`, `{Domain}Fixtures.create(...)`

**Application 레이어**:
- Mockito `@ExtendWith(MockitoExtension.class)` 기반
- Port/Repository를 `@Mock`, Service를 `@InjectMocks`
- `given().willReturn()` → `when` → `then` + `verify()` 패턴

**Adapter-Out 레이어**:
- `@DataJpaTest` 또는 `@SpringBootTest` 기반
- JPA Entity Fixtures 사용
- Mapper, ConditionBuilder, Adapter 각각 테스트

**Adapter-In 레이어**:
- RestDocs 기반 API 문서화 테스트
- ApiMapper 단위 테스트
- MockMvc 또는 REST Assured 사용

### 2.3 testFixtures 생성 규칙

갭 보완 시 testFixtures가 없으면 먼저 생성한다.

```java
// domain/src/testFixtures/java/.../fixtures/{Domain}Fixtures.java
public class SellerFixtures {

    public static Seller createDefault() {
        return Seller.create(
            "테스트셀러",
            SellerStatus.ACTIVE,
            // ... 기본값
        );
    }

    public static Seller create(String name, SellerStatus status) {
        return Seller.create(name, status, /* ... */);
    }
}
```

---

## Phase 3: 검증 및 리포트 (Verify)

### 3.1 테스트 실행

보완된 테스트를 실행하여 전부 통과하는지 확인한다.

```bash
# 레이어별 실행
./gradlew :{module}:test --tests "*{패턴}*"
```

실패하는 테스트가 있으면:
1. 에러 메시지 분석
2. 코드 수정
3. 재실행
4. 최대 3회 시도 후에도 실패하면 리포트에 기록

### 3.2 결과 리포트

콘솔에 요약 리포트를 출력한다.

```
══════════════════════════════════════════════
  테스트 커버리지 리포트: domain/seller
══════════════════════════════════════════════

📊 분석 결과:
   소스 클래스: 12개
   테스트 클래스: 10개 → 12개 (+2)
   커버리지: 83% → 100%

🔍 갭 분석:
   HIGH  : 2개 → 0개 ✅
   MEDIUM: 3개 → 0개 ✅
   LOW   : 1개 → 0개 ✅

📝 처리 내역:
   [HIGH]   MISSING_TEST    SellerTest.java → 신규 생성 ✅
   [HIGH]   MISSING_METHOD  SellerServiceTest#updateStatus → 추가 ✅
   [MEDIUM] MISSING_EDGE_CASE SellerTest#createWithInvalidName → 추가 ✅
   [MEDIUM] MISSING_FIXTURES SellerFixtures.java → 신규 생성 ✅
   [MEDIUM] MISSING_STATE_TRANSITION SellerTest#stateTransition → 추가 ✅
   [LOW]    PATTERN_VIOLATION SellerIdTest → 컨벤션 수정 ✅

🧪 테스트 실행:
   전체: 24개 | 성공: 24개 | 실패: 0개
   실행 시간: 3.2초

══════════════════════════════════════════════
```

### 3.3 리포트 파일 저장

분석 결과를 문서로도 저장한다.

```
→ claudedocs/test-coverage/{layer}-{package}-report.md
```

리포트 파일에는 다음을 포함한다:
- 분석 일시
- 갭 분석 상세 (유형, 우선순위, 대상 클래스, 처리 상태)
- 생성/수정된 파일 목록
- 테스트 실행 결과
- 남은 이슈 (있는 경우)

---

## 옵션별 동작 요약

| 옵션 | Phase 1 (분석) | Phase 2 (보완) | Phase 3 (검증) |
|------|--------------|--------------|--------------|
| (기본) | 전체 분석 | 전체 보완 | 실행 + 리포트 |
| `--audit-only` | 전체 분석 | 건너뜀 | 리포트만 |
| `--dry-run` | 전체 분석 | 계획만 출력 | 건너뜀 |
| `--no-run` | 전체 분석 | 전체 보완 | 리포트만 (실행 안 함) |
| `--high-only` | 전체 분석 | HIGH만 보완 | HIGH만 검증 |
| `--all` | 레이어 전체 | 전체 보완 | 전체 검증 |
