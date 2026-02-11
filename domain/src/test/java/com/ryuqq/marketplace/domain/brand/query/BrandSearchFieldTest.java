package com.ryuqq.marketplace.domain.brand.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandSearchField 단위 테스트")
class BrandSearchFieldTest {

    @Nested
    @DisplayName("fieldName() 메서드 테스트")
    class FieldNameTest {
        @Test
        @DisplayName("CODE는 'code' 필드명을 반환한다")
        void codeFieldName() {
            // given
            BrandSearchField field = BrandSearchField.CODE;

            // when
            String fieldName = field.fieldName();

            // then
            assertThat(fieldName).isEqualTo("code");
        }

        @Test
        @DisplayName("NAME_KO는 'nameKo' 필드명을 반환한다")
        void nameKoFieldName() {
            // given
            BrandSearchField field = BrandSearchField.NAME_KO;

            // when
            String fieldName = field.fieldName();

            // then
            assertThat(fieldName).isEqualTo("nameKo");
        }

        @Test
        @DisplayName("NAME_EN은 'nameEn' 필드명을 반환한다")
        void nameEnFieldName() {
            // given
            BrandSearchField field = BrandSearchField.NAME_EN;

            // when
            String fieldName = field.fieldName();

            // then
            assertThat(fieldName).isEqualTo("nameEn");
        }
    }

    @Nested
    @DisplayName("fromString() 메서드 테스트")
    class FromStringTest {
        @Test
        @DisplayName("필드명으로 BrandSearchField를 찾는다")
        void findByFieldName() {
            // when & then
            assertThat(BrandSearchField.fromString("code")).isEqualTo(BrandSearchField.CODE);
            assertThat(BrandSearchField.fromString("nameKo")).isEqualTo(BrandSearchField.NAME_KO);
            assertThat(BrandSearchField.fromString("nameEn")).isEqualTo(BrandSearchField.NAME_EN);
        }

        @Test
        @DisplayName("enum 이름으로 BrandSearchField를 찾는다")
        void findByEnumName() {
            // when & then
            assertThat(BrandSearchField.fromString("CODE")).isEqualTo(BrandSearchField.CODE);
            assertThat(BrandSearchField.fromString("NAME_KO")).isEqualTo(BrandSearchField.NAME_KO);
            assertThat(BrandSearchField.fromString("NAME_EN")).isEqualTo(BrandSearchField.NAME_EN);
        }

        @Test
        @DisplayName("대소문자 구분 없이 찾는다")
        void findIsCaseInsensitive() {
            // when & then
            assertThat(BrandSearchField.fromString("Code")).isEqualTo(BrandSearchField.CODE);
            assertThat(BrandSearchField.fromString("NAMEKO")).isEqualTo(BrandSearchField.NAME_KO);
            assertThat(BrandSearchField.fromString("nameen")).isEqualTo(BrandSearchField.NAME_EN);
        }

        @Test
        @DisplayName("null이면 null을 반환한다")
        void returnsNullWhenNull() {
            // when
            BrandSearchField field = BrandSearchField.fromString(null);

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("빈 문자열이면 null을 반환한다")
        void returnsNullWhenBlank() {
            // when
            BrandSearchField field = BrandSearchField.fromString("   ");

            // then
            assertThat(field).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 값이면 null을 반환한다")
        void returnsNullWhenNotFound() {
            // when
            BrandSearchField field = BrandSearchField.fromString("INVALID");

            // then
            assertThat(field).isNull();
        }
    }

    @Nested
    @DisplayName("SearchField 인터페이스 구현 테스트")
    class SearchFieldImplementationTest {
        @Test
        @DisplayName("모든 BrandSearchField는 SearchField 인터페이스를 구현한다")
        void allFieldsImplementSearchField() {
            // given
            BrandSearchField[] fields = BrandSearchField.values();

            // when & then
            for (BrandSearchField field : fields) {
                assertThat(field.fieldName()).isNotNull();
                assertThat(field.fieldName()).isNotBlank();
            }
        }
    }
}
