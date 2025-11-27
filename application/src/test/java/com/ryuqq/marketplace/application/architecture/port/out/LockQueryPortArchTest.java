package com.ryuqq.marketplace.application.architecture.port.out;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LockQueryPort ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>Lock을 사용하는 QueryPort는 정확히 이 규칙을 따라야 합니다:
 *
 * <ul>
 *   <li>인터페이스명: *LockQueryPort (필수)
 *   <li>패키지: ..application..port.out.query.. (필수)
 *   <li>메서드가 있다면 네이밍 패턴 준수 (필수):
 *       <ul>
 *         <li>ForUpdate - Pessimistic Write Lock (SELECT ... FOR UPDATE)
 *         <li>ForShare - Pessimistic Read Lock (SELECT ... FOR SHARE)
 *         <li>WithLock - 일반 Lock (Pessimistic Write)
 *         <li>WithReadLock - 읽기 Lock
 *         <li>Nowait - 대기 없이 실패 (FOR UPDATE NOWAIT)
 *         <li>SkipLocked - 락 걸린 행 건너뛰기 (FOR UPDATE SKIP LOCKED)
 *         <li>WithOptimisticLock - Optimistic Lock (버전 기반)
 *       </ul>
 *   <li>반환: Optional<Domain> (단건만, 필수)
 *   <li>특정 메서드 필수 여부: 없음 (프로젝트에 맞게 선택적 구현)
 *   <li>주의사항: @Transactional 내에서만 호출 (Lock은 Transaction 내에서만 유효)
 * </ul>
 *
 * <h3>사용 예시:</h3>
 *
 * <pre>
 * // 최소 구성 (재고 관리)
 * interface InventoryLockQueryPort {
 *     Optional&lt;Inventory&gt; findByIdForUpdate(InventoryId id);
 * }
 *
 * // 선착순 기능 추가
 * interface EventLockQueryPort {
 *     Optional&lt;Event&gt; findByIdForUpdate(EventId id);
 *     Optional&lt;Event&gt; findByIdForUpdateNowait(EventId id);
 * }
 *
 * // 큐 처리 추가
 * interface OrderLockQueryPort {
 *     Optional&lt;Order&gt; findByIdForUpdate(OrderId id);
 *     Optional&lt;Order&gt; findNextForUpdateSkipLocked();
 * }
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("LockQueryPort ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
class LockQueryPortArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.marketplace.application");
    }

    /** 규칙 1: 인터페이스명 규칙 */
    @Test
    @DisplayName("[필수] LockQueryPort는 '*LockQueryPort' 접미사를 가져야 한다")
    void lockQueryPort_MustHaveCorrectSuffix() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..port.out.query..")
                        .and()
                        .areInterfaces()
                        .and()
                        .haveSimpleNameContaining("Lock")
                        .should()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .because("락을 사용하는 Query Port는 'LockQueryPort' 접미사를 사용해야 합니다");

        rule.check(classes);
    }

    /** 규칙 2: 패키지 위치 */
    @Test
    @DisplayName("[필수] LockQueryPort는 ..application..port.out.query.. 패키지에 위치해야 한다")
    void lockQueryPort_MustBeInCorrectPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .resideInAPackage("..application..port.out.query..")
                        .because("LockQueryPort는 application.*.port.out.query 패키지에 위치해야 합니다");

        rule.check(classes);
    }

    /** 규칙 3: Interface 여야 함 */
    @Test
    @DisplayName("[필수] LockQueryPort는 Interface여야 한다")
    void lockQueryPort_MustBeInterface() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .beInterfaces()
                        .because("LockQueryPort는 Interface로 선언되어야 합니다 (구현체는 Adapter)");

        rule.check(classes);
    }

    /** 규칙 4: Public Interface */
    @Test
    @DisplayName("[필수] LockQueryPort는 public이어야 한다")
    void lockQueryPort_MustBePublic() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .bePublic()
                        .because("LockQueryPort는 외부에서 접근 가능해야 합니다");

        rule.check(classes);
    }

    /** 규칙 5: Lock 메서드 네이밍 패턴 (있다면) */
    @Test
    @DisplayName("[패턴] LockQueryPort 메서드는 Lock 관련 네이밍 패턴을 따라야 한다")
    void lockQueryPort_MethodsMustFollowLockNamingPattern() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .and()
                        .arePublic()
                        .should()
                        .haveNameMatching(
                                ".*(ForUpdate|ForShare|WithLock|WithReadLock|Nowait|SkipLocked|WithOptimisticLock)")
                        .because("락을 사용하는 메서드는 명시적으로 Lock 타입을 표현해야 합니다");

        rule.check(classes);
    }

    /** 규칙 6: Optional 반환 (있다면) */
    @Test
    @DisplayName("[필수] LockQueryPort 메서드는 Optional을 반환해야 한다")
    void lockQueryPort_MethodsMustReturnOptional() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .and()
                        .arePublic()
                        .should()
                        .haveRawReturnType(Optional.class)
                        .because("LockQueryPort는 단건 조회만 허용하며 Optional을 반환해야 합니다");

        rule.check(classes);
    }

    /** 규칙 7: List/PageResponse 반환 금지 */
    @Test
    @DisplayName("[금지] LockQueryPort는 List/PageResponse를 반환하지 않아야 한다")
    void lockQueryPort_MustNotReturnListOrPage() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .haveRawReturnType(List.class)
                        .orShould()
                        .haveRawReturnType(".*PageResponse")
                        .orShould()
                        .haveRawReturnType(".*SliceResponse")
                        .because("Lock은 단건 조회에만 사용해야 합니다 (성능 이슈)");

        rule.check(classes);
    }

    /** 규칙 8: 저장/수정/삭제 메서드 금지 */
    @Test
    @DisplayName("[금지] LockQueryPort는 저장/수정/삭제 메서드를 가지지 않아야 한다")
    void lockQueryPort_MustNotHaveCommandMethods() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .haveNameMatching("save|update|delete|remove|persist")
                        .because("LockQueryPort는 조회만 담당합니다 (CQRS 분리)");

        rule.check(classes);
    }

    /** 규칙 9: DTO 반환 금지 */
    @Test
    @DisplayName("[금지] LockQueryPort는 DTO를 반환하지 않아야 한다")
    void lockQueryPort_MustNotReturnDto() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .haveRawReturnType(".*Dto.*")
                        .because("LockQueryPort는 Domain을 반환해야 합니다 (DTO 반환 금지)");

        rule.check(classes);
    }

    /** 규칙 10: Entity 반환 금지 */
    @Test
    @DisplayName("[금지] LockQueryPort는 Entity를 반환하지 않아야 한다")
    void lockQueryPort_MustNotReturnEntity() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .haveRawReturnType(".*JpaEntity.*")
                        .orShould()
                        .haveRawReturnType(".*Entity")
                        .because("LockQueryPort는 Domain을 반환해야 합니다 (Entity 반환 금지)");

        rule.check(classes);
    }

    /** 규칙 11: 원시 타입 파라미터 금지 */
    @Test
    @DisplayName("[금지] LockQueryPort는 원시 타입을 파라미터로 받지 않아야 한다")
    void lockQueryPort_MustNotAcceptPrimitiveTypes() {
        ArchRule rule =
                noMethods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .and()
                        .haveNameMatching("findBy.*")
                        .should()
                        .haveRawParameterTypes(Long.class)
                        .orShould()
                        .haveRawParameterTypes(String.class)
                        .orShould()
                        .haveRawParameterTypes(Integer.class)
                        .because("LockQueryPort는 Value Object를 파라미터로 받아야 합니다 (타입 안전성)");

        rule.check(classes);
    }

    /** 규칙 12: Domain Layer 의존성만 허용 */
    @Test
    @DisplayName("[필수] LockQueryPort는 Domain Layer만 의존해야 한다")
    void lockQueryPort_MustOnlyDependOnDomainLayer() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("LockQueryPort")
                        .should()
                        .onlyAccessClassesThat()
                        .resideInAnyPackage(
                                "com.ryuqq.marketplace.domain..",
                                "java..",
                                "com.ryuqq.marketplace.application.." // 같은 application 내 DTO는 허용
                                )
                        .because("LockQueryPort는 Domain Layer만 의존해야 합니다 (Infrastructure 의존 금지)");

        rule.check(classes);
    }
}
