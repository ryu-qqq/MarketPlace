package com.ryuqq.marketplace.domain.brand.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandSortKey 단위 테스트")
class BrandSortKeyTest {

    @Nested
    @DisplayName("fieldName() 메서드 테스트")
    class FieldNameTest {
        @Test
        @DisplayName("CREATED_AT은 'createdAt' 필드명을 반환한다")
        void createdAtFieldName() {
            // given
            BrandSortKey key = BrandSortKey.CREATED_AT;

            // when
            String fieldName = key.fieldName();

            // then
            assertThat(fieldName).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("NAME_KO는 'nameKo' 필드명을 반환한다")
        void nameKoFieldName() {
            // given
            BrandSortKey key = BrandSortKey.NAME_KO;

            // when
            String fieldName = key.fieldName();

            // then
            assertThat(fieldName).isEqualTo("nameKo");
        }

        @Test
        @DisplayName("UPDATED_AT은 'updatedAt' 필드명을 반환한다")
        void updatedAtFieldName() {
            // given
            BrandSortKey key = BrandSortKey.UPDATED_AT;

            // when
            String fieldName = key.fieldName();

            // then
            assertThat(fieldName).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("defaultKey() 메서드 테스트")
    class DefaultKeyTest {
        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultKeyIsCreatedAt() {
            // when
            BrandSortKey defaultKey = BrandSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(BrandSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("SortKey 인터페이스 구현 테스트")
    class SortKeyImplementationTest {
        @Test
        @DisplayName("모든 BrandSortKey는 SortKey 인터페이스를 구현한다")
        void allKeysImplementSortKey() {
            // given
            BrandSortKey[] keys = BrandSortKey.values();

            // when & then
            for (BrandSortKey key : keys) {
                assertThat(key.fieldName()).isNotNull();
                assertThat(key.fieldName()).isNotBlank();
            }
        }

        @Test
        @DisplayName("모든 BrandSortKey는 고유한 필드명을 가진다")
        void allKeysHaveUniqueFieldNames() {
            // given
            BrandSortKey[] keys = BrandSortKey.values();

            // when
            long uniqueCount =
                    java.util.Arrays.stream(keys)
                            .map(BrandSortKey::fieldName)
                            .distinct()
                            .count();

            // then
            assertThat(uniqueCount).isEqualTo(keys.length);
        }
    }
}
