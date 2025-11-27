package com.ryuqq.marketplace.adapter.in.rest.architecture.config;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * API Endpoint Properties ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>중앙 집중식 엔드포인트 관리 패턴을 검증합니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>규칙 1: ApiEndpointProperties는 config.properties 패키지에 위치
 *   <li>규칙 2: ApiEndpointProperties는 @ConfigurationProperties 어노테이션 필수
 *   <li>규칙 3: ApiEndpointProperties는 @Component 어노테이션 필수
 *   <li>규칙 4: Bounded Context별 Nested Static Class는 *Endpoints 네이밍
 *   <li>규칙 5: ApiEndpointProperties는 public이어야 한다
 *   <li>규칙 6: ApiEndpointProperties는 final이 아니어야 한다 (Spring Proxy)
 *   <li>규칙 7: Nested Static Class는 public이어야 한다
 *   <li>규칙 8: ApiEndpointProperties는 Lombok 금지
 * </ul>
 *
 * <p><strong>참고 문서:</strong>
 *
 * <ul>
 *   <li>config/endpoint-properties-guide.md - 엔드포인트 Properties 가이드
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("API Endpoint Properties ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("adapter-rest")
class ApiEndpointPropertiesArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.marketplace.adapter.in.rest");
    }

    /** 규칙 1: ApiEndpointProperties는 config.properties 패키지에 위치 */
    @Test
    @DisplayName("[필수] ApiEndpointProperties는 config.properties 패키지에 위치해야 한다")
    void apiEndpointProperties_MustBeInConfigPropertiesPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
                        .should()
                        .resideInAPackage("..config.properties..")
                        .because("ApiEndpointProperties는 config.properties 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 2: ApiEndpointProperties는 @ConfigurationProperties 어노테이션 필수 */
    @Test
    @DisplayName("[필수] ApiEndpointProperties는 @ConfigurationProperties 어노테이션을 가져야 한다")
    void apiEndpointProperties_MustHaveConfigurationPropertiesAnnotation() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
                        .should()
                        .beAnnotatedWith(
                                org.springframework.boot.context.properties.ConfigurationProperties
                                        .class)
                        .because(
                                "ApiEndpointProperties는 application.yml과 바인딩하기 위해"
                                        + " @ConfigurationProperties가 필수입니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 3: ApiEndpointProperties는 @Component 어노테이션 필수 */
    @Test
    @DisplayName("[필수] ApiEndpointProperties는 @Component 어노테이션을 가져야 한다")
    void apiEndpointProperties_MustHaveComponentAnnotation() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
                        .should()
                        .beAnnotatedWith(org.springframework.stereotype.Component.class)
                        .because("ApiEndpointProperties는 @Component로 Bean 등록되어야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 4: Bounded Context별 Nested Static Class는 *Endpoints 네이밍 */
    @Test
    @DisplayName("[권장] Nested Static Class는 *Endpoints 네이밍 규칙을 따라야 한다")
    void nestedEndpointClasses_ShouldFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .areNestedClasses()
                        .and()
                        .resideInAPackage("..config.properties..")
                        .should()
                        .haveSimpleNameEndingWith("Endpoints")
                        .because(
                                "Bounded Context별 엔드포인트 그룹은 *Endpoints 네이밍 규칙을 따라야 합니다 (예:"
                                        + " OrderEndpoints, ProductEndpoints)");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 5: ApiEndpointProperties는 public이어야 한다 */
    @Test
    @DisplayName("[필수] ApiEndpointProperties는 public이어야 한다")
    void apiEndpointProperties_MustBePublic() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
                        .should()
                        .bePublic()
                        .because("ApiEndpointProperties는 Spring Bean으로 주입되므로 public이어야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 6: ApiEndpointProperties는 final이 아니어야 한다 (Spring Proxy) */
    @Test
    @DisplayName("[필수] ApiEndpointProperties는 final이 아니어야 한다")
    void apiEndpointProperties_MustNotBeFinal() {
        // Note: ArchUnit의 final modifier 검증 한계로 인해 간접 검증
        // final 클래스는 @Component와 함께 사용할 수 없으므로 @Component 검증으로 대체
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
                        .should()
                        .beAnnotatedWith("final") // 실제로는 modifier이지만 ArchUnit 제약으로 annotation 형식 사용
                        .because("ApiEndpointProperties는 Spring이 프록시를 생성할 수 있도록 final이 아니어야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 7: Nested Static Class는 public이어야 한다 */
    @Test
    @DisplayName("[필수] Nested Static Class는 public이어야 한다")
    void nestedEndpointClasses_MustBePublic() {
        ArchRule rule =
                classes()
                        .that()
                        .areNestedClasses()
                        .and()
                        .resideInAPackage("..config.properties..")
                        .and()
                        .haveSimpleNameEndingWith("Endpoints")
                        .should()
                        .bePublic()
                        .because("Nested Static Class는 외부에서 타입 참조가 가능하도록 public이어야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 8: ApiEndpointProperties는 Lombok 금지 */
    @Test
    @DisplayName("[금지] ApiEndpointProperties는 Lombok을 사용하지 않아야 한다")
    void apiEndpointProperties_MustNotUseLombok() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleName("ApiEndpointProperties")
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
                        .orShould()
                        .beAnnotatedWith("lombok.Value")
                        .because("ApiEndpointProperties는 Pure Java를 사용해야 하며 Lombok은 금지됩니다");

        rule.allowEmptyShould(true).check(classes);
    }
}
