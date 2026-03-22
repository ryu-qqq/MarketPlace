package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeSellerTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.adapter.LegacySellerCompositionQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.mapper.LegacySellerCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.repository.LegacySellerCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * 레거시 셀러 Composite 조회 E2E 테스트.
 *
 * <p>Adapter → Mapper → QueryDSL Repository → H2 DB 전체 흐름을 실제 객체로 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("레거시 셀러 Composite 조회 E2E 테스트")
class LegacySellerCompositeE2ETest {

    @Autowired private EntityManager entityManager;

    private LegacySellerCompositionQueryAdapter adapter;
    private LegacyCompositeSellerTestHelper helper;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        LegacySellerCompositeQueryDslRepository repository =
                new LegacySellerCompositeQueryDslRepository(queryFactory);
        LegacySellerCompositeMapper mapper = new LegacySellerCompositeMapper();
        adapter = new LegacySellerCompositionQueryAdapter(repository, mapper);
        helper = new LegacyCompositeSellerTestHelper(entityManager);
    }

    @Nested
    @DisplayName("셀러 Composite 조회 E2E 시나리오")
    class FindAdminCompositeByIdE2ETest {

        @Test
        @DisplayName("전체 데이터 — 2테이블 JOIN 결과가 SellerAdminCompositeResult로 올바르게 변환됩니다")
        void fullData_ReturnsCompleteCompositeResult() {
            // given
            long sellerId = helper.setupFullSellerData();

            // when
            Optional<SellerAdminCompositeResult> result =
                    adapter.findAdminCompositeById(sellerId);

            // then
            assertThat(result).isPresent();
            SellerAdminCompositeResult composite = result.get();

            // SellerInfo
            assertThat(composite.seller().id()).isEqualTo(sellerId);
            assertThat(composite.seller().sellerName()).isEqualTo("테스트 셀러");
            assertThat(composite.seller().logoUrl()).isEqualTo("https://cdn.example.com/logo.png");
            assertThat(composite.seller().description()).isEqualTo("셀러 설명");
            assertThat(composite.seller().active()).isTrue();

            // BusinessInfo
            assertThat(composite.businessInfo().registrationNumber()).isEqualTo("123-45-67890");
            assertThat(composite.businessInfo().companyName()).isEqualTo("테스트 주식회사");
            assertThat(composite.businessInfo().representative()).isEqualTo("홍길동");
            assertThat(composite.businessInfo().saleReportNumber()).isEqualTo("2025-서울강남-0001");
            assertThat(composite.businessInfo().businessZipcode()).isEqualTo("06123");
            assertThat(composite.businessInfo().businessAddress()).isEqualTo("서울시 강남구 테헤란로 123");
            assertThat(composite.businessInfo().businessAddressDetail()).isEqualTo("4층");

            // CsInfo
            assertThat(composite.csInfo().csPhone()).isEqualTo("02-1234-5678");
            assertThat(composite.csInfo().csMobile()).isEqualTo("010-1234-5678");
            assertThat(composite.csInfo().csEmail()).isEqualTo("cs@test.com");

            // ContractInfo
            assertThat(composite.contractInfo().commissionRate())
                    .isEqualByComparingTo(BigDecimal.valueOf(15.5));

            // SettlementInfo
            assertThat(composite.settlementInfo().bankName()).isEqualTo("국민은행");
            assertThat(composite.settlementInfo().accountNumber()).isEqualTo("123456789012");
            assertThat(composite.settlementInfo().accountHolderName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("null 필드 — 빈 문자열로 기본값 변환됩니다")
        void nullFields_DefaultsToEmptyString() {
            // given
            helper.insertSeller(20L, "최소 셀러", null, null, 0.0);
            helper.insertBusinessInfo(
                    20L, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null);
            helper.flushAndClear();

            // when
            Optional<SellerAdminCompositeResult> result =
                    adapter.findAdminCompositeById(20L);

            // then
            assertThat(result).isPresent();
            SellerAdminCompositeResult composite = result.get();
            assertThat(composite.seller().logoUrl()).isEmpty();
            assertThat(composite.seller().description()).isEmpty();
            assertThat(composite.businessInfo().registrationNumber()).isEmpty();
            assertThat(composite.csInfo().csPhone()).isEmpty();
            assertThat(composite.contractInfo().commissionRate())
                    .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("존재하지 않는 ID — 빈 Optional 반환")
        void nonExistentId_ReturnsEmpty() {
            // when
            Optional<SellerAdminCompositeResult> result =
                    adapter.findAdminCompositeById(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("사업자 정보 없는 셀러 — INNER JOIN이므로 빈 Optional 반환")
        void sellerWithoutBusinessInfo_ReturnsEmpty() {
            // given
            helper.insertSeller(30L, "사업자없는 셀러");
            helper.flushAndClear();

            // when
            Optional<SellerAdminCompositeResult> result =
                    adapter.findAdminCompositeById(30L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
