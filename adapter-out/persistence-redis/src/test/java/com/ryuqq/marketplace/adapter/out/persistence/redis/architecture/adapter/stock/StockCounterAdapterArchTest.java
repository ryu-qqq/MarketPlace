package com.ryuqq.marketplace.adapter.out.persistence.redis.architecture.adapter.stock;

import static com.ryuqq.marketplace.adapter.out.persistence.redis.architecture.ArchUnitPackageConstants.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * StockCounterAdapterArchTest - Stock Counter Adapter 아키텍처 규칙 검증
 *
 * <p><strong>검증 항목:</strong>
 *
 * <ul>
 *   <li>클래스 구조: @Component, StockCounterPort 구현
 *   <li>의존성: RedisTemplate (Lettuce)
 *   <li>금지 사항: @Transactional, Redisson, DB 접근
 *   <li>메서드: decrement, increment, getStock 등
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("StockCounterAdapter 아키텍처 규칙 검증")
class StockCounterAdapterArchTest {

    private static JavaClasses allClasses;
    private static JavaClasses stockAdapterClasses;

    @BeforeAll
    static void setUp() {
        allClasses =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(REDIS);

        stockAdapterClasses =
                allClasses.that(
                        DescribedPredicate.describe(
                                "Stock Counter Adapter 클래스",
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("StockCounterAdapter")
                                                && !javaClass.isInterface()));
    }

    // ========================================================================
    // 1. 클래스 구조 규칙
    // ========================================================================

    @Nested
    @DisplayName("1. 클래스 구조 규칙")
    class ClassStructureRules {

        @Test
        @DisplayName("규칙 1-1: StockCounterAdapter는 클래스여야 합니다")
        void stockCounterAdapter_MustBeClass() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .notBeInterfaces()
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 클래스로 정의되어야 합니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-2: @Component 어노테이션이 필수입니다")
        void stockCounterAdapter_MustHaveComponentAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .beAnnotatedWith(Component.class)
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 @Component 어노테이션이 필수입니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-3: StockCounterPort 인터페이스를 구현해야 합니다")
        void stockCounterAdapter_MustImplementStockCounterPort() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "StockCounterPort 인터페이스 구현",
                                                    javaClass ->
                                                            javaClass.getAllRawInterfaces().stream()
                                                                    .anyMatch(
                                                                            iface ->
                                                                                    iface.getName()
                                                                                            .contains(
                                                                                                    "StockCounterPort")))))
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 StockCounterPort 인터페이스를 구현해야 합니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 1-4: 모든 필드는 final이어야 합니다")
        void stockCounterAdapter_AllFieldsMustBeFinal() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .resideInAPackage("..stock.adapter..")
                            .and()
                            .areNotStatic()
                            .should()
                            .beFinal()
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter의 모든 인스턴스 필드는 final로 불변성을 보장해야 합니다");

            rule.check(stockAdapterClasses);
        }
    }

    // ========================================================================
    // 2. 의존성 규칙
    // ========================================================================

    @Nested
    @DisplayName("2. 의존성 규칙")
    class DependencyRules {

        @Test
        @DisplayName("규칙 2-1: RedisTemplate 의존성이 필수입니다")
        void stockCounterAdapter_MustDependOnRedisTemplate() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "RedisTemplate 필드",
                                                    javaClass ->
                                                            javaClass.getAllFields().stream()
                                                                    .anyMatch(
                                                                            field ->
                                                                                    field.getRawType()
                                                                                            .getName()
                                                                                            .contains(
                                                                                                    "RedisTemplate")))))
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 RedisTemplate 의존성이 필수입니다 (Lettuce)");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2-2: RedissonClient 의존성이 금지됩니다")
        void stockCounterAdapter_MustNotDependOnRedisson() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*Redisson.*")
                            .allowEmptyShould(true)
                            .because(
                                    "Stock Counter Adapter는 Lettuce(RedisTemplate)만 사용해야 합니다."
                                            + " Redisson은 Lock 전용입니다");

            rule.check(stockAdapterClasses);
        }
    }

    // ========================================================================
    // 3. 메서드 규칙
    // ========================================================================

    @Nested
    @DisplayName("3. 메서드 규칙")
    class MethodRules {

        @Test
        @DisplayName("규칙 3-1: decrement 메서드가 필수입니다")
        void stockCounterAdapter_MustHaveDecrementMethod() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "decrement 메서드",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method.getName()
                                                                                            .equals(
                                                                                                    "decrement")))))
                            .allowEmptyShould(true)
                            .because("원자적 재고 차감을 위한 decrement 메서드가 필수입니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 3-2: increment 메서드가 필수입니다")
        void stockCounterAdapter_MustHaveIncrementMethod() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "increment 메서드",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method.getName()
                                                                                            .equals(
                                                                                                    "increment")))))
                            .allowEmptyShould(true)
                            .because("원자적 재고 증가/롤백을 위한 increment 메서드가 필수입니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 3-3: getStock 메서드가 필수입니다")
        void stockCounterAdapter_MustHaveGetStockMethod() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should(
                                    ArchCondition.from(
                                            DescribedPredicate.describe(
                                                    "getStock 메서드",
                                                    javaClass ->
                                                            javaClass.getMethods().stream()
                                                                    .anyMatch(
                                                                            method ->
                                                                                    method.getName()
                                                                                            .equals(
                                                                                                    "getStock")))))
                            .allowEmptyShould(true)
                            .because("재고 조회를 위한 getStock 메서드가 필수입니다");

            rule.check(stockAdapterClasses);
        }
    }

    // ========================================================================
    // 4. 금지 사항 규칙
    // ========================================================================

    @Nested
    @DisplayName("4. 금지 사항 규칙")
    class ProhibitionRules {

        @Test
        @DisplayName("규칙 4-1: @Transactional 사용이 금지됩니다")
        void stockCounterAdapter_MustNotBeTransactional() {
            ArchRule classRule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .notBeAnnotatedWith(Transactional.class)
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter에 @Transactional 사용 금지");

            ArchRule methodRule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .should()
                            .notBeAnnotatedWith(Transactional.class)
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter 메서드에 @Transactional 사용 금지");

            classRule.check(stockAdapterClasses);
            methodRule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-2: 비즈니스 로직 포함이 금지됩니다")
        void stockCounterAdapter_MustNotContainBusinessLogic() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAPackage(DOMAIN_ALL)
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 비즈니스 로직을 포함하지 않아야 합니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-3: DB 접근이 금지됩니다")
        void stockCounterAdapter_MustNotAccessDatabase() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*(Repository|JpaRepository|EntityManager).*")
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 DB에 직접 접근하지 않습니다");

            rule.check(stockAdapterClasses);
        }

        @Test
        @DisplayName("규칙 4-4: 로깅이 금지됩니다")
        void stockCounterAdapter_MustNotContainLogging() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("StockCounterAdapter")
                            .and()
                            .resideInAPackage("..stock.adapter..")
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Logger.*")
                            .allowEmptyShould(true)
                            .because("Stock Counter Adapter는 로깅을 포함하지 않습니다. AOP로 처리하세요");

            rule.check(stockAdapterClasses);
        }
    }
}
