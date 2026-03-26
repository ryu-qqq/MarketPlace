---
name: test-domain
description: Domain 레이어 테스트 자동 생성. testFixtures + Aggregate/VO/Entity/Event 단위 테스트. 도메인 테스트, 도메인 단위 테스트, Aggregate 테스트, VO 테스트 요청 시 사용.
context: fork
agent: domain-tester
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-domain

Domain 레이어의 테스트를 자동 생성한다. Aggregate, VO, Entity, Domain Event 단위 테스트.

## 사용법

```bash
/test-domain seller
/test-domain brand --fixtures-only
/test-domain category --aggregate-only
/test-domain order --vo-only
/test-domain payment --no-run
```

## 입력

- `$ARGUMENTS[0]`: 패키지명 (예: seller, brand, category, order)
- `$ARGUMENTS[1]`: (선택) `--fixtures-only`, `--aggregate-only`, `--vo-only`, `--no-run`

---

## 실행 흐름

### Step 1: 소스 분석

대상 패키지의 Domain 클래스를 모두 스캔한다.

```
domain/src/main/java/com/ryuqq/marketplace/domain/{package}/
```

클래스 유형별 분류:
- **Aggregate**: 최상위 도메인 객체 (생성 팩토리 메서드, 상태 전이, 비즈니스 규칙)
- **Entity**: Aggregate 하위 엔티티
- **VO (Value Object)**: 불변 값 객체
- **Domain Id**: 식별자 VO (record 타입)
- **Domain Event**: 도메인 이벤트
- **ErrorCode**: enum 기반 에러 코드

### Step 2: 기존 테스트/Fixtures 확인

이미 존재하는 테스트와 Fixtures를 확인하여 중복 생성을 방지한다.

```
domain/src/test/java/com/ryuqq/marketplace/domain/{package}/
domain/src/testFixtures/java/com/ryuqq/marketplace/domain/{package}/
```

### Step 3: testFixtures 생성

테스트 데이터를 중앙 관리하는 Fixtures 클래스를 먼저 생성한다.
다른 모듈(application, adapter-out 등)에서도 재사용 가능하도록 testFixtures 디렉토리에 배치한다.

**Fixtures 패턴** (실제 프로젝트 기준):

```java
package com.ryuqq.marketplace.domain.{package};

/**
 * {Domain} 테스트 Fixture.
 * 다양한 상태의 {Domain} 인스턴스를 생성한다.
 */
public class {Domain}Fixtures {

    // 기본 생성 (가장 흔한 상태)
    public static {Domain} createDefault() {
        return {Domain}.create(
            // 기본 파라미터 값
        );
    }

    // 상태별 팩토리 메서드
    public static {Domain} active{Domain}() { /* ACTIVE 상태 */ }
    public static {Domain} deleted{Domain}() { /* DELETED 상태 */ }

    // 파라미터화된 생성
    public static {Domain} create(String name, {Domain}Status status) {
        return {Domain}.create(name, status, /* ... */);
    }
}
```

**핵심 규칙**:
- Aggregate마다 하나의 Fixtures 클래스
- `createDefault()` 메서드는 필수
- 상태별 팩토리 메서드 제공 (예: `activeSeller()`, `deletedSeller()`)
- 하위 Entity/VO도 같은 Fixtures에 포함 가능 (별도 클래스보다 응집도 높음)

### Step 4: 테스트 코드 생성

#### Aggregate 테스트

```java
@Tag("unit")
@DisplayName("{Domain} Aggregate 테스트")
class {Domain}Test {

    @Nested
    @DisplayName("생성")
    class Create {
        @Test
        @DisplayName("유효한 파라미터로 생성 성공")
        void createWithValidParams() {
            // given
            // when
            var domain = {Domain}.create(/* params */);
            // then
            assertThat(domain.getName()).isEqualTo(expected);
            assertThat(domain.getStatus()).isEqualTo({Domain}Status.ACTIVE);
        }

        @Test
        @DisplayName("필수값 누락 시 예외")
        void createWithNullName_throwsException() {
            // given & when & then
            assertThatThrownBy(() -> {Domain}.create(null, /* ... */))
                .isInstanceOf(MarketPlaceException.class);
        }
    }

    @Nested
    @DisplayName("상태 전이")
    class StateTransition {
        @Test
        @DisplayName("ACTIVE → DELETED 전이 성공")
        void activeToDeleted() {
            // given
            var domain = {Domain}Fixtures.active{Domain}();
            // when
            domain.delete();
            // then
            assertThat(domain.getStatus()).isEqualTo({Domain}Status.DELETED);
        }

        @Test
        @DisplayName("DELETED 상태에서 재삭제 시 예외")
        void deletedToDeleted_throwsException() {
            // given
            var domain = {Domain}Fixtures.deleted{Domain}();
            // when & then
            assertThatThrownBy(domain::delete)
                .isInstanceOf(MarketPlaceException.class);
        }
    }

    @Nested
    @DisplayName("비즈니스 규칙")
    class BusinessRules {
        // 도메인 특화 비즈니스 로직 테스트
    }
}
```

**Aggregate 테스트 필수 시나리오**:
1. 생성 (정상 + 유효성 실패)
2. 상태 전이 (모든 유효한 전이 + 불가능한 전이)
3. 비즈니스 규칙 (도메인 로직)
4. 수정 메서드 (update, patch 등)

#### VO 테스트

```java
@Tag("unit")
@DisplayName("{VoName} VO 테스트")
class {VoName}Test {

    @Test
    @DisplayName("유효한 값으로 생성")
    void createValid() {
        var vo = new {VoName}(/* valid params */);
        assertThat(vo.value()).isEqualTo(expected);
    }

    @Test
    @DisplayName("유효하지 않은 값으로 생성 시 예외")
    void createInvalid() {
        assertThatThrownBy(() -> new {VoName}(/* invalid */))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("동등성 검증")
    void equality() {
        var vo1 = new {VoName}(/* same value */);
        var vo2 = new {VoName}(/* same value */);
        assertThat(vo1).isEqualTo(vo2);
    }
}
```

#### Domain Event 테스트

```java
@Tag("unit")
@DisplayName("{Domain}Event 테스트")
class {Domain}EventTest {

    @Test
    @DisplayName("이벤트 생성 시 필수 필드 포함")
    void createEvent() {
        var event = new {Domain}CreatedEvent(/* params */);
        assertThat(event.domainId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();
    }
}
```

### Step 5: 테스트 실행 (--no-run이 아닌 경우)

```bash
./gradlew :domain:test --tests "*{Domain}*"
```

실패 시 에러를 분석하고 코드를 수정한 뒤 재실행한다 (최대 3회).

---

## 생성 파일 구조

```
domain/src/testFixtures/java/.../domain/{package}/
└── {Domain}Fixtures.java

domain/src/test/java/.../domain/{package}/
├── aggregate/
│   ├── {Domain}Test.java
│   └── {SubEntity}Test.java
├── vo/
│   └── {VoName}Test.java
├── id/
│   └── {DomainId}Test.java
├── event/
│   └── {Domain}EventTest.java
└── exception/
    └── {Domain}ErrorCodeTest.java
```

## 코드 품질 체크리스트

생성된 테스트 코드가 다음을 만족하는지 확인한다:

- [ ] `@Tag("unit")` 태그 포함
- [ ] `@Nested` + `@DisplayName`으로 계층 구조화
- [ ] BDD 주석 패턴 (// given, // when, // then)
- [ ] AssertJ 사용 (`assertThat`, `assertThatThrownBy`)
- [ ] testFixtures 재사용 (하드코딩된 테스트 데이터 금지)
- [ ] 모든 public 메서드에 대한 테스트 존재
- [ ] 예외 케이스 테스트 포함
- [ ] 상태 전이가 있다면 모든 전이 경로 커버
