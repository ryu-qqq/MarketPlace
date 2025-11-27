package com.ryuqq.marketplace.domain.brand.aggregate.brand;

import com.ryuqq.marketplace.domain.brand.fixture.BrandAliasFixture;
import com.ryuqq.marketplace.domain.brand.fixture.BrandVoFixture;
import com.ryuqq.marketplace.domain.brand.vo.AliasName;
import com.ryuqq.marketplace.domain.brand.vo.AliasSource;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import com.ryuqq.marketplace.domain.brand.vo.BrandAliasId;
import com.ryuqq.marketplace.domain.brand.vo.Confidence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BrandAlias Entity 단위 테스트
 *
 * <p><strong>테스트 범위</strong>:</p>
 * <ul>
 *   <li>생성 테스트 - create(), reconstitute()</li>
 *   <li>도메인 행위 테스트 - confirm(), reject(), updateConfidence()</li>
 *   <li>쿼리 메서드 테스트 - isConfirmed(), isRejected(), isActive(), matchesScope()</li>
 *   <li>Getter 테스트 - Law of Demeter 준수 확인</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("BrandAlias Entity 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("brand")
class BrandAliasTest {

    // ==================== 생성 테스트 ====================

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("[성공] BrandAlias.create()로 신규 별칭 생성")
        void create_ShouldCreateNewAlias() {
            // Given
            Long brandId = 1L;
            AliasName aliasName = BrandVoFixture.aliasName();
            AliasSource source = BrandVoFixture.aliasSourceManual();
            Confidence confidence = BrandVoFixture.confidenceHigh();
            AliasStatus status = AliasStatus.CONFIRMED;

            // When
            BrandAlias alias = BrandAlias.create(brandId, aliasName, source, confidence, status);

            // Then
            assertNotNull(alias);
            assertNotNull(alias.id());
            assertTrue(alias.id().isNew());
            assertEquals(brandId, alias.brandId());
            assertEquals(aliasName, alias.aliasName());
            assertEquals(source, alias.source());
            assertEquals(confidence, alias.confidence());
            assertEquals(status, alias.status());
        }

        @Test
        @DisplayName("[성공] BrandAlias.create()로 PENDING_REVIEW 상태 별칭 생성")
        void create_PendingReviewStatus_ShouldCreateAlias() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.pendingAlias();

            // Then
            assertNotNull(alias);
            assertEquals(AliasStatus.PENDING_REVIEW, alias.status());
        }

        @Test
        @DisplayName("[성공] BrandAlias.create()로 AUTO_SUGGESTED 상태 별칭 생성")
        void create_AutoSuggestedStatus_ShouldCreateAlias() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.autoSuggestedAlias();

            // Then
            assertNotNull(alias);
            assertEquals(AliasStatus.AUTO_SUGGESTED, alias.status());
        }

        @Test
        @DisplayName("[성공] BrandAlias.reconstitute()로 기존 별칭 재구성")
        void reconstitute_ShouldReconstituteExistingAlias() {
            // Given
            BrandAliasId id = BrandVoFixture.brandAliasId(100L);
            Long brandId = 1L;
            AliasName aliasName = BrandVoFixture.aliasName();
            AliasSource source = BrandVoFixture.aliasSourceManual();
            Confidence confidence = BrandVoFixture.confidenceHigh();
            AliasStatus status = AliasStatus.CONFIRMED;

            // When
            BrandAlias alias = BrandAlias.reconstitute(id, brandId, aliasName, source, confidence, status);

            // Then
            assertNotNull(alias);
            assertEquals(id, alias.id());
            assertFalse(alias.id().isNew());
            assertEquals(brandId, alias.brandId());
        }
    }

    // ==================== 도메인 행위 테스트 ====================

    @Nested
    @DisplayName("도메인 행위 테스트")
    class DomainBehaviorTest {

        @Test
        @DisplayName("[성공] confirm()으로 별칭 확정")
        void confirm_ShouldChangeStatusToConfirmed() {
            // Given
            BrandAlias alias = BrandAliasFixture.pendingAlias();
            assertEquals(AliasStatus.PENDING_REVIEW, alias.status());

            // When
            alias.confirm();

            // Then
            assertEquals(AliasStatus.CONFIRMED, alias.status());
            assertTrue(alias.isConfirmed());
        }

        @Test
        @DisplayName("[성공] reject()로 별칭 거부")
        void reject_ShouldChangeStatusToRejected() {
            // Given
            BrandAlias alias = BrandAliasFixture.pendingAlias();
            assertEquals(AliasStatus.PENDING_REVIEW, alias.status());

            // When
            alias.reject();

            // Then
            assertEquals(AliasStatus.REJECTED, alias.status());
            assertTrue(alias.isRejected());
        }

        @Test
        @DisplayName("[성공] AUTO_SUGGESTED 상태에서 confirm()")
        void confirm_FromAutoSuggested_ShouldWork() {
            // Given
            BrandAlias alias = BrandAliasFixture.autoSuggestedAlias();
            assertEquals(AliasStatus.AUTO_SUGGESTED, alias.status());

            // When
            alias.confirm();

            // Then
            assertEquals(AliasStatus.CONFIRMED, alias.status());
        }

        @Test
        @DisplayName("[성공] AUTO_SUGGESTED 상태에서 reject()")
        void reject_FromAutoSuggested_ShouldWork() {
            // Given
            BrandAlias alias = BrandAliasFixture.autoSuggestedAlias();
            assertEquals(AliasStatus.AUTO_SUGGESTED, alias.status());

            // When
            alias.reject();

            // Then
            assertEquals(AliasStatus.REJECTED, alias.status());
        }

        @Test
        @DisplayName("[성공] updateConfidence()로 신뢰도 업데이트")
        void updateConfidence_ShouldUpdateConfidenceValue() {
            // Given
            BrandAlias alias = BrandAliasFixture.defaultAlias();
            Confidence newConfidence = Confidence.of(0.95);

            // When
            alias.updateConfidence(newConfidence);

            // Then
            assertEquals(newConfidence, alias.confidence());
            assertEquals(0.95, alias.confidenceValue(), 0.001);
        }
    }

    // ==================== 쿼리 메서드 테스트 ====================

    @Nested
    @DisplayName("쿼리 메서드 테스트")
    class QueryMethodTest {

        @Test
        @DisplayName("[성공] isConfirmed()는 CONFIRMED 상태일 때 true")
        void isConfirmed_ConfirmedStatus_ShouldReturnTrue() {
            // Given
            BrandAlias alias = BrandAliasFixture.defaultAlias();

            // When & Then
            assertTrue(alias.isConfirmed());
        }

        @Test
        @DisplayName("[성공] isConfirmed()는 다른 상태일 때 false")
        void isConfirmed_OtherStatus_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.pendingAlias();

            // When & Then
            assertFalse(alias.isConfirmed());
        }

        @Test
        @DisplayName("[성공] isRejected()는 REJECTED 상태일 때 true")
        void isRejected_RejectedStatus_ShouldReturnTrue() {
            // Given
            BrandAlias alias = BrandAliasFixture.rejectedAlias();

            // When & Then
            assertTrue(alias.isRejected());
        }

        @Test
        @DisplayName("[성공] isRejected()는 다른 상태일 때 false")
        void isRejected_OtherStatus_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.defaultAlias();

            // When & Then
            assertFalse(alias.isRejected());
        }

        @Test
        @DisplayName("[성공] isActive()는 활성 상태(CONFIRMED, AUTO_SUGGESTED)일 때 true")
        void isActive_ActiveStatuses_ShouldReturnTrue() {
            // Given & When & Then
            // 참고: PENDING_REVIEW는 활성 상태가 아님 (검토 대기 중)
            assertTrue(BrandAliasFixture.defaultAlias().isActive()); // CONFIRMED
            assertTrue(BrandAliasFixture.autoSuggestedAlias().isActive()); // AUTO_SUGGESTED
        }

        @Test
        @DisplayName("[성공] isActive()는 PENDING_REVIEW 상태일 때 false")
        void isActive_PendingReviewStatus_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.pendingAlias();

            // When & Then
            // PENDING_REVIEW는 검토 대기 상태이므로 활성이 아님
            assertFalse(alias.isActive());
        }

        @Test
        @DisplayName("[성공] isActive()는 REJECTED 상태일 때 false")
        void isActive_RejectedStatus_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.rejectedAlias();

            // When & Then
            assertFalse(alias.isActive());
        }

        @Test
        @DisplayName("[성공] matchesScope()는 동일한 scope일 때 true")
        void matchesScope_SameScope_ShouldReturnTrue() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();
            String normalized = alias.normalizedAlias();
            String mallCode = alias.mallCode();
            Long sellerId = alias.sellerId();

            // When & Then
            assertTrue(alias.matchesScope(normalized, mallCode, sellerId));
        }

        @Test
        @DisplayName("[성공] matchesScope()는 다른 normalizedAlias일 때 false")
        void matchesScope_DifferentNormalizedAlias_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertFalse(alias.matchesScope("different_alias", alias.mallCode(), alias.sellerId()));
        }

        @Test
        @DisplayName("[성공] matchesScope()는 다른 mallCode일 때 false")
        void matchesScope_DifferentMallCode_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertFalse(alias.matchesScope(alias.normalizedAlias(), "DIFFERENT_MALL", alias.sellerId()));
        }

        @Test
        @DisplayName("[성공] matchesScope()는 다른 sellerId일 때 false")
        void matchesScope_DifferentSellerId_ShouldReturnFalse() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertFalse(alias.matchesScope(alias.normalizedAlias(), alias.mallCode(), 99999L));
        }
    }

    // ==================== Getter 테스트 (Law of Demeter 준수) ====================

    @Nested
    @DisplayName("Getter 테스트 (Law of Demeter 준수)")
    class GetterTest {

        @Test
        @DisplayName("[성공] normalizedAlias()는 정규화된 별칭 반환")
        void normalizedAlias_ShouldReturnNormalizedValue() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertNotNull(alias.normalizedAlias());
            assertEquals(alias.aliasName().normalized(), alias.normalizedAlias());
        }

        @Test
        @DisplayName("[성공] originalAlias()는 원본 별칭 반환")
        void originalAlias_ShouldReturnOriginalValue() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertNotNull(alias.originalAlias());
            assertEquals(alias.aliasName().original(), alias.originalAlias());
        }

        @Test
        @DisplayName("[성공] sourceType()는 소스 타입명 반환")
        void sourceType_ShouldReturnSourceTypeName() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertNotNull(alias.sourceType());
            assertEquals(alias.source().sourceType().name(), alias.sourceType());
        }

        @Test
        @DisplayName("[성공] confidenceValue()는 신뢰도 값 반환")
        void confidenceValue_ShouldReturnDoubleValue() {
            // Given
            BrandAlias alias = BrandAliasFixture.reconstitutedAlias();

            // When & Then
            assertEquals(alias.confidence().value(), alias.confidenceValue(), 0.001);
        }

        @Test
        @DisplayName("[성공] sellerId()는 셀러 ID 반환")
        void sellerId_ShouldReturnSellerId() {
            // Given
            BrandAlias alias = BrandAliasFixture.sellerAlias(123L);

            // When & Then
            assertEquals(123L, alias.sellerId());
        }

        @Test
        @DisplayName("[성공] mallCode()는 몰 코드 반환")
        void mallCode_ShouldReturnMallCode() {
            // Given
            BrandAlias alias = BrandAliasFixture.externalMallAlias("COUPANG");

            // When & Then
            assertEquals("COUPANG", alias.mallCode());
        }
    }

    // ==================== 소스별 Fixture 테스트 ====================

    @Nested
    @DisplayName("소스별 Fixture 테스트")
    class SourceSpecificTest {

        @Test
        @DisplayName("[성공] Manual 소스 별칭 생성")
        void manualSource_ShouldCreateCorrectly() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.defaultAlias();

            // Then
            assertEquals("MANUAL", alias.sourceType());
        }

        @Test
        @DisplayName("[성공] Seller 소스 별칭 생성")
        void sellerSource_ShouldCreateCorrectly() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.sellerAlias(456L);

            // Then
            assertEquals("SELLER", alias.sourceType());
            assertEquals(456L, alias.sellerId());
        }

        @Test
        @DisplayName("[성공] ExternalMall 소스 별칭 생성")
        void externalMallSource_ShouldCreateCorrectly() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.externalMallAlias("NAVER");

            // Then
            assertEquals("EXTERNAL_MALL", alias.sourceType());
            assertEquals("NAVER", alias.mallCode());
        }

        @Test
        @DisplayName("[성공] System 소스 별칭 생성")
        void systemSource_ShouldCreateCorrectly() {
            // Given & When
            BrandAlias alias = BrandAliasFixture.systemAlias();

            // Then
            assertEquals("SYSTEM", alias.sourceType());
        }
    }
}
