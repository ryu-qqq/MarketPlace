package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisStatus;
import com.ryuqq.marketplace.domain.productintelligence.vo.DecisionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductProfileJpaEntityMapperTest - 상품 프로파일 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductProfileJpaEntityMapper 단위 테스트")
class ProductProfileJpaEntityMapperTest {

    private ProductProfileJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper = new ProductProfileJpaEntityMapper(objectMapper);
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 ProductProfile의 모든 필드를 Entity로 변환합니다")
        void toEntity_WithPendingProductProfile_ConvertsAllFieldsCorrectly() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.existingPendingProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupId());
            assertThat(entity.getProfileVersion()).isEqualTo(domain.profileVersion());
            assertThat(entity.getStatus()).isEqualTo(ProductProfileJpaEntity.Status.PENDING);
            assertThat(entity.getExpectedAnalysisCount()).isEqualTo(domain.expectedAnalysisCount());
            assertThat(entity.getCompletedAnalysisCount())
                    .isEqualTo(domain.completedAnalysisCount());
        }

        @Test
        @DisplayName("신규 ProductProfile 변환 시 ID가 null입니다")
        void toEntity_WithNewProductProfile_IdIsNull() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.pendingProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태 ProductProfile을 Entity로 변환 시 decision 필드가 설정됩니다")
        void toEntity_WithCompletedProductProfile_ConvertsDecisionFields() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.completedProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductProfileJpaEntity.Status.COMPLETED);
            assertThat(entity.getDecisionType())
                    .isEqualTo(ProductProfileJpaEntity.DecisionType.AUTO_APPROVED);
            assertThat(entity.getOverallConfidence()).isNotNull();
            assertThat(entity.getOverallConfidence()).isEqualTo(0.95);
        }

        @Test
        @DisplayName("completedAnalysisTypes를 CSV 문자열로 변환합니다")
        void toEntity_WithCompletedAnalysisTypes_ConvertsToCsv() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.completedProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCompletedAnalysisTypes()).isNotNull();
            assertThat(entity.getCompletedAnalysisTypes()).contains("DESCRIPTION");
            assertThat(entity.getCompletedAnalysisTypes()).contains("NOTICE");
            assertThat(entity.getCompletedAnalysisTypes()).contains("OPTION");
        }

        @Test
        @DisplayName("decision이 null인 경우 decisionType도 null입니다")
        void toEntity_WithNullDecision_DecisionFieldsAreNull() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.existingPendingProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDecisionType()).isNull();
            assertThat(entity.getOverallConfidence()).isNull();
            assertThat(entity.getDecisionReasonsJson()).isNull();
        }

        @Test
        @DisplayName("extractedAttributes를 JSON으로 직렬화합니다")
        void toEntity_WithExtractedAttributes_SerializesToJson() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.completedProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExtractedAttributesJson()).isNotNull();
            assertThat(entity.getExtractedAttributesJson()).contains("소재");
        }

        @Test
        @DisplayName("FAILED 상태 ProductProfile을 Entity로 변환합니다")
        void toEntity_WithFailedProductProfile_ConvertsStatus() {
            // given
            ProductProfile domain = ProductIntelligenceFixtures.failedProductProfile();

            // when
            ProductProfileJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductProfileJpaEntity.Status.FAILED);
            assertThat(entity.getErrorMessage()).isEqualTo("분석 실패");
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsAllFieldsCorrectly() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupId()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.profileVersion()).isEqualTo(entity.getProfileVersion());
            assertThat(domain.status()).isEqualTo(AnalysisStatus.PENDING);
            assertThat(domain.expectedAnalysisCount()).isEqualTo(entity.getExpectedAnalysisCount());
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 forNew ID가 생성됩니다")
        void toDomain_WithNullId_CreatesNewId() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity();

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환 시 decision이 생성됩니다")
        void toDomain_WithCompletedEntity_ConvertsDecisionCorrectly() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(AnalysisStatus.COMPLETED);
            assertThat(domain.decision()).isNotNull();
            assertThat(domain.decision().decisionType()).isEqualTo(DecisionType.AUTO_APPROVED);
            assertThat(domain.decision().overallConfidence().value()).isEqualTo(0.95);
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsStatus() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.failedProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(AnalysisStatus.FAILED);
            assertThat(domain.errorMessage()).isEqualTo("분석 실패");
        }

        @Test
        @DisplayName("completedAnalysisTypes CSV를 Set으로 파싱합니다")
        void toDomain_WithCompletedAnalysisTypesCsv_ParsesToSet() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.completedAnalysisTypes()).hasSize(3);
        }

        @Test
        @DisplayName("null completedAnalysisTypes를 빈 Set으로 파싱합니다")
        void toDomain_WithNullCompletedAnalysisTypes_ReturnsEmptySet() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.completedAnalysisTypes()).isEmpty();
        }

        @Test
        @DisplayName("만료된 Entity를 Domain으로 변환 시 expiredAt이 설정됩니다")
        void toDomain_WithExpiredEntity_ConvertsExpiredAt() {
            // given
            ProductProfileJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.expiredProfileEntity(1L, 100L);

            // when
            ProductProfile domain = mapper.toDomain(entity);

            // then
            assertThat(domain.expiredAt()).isNotNull();
        }
    }
}
