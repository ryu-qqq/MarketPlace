package com.ryuqq.marketplace.domain.canonicaloption.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionValue Entity 단위 테스트")
class CanonicalOptionValueTest {

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("필수 필드로 CanonicalOptionValue를 복원한다")
        void reconstituteCanonicalOptionValue() {
            // given
            CanonicalOptionValueId id = CanonicalOptionValueId.of(1L);
            CanonicalOptionValueCode code = CanonicalOptionValueCode.of("BLACK");
            CanonicalOptionValueName name = CanonicalOptionValueName.of("검정색", "Black");
            int sortOrder = 0;

            // when
            CanonicalOptionValue value =
                    CanonicalOptionValue.reconstitute(id, code, name, sortOrder);

            // then
            assertThat(value).isNotNull();
            assertThat(value.id()).isEqualTo(id);
            assertThat(value.code()).isEqualTo(code);
            assertThat(value.name()).isEqualTo(name);
            assertThat(value.sortOrder()).isEqualTo(sortOrder);
        }

        @Test
        @DisplayName("다양한 sortOrder 값으로 CanonicalOptionValue를 복원한다")
        void reconstituteWithVariousSortOrders() {
            // when
            CanonicalOptionValue value1 = CanonicalOptionFixtures.canonicalOptionValue(1L, "S", "스몰", 0);
            CanonicalOptionValue value2 = CanonicalOptionFixtures.canonicalOptionValue(2L, "M", "미디움", 1);
            CanonicalOptionValue value3 = CanonicalOptionFixtures.canonicalOptionValue(3L, "L", "라지", 2);

            // then
            assertThat(value1.sortOrder()).isEqualTo(0);
            assertThat(value2.sortOrder()).isEqualTo(1);
            assertThat(value3.sortOrder()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            CanonicalOptionValue value = CanonicalOptionFixtures.canonicalOptionValue(100L, "CODE", "이름", 0);

            // when
            Long idValue = value.idValue();

            // then
            assertThat(idValue).isEqualTo(100L);
        }

        @Test
        @DisplayName("codeValue()는 코드의 값을 반환한다")
        void codeValueReturnsCodeValue() {
            // given
            CanonicalOptionValue value = CanonicalOptionFixtures.canonicalOptionValue(1L, "BLACK", "검정색", 0);

            // when
            String codeValue = value.codeValue();

            // then
            assertThat(codeValue).isEqualTo("BLACK");
        }

        @Test
        @DisplayName("nameKo()는 한국어 이름을 반환한다")
        void nameKoReturnsKoreanName() {
            // given
            CanonicalOptionValue value = CanonicalOptionFixtures.canonicalOptionValue(1L, "BLACK", "검정색", 0);

            // when
            String nameKo = value.nameKo();

            // then
            assertThat(nameKo).isEqualTo("검정색");
        }

        @Test
        @DisplayName("nameEn()은 영어 이름을 반환한다")
        void nameEnReturnsEnglishName() {
            // given
            CanonicalOptionValue value = CanonicalOptionValue.reconstitute(
                    CanonicalOptionValueId.of(2L),
                    CanonicalOptionValueCode.of("WHITE"),
                    CanonicalOptionValueName.of("흰색", "White"),
                    1);

            // when
            String nameEn = value.nameEn();

            // then
            assertThat(nameEn).isEqualTo("White");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 ID를 가진 CanonicalOptionValue는 동등하다")
        void sameIdEquals() {
            // given
            CanonicalOptionValue value1 =
                    CanonicalOptionValue.reconstitute(
                            CanonicalOptionValueId.of(1L),
                            CanonicalOptionValueCode.of("BLACK"),
                            CanonicalOptionValueName.of("검정색", "Black"),
                            0);

            CanonicalOptionValue value2 =
                    CanonicalOptionValue.reconstitute(
                            CanonicalOptionValueId.of(1L),
                            CanonicalOptionValueCode.of("BLACK"),
                            CanonicalOptionValueName.of("검정색", "Black"),
                            0);

            // then
            assertThat(value1.id()).isEqualTo(value2.id());
        }

        @Test
        @DisplayName("다른 ID를 가진 CanonicalOptionValue는 동등하지 않다")
        void differentIdNotEquals() {
            // given
            CanonicalOptionValue value1 = CanonicalOptionFixtures.canonicalOptionValue(1L, 0);
            CanonicalOptionValue value2 = CanonicalOptionFixtures.canonicalOptionValue(2L, 1);

            // then
            assertThat(value1.id()).isNotEqualTo(value2.id());
        }
    }
}
