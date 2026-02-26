package com.ryuqq.marketplace.domain.canonicaloption.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroup Aggregate 단위 테스트")
class CanonicalOptionGroupTest {

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("필수 필드로 활성 CanonicalOptionGroup을 복원한다")
        void reconstituteActiveCanonicalOptionGroup() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(1L);
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of("COLOR");
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("색상", "Color");
            List<CanonicalOptionValue> values =
                    List.of(
                            CanonicalOptionFixtures.canonicalOptionValue(1L, 0),
                            CanonicalOptionFixtures.canonicalOptionValue(2L, 1));
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            CanonicalOptionGroup group =
                    CanonicalOptionGroup.reconstitute(
                            id, code, name, true, values, createdAt, updatedAt);

            // then
            assertThat(group).isNotNull();
            assertThat(group.id()).isEqualTo(id);
            assertThat(group.code()).isEqualTo(code);
            assertThat(group.name()).isEqualTo(name);
            assertThat(group.isActive()).isTrue();
            assertThat(group.values()).hasSize(2);
            assertThat(group.createdAt()).isEqualTo(createdAt);
            assertThat(group.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("비활성 CanonicalOptionGroup을 복원한다")
        void reconstituteInactiveCanonicalOptionGroup() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(2L);
            CanonicalOptionGroupCode code = CanonicalOptionGroupCode.of("SIZE");
            CanonicalOptionGroupName name = CanonicalOptionGroupName.of("사이즈", "Size");

            // when
            CanonicalOptionGroup group =
                    CanonicalOptionGroup.reconstitute(
                            id,
                            code,
                            name,
                            false,
                            List.of(),
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            // then
            assertThat(group.isActive()).isFalse();
            assertThat(group.values()).isEmpty();
        }

        @Test
        @DisplayName("값이 없는 CanonicalOptionGroup을 복원한다")
        void reconstituteCanonicalOptionGroupWithoutValues() {
            // when
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();

            // then
            assertThat(group.values()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup(100L);

            // when
            Long idValue = group.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("codeValue()는 코드의 값을 반환한다")
        void codeValueReturnsCodeValue() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();

            // when
            String codeValue = group.codeValue();

            // then
            assertThat(codeValue).isEqualTo("COLOR");
        }

        @Test
        @DisplayName("nameKo()는 한국어 이름을 반환한다")
        void nameKoReturnsKoreanName() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();

            // when
            String nameKo = group.nameKo();

            // then
            assertThat(nameKo).isEqualTo("색상");
        }

        @Test
        @DisplayName("nameEn()은 영어 이름을 반환한다")
        void nameEnReturnsEnglishName() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();

            // when
            String nameEn = group.nameEn();

            // then
            assertThat(nameEn).isEqualTo("Color");
        }

        @Test
        @DisplayName("values()는 수정 불가능한 리스트를 반환한다")
        void valuesReturnsUnmodifiableList() {
            // given
            List<CanonicalOptionValue> values = CanonicalOptionFixtures.canonicalOptionValues();
            CanonicalOptionGroup group =
                    CanonicalOptionFixtures.canonicalOptionGroupWithValues(1L, values);

            // when
            List<CanonicalOptionValue> returnedValues = group.values();

            // then
            assertThatThrownBy(
                            () ->
                                    returnedValues.add(
                                            CanonicalOptionFixtures.canonicalOptionValue(99L, 99)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("values 리스트는 외부에서 수정할 수 없다")
        void valuesListIsImmutable() {
            // given
            List<CanonicalOptionValue> values = CanonicalOptionFixtures.canonicalOptionValues();
            CanonicalOptionGroup group =
                    CanonicalOptionFixtures.canonicalOptionGroupWithValues(1L, values);
            List<CanonicalOptionValue> originalValues = group.values();
            int originalSize = originalValues.size();

            // when & then
            assertThatThrownBy(() -> originalValues.clear())
                    .isInstanceOf(UnsupportedOperationException.class);

            assertThat(group.values()).hasSize(originalSize);
        }

        @Test
        @DisplayName("생성자에 전달된 리스트를 수정해도 Aggregate에 영향을 주지 않는다")
        void modifyingConstructorListDoesNotAffectAggregate() {
            // given
            List<CanonicalOptionValue> valuesList =
                    new java.util.ArrayList<>(
                            List.of(
                                    CanonicalOptionFixtures.canonicalOptionValue(1L, 0),
                                    CanonicalOptionFixtures.canonicalOptionValue(2L, 1)));

            CanonicalOptionGroup group =
                    CanonicalOptionGroup.reconstitute(
                            CanonicalOptionGroupId.of(1L),
                            CanonicalOptionGroupCode.of("COLOR"),
                            CanonicalOptionGroupName.of("색상", "Color"),
                            true,
                            valuesList,
                            CommonVoFixtures.yesterday(),
                            CommonVoFixtures.yesterday());

            int originalSize = group.values().size();

            // when
            valuesList.add(CanonicalOptionFixtures.canonicalOptionValue(3L, 2));

            // then
            assertThat(group.values()).hasSize(originalSize);
        }
    }
}
