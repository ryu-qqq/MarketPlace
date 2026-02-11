package com.ryuqq.marketplace.domain.productnotice.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductNotice Aggregate 테스트")
class ProductNoticeTest {

    @Nested
    @DisplayName("forNew() - 신규 상품 고시정보 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 상품 고시정보를 생성한다")
        void createNewProductNoticeWithRequiredFields() {
            // given
            ProductGroupId productGroupId =
                    ProductGroupId.of(ProductNoticeFixtures.DEFAULT_PRODUCT_GROUP_ID);
            NoticeCategoryId noticeCategoryId =
                    NoticeCategoryId.of(ProductNoticeFixtures.DEFAULT_NOTICE_CATEGORY_ID);
            List<ProductNoticeEntry> entries = ProductNoticeFixtures.defaultEntries();
            Instant now = CommonVoFixtures.now();

            // when
            ProductNotice notice =
                    ProductNotice.forNew(productGroupId, noticeCategoryId, entries, now);

            // then
            assertThat(notice.id().isNew()).isTrue();
            assertThat(notice.productGroupId()).isEqualTo(productGroupId);
            assertThat(notice.productGroupIdValue())
                    .isEqualTo(ProductNoticeFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(notice.noticeCategoryId()).isEqualTo(noticeCategoryId);
            assertThat(notice.noticeCategoryIdValue())
                    .isEqualTo(ProductNoticeFixtures.DEFAULT_NOTICE_CATEGORY_ID);
            assertThat(notice.entryCount()).isEqualTo(3);
            assertThat(notice.createdAt()).isEqualTo(now);
            assertThat(notice.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 항목 리스트로 상품 고시정보를 생성한다")
        void createProductNoticeWithEmptyEntries() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            NoticeCategoryId noticeCategoryId = NoticeCategoryId.of(10L);
            List<ProductNoticeEntry> emptyEntries = List.of();
            Instant now = CommonVoFixtures.now();

            // when
            ProductNotice notice =
                    ProductNotice.forNew(productGroupId, noticeCategoryId, emptyEntries, now);

            // then
            assertThat(notice.entryCount()).isZero();
            assertThat(notice.entries()).isEmpty();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 상품 고시정보를 복원한다")
        void reconstituteProductNotice() {
            // given
            ProductNoticeId id = ProductNoticeId.of(1L);
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            NoticeCategoryId noticeCategoryId = NoticeCategoryId.of(10L);
            List<ProductNoticeEntry> entries = ProductNoticeFixtures.defaultEntries();
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            ProductNotice notice =
                    ProductNotice.reconstitute(
                            id, productGroupId, noticeCategoryId, entries, createdAt, updatedAt);

            // then
            assertThat(notice.id()).isEqualTo(id);
            assertThat(notice.idValue()).isEqualTo(1L);
            assertThat(notice.productGroupId()).isEqualTo(productGroupId);
            assertThat(notice.noticeCategoryId()).isEqualTo(noticeCategoryId);
            assertThat(notice.entryCount()).isEqualTo(3);
            assertThat(notice.createdAt()).isEqualTo(createdAt);
            assertThat(notice.updatedAt()).isEqualTo(updatedAt);
        }

        @Test
        @DisplayName("Fixtures를 사용하여 상품 고시정보를 복원한다")
        void reconstituteUsingFixtures() {
            // when
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice(123L);

            // then
            assertThat(notice.idValue()).isEqualTo(123L);
            assertThat(notice.id().isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("replaceEntries() - 고시정보 항목 전체 교체")
    class ReplaceEntriesTest {

        @Test
        @DisplayName("고시정보 항목을 전체 교체한다")
        void replaceAllEntries() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            int originalCount = notice.entryCount();
            Instant originalUpdatedAt = notice.updatedAt();

            List<ProductNoticeEntry> newEntries =
                    List.of(
                            ProductNoticeFixtures.entry(200L, "새로운 제조국"),
                            ProductNoticeFixtures.entry(201L, "새로운 제조사"));
            Instant now = CommonVoFixtures.now();

            // when
            notice.replaceEntries(newEntries, now);

            // then
            assertThat(notice.entryCount()).isEqualTo(2);
            assertThat(notice.entryCount()).isNotEqualTo(originalCount);
            assertThat(notice.updatedAt()).isEqualTo(now);
            assertThat(notice.updatedAt()).isNotEqualTo(originalUpdatedAt);
        }

        @Test
        @DisplayName("빈 리스트로 항목을 교체하면 모든 항목이 제거된다")
        void replaceWithEmptyList() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            Instant now = CommonVoFixtures.now();

            // when
            notice.replaceEntries(List.of(), now);

            // then
            assertThat(notice.entryCount()).isZero();
            assertThat(notice.entries()).isEmpty();
            assertThat(notice.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("addEntry() - 고시정보 항목 추가")
    class AddEntryTest {

        @Test
        @DisplayName("새로운 고시정보 항목을 추가한다")
        void addNewEntry() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            int originalCount = notice.entryCount();
            Instant originalUpdatedAt = notice.updatedAt();

            ProductNoticeEntry newEntry = ProductNoticeFixtures.entry(300L, "추가된 고시정보");
            Instant now = CommonVoFixtures.now();

            // when
            notice.addEntry(newEntry, now);

            // then
            assertThat(notice.entryCount()).isEqualTo(originalCount + 1);
            assertThat(notice.updatedAt()).isEqualTo(now);
            assertThat(notice.updatedAt()).isNotEqualTo(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("entryCount() - 항목 수 확인")
    class EntryCountTest {

        @Test
        @DisplayName("현재 항목 수를 반환한다")
        void returnCurrentEntryCount() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();

            // when
            int count = notice.entryCount();

            // then
            assertThat(count).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("entries() - 불변 리스트 반환")
    class EntriesTest {

        @Test
        @DisplayName("불변 리스트를 반환한다")
        void returnUnmodifiableList() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();

            // when
            List<ProductNoticeEntry> entries = notice.entries();

            // then
            assertThatThrownBy(() -> entries.add(ProductNoticeFixtures.defaultEntry()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("반환된 리스트의 변경은 원본에 영향을 주지 않는다")
        void modificationDoesNotAffectOriginal() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();
            int originalCount = notice.entryCount();

            // when
            List<ProductNoticeEntry> entries = notice.entries();

            // then - 불변 리스트이므로 수정 시도 시 예외 발생
            assertThatThrownBy(() -> entries.clear())
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThat(notice.entryCount()).isEqualTo(originalCount);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID 값을 반환한다")
        void idValueReturnsValue() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice(999L);

            // when & then
            assertThat(notice.idValue()).isEqualTo(999L);
        }

        @Test
        @DisplayName("productGroupIdValue()는 ProductGroup ID 값을 반환한다")
        void productGroupIdValueReturnsValue() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice(1L, 500L, 10L);

            // when & then
            assertThat(notice.productGroupIdValue()).isEqualTo(500L);
        }

        @Test
        @DisplayName("noticeCategoryIdValue()는 NoticeCategory ID 값을 반환한다")
        void noticeCategoryIdValueReturnsValue() {
            // given
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice(1L, 100L, 50L);

            // when & then
            assertThat(notice.noticeCategoryIdValue()).isEqualTo(50L);
        }
    }
}
