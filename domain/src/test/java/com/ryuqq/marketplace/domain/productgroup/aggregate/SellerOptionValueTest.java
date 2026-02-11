package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerOptionValue Entity 단위 테스트")
class SellerOptionValueTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 SellerOptionValue를 생성한다")
        void createNewSellerOptionValue() {
            // given
            SellerOptionGroupId sellerOptionGroupId = SellerOptionGroupId.forNew();
            OptionValueName optionValueName = ProductGroupFixtures.defaultOptionValueName();
            int sortOrder = 0;

            // when
            SellerOptionValue optionValue = SellerOptionValue.forNew(
                    sellerOptionGroupId, optionValueName, sortOrder);

            // then
            assertThat(optionValue).isNotNull();
            assertThat(optionValue.id().isNew()).isTrue();
            assertThat(optionValue.sellerOptionGroupId()).isEqualTo(sellerOptionGroupId);
            assertThat(optionValue.optionValueName()).isEqualTo(optionValueName);
            assertThat(optionValue.canonicalOptionValueId()).isNull();
            assertThat(optionValue.sortOrder()).isEqualTo(sortOrder);
            assertThat(optionValue.isMappedToCanonical()).isFalse();
        }
    }

    @Nested
    @DisplayName("forNewWithCanonical 팩토리 메서드 테스트")
    class ForNewWithCanonicalTest {

        @Test
        @DisplayName("캐노니컬 매핑과 함께 새 SellerOptionValue를 생성한다")
        void createNewSellerOptionValueWithCanonical() {
            // given
            SellerOptionGroupId sellerOptionGroupId = SellerOptionGroupId.forNew();
            OptionValueName optionValueName = ProductGroupFixtures.defaultOptionValueName();
            CanonicalOptionValueId canonicalOptionValueId = CanonicalOptionValueId.of(1L);
            int sortOrder = 0;

            // when
            SellerOptionValue optionValue = SellerOptionValue.forNewWithCanonical(
                    sellerOptionGroupId, optionValueName, canonicalOptionValueId, sortOrder);

            // then
            assertThat(optionValue.canonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(optionValue.isMappedToCanonical()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 SellerOptionValue를 복원한다")
        void reconstituteSellerOptionValue() {
            // given
            SellerOptionValueId id = ProductGroupFixtures.defaultSellerOptionValueId();
            SellerOptionGroupId sellerOptionGroupId = ProductGroupFixtures.defaultSellerOptionGroupId();
            OptionValueName optionValueName = ProductGroupFixtures.defaultOptionValueName();
            CanonicalOptionValueId canonicalOptionValueId = CanonicalOptionValueId.of(1L);
            int sortOrder = 0;

            // when
            SellerOptionValue optionValue = SellerOptionValue.reconstitute(
                    id, sellerOptionGroupId, optionValueName, canonicalOptionValueId, sortOrder);

            // then
            assertThat(optionValue.id()).isEqualTo(id);
            assertThat(optionValue.sellerOptionGroupId()).isEqualTo(sellerOptionGroupId);
            assertThat(optionValue.optionValueName()).isEqualTo(optionValueName);
            assertThat(optionValue.canonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(optionValue.sortOrder()).isEqualTo(sortOrder);
        }
    }

    @Nested
    @DisplayName("캐노니컬 매핑 관리 테스트")
    class CanonicalMappingTest {

        @Test
        @DisplayName("캐노니컬 옵션에 매핑한다")
        void mapToCanonical() {
            // given
            SellerOptionValue optionValue = ProductGroupFixtures.defaultSellerOptionValue();
            CanonicalOptionValueId canonicalOptionValueId = CanonicalOptionValueId.of(1L);

            // when
            optionValue.mapToCanonical(canonicalOptionValueId);

            // then
            assertThat(optionValue.canonicalOptionValueId()).isEqualTo(canonicalOptionValueId);
            assertThat(optionValue.isMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("캐노니컬 옵션 매핑을 해제한다")
        void unmapCanonical() {
            // given
            SellerOptionValue optionValue = ProductGroupFixtures.mappedSellerOptionValue();

            // when
            optionValue.unmapCanonical();

            // then
            assertThat(optionValue.canonicalOptionValueId()).isNull();
            assertThat(optionValue.isMappedToCanonical()).isFalse();
        }

        @Test
        @DisplayName("캐노니컬 매핑 여부를 확인한다")
        void isMappedToCanonical() {
            // given
            SellerOptionValue unmapped = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValue mapped = ProductGroupFixtures.mappedSellerOptionValue();

            // when & then
            assertThat(unmapped.isMappedToCanonical()).isFalse();
            assertThat(mapped.isMappedToCanonical()).isTrue();
        }
    }

    @Nested
    @DisplayName("정보 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("옵션 값 이름을 수정한다")
        void updateName() {
            // given
            SellerOptionValue optionValue = ProductGroupFixtures.defaultSellerOptionValue();
            OptionValueName newName = ProductGroupFixtures.optionValueName("흰색");

            // when
            optionValue.updateName(newName);

            // then
            assertThat(optionValue.optionValueName()).isEqualTo(newName);
            assertThat(optionValue.optionValueNameValue()).isEqualTo("흰색");
        }

        @Test
        @DisplayName("정렬 순서를 변경한다")
        void updateSortOrder() {
            // given
            SellerOptionValue optionValue = ProductGroupFixtures.defaultSellerOptionValue();

            // when
            optionValue.updateSortOrder(5);

            // then
            assertThat(optionValue.sortOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            SellerOptionValue optionValue = SellerOptionValue.reconstitute(
                    SellerOptionValueId.of(100L),
                    ProductGroupFixtures.defaultSellerOptionGroupId(),
                    ProductGroupFixtures.defaultOptionValueName(),
                    null,
                    0);

            // when & then
            assertThat(optionValue.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("sellerOptionGroupIdValue()는 SellerOptionGroupId의 값을 반환한다")
        void sellerOptionGroupIdValueReturnsValue() {
            // given
            SellerOptionValue optionValue = SellerOptionValue.reconstitute(
                    ProductGroupFixtures.defaultSellerOptionValueId(),
                    SellerOptionGroupId.of(200L),
                    ProductGroupFixtures.defaultOptionValueName(),
                    null,
                    0);

            // when & then
            assertThat(optionValue.sellerOptionGroupIdValue()).isEqualTo(200L);
        }
    }
}
