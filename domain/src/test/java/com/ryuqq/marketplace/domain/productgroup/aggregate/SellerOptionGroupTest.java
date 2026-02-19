package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerOptionGroup Entity 단위 테스트")
class SellerOptionGroupTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 SellerOptionGroup을 생성한다")
        void createNewSellerOptionGroup() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.newProductGroupId();
            OptionGroupName optionGroupName = ProductGroupFixtures.defaultOptionGroupName();
            int sortOrder = 0;
            List<SellerOptionValue> optionValues =
                    List.of(ProductGroupFixtures.defaultSellerOptionValue());

            // when
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.forNew(
                            productGroupId,
                            optionGroupName,
                            OptionInputType.PREDEFINED,
                            sortOrder,
                            optionValues);

            // then
            assertThat(optionGroup).isNotNull();
            assertThat(optionGroup.id().isNew()).isTrue();
            assertThat(optionGroup.productGroupId()).isEqualTo(productGroupId);
            assertThat(optionGroup.optionGroupName()).isEqualTo(optionGroupName);
            assertThat(optionGroup.canonicalOptionGroupId()).isNull();
            assertThat(optionGroup.sortOrder()).isEqualTo(sortOrder);
            assertThat(optionGroup.optionValues()).hasSize(1);
            assertThat(optionGroup.isMappedToCanonical()).isFalse();
        }
    }

    @Nested
    @DisplayName("forNewWithCanonical 팩토리 메서드 테스트")
    class ForNewWithCanonicalTest {

        @Test
        @DisplayName("캐노니컬 매핑과 함께 새 SellerOptionGroup을 생성한다")
        void createNewSellerOptionGroupWithCanonical() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.newProductGroupId();
            OptionGroupName optionGroupName = ProductGroupFixtures.defaultOptionGroupName();
            CanonicalOptionGroupId canonicalOptionGroupId = CanonicalOptionGroupId.of(1L);
            int sortOrder = 0;
            List<SellerOptionValue> optionValues =
                    List.of(ProductGroupFixtures.defaultSellerOptionValue());

            // when
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.forNewWithCanonical(
                            productGroupId,
                            optionGroupName,
                            canonicalOptionGroupId,
                            OptionInputType.PREDEFINED,
                            sortOrder,
                            optionValues);

            // then
            assertThat(optionGroup.canonicalOptionGroupId()).isEqualTo(canonicalOptionGroupId);
            assertThat(optionGroup.isMappedToCanonical()).isTrue();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 SellerOptionGroup을 복원한다")
        void reconstituteSellerOptionGroup() {
            // given
            SellerOptionGroupId id = ProductGroupFixtures.defaultSellerOptionGroupId();
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            OptionGroupName optionGroupName = ProductGroupFixtures.defaultOptionGroupName();
            CanonicalOptionGroupId canonicalOptionGroupId = CanonicalOptionGroupId.of(1L);
            int sortOrder = 0;
            List<SellerOptionValue> optionValues =
                    List.of(ProductGroupFixtures.mappedSellerOptionValue());

            // when
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.reconstitute(
                            id,
                            productGroupId,
                            optionGroupName,
                            canonicalOptionGroupId,
                            OptionInputType.PREDEFINED,
                            sortOrder,
                            optionValues,
                            DeletionStatus.active());

            // then
            assertThat(optionGroup.id()).isEqualTo(id);
            assertThat(optionGroup.productGroupId()).isEqualTo(productGroupId);
            assertThat(optionGroup.optionGroupName()).isEqualTo(optionGroupName);
            assertThat(optionGroup.canonicalOptionGroupId()).isEqualTo(canonicalOptionGroupId);
            assertThat(optionGroup.optionValues()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("캐노니컬 매핑 관리 테스트")
    class CanonicalMappingTest {

        @Test
        @DisplayName("캐노니컬 옵션 그룹에 매핑한다")
        void mapToCanonical() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.defaultSellerOptionGroup();
            CanonicalOptionGroupId canonicalOptionGroupId = CanonicalOptionGroupId.of(1L);

            // when
            optionGroup.mapToCanonical(canonicalOptionGroupId);

            // then
            assertThat(optionGroup.canonicalOptionGroupId()).isEqualTo(canonicalOptionGroupId);
            assertThat(optionGroup.isMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("캐노니컬 옵션 그룹 매핑을 해제한다")
        void unmapCanonical() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.mappedSellerOptionGroup();

            // when
            optionGroup.unmapCanonical();

            // then
            assertThat(optionGroup.canonicalOptionGroupId()).isNull();
            assertThat(optionGroup.isMappedToCanonical()).isFalse();
        }

        @Test
        @DisplayName("그룹과 모든 값이 매핑되면 fully mapped다")
        void isFullyMappedWhenAllMapped() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.fullyMappedSellerOptionGroup();

            // when & then
            assertThat(optionGroup.isFullyMapped()).isTrue();
        }

        @Test
        @DisplayName("그룹이 매핑되지 않으면 fully mapped가 아니다")
        void isNotFullyMappedWhenGroupNotMapped() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.defaultSellerOptionGroup();

            // when & then
            assertThat(optionGroup.isFullyMapped()).isFalse();
        }

        @Test
        @DisplayName("그룹은 매핑되었지만 값 중 하나라도 매핑되지 않으면 fully mapped가 아니다")
        void isNotFullyMappedWhenSomeValuesNotMapped() {
            // given
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.forNewWithCanonical(
                            ProductGroupFixtures.newProductGroupId(),
                            ProductGroupFixtures.defaultOptionGroupName(),
                            CanonicalOptionGroupId.of(1L),
                            OptionInputType.PREDEFINED,
                            0,
                            List.of(ProductGroupFixtures.defaultSellerOptionValue())); // 매핑 안 된 값

            // when & then
            assertThat(optionGroup.isFullyMapped()).isFalse();
        }
    }

    @Nested
    @DisplayName("옵션 값 관리 테스트")
    class OptionValueManagementTest {

        @Test
        @DisplayName("옵션 값을 추가한다")
        void addOptionValue() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionValue newValue = ProductGroupFixtures.defaultSellerOptionValue();

            // when
            optionGroup.addOptionValue(newValue);

            // then
            assertThat(optionGroup.optionValues()).hasSize(2);
            assertThat(optionGroup.optionValueCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("옵션 값 수를 반환한다")
        void optionValueCount() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.fullyMappedSellerOptionGroup();

            // when
            int count = optionGroup.optionValueCount();

            // then
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("정보 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("옵션 그룹명을 수정한다")
        void updateName() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.defaultSellerOptionGroup();
            OptionGroupName newName = ProductGroupFixtures.optionGroupName("사이즈");

            // when
            optionGroup.updateName(newName);

            // then
            assertThat(optionGroup.optionGroupName()).isEqualTo(newName);
            assertThat(optionGroup.optionGroupNameValue()).isEqualTo("사이즈");
        }

        @Test
        @DisplayName("정렬 순서를 변경한다")
        void updateSortOrder() {
            // given
            SellerOptionGroup optionGroup = ProductGroupFixtures.defaultSellerOptionGroup();

            // when
            optionGroup.updateSortOrder(5);

            // then
            assertThat(optionGroup.sortOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.reconstitute(
                            SellerOptionGroupId.of(100L),
                            ProductGroupFixtures.defaultProductGroupId(),
                            ProductGroupFixtures.defaultOptionGroupName(),
                            null,
                            OptionInputType.PREDEFINED,
                            0,
                            List.of(),
                            DeletionStatus.active());

            // when & then
            assertThat(optionGroup.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("productGroupIdValue()는 ProductGroupId의 값을 반환한다")
        void productGroupIdValueReturnsValue() {
            // given
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.reconstitute(
                            ProductGroupFixtures.defaultSellerOptionGroupId(),
                            ProductGroupId.of(200L),
                            ProductGroupFixtures.defaultOptionGroupName(),
                            null,
                            OptionInputType.PREDEFINED,
                            0,
                            List.of(),
                            DeletionStatus.active());

            // when & then
            assertThat(optionGroup.productGroupIdValue()).isEqualTo(200L);
        }
    }
}
