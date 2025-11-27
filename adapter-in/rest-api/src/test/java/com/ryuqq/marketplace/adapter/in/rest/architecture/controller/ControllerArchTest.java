package com.ryuqq.marketplace.adapter.in.rest.architecture.controller;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Controller ArchUnit 검증 테스트 (완전 강제)
 *
 * <p>모든 Controller는 정확히 이 규칙을 따라야 합니다.
 *
 * <p>검증 규칙:
 *
 * <ul>
 *   <li>1. @RestController 어노테이션 필수
 *   <li>2. @RequestMapping 어노테이션 필수 (클래스 레벨)
 *   <li>3. *Controller 네이밍 규칙
 *   <li>4. @Transactional 사용 금지
 *   <li>5. @Service 사용 금지
 *   <li>6. Lombok 어노테이션 금지
 *   <li>7. DELETE 메서드 금지 (@DeleteMapping)
 *   <li>8. 올바른 패키지 위치
 *   <li>9. Domain 객체 직접 생성 금지
 *   <li>10. UseCase 의존성 필수
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-13
 */
@DisplayName("Controller ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("adapter-rest")
class ControllerArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.marketplace.adapter.in.rest");
    }

    /** 규칙 1: @RestController 어노테이션 필수 */
    @Test
    @DisplayName("[필수] Controller는 @RestController 어노테이션을 가져야 한다")
    void controller_MustHaveRestControllerAnnotation() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..controller..")
                        .and()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .beAnnotatedWith(
                                org.springframework.web.bind.annotation.RestController.class)
                        .because("Controller는 @RestController 어노테이션이 필수입니다");

        rule.check(classes);
    }

    /** 규칙 2: @RequestMapping 어노테이션 필수 */
    @Test
    @DisplayName("[필수] Controller는 @RequestMapping 어노테이션을 가져야 한다")
    void controller_MustHaveRequestMappingAnnotation() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..controller..")
                        .and()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .beAnnotatedWith(
                                org.springframework.web.bind.annotation.RequestMapping.class)
                        .because("Controller는 @RequestMapping 어노테이션이 필수입니다");

        rule.check(classes);
    }

    /** 규칙 3: 네이밍 규칙 (*Controller) */
    @Test
    @DisplayName("[필수] Controller는 *Controller 접미사를 가져야 한다")
    void controller_MustHaveControllerSuffix() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..controller..")
                        .and()
                        .areAnnotatedWith(
                                org.springframework.web.bind.annotation.RestController.class)
                        .should()
                        .haveSimpleNameEndingWith("Controller")
                        .because(
                                "Controller는 *Controller 네이밍 규칙을 따라야 합니다 (예:"
                                        + " OrderCommandController, OrderQueryController)");

        rule.check(classes);
    }

    /** 규칙 4: @Transactional 사용 금지 */
    @Test
    @DisplayName("[금지] Controller는 @Transactional을 사용하지 않아야 한다")
    void controller_MustNotUseTransactional() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..controller..")
                        .should()
                        .beAnnotatedWith("org.springframework.transaction.annotation.Transactional")
                        .because("Controller는 트랜잭션 관리를 하지 않습니다. UseCase에서 @Transactional을 사용하세요.");

        rule.check(classes);
    }

    /** 규칙 5: @Service 사용 금지 */
    @Test
    @DisplayName("[금지] Controller는 @Service를 사용하지 않아야 한다")
    void controller_MustNotUseServiceAnnotation() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..controller..")
                        .should()
                        .beAnnotatedWith(org.springframework.stereotype.Service.class)
                        .because("Controller는 @RestController만 사용해야 합니다");

        rule.check(classes);
    }

    /** 규칙 6: Lombok 어노테이션 금지 */
    @Test
    @DisplayName("[금지] Controller는 Lombok 어노테이션을 가지지 않아야 한다")
    void controller_MustNotUseLombok() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..controller..")
                        .should()
                        .beAnnotatedWith("lombok.Data")
                        .orShould()
                        .beAnnotatedWith("lombok.Builder")
                        .orShould()
                        .beAnnotatedWith("lombok.Getter")
                        .orShould()
                        .beAnnotatedWith("lombok.Setter")
                        .orShould()
                        .beAnnotatedWith("lombok.AllArgsConstructor")
                        .orShould()
                        .beAnnotatedWith("lombok.NoArgsConstructor")
                        .orShould()
                        .beAnnotatedWith("lombok.RequiredArgsConstructor")
                        .because("Controller는 Pure Java를 사용해야 하며 Lombok은 금지됩니다");

        rule.check(classes);
    }

    /** 규칙 7: DELETE 메서드 금지 */
    @Test
    @DisplayName("[금지] Controller는 @DeleteMapping을 사용하지 않아야 한다")
    void controller_MustNotUseDeleteMapping() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .resideInAPackage("..controller..")
                        .should()
                        .beAnnotatedWith(
                                org.springframework.web.bind.annotation.DeleteMapping.class)
                        .because("DELETE 메서드는 지원하지 않습니다. 소프트 삭제는 PATCH로 처리하세요.");

        rule.check(classes);
    }

    /** 규칙 8: 패키지 위치 검증 */
    @Test
    @DisplayName("[필수] Controller는 올바른 패키지에 위치해야 한다")
    void controller_MustBeInCorrectPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Controller")
                        .and()
                        .areAnnotatedWith(
                                org.springframework.web.bind.annotation.RestController.class)
                        .should()
                        .resideInAPackage("..adapter.in.rest..controller..")
                        .because("Controller는 adapter.in.rest.[bc].controller 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    /** 규칙 9: Domain 객체 직접 생성 금지 */
    @Test
    @DisplayName("[금지] Controller는 Domain 객체를 직접 생성하지 않아야 한다")
    void controller_MustNotCreateDomainObjects() {
        ArchRule rule =
                noClasses()
                        .that()
                        .resideInAPackage("..controller..")
                        .and()
                        .haveSimpleNameNotEndingWith("GlobalExceptionHandler")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..domain..")
                        .because(
                                "Controller는 Domain 객체를 직접 생성/조작하지 않습니다. UseCase를 통해 간접 호출하세요. (예외:"
                                        + " GlobalExceptionHandler는 DomainException 처리 허용)");

        rule.check(classes);
    }

    /** 규칙 10: UseCase 의존성 필수 */
    @Test
    @DisplayName("[필수] Controller는 UseCase 인터페이스에 의존해야 한다")
    void controller_MustDependOnUseCaseInterfaces() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..controller..")
                        .and()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..application..port.in..")
                        .because("Controller는 UseCase 인터페이스에 의존해야 합니다");

        rule.check(classes);
    }

    /** 규칙 11: ResponseEntity<ApiResponse<T>> 반환 타입 권장 */
    @Test
    @DisplayName("[권장] Controller 메서드는 ResponseEntity<ApiResponse<T>> 형식으로 반환해야 한다")
    void controller_ShouldReturnResponseEntityWithApiResponse() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .resideInAPackage("..controller..")
                        .and()
                        .arePublic()
                        .and()
                        .areAnnotatedWith(org.springframework.web.bind.annotation.PostMapping.class)
                        .or()
                        .areAnnotatedWith(org.springframework.web.bind.annotation.GetMapping.class)
                        .or()
                        .areAnnotatedWith(
                                org.springframework.web.bind.annotation.PatchMapping.class)
                        .or()
                        .areAnnotatedWith(org.springframework.web.bind.annotation.PutMapping.class)
                        .should()
                        .haveRawReturnType(org.springframework.http.ResponseEntity.class)
                        .because("Controller 메서드는 ResponseEntity<ApiResponse<T>> 형식으로 반환해야 합니다");

        // Note: 이 규칙은 권장사항이므로 실패 시 경고만 표시
        try {
            rule.check(classes);
        } catch (AssertionError e) {
            System.out.println("⚠️  Warning: " + e.getMessage());
        }
    }
}
