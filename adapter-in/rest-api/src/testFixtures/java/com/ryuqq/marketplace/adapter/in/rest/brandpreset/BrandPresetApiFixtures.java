package com.ryuqq.marketplace.adapter.in.rest.brandpreset;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.DeleteBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.RegisterBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.UpdateBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.query.SearchBrandPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetApiResponse;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import java.time.Instant;
import java.util.List;

/**
 * BrandPreset API 테스트 Fixtures.
 *
 * <p>BrandPreset REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class BrandPresetApiFixtures {

    private BrandPresetApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_BRAND_ID = 100L;
    public static final String DEFAULT_PRESET_NAME = "테스트 브랜드 프리셋";
    public static final List<Long> DEFAULT_INTERNAL_BRAND_IDS = List.of(10L, 11L, 12L);

    // ===== RegisterBrandPresetApiRequest =====

    public static RegisterBrandPresetApiRequest registerRequest() {
        return new RegisterBrandPresetApiRequest(
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static RegisterBrandPresetApiRequest registerRequest(
            Long shopId, Long salesChannelBrandId, String presetName) {
        return new RegisterBrandPresetApiRequest(
                shopId, salesChannelBrandId, presetName, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static RegisterBrandPresetApiRequest registerRequestWithBrands(
            List<Long> internalBrandIds) {
        return new RegisterBrandPresetApiRequest(
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                internalBrandIds);
    }

    // ===== UpdateBrandPresetApiRequest =====

    public static UpdateBrandPresetApiRequest updateRequest() {
        return new UpdateBrandPresetApiRequest(
                "수정된 프리셋명", DEFAULT_SALES_CHANNEL_BRAND_ID, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static UpdateBrandPresetApiRequest updateRequest(String presetName) {
        return new UpdateBrandPresetApiRequest(
                presetName, DEFAULT_SALES_CHANNEL_BRAND_ID, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static UpdateBrandPresetApiRequest updateRequestWithBrands(List<Long> internalBrandIds) {
        return new UpdateBrandPresetApiRequest(
                "수정된 프리셋명", DEFAULT_SALES_CHANNEL_BRAND_ID, internalBrandIds);
    }

    // ===== DeleteBrandPresetsApiRequest =====

    public static DeleteBrandPresetsApiRequest deleteRequest() {
        return new DeleteBrandPresetsApiRequest(List.of(1L, 2L, 3L));
    }

    public static DeleteBrandPresetsApiRequest deleteRequest(List<Long> ids) {
        return new DeleteBrandPresetsApiRequest(ids);
    }

    // ===== SearchBrandPresetsApiRequest =====

    public static SearchBrandPresetsApiRequest searchRequest() {
        return new SearchBrandPresetsApiRequest(
                null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchBrandPresetsApiRequest searchRequest(int page, int size) {
        return new SearchBrandPresetsApiRequest(
                null, null, null, null, null, null, null, null, page, size);
    }

    public static SearchBrandPresetsApiRequest searchRequest(
            List<Long> salesChannelIds, String searchField, String searchWord) {
        return new SearchBrandPresetsApiRequest(
                salesChannelIds,
                null,
                searchField,
                searchWord,
                null,
                null,
                "createdAt",
                "DESC",
                0,
                20);
    }

    // ===== BrandPresetResult (Application) =====

    public static BrandPresetResult brandPresetResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new BrandPresetResult(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                DEFAULT_PRESET_NAME,
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                now);
    }

    public static BrandPresetResult brandPresetResult(Long id, String presetName) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        return new BrandPresetResult(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                now);
    }

    public static List<BrandPresetResult> brandPresetResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> brandPresetResult((long) i, DEFAULT_PRESET_NAME + "_" + i))
                .toList();
    }

    public static BrandPresetPageResult pageResult(int count, int page, int size) {
        List<BrandPresetResult> results = brandPresetResults(count);
        return BrandPresetPageResult.of(results, page, size, (long) count);
    }

    public static BrandPresetPageResult emptyPageResult() {
        return BrandPresetPageResult.of(List.of(), 0, 20, 0L);
    }

    // ===== BrandPresetApiResponse =====

    public static BrandPresetApiResponse apiResponse(Long id) {
        return new BrandPresetApiResponse(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                DEFAULT_PRESET_NAME,
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                "2025-02-10T10:30:00+09:00");
    }

    public static BrandPresetApiResponse apiResponse(Long id, String presetName) {
        return new BrandPresetApiResponse(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                presetName,
                "테스트 브랜드",
                "TEST_BRAND_CODE",
                "2025-02-10T10:30:00+09:00");
    }

    // ===== BrandPresetDetailResult (Application) =====

    public static BrandPresetDetailResult brandPresetDetailResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        BrandPresetDetailResult.MappingBrand mappingBrand =
                new BrandPresetDetailResult.MappingBrand("TEST_BRAND_CODE", "테스트 브랜드");
        List<BrandPresetDetailResult.InternalBrand> internalBrands =
                List.of(
                        new BrandPresetDetailResult.InternalBrand(100L, "내부 브랜드 A"),
                        new BrandPresetDetailResult.InternalBrand(200L, "내부 브랜드 B"));
        return new BrandPresetDetailResult(
                id,
                DEFAULT_SHOP_ID,
                "테스트 Shop",
                1L,
                "테스트 판매채널",
                "test-account",
                DEFAULT_PRESET_NAME,
                mappingBrand,
                internalBrands,
                now,
                now);
    }
}
