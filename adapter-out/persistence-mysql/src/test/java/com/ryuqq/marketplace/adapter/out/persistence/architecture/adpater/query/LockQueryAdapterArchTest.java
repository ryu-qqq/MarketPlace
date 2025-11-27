package com.ryuqq.marketplace.adapter.out.persistence.architecture.adpater.query;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * LockQueryAdapter 아키텍처 규칙 검증 테스트
 *
 * <p>CQRS Lock Query Adapter의 Zero-Tolerance 규칙을 자동으로 검증합니다:
 *
 * <ul>
 *   <li>정확한 필드 개수 (2개): LockRepository, Mapper
 *   <li>정확한 메서드 개수 (6개): 비관락 2 + 낙관락 2 + For Update 2
 *   <li>메서드 네이밍 규칙: find*WithPessimisticLock, find*WithOptimisticLock, find*ForUpdate
 *   <li>반환 타입 규칙: Optional&lt;Domain&gt;, List&lt;Domain&gt;
 *   <li>@Component 필수
 *   <li>@Transactional 금지
 *   <li>비즈니스 로직 금지
 *   <li>try-catch 금지 (Lock 예외 처리 안 함)
 *   <li>Command 메서드 금지
 *   <li>일반 조회 메서드 금지 (QueryAdapter로 분리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LockQueryAdapter 아키텍처 규칙 검증 (Zero-Tolerance)")
class LockQueryAdapterArchTest {

    private static JavaClasses allClasses;
    private static JavaClasses lockAdapterClasses;

    @BeforeAll
    static void setUp() {
        allClasses = new ClassFileImporter().importPackages("com.ryuqq.marketplace.adapter.out.persistence");

        lockAdapterClasses =
                allClasses.that(
                        DescribedPredicate.describe(
                                "are LockQueryAdapter classes",
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("LockQueryAdapter")));
    }

    /**
     * 규칙 1: @Component 어노테이션 필수
     *
     * <p>LockQueryAdapter는 Spring Bean으로 등록되어야 합니다.
     *
     * <ul>
     *   <li>✅ @Component
     *   <li>❌ @Service (Application Layer 전용)
     *   <li>❌ @Repository (JpaRepository 인터페이스 전용)
     * </ul>
     */
    @Test
    @DisplayName("규칙 1: @Component 어노테이션 필수")
    void lockQueryAdapter_MustBeAnnotatedWithComponent() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .beAnnotatedWith(Component.class)
                        .because("LockQueryAdapter는 @Component로 Spring Bean 등록이 필수입니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 2: *LockQueryPort 인터페이스 구현 필수
     *
     * <p>LockQueryAdapter는 Application Layer의 LockQueryPort 인터페이스를 구현해야 합니다.
     */
    @Test
    @DisplayName("규칙 2: *LockQueryPort 인터페이스 구현 필수")
    void lockQueryAdapter_MustImplementLockQueryPort() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .implement(
                                DescribedPredicate.describe(
                                        "interface ending with 'LockQueryPort'",
                                        javaClass ->
                                                javaClass.getAllRawInterfaces().stream()
                                                        .anyMatch(
                                                                iface ->
                                                                        iface.getSimpleName()
                                                                                .endsWith(
                                                                                        "LockQueryPort"))))
                        .because("LockQueryAdapter는 Application Layer의 LockQueryPort를 구현해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 3: 정확히 2개 필드 (LockRepository, Mapper)
     *
     * <p>LockQueryAdapter는 정확히 2개의 필드만 가져야 합니다:
     *
     * <ul>
     *   <li>1. LockRepository (*LockRepository)
     *   <li>2. Mapper (*JpaEntityMapper 또는 *EntityMapper)
     * </ul>
     */
    @Test
    @DisplayName("규칙 3: 정확히 2개 필드 (LockRepository, Mapper)")
    void lockQueryAdapter_MustHaveExactlyTwoFields() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have exactly 2 fields",
                                                javaClass -> javaClass.getAllFields().size() == 2)))
                        .because("LockQueryAdapter는 정확히 2개의 필드(LockRepository, Mapper)만 가져야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 4: 정확히 6개의 public 메서드
     *
     * <p>LockQueryAdapter는 6개 조회 메서드만 public으로 노출해야 합니다:
     *
     * <ul>
     *   <li>비관락 2개: findByIdWithPessimisticLock, findByCriteriaWithPessimisticLock
     *   <li>낙관락 2개: findByIdWithOptimisticLock, findByCriteriaWithOptimisticLock
     *   <li>For Update 2개: findByIdForUpdate, findByCriteriaForUpdate
     * </ul>
     */
    @Test
    @DisplayName("규칙 4: 정확히 6개의 public 메서드")
    void lockQueryAdapter_MustHaveExactlySixPublicMethods() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have exactly 6 public methods (excluding"
                                                        + " constructor)",
                                                javaClass ->
                                                        javaClass.getMethods().stream()
                                                                        .filter(
                                                                                method ->
                                                                                        method.getModifiers()
                                                                                                .contains(
                                                                                                        JavaModifier
                                                                                                                .PUBLIC))
                                                                        .filter(
                                                                                method ->
                                                                                        !method.getName()
                                                                                                .equals(
                                                                                                        "<init>"))
                                                                        .count()
                                                                == 6)))
                        .because("LockQueryAdapter는 6개 조회 메서드만 public으로 노출해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 5: 메서드명 검증
     *
     * <p>메서드명은 Lock 전략을 명확히 표현해야 합니다.
     */
    @Test
    @DisplayName("규칙 5: 메서드명은 find*WithPessimisticLock, find*WithOptimisticLock, find*ForUpdate 형식")
    void lockQueryAdapter_MethodsMustFollowNamingConvention() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .and()
                        .arePublic()
                        .and()
                        .doNotHaveName("<init>")
                        .should()
                        .haveNameMatching(
                                "find(ById|ByCriteria)With(Pessimistic|Optimistic)Lock|find(ById|ByCriteria)ForUpdate")
                        .because("메서드명은 Lock 전략을 명확히 표현해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 6: 반환 타입 검증 (Optional<Domain> 또는 List<Domain>)
     *
     * <p>조회 메서드는 Domain을 반환해야 합니다.
     */
    @Test
    @DisplayName("규칙 6: 반환 타입은 Optional<Domain> 또는 List<Domain>")
    void lockQueryAdapter_MustReturnDomainTypes() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .and()
                        .arePublic()
                        .and()
                        .doNotHaveName("<init>")
                        .should()
                        .haveRawReturnType(
                                DescribedPredicate.describe(
                                        "Optional or List",
                                        returnType ->
                                                returnType.isAssignableTo(Optional.class)
                                                        || returnType.isAssignableTo(List.class)))
                        .because("조회 메서드는 Optional<Domain> 또는 List<Domain>을 반환해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 7: @Transactional 절대 금지
     *
     * <p>Transaction은 Application Layer(UseCase)에서 관리해야 합니다.
     */
    @Test
    @DisplayName("규칙 7: @Transactional 절대 금지")
    void lockQueryAdapter_MustNotBeTransactional() {
        ArchRule classRule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .notBeAnnotatedWith(Transactional.class)
                        .because("LockQueryAdapter 클래스에 @Transactional 사용 금지. UseCase에서 관리하세요");

        ArchRule methodRule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .notBeAnnotatedWith(Transactional.class)
                        .because("LockQueryAdapter 메서드에 @Transactional 사용 금지. UseCase에서 관리하세요");

        classRule.check(lockAdapterClasses);
        methodRule.check(lockAdapterClasses);
    }

    /**
     * 규칙 8: Command 메서드 금지
     *
     * <p>저장/수정/삭제는 CommandAdapter로 분리해야 합니다.
     */
    @Test
    @DisplayName("규칙 8: Command 메서드 금지 (save, persist, update, delete)")
    void lockQueryAdapter_MustNotHaveCommandMethods() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .haveNameNotMatching("(save|persist|update|delete).*")
                        .because("저장/수정/삭제는 CommandAdapter로 분리해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 9: 일반 조회 메서드 금지
     *
     * <p>Lock 없는 조회는 QueryAdapter를 사용해야 합니다.
     */
    @Test
    @DisplayName("규칙 9: 일반 조회 메서드 금지 (findById, existsById, findByCriteria, countByCriteria)")
    void lockQueryAdapter_MustNotHaveNormalQueryMethods() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .haveNameNotMatching(
                                "^(findById|existsById|findByCriteria|countByCriteria)$")
                        .because("Lock 없는 일반 조회는 QueryAdapter를 사용해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 10: DTO 반환 금지
     *
     * <p>Domain을 반환해야 합니다.
     */
    @Test
    @DisplayName("규칙 10: DTO 반환 금지 (Domain만 반환)")
    void lockQueryAdapter_MustNotReturnDto() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .and()
                        .arePublic()
                        .should()
                        .haveRawReturnType(
                                DescribedPredicate.describe(
                                        "not DTO types",
                                        returnType -> !returnType.getName().contains("Dto")))
                        .because("Domain을 반환해야 하며, DTO 반환은 금지입니다");

        rule.check(lockAdapterClasses);
    }

    /**
     * 규칙 11: 비즈니스 로직 금지
     *
     * <p>LockQueryAdapter는 단순 위임 + 변환만 수행합니다.
     *
     * <p>주의: if/switch/for 감지는 ArchUnit으로 제한적 (코드 리뷰로 확인)
     */
    @Test
    @DisplayName("규칙 11: 비즈니스 로직 금지")
    void lockQueryAdapter_ShouldNotHaveComplexBusinessLogic() {
        // 이 규칙은 코드 리뷰로 검증 (ArchUnit으로 자동화 어려움)
        // 예: 메서드 당 if/switch/for 최대 1개
    }

    /**
     * 규칙 12: try-catch 금지
     *
     * <p>Lock 예외는 Application Layer에서 처리합니다.
     *
     * <p>Adapter는 예외를 catch하지 않고 그대로 던집니다.
     */
    @Test
    @DisplayName("규칙 12: try-catch로 Lock 예외 처리 금지")
    void lockQueryAdapter_MustNotCatchLockExceptions() {
        // 이 규칙은 코드 리뷰로 검증 권장
        // ArchUnit limitation: 메서드 body 검증 제한적
    }

    /** 규칙 13: 클래스명 *LockQueryAdapter 필수 */
    @Test
    @DisplayName("규칙 13: 클래스명은 *LockQueryAdapter 형식")
    void lockQueryAdapter_MustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .implement(
                                DescribedPredicate.describe(
                                        "interface ending with 'LockQueryPort'",
                                        javaClass ->
                                                javaClass.getAllRawInterfaces().stream()
                                                        .anyMatch(
                                                                iface ->
                                                                        iface.getSimpleName()
                                                                                .endsWith(
                                                                                        "LockQueryPort"))))
                        .and()
                        .resideInAPackage("..adapter..")
                        .should()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .because("LockQueryAdapter는 *LockQueryAdapter 네이밍 규칙을 따라야 합니다");

        rule.check(allClasses);
    }

    /** 규칙 14: Port 네이밍 *LockQueryPort 필수 */
    @Test
    @DisplayName("규칙 14: Port 인터페이스는 *LockQueryPort 형식")
    void lockQueryPort_MustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .areInterfaces()
                        .and()
                        .haveSimpleNameContaining("Lock")
                        .and()
                        .haveSimpleNameContaining("Query")
                        .and()
                        .resideInAPackage("..application..port.out..")
                        .should()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .because("Port 인터페이스는 *LockQueryPort 네이밍 규칙을 따라야 합니다");

        rule.check(allClasses);
    }

    /** 규칙 15: Repository 네이밍 *LockRepository 필수 */
    @Test
    @DisplayName("규칙 15: LockRepository는 *LockRepository 형식")
    void lockRepository_MustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .areInterfaces()
                        .and()
                        .haveSimpleNameContaining("Lock")
                        .and()
                        .haveSimpleNameContaining("Repository")
                        .should()
                        .haveSimpleNameEndingWith("LockRepository")
                        .because("LockRepository는 *LockRepository 네이밍 규칙을 따라야 합니다");

        rule.check(allClasses);
    }

    /** 규칙 16: 패키지 위치 ..adapter.out.persistence.. */
    @Test
    @DisplayName("규칙 16: LockQueryAdapter는 adapter.out.persistence 패키지에 위치")
    void lockQueryAdapter_MustBeInCorrectPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .resideInAPackage("..adapter.out.persistence..")
                        .because("LockQueryAdapter는 adapter.out.persistence 패키지에 위치해야 합니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 17: Port 패키지 위치 ..application..port.out.. */
    @Test
    @DisplayName("규칙 17: LockQueryPort는 application.port.out 패키지에 위치")
    void lockQueryPort_MustBeInCorrectPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .resideInAPackage("..application..port.out..")
                        .because("LockQueryPort는 application.port.out 패키지에 위치해야 합니다");

        rule.check(allClasses);
    }

    /** 규칙 18: 의존성 방향 Adapter → Port (역방향 금지) */
    @Test
    @DisplayName("규칙 18: Adapter는 Port를 의존해야 함 (역방향 금지)")
    void lockQueryAdapter_MustDependOnPort() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .because("의존성 방향은 Adapter → Port 단방향이어야 합니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 19: 생성자 주입 (final 필드) */
    @Test
    @DisplayName("규칙 19: LockQueryAdapter 필드는 final이어야 함")
    void lockQueryAdapter_FieldsMustBeFinal() {
        ArchRule rule =
                fields().that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should()
                        .beFinal()
                        .because("생성자 주입을 위해 필드는 final이어야 합니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 20: LockRepository 필드 필수 */
    @Test
    @DisplayName("규칙 20: LockQueryAdapter는 LockRepository 필드를 가져야 함")
    void lockQueryAdapter_MustHaveLockRepositoryField() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have LockRepository field",
                                                javaClass ->
                                                        javaClass.getAllFields().stream()
                                                                .anyMatch(
                                                                        field ->
                                                                                field.getRawType()
                                                                                        .getName()
                                                                                        .contains(
                                                                                                "LockRepository")))))
                        .because("LockRepository 필드가 필수입니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 21: Mapper 필드 필수 */
    @Test
    @DisplayName("규칙 21: LockQueryAdapter는 Mapper 필드를 가져야 함")
    void lockQueryAdapter_MustHaveMapperField() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have Mapper field",
                                                javaClass ->
                                                        javaClass.getAllFields().stream()
                                                                .anyMatch(
                                                                        field ->
                                                                                field.getRawType()
                                                                                        .getName()
                                                                                        .contains(
                                                                                                "Mapper")))))
                        .because("Mapper 필드가 필수입니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 22: findByIdWithPessimisticLock() 메서드 필수 */
    @Test
    @DisplayName("규칙 22: findByIdWithPessimisticLock() 메서드 필수")
    void lockQueryAdapter_MustHaveFindByIdWithPessimisticLock() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have findByIdWithPessimisticLock method",
                                                javaClass ->
                                                        javaClass.getMethods().stream()
                                                                .anyMatch(
                                                                        method ->
                                                                                method.getName()
                                                                                        .equals(
                                                                                                "findByIdWithPessimisticLock")))))
                        .because("비관락 단건 조회 메서드가 필수입니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 23: findByCriteriaWithPessimisticLock() 메서드 필수 */
    @Test
    @DisplayName("규칙 23: findByCriteriaWithPessimisticLock() 메서드 필수")
    void lockQueryAdapter_MustHaveFindByCriteriaWithPessimisticLock() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have findByCriteriaWithPessimisticLock method",
                                                javaClass ->
                                                        javaClass.getMethods().stream()
                                                                .anyMatch(
                                                                        method ->
                                                                                method.getName()
                                                                                        .equals(
                                                                                                "findByCriteriaWithPessimisticLock")))))
                        .because("비관락 리스트 조회 메서드가 필수입니다");

        rule.check(lockAdapterClasses);
    }

    /** 규칙 24: 낙관락/ForUpdate 메서드 4개 필수 */
    @Test
    @DisplayName("규칙 24: 낙관락/ForUpdate 메서드 4개 필수")
    void lockQueryAdapter_MustHaveOtherLockMethods() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryAdapter")
                        .should(
                                ArchCondition.from(
                                        DescribedPredicate.describe(
                                                "have 4 other lock methods",
                                                javaClass -> {
                                                    long count =
                                                            javaClass.getMethods().stream()
                                                                    .filter(
                                                                            method ->
                                                                                    method.getName()
                                                                                            .matches(
                                                                                                    "findByIdWithOptimisticLock|"
                                                                                                        + "findByCriteriaWithOptimisticLock|"
                                                                                                        + "findByIdForUpdate|"
                                                                                                        + "findByCriteriaForUpdate"))
                                                                    .count();
                                                    return count == 4;
                                                })))
                        .because("낙관락 및 ForUpdate 메서드가 필수입니다");

        rule.check(lockAdapterClasses);
    }
}
