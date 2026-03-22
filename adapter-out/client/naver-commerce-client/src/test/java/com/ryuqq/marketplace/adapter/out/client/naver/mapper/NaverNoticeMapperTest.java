package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginAreaInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverNoticeMapper 단위 테스트")
class NaverNoticeMapperTest {

    // ── 헬퍼 메서드 ──

    private ProductGroupSyncData createSyncDataWithNotice(
            ProductNoticeResult notice, NoticeCategoryResult category) {
        var queryResult = new ProductGroupDetailCompositeQueryResult(
                1L, 1L, "테스트셀러", 100L, "테스트브랜드", 200L,
                "테스트카테고리", "상의", "1/200", "테스트 상품 그룹",
                "NONE", "ACTIVE", Instant.now(), Instant.now(), null, null);
        var product = new ProductResult(
                1L, 1L, "SKU-001", 100000, 80000, 60000,
                25, 100, "ACTIVE", 1, List.of(), Instant.now(), Instant.now());
        return new ProductGroupSyncData(
                queryResult, List.of(), List.of(), "ACTIVE", false,
                List.of(product), Optional.empty(),
                notice != null ? Optional.of(notice) : Optional.empty(),
                category != null ? Optional.of(category) : Optional.empty(),
                Optional.empty(), Map.of());
    }

    private ProductNoticeResult noticeWithEntries(List<ProductNoticeEntryResult> entries) {
        return new ProductNoticeResult(1L, 1L, entries, Instant.now(), Instant.now());
    }

    private NoticeCategoryResult categoryWithFields(
            String code, List<NoticeFieldResult> fields) {
        return new NoticeCategoryResult(1L, code, "카테고리명", "Category", "CLOTHING", true, fields, Instant.now());
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("mapNotice()")
    class MapNoticeTest {

        @Test
        @DisplayName("notice가 없으면 null 반환")
        void noNoticeReturnsNull() {
            var syncData = createSyncDataWithNotice(null, null);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("notice entries가 비어있으면 null 반환")
        void emptyEntriesReturnsNull() {
            var notice = noticeWithEntries(List.of());
            var syncData = createSyncDataWithNotice(notice, null);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("카테고리 없으면 ETC 타입으로 매핑")
        void noCategoryDefaultsToEtc() {
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "테스트값"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, null);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result).isNotNull();
            assertThat(result.productInfoProvidedNoticeType()).isEqualTo("ETC");
        }

        @Test
        @DisplayName("CLOTHING 카테고리는 WEAR로 매핑")
        void clothingMapsToWear() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "material", "소재", true, 1));
            var category = categoryWithFields("CLOTHING", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "면 100%"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result).isNotNull();
            assertThat(result.productInfoProvidedNoticeType()).isEqualTo("WEAR");
        }

        @Test
        @DisplayName("SHOES 카테고리는 SHOES로 매핑")
        void shoesMapsToShoes() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "material_upper", "갑피", true, 1));
            var category = categoryWithFields("SHOES", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "가죽"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result.productInfoProvidedNoticeType()).isEqualTo("SHOES");
        }

        @Test
        @DisplayName("BAGS 카테고리는 BAG으로 매핑")
        void bagsMapsToBAG() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "type", "종류", true, 1));
            var category = categoryWithFields("BAGS", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "백팩"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result.productInfoProvidedNoticeType()).isEqualTo("BAG");
        }

        @Test
        @DisplayName("ACCESSORIES 카테고리는 FASHION_ITEMS로 매핑")
        void accessoriesMapsToFashionItems() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "type", "종류", true, 1));
            var category = categoryWithFields("ACCESSORIES", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "벨트"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result.productInfoProvidedNoticeType()).isEqualTo("FASHION_ITEMS");
        }

        @Test
        @DisplayName("기본 필수 필드가 항상 포함된다 (returnCostReason 등)")
        void requiredFieldsAlwaysPresent() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "material", "소재", true, 1));
            var category = categoryWithFields("CLOTHING", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "면 100%"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            ProductInfoProvidedNotice result = NaverNoticeMapper.mapNotice(syncData);

            assertThat(result.wear()).containsKey("returnCostReason");
            assertThat(result.wear()).containsKey("noRefundReason");
            assertThat(result.wear()).containsKey("qualityAssuranceStandard");
        }
    }

    @Nested
    @DisplayName("mapOriginAreaInfo()")
    class MapOriginAreaInfoTest {

        @Test
        @DisplayName("notice가 없으면 기본값 반환")
        void noNoticeReturnsDefault() {
            var syncData = createSyncDataWithNotice(null, null);

            OriginAreaInfo result = NaverNoticeMapper.mapOriginAreaInfo(syncData);

            assertThat(result.originAreaCode()).isEqualTo("03");
            assertThat(result.content()).isEqualTo("상세설명에 표시");
        }

        @Test
        @DisplayName("카테고리에 made_in 필드가 있으면 해당 값 사용")
        void madeInFieldUsed() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "made_in", "제조국", true, 1));
            var category = categoryWithFields("CLOTHING", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "이탈리아"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            OriginAreaInfo result = NaverNoticeMapper.mapOriginAreaInfo(syncData);

            assertThat(result.originAreaCode()).isEqualTo("03");
            assertThat(result.content()).isEqualTo("이탈리아");
        }

        @Test
        @DisplayName("made_in 필드가 없으면 기본값 반환")
        void noMadeInFieldReturnsDefault() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "material", "소재", true, 1));
            var category = categoryWithFields("CLOTHING", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "면 100%"));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            OriginAreaInfo result = NaverNoticeMapper.mapOriginAreaInfo(syncData);

            assertThat(result.content()).isEqualTo("상세설명에 표시");
        }

        @Test
        @DisplayName("made_in 필드값이 blank이면 기본값 반환")
        void blankMadeInReturnsDefault() {
            var fields = List.of(
                    new NoticeFieldResult(100L, "made_in", "제조국", true, 1));
            var category = categoryWithFields("CLOTHING", fields);
            var entries = List.of(
                    new ProductNoticeEntryResult(1L, 100L, "  "));
            var notice = noticeWithEntries(entries);
            var syncData = createSyncDataWithNotice(notice, category);

            OriginAreaInfo result = NaverNoticeMapper.mapOriginAreaInfo(syncData);

            assertThat(result.content()).isEqualTo("상세설명에 표시");
        }
    }
}
