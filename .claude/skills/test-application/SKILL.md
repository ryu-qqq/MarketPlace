---
name: test-application
description: Application 레이어 테스트 자동 생성. testFixtures + Service/Factory/Assembler/Manager Mockito 기반 단위 테스트. 서비스 테스트, 유즈케이스 테스트, Application 단위 테스트 요청 시 사용.
context: fork
agent: application-tester
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-application

Application 레이어의 테스트를 자동 생성한다. Mockito 기반 Service, Factory, Assembler, Manager 단위 테스트.

## 사용법

```bash
/test-application seller
/test-application brand --fixtures-only
/test-application category --service-only
/test-application order --factory-only
/test-application payment --no-run
```

## 입력

- `$ARGUMENTS[0]`: 패키지명 (예: seller, brand, category, order)
- `$ARGUMENTS[1]`: (선택) `--fixtures-only`, `--service-only`, `--factory-only`, `--assembler-only`, `--no-run`

---

## 실행 흐름

### Step 1: 소스 분석

대상 패키지의 Application 클래스를 모두 스캔한다.

```
application/src/main/java/com/ryuqq/marketplace/application/{package}/
```

클래스 유형별 분류:
- **Service (Command)**: `Register{Domain}Service`, `Update{Domain}Service` 등 — UseCase 구현체
- **Service (Query)**: `Get{Domain}Service`, `Search{Domain}Service` 등 — ReadUseCase 구현체
- **Factory**: `{Domain}CommandFactory` — Command → Domain 객체 변환
- **Assembler**: `{Domain}Assembler` — Domain → Query 응답 변환
- **Manager**: `{Domain}CommandManager`, `{Domain}ReadManager` — 복합 오케스트레이션
- **Coordinator**: `{Domain}Coordinator` — 도메인 간 조율
- **Validator**: `{Domain}Validator` — 비즈니스 규칙 검증

### Step 2: 의존성 분석

각 클래스의 생성자 주입 의존성을 파악하여 Mock 대상을 결정한다.

- **Port/Repository 인터페이스** → `@Mock`
- **다른 Service** → `@Mock`
- **대상 클래스** → `@InjectMocks`

### Step 3: testFixtures 생성

Command/Query Fixtures를 생성한다.

**Command Fixtures 패턴**:

```java
package com.ryuqq.marketplace.application.{package};

public class {Domain}CommandFixtures {

    public static Register{Domain}Command registerCommand() {
        return new Register{Domain}Command(
            // 기본 파라미터 값
        );
    }

    public static Update{Domain}Command updateCommand() {
        return new Update{Domain}Command(
            // 기본 파라미터 값
        );
    }

    // 파라미터화된 생성
    public static Register{Domain}Command registerCommand(String name) {
        return new Register{Domain}Command(name, /* ... */);
    }
}
```

**Query Fixtures 패턴**:

```java
public class {Domain}QueryFixtures {

    public static {Domain}QueryResponse queryResponse() {
        return new {Domain}QueryResponse(
            // 기본 응답 값
        );
    }

    public static List<{Domain}QueryResponse> queryResponseList(int size) {
        return IntStream.range(0, size)
            .mapToObj(i -> queryResponse())
            .toList();
    }
}
```

### Step 4: 테스트 코드 생성

#### Service (Command) 테스트

```java
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Register{Domain}Service 테스트")
class Register{Domain}ServiceTest {

    @Mock private {Domain}CommandPort {domain}CommandPort;
    @Mock private {Domain}QueryPort {domain}QueryPort;
    @InjectMocks private Register{Domain}Service sut;

    @Nested
    @DisplayName("execute")
    class Execute {
        @Test
        @DisplayName("유효한 커맨드로 등록 성공")
        void executeWithValidCommand() {
            // given
            var command = {Domain}CommandFixtures.registerCommand();
            given({domain}CommandPort.save(any())).willReturn(1L);

            // when
            var result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(1L);
            then({domain}CommandPort).should().save(any({Domain}.class));
        }
    }
}
```

**핵심 패턴**:
- `@ExtendWith(MockitoExtension.class)` 필수
- BDD Mockito: `given().willReturn()` + `then().should().method()`
- `sut` (System Under Test) 네이밍 권장
- Port/Repository는 반드시 `@Mock`

#### Service (Query) 테스트

```java
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("Get{Domain}Service 테스트")
class Get{Domain}ServiceTest {

    @Mock private {Domain}QueryPort {domain}QueryPort;
    @InjectMocks private Get{Domain}Service sut;

    @Nested
    @DisplayName("getById")
    class GetById {
        @Test
        @DisplayName("존재하는 ID로 조회 성공")
        void getByIdSuccess() {
            // given
            var expected = {Domain}Fixtures.createDefault();
            given({domain}QueryPort.findById(1L)).willReturn(Optional.of(expected));

            // when
            var result = sut.getById(1L);

            // then
            assertThat(result.name()).isEqualTo(expected.getName());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외")
        void getByIdNotFound() {
            // given
            given({domain}QueryPort.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(999L))
                .isInstanceOf(MarketPlaceException.class);
        }
    }
}
```

#### Factory 테스트

```java
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("{Domain}CommandFactory 테스트")
class {Domain}CommandFactoryTest {

    @InjectMocks private {Domain}CommandFactory sut;

    @Test
    @DisplayName("RegisterCommand → Domain 변환 성공")
    void createFromRegisterCommand() {
        // given
        var command = {Domain}CommandFixtures.registerCommand();

        // when
        var domain = sut.create(command);

        // then
        assertThat(domain.getName()).isEqualTo(command.name());
        assertThat(domain.getStatus()).isEqualTo({Domain}Status.ACTIVE);
    }
}
```

#### Assembler 테스트

```java
@Tag("unit")
@DisplayName("{Domain}Assembler 테스트")
class {Domain}AssemblerTest {

    private final {Domain}Assembler sut = new {Domain}Assembler();

    @Test
    @DisplayName("Domain → QueryResponse 변환 성공")
    void toQueryResponse() {
        // given
        var domain = {Domain}Fixtures.createDefault();

        // when
        var response = sut.toQueryResponse(domain);

        // then
        assertThat(response.name()).isEqualTo(domain.getName());
    }
}
```

### Step 5: 테스트 실행 (--no-run이 아닌 경우)

```bash
./gradlew :application:test --tests "*{Domain}*"
```

---

## 생성 파일 구조

```
application/src/testFixtures/java/.../application/{package}/
├── {Domain}CommandFixtures.java
└── {Domain}QueryFixtures.java

application/src/test/java/.../application/{package}/
├── service/
│   ├── command/
│   │   ├── Register{Domain}ServiceTest.java
│   │   └── Update{Domain}ServiceTest.java
│   └── query/
│       └── Get{Domain}ServiceTest.java
├── factory/
│   └── {Domain}CommandFactoryTest.java
├── assembler/
│   └── {Domain}AssemblerTest.java
├── manager/
│   ├── {Domain}CommandManagerTest.java
│   └── {Domain}ReadManagerTest.java
├── internal/
│   └── {Domain}CoordinatorTest.java
└── validator/
    └── {Domain}ValidatorTest.java
```

## 코드 품질 체크리스트

- [ ] `@Tag("unit")` 태그 포함
- [ ] `@ExtendWith(MockitoExtension.class)` 사용
- [ ] Port/Repository는 `@Mock`, 대상 클래스는 `@InjectMocks`
- [ ] BDD Mockito 패턴 (`given/willReturn` + `then/should`)
- [ ] testFixtures의 Command/Query Fixtures 활용
- [ ] Domain Fixtures도 활용 (domain 모듈 testFixtures 의존)
- [ ] 정상 케이스 + 예외 케이스 모두 포함
- [ ] `@Nested` + `@DisplayName`으로 메서드별 그룹화
- [ ] 불필요한 `verify()` 남발 금지 — 행위 검증은 핵심 상호작용만
