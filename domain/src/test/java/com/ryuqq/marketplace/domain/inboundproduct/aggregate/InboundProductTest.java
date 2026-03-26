package com.ryuqq.marketplace.domain.inboundproduct.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.InboundProductFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.vo.ExternalProductCode;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProduct Aggregate 단위 테스트")
class InboundProductTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundProduct 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 InboundProduct는 RECEIVED 상태로 생성된다")
        void createNewInboundProductWithReceivedStatus() {
            InboundProduct product = InboundProductFixtures.newInboundProduct();

            assertThat(product.status()).isEqualTo(InboundProductStatus.RECEIVED);
            assertThat(product.isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 생성 시 내부 브랜드/카테고리/ProductGroup ID는 null이다")
        void createNewInboundProductHasNullInternalIds() {
            InboundProduct product = InboundProductFixtures.newInboundProduct();

            assertThat(product.internalBrandId()).isNull();
            assertThat(product.internalCategoryId()).isNull();
            assertThat(product.internalProductGroupId()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 정책 해석 ID는 null이다")
        void createNewInboundProductHasNullResolvedIds() {
            InboundProduct product = InboundProductFixtures.newInboundProduct();

            assertThat(product.resolvedShippingPolicyId()).isNull();
            assertThat(product.resolvedRefundPolicyId()).isNull();
            assertThat(product.resolvedNoticeCategoryId()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 isMapped()와 isConverted()는 false이다")
        void createNewInboundProductIsNotMappedOrConverted() {
            InboundProduct product = InboundProductFixtures.newInboundProduct();

            assertThat(product.isMapped()).isFalse();
            assertThat(product.isConverted()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("MAPPED 상태로 복원하면 isMapped()가 true이다")
        void reconstituteAsMapped() {
            InboundProduct product = InboundProductFixtures.mappedProduct();

            assertThat(product.isMapped()).isTrue();
            assertThat(product.isNew()).isFalse();
        }

        @Test
        @DisplayName("CONVERTED 상태로 복원하면 isConverted()가 true이다")
        void reconstituteAsConverted() {
            InboundProduct product = InboundProductFixtures.convertedProduct();

            assertThat(product.isConverted()).isTrue();
            assertThat(product.internalProductGroupId())
                    .isEqualTo(InboundProductFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태로 복원하면 canApplyMapping()이 true이다")
        void reconstitueAsPendingMapping() {
            InboundProduct product = InboundProductFixtures.pendingMappingProduct();

            assertThat(product.status()).isEqualTo(InboundProductStatus.PENDING_MAPPING);
            assertThat(product.isMapped()).isFalse();
        }

        @Test
        @DisplayName("복원된 ID는 isNew()가 false이다")
        void reconstitutedIdIsNotNew() {
            InboundProduct product = InboundProductFixtures.receivedProduct();

            assertThat(product.id().isNew()).isFalse();
            assertThat(product.idValue()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("markPendingMapping() - PENDING_MAPPING 전이")
    class MarkPendingMappingTest {

        @Test
        @DisplayName("RECEIVED 상태에서 PENDING_MAPPING으로 전이한다")
        void markPendingMappingFromReceived() {
            InboundProduct product = InboundProductFixtures.receivedProduct();
            Instant now = CommonVoFixtures.now();

            product.markPendingMapping(now);

            assertThat(product.status()).isEqualTo(InboundProductStatus.PENDING_MAPPING);
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태에서 PENDING_MAPPING으로 재전이 가능하다")
        void markPendingMappingFromPendingMapping() {
            InboundProduct product = InboundProductFixtures.pendingMappingProduct();
            Instant now = CommonVoFixtures.now();

            product.markPendingMapping(now);

            assertThat(product.status()).isEqualTo(InboundProductStatus.PENDING_MAPPING);
        }

        @Test
        @DisplayName("MAPPED 상태에서 PENDING_MAPPING으로 전이하면 예외가 발생한다")
        void markPendingMappingFromMapped_ThrowsException() {
            InboundProduct product = InboundProductFixtures.mappedProduct();

            assertThatThrownBy(() -> product.markPendingMapping(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("CONVERTED 상태에서 PENDING_MAPPING으로 전이하면 예외가 발생한다")
        void markPendingMappingFromConverted_ThrowsException() {
            InboundProduct product = InboundProductFixtures.convertedProduct();

            assertThatThrownBy(() -> product.markPendingMapping(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("applyMapping() - 매핑 적용")
    class ApplyMappingTest {

        @Test
        @DisplayName("RECEIVED 상태에서 매핑을 적용하면 MAPPED로 전이한다")
        void applyMappingFromReceived() {
            InboundProduct product = InboundProductFixtures.receivedProduct();
            Instant now = CommonVoFixtures.now();

            product.applyMapping(100L, 200L, now);

            assertThat(product.status()).isEqualTo(InboundProductStatus.MAPPED);
            assertThat(product.internalBrandId()).isEqualTo(100L);
            assertThat(product.internalCategoryId()).isEqualTo(200L);
            assertThat(product.isMapped()).isTrue();
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태에서 매핑을 적용하면 MAPPED로 전이한다")
        void applyMappingFromPendingMapping() {
            InboundProduct product = InboundProductFixtures.pendingMappingProduct();
            Instant now = CommonVoFixtures.now();

            product.applyMapping(100L, 200L, now);

            assertThat(product.status()).isEqualTo(InboundProductStatus.MAPPED);
            assertThat(product.isMapped()).isTrue();
        }

        @Test
        @DisplayName("MAPPED 상태에서 매핑을 재적용하면 예외가 발생한다")
        void applyMappingFromMapped_ThrowsException() {
            InboundProduct product = InboundProductFixtures.mappedProduct();

            assertThatThrownBy(() -> product.applyMapping(100L, 200L, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("CONVERTED 상태에서 매핑을 적용하면 예외가 발생한다")
        void applyMappingFromConverted_ThrowsException() {
            InboundProduct product = InboundProductFixtures.convertedProduct();

            assertThatThrownBy(() -> product.applyMapping(100L, 200L, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("applyResolution() - 정책 해석 적용")
    class ApplyResolutionTest {

        @Test
        @DisplayName("MAPPED 상태에서 정책 해석을 적용한다")
        void applyResolutionFromMapped() {
            InboundProduct product = InboundProductFixtures.mappedProduct();
            Instant now = CommonVoFixtures.now();

            product.applyResolution(10L, 20L, 30L, now);

            assertThat(product.resolvedShippingPolicyId()).isEqualTo(10L);
            assertThat(product.resolvedRefundPolicyId()).isEqualTo(20L);
            assertThat(product.resolvedNoticeCategoryId()).isEqualTo(30L);
        }

        @Test
        @DisplayName("CONVERTED 상태에서 정책 해석을 갱신한다")
        void applyResolutionFromConverted() {
            InboundProduct product = InboundProductFixtures.convertedProduct();
            Instant now = CommonVoFixtures.now();

            product.applyResolution(11L, 21L, 31L, now);

            assertThat(product.resolvedShippingPolicyId()).isEqualTo(11L);
        }

        @Test
        @DisplayName("RECEIVED 상태에서 정책 해석을 적용하면 예외가 발생한다")
        void applyResolutionFromReceived_ThrowsException() {
            InboundProduct product = InboundProductFixtures.receivedProduct();

            assertThatThrownBy(() -> product.applyResolution(10L, 20L, 30L, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("markConverted() - 변환 완료 처리")
    class MarkConvertedTest {

        @Test
        @DisplayName("변환 완료 처리 시 CONVERTED 상태가 된다")
        void markConvertedSetsConvertedStatus() {
            InboundProduct product = InboundProductFixtures.mappedProduct();
            Instant now = CommonVoFixtures.now();

            product.markConverted(999L, now);

            assertThat(product.status()).isEqualTo(InboundProductStatus.CONVERTED);
            assertThat(product.internalProductGroupId()).isEqualTo(999L);
            assertThat(product.isConverted()).isTrue();
        }

        @Test
        @DisplayName("변환 완료 처리 시 rawPayload는 null로 초기화된다")
        void markConvertedClearsRawPayload() {
            InboundProduct product = InboundProductFixtures.mappedProduct();

            product.markConverted(999L, CommonVoFixtures.now());

            assertThat(product.rawPayload()).isNull();
        }
    }

    @Nested
    @DisplayName("updateRawPayload() - 페이로드 갱신")
    class UpdateRawPayloadTest {

        @Test
        @DisplayName("rawPayload를 갱신한다")
        void updateRawPayload() {
            InboundProduct product = InboundProductFixtures.receivedProduct();
            String newPayload = "{\"updated\":true}";

            product.updateRawPayload(newPayload, CommonVoFixtures.now());

            assertThat(product.rawPayload()).isEqualTo(newPayload);
        }
    }

    @Nested
    @DisplayName("assignExternalProductCode() - 외부 상품 코드 변경")
    class AssignExternalProductCodeTest {

        @Test
        @DisplayName("외부 상품 코드를 변경한다")
        void assignExternalProductCode() {
            InboundProduct product = InboundProductFixtures.receivedProduct();
            ExternalProductCode newCode = ExternalProductCode.of("NEW-EXT-CODE");

            product.assignExternalProductCode(newCode, CommonVoFixtures.now());

            assertThat(product.externalProductCode()).isEqualTo(newCode);
            assertThat(product.externalProductCodeValue()).isEqualTo("NEW-EXT-CODE");
        }
    }
}
