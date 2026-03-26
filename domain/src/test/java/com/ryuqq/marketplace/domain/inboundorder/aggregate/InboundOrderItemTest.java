package com.ryuqq.marketplace.domain.inboundorder.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.inboundorder.InboundOrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrderItem Entity 단위 테스트")
class InboundOrderItemTest {

    @Nested
    @DisplayName("forNew() - 신규 InboundOrderItem 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 아이템은 매핑되지 않은 상태로 생성된다")
        void createNewItemIsNotMapped() {
            InboundOrderItem item = InboundOrderFixtures.newItem();

            assertThat(item.isMapped()).isFalse();
            assertThat(item.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 아이템의 resolved 필드는 모두 null이다")
        void createNewItemHasNullResolvedFields() {
            InboundOrderItem item = InboundOrderFixtures.newItem();

            assertThat(item.resolvedProductGroupId()).isNull();
            assertThat(item.resolvedProductId()).isNull();
            assertThat(item.resolvedSellerId()).isNull();
            assertThat(item.resolvedBrandId()).isNull();
            assertThat(item.resolvedSkuCode()).isNull();
            assertThat(item.resolvedProductGroupName()).isNull();
        }

        @Test
        @DisplayName("신규 아이템의 외부 정보가 올바르게 설정된다")
        void createNewItemHasCorrectExternalInfo() {
            InboundOrderItem item = InboundOrderFixtures.newItem();

            assertThat(item.externalProductId())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
            assertThat(item.externalProductName())
                    .isEqualTo(InboundOrderFixtures.DEFAULT_EXTERNAL_PRODUCT_NAME);
            assertThat(item.unitPrice()).isEqualTo(InboundOrderFixtures.DEFAULT_UNIT_PRICE);
            assertThat(item.quantity()).isEqualTo(InboundOrderFixtures.DEFAULT_QUANTITY);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("매핑 완료된 아이템으로 복원하면 isMapped()가 true이다")
        void reconstitutedMappedItemIsMapped() {
            InboundOrderItem item = InboundOrderFixtures.mappedItem(10L);

            assertThat(item.isMapped()).isTrue();
            assertThat(item.id().isNew()).isFalse();
            assertThat(item.idValue()).isEqualTo(10L);
        }

        @Test
        @DisplayName("매핑 완료된 아이템은 resolved 필드가 설정된다")
        void reconstitutedMappedItemHasResolvedFields() {
            InboundOrderItem item = InboundOrderFixtures.mappedItem(10L);

            assertThat(item.resolvedProductGroupId()).isEqualTo(300L);
            assertThat(item.resolvedProductId()).isEqualTo(400L);
            assertThat(item.resolvedSkuCode()).isEqualTo("SKU-001");
        }
    }

    @Nested
    @DisplayName("applyMapping() - 매핑 적용")
    class ApplyMappingTest {

        @Test
        @DisplayName("매핑 적용 후 isMapped()가 true가 된다")
        void applyMappingSetsIsMappedTrue() {
            InboundOrderItem item = InboundOrderFixtures.newItem();

            item.applyMapping(300L, 400L, 1L, 500L, "SKU-001", "테스트 상품그룹");

            assertThat(item.isMapped()).isTrue();
        }

        @Test
        @DisplayName("매핑 적용 후 resolved 필드가 올바르게 설정된다")
        void applyMappingSetsResolvedFields() {
            InboundOrderItem item = InboundOrderFixtures.newItem();

            item.applyMapping(300L, 400L, 1L, 500L, "SKU-001", "테스트 상품그룹");

            assertThat(item.resolvedProductGroupId()).isEqualTo(300L);
            assertThat(item.resolvedProductId()).isEqualTo(400L);
            assertThat(item.resolvedSellerId()).isEqualTo(1L);
            assertThat(item.resolvedBrandId()).isEqualTo(500L);
            assertThat(item.resolvedSkuCode()).isEqualTo("SKU-001");
            assertThat(item.resolvedProductGroupName()).isEqualTo("테스트 상품그룹");
        }
    }
}
