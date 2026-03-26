---
name: test-repository
description: persistence-mysql 모듈 Repository 테스트 자동 생성. testFixtures + Mapper/ConditionBuilder 단위 테스트 + QueryDsl 통합 테스트. Repository 테스트, JPA 테스트, 영속성 테스트 요청 시 사용.
context: fork
agent: repository-tester
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-repository

persistence-mysql 모듈의 Repository 테스트를 자동 생성한다.
Mapper, ConditionBuilder 단위 테스트 + QueryDsl/JPA 통합 테스트.

## 사용법

```bash
/test-repository selleradmin
/test-repository brand --unit-only
/test-repository category --integration-only
/test-repository seller --fixtures-only
/test-repository order --no-run
```

## 입력

- `$ARGUMENTS[0]`: 패키지명 (예: selleradmin, brand, category)
- `$ARGUMENTS[1]`: (선택) `--unit-only`, `--integration-only`, `--fixtures-only`, `--no-run`

---

## 실행 흐름

### Step 1: 소스 분석

대상 패키지의 Adapter-Out 클래스를 스캔한다.

```
adapter-out/persistence-mysql/src/main/java/com/ryuqq/marketplace/adapter/out/persistence/{package}/
```

클래스 유형별 분류:
- **JpaEntity**: `{Domain}JpaEntity` — JPA 엔티티 매핑
- **Mapper**: `{Domain}Mapper` — JpaEntity ↔ Domain 변환
- **ConditionBuilder**: `{Domain}ConditionBuilder` — QueryDsl 조건 빌더
- **JpaAdapter**: `{Domain}JpaAdapter` — Port 구현체 (Command/Query)
- **JpaRepository**: `{Domain}JpaRepository` — Spring Data JPA
- **QueryDslRepository**: `{Domain}QueryDslRepository` — QueryDsl 커스텀 쿼리

### Step 2: testFixtures 생성

JPA Entity Fixtures를 생성한다. Domain Fixtures와 별도로 관리한다.

```java
package com.ryuqq.marketplace.adapter.out.persistence.{package};

public class {Domain}JpaEntityFixtures {

    public static {Domain}JpaEntity createDefault() {
        return {Domain}JpaEntity.builder()
            .name("테스트")
            .status({Domain}Status.ACTIVE)
            // ...
            .build();
    }

    public static {Domain}JpaEntity create(String name, {Domain}Status status) {
        return {Domain}JpaEntity.builder()
            .name(name)
            .status(status)
            // ...
            .build();
    }
}
```

### Step 3: 단위 테스트 생성

#### Mapper 테스트

```java
@Tag("unit")
@DisplayName("{Domain}Mapper 테스트")
class {Domain}MapperTest {

    private final {Domain}Mapper sut = new {Domain}Mapper();

    @Nested
    @DisplayName("toDomain")
    class ToDomain {
        @Test
        @DisplayName("JpaEntity → Domain 변환 성공")
        void convertToDomain() {
            // given
            var entity = {Domain}JpaEntityFixtures.createDefault();

            // when
            var domain = sut.toDomain(entity);

            // then
            assertThat(domain.getName()).isEqualTo(entity.getName());
            assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
        }
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {
        @Test
        @DisplayName("Domain → JpaEntity 변환 성공")
        void convertToEntity() {
            // given
            var domain = {Domain}Fixtures.createDefault();

            // when
            var entity = sut.toEntity(domain);

            // then
            assertThat(entity.getName()).isEqualTo(domain.getName());
        }
    }
}
```

#### ConditionBuilder 테스트

```java
@Tag("unit")
@DisplayName("{Domain}ConditionBuilder 테스트")
class {Domain}ConditionBuilderTest {

    @Test
    @DisplayName("이름 조건 생성")
    void nameCondition() {
        // given
        var condition = new {Domain}SearchCondition("테스트", null, null);

        // when
        var predicate = {Domain}ConditionBuilder.build(condition);

        // then
        assertThat(predicate).isNotNull();
    }

    @Test
    @DisplayName("조건 없으면 null 반환")
    void noCondition() {
        // given
        var condition = new {Domain}SearchCondition(null, null, null);

        // when
        var predicate = {Domain}ConditionBuilder.build(condition);

        // then - 전체 조회 (조건 없음)
    }
}
```

### Step 4: 통합 테스트 생성

#### QueryDsl Repository 테스트

```java
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = {PersistenceMysqlTestApplication.class})
@DisplayName("{Domain}QueryDslRepository 통합 테스트")
class {Domain}QueryDslRepositoryTest {

    @Autowired private EntityManager em;
    private JPAQueryFactory queryFactory;
    private {Domain}QueryDslRepository sut;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(em);
        sut = new {Domain}QueryDslRepository(queryFactory);
    }

    @Nested
    @DisplayName("findByCondition")
    class FindByCondition {
        @Test
        @DisplayName("조건에 맞는 데이터 조회")
        void findWithCondition() {
            // given: 테스트 데이터 저장
            var entity = {Domain}JpaEntityFixtures.createDefault();
            em.persist(entity);
            em.flush();
            em.clear();

            // when
            var results = sut.findByCondition(/* condition */);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getName()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("soft-delete된 데이터는 조회되지 않음")
        void excludeDeleted() {
            // given
            var entity = {Domain}JpaEntityFixtures.create("삭제됨", {Domain}Status.DELETED);
            em.persist(entity);
            em.flush();
            em.clear();

            // when
            var results = sut.findByCondition(/* condition with notDeleted */);

            // then
            assertThat(results).isEmpty();
        }
    }
}
```

**통합 테스트 핵심 패턴**:
- `@DataJpaTest` + `@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)`
- `EntityManager`로 직접 데이터 시딩 (`persist` → `flush` → `clear`)
- soft-delete 필터 검증 필수
- 페이지네이션 검증 (offset, limit)

### Step 5: 테스트 실행

```bash
./gradlew :adapter-out:persistence-mysql:test --tests "*{패턴}*"
```

---

## 생성 파일 구조

```
adapter-out/persistence-mysql/src/testFixtures/java/.../{package}/
└── {Domain}JpaEntityFixtures.java

adapter-out/persistence-mysql/src/test/java/.../{package}/
├── mapper/
│   └── {Domain}MapperTest.java
├── condition/
│   └── {Domain}ConditionBuilderTest.java
├── adapter/
│   └── {Domain}JpaAdapterTest.java
└── repository/
    ├── {Domain}JpaRepositoryTest.java
    └── {Domain}QueryDslRepositoryTest.java
```

## 코드 품질 체크리스트

- [ ] 단위 테스트: `@Tag("unit")`, 통합 테스트: `@Tag("integration")`
- [ ] Mapper 테스트: 양방향 변환 (toDomain + toEntity) 모두 검증
- [ ] QueryDsl 테스트: `@DataJpaTest` + `PersistenceMysqlTestApplication` 사용
- [ ] soft-delete 필터 검증 포함 (notDeleted 조건)
- [ ] 페이지네이션/정렬 검증 포함 (해당 시)
- [ ] `em.flush(); em.clear();` 후 조회 (영속성 컨텍스트 캐시 방지)
- [ ] testFixtures로 JpaEntity 생성 (하드코딩 금지)
- [ ] `@Nested` + `@DisplayName` 구조화
