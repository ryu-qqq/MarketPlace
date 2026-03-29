package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyOptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper.LegacyDescriptionCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper.LegacyImageCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupCommandApiMapper 단위 테스트")
class LegacyProductGroupCommandApiMapperTest {

    @Mock private LegacyNoticeCategoryResolver legacyNoticeCategoryResolver;
    @Mock private LegacyImageCommandApiMapper legacyImageCommandApiMapper;
    @Mock private LegacyDescriptionCommandApiMapper legacyDescriptionCommandApiMapper;
    @Mock private LegacyOptionCommandApiMapper legacyOptionCommandApiMapper;

    private LegacyProductGroupCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper =
                new LegacyProductGroupCommandApiMapper(
                        legacyNoticeCategoryResolver,
                        legacyImageCommandApiMapper,
                        legacyDescriptionCommandApiMapper,
                        legacyOptionCommandApiMapper,
                        new com.ryuqq.marketplace.adapter.in.rest.legacy.product.validator
                                .LegacyOptionValidator());
    }

    @Nested
    @DisplayName("toDisplayStatusCommand - 진열 상태 변경 요청 변환")
    class ToDisplayStatusCommandTest {

        @Test
        @DisplayName("Y 값이 LegacyUpdateDisplayStatusCommand로 올바르게 변환된다")
        void toDisplayStatusCommand_DisplayYnY_ConvertsCorrectly() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateDisplayYnRequest request = LegacyProductGroupApiFixtures.displayOnRequest();

            // when
            LegacyUpdateDisplayStatusCommand command =
                    mapper.toDisplayStatusCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.displayYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("N 값이 LegacyUpdateDisplayStatusCommand로 올바르게 변환된다")
        void toDisplayStatusCommand_DisplayYnN_ConvertsCorrectly() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateDisplayYnRequest request =
                    LegacyProductGroupApiFixtures.displayOffRequest();

            // when
            LegacyUpdateDisplayStatusCommand command =
                    mapper.toDisplayStatusCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.displayYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("toLegacyMarkOutOfStockCommand - 품절 처리 Command 변환")
    class ToLegacyMarkOutOfStockCommandTest {

        @Test
        @DisplayName("productGroupId로 LegacyMarkOutOfStockCommand를 생성한다")
        void toLegacyMarkOutOfStockCommand_CreatesCommand_WithProductGroupId() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

            // when
            LegacyMarkOutOfStockCommand command =
                    mapper.toLegacyMarkOutOfStockCommand(productGroupId);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
        }
    }

    @Nested
    @DisplayName("toCreateResponse - 등록 결과를 응답 DTO로 변환")
    class ToCreateResponseTest {

        @Test
        @DisplayName("LegacyProductRegistrationResult를 LegacyCreateProductGroupResponse로 변환한다")
        void toCreateResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacyProductRegistrationResult result =
                    LegacyProductGroupApiFixtures.registrationResult();

            // when
            LegacyCreateProductGroupResponse response = mapper.toCreateResponse(result);

            // then
            assertThat(response.productGroupId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(response.sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.productIds()).containsExactly(2001L, 2002L);
        }
    }

    @Nested
    @DisplayName("toNoticeCommand - 고시정보 Command 변환")
    class ToNoticeCommandTest {

        @Test
        @DisplayName("productGroupId가 0L이면 LEGACY_DEFAULT noticeCategoryId와 빈 entries를 반환한다")
        void toNoticeCommand_ZeroProductGroupId_ReturnsEmptyEntries() {
            // given
            long productGroupId = 0L;

            // when
            var command = mapper.toNoticeCommand(productGroupId, null);

            // then
            assertThat(command.productGroupId()).isEqualTo(0L);
            assertThat(command.noticeCategoryId())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping
                                    .LEGACY_NOTICE_CATEGORY_ID);
            assertThat(command.entries()).isEmpty();
        }

        @Test
        @DisplayName("request가 null이면 빈 entries를 반환한다")
        void toNoticeCommand_NullRequest_ReturnsEmptyEntries() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

            // when
            var command = mapper.toNoticeCommand(productGroupId, null);

            // then
            assertThat(command.entries()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toUpdateBundle - 상품그룹 수정 번들 변환")
    class ToUpdateBundleTest {

        @Test
        @DisplayName("updateStatus.imageStatus가 false이면 imageCommand는 null이다")
        void toUpdateBundle_ImageStatusFalse_ImageCommandIsNull() {
            // given - imageStatus=false인 updateRequest (productGroupDetails는 포함)
            LegacyUpdateProductGroupRequest request =
                    new LegacyUpdateProductGroupRequest(
                            LegacyProductGroupApiFixtures.productGroupDetails(),
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            List.of(),
                            LegacyProductGroupApiFixtures.updateStatus(
                                    true, false, false, false, false, false, false));

            // when
            var bundle = mapper.toUpdateBundle(request);

            // then
            assertThat(bundle.imageCommand()).isNull();
        }

        @Test
        @DisplayName("updateStatus.stockOptionStatus가 false이면 optionGroupCommand는 null이다")
        void toUpdateBundle_StockOptionStatusFalse_OptionCommandIsNull() {
            // given - stockOptionStatus=false인 updateRequest
            LegacyUpdateProductGroupRequest request =
                    new LegacyUpdateProductGroupRequest(
                            LegacyProductGroupApiFixtures.productGroupDetails(),
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            List.of(),
                            LegacyProductGroupApiFixtures.updateStatus(
                                    true, false, false, false, false, false, false));

            // when
            var bundle = mapper.toUpdateBundle(request);

            // then
            assertThat(bundle.optionGroupCommand()).isNull();
            assertThat(bundle.productEntries()).isEmpty();
        }

        @Test
        @DisplayName("updateStatus.descriptionStatus가 false이면 descriptionCommand는 null이다")
        void toUpdateBundle_DescriptionStatusFalse_DescriptionCommandIsNull() {
            // given - descriptionStatus=false인 updateRequest
            LegacyUpdateProductGroupRequest request =
                    new LegacyUpdateProductGroupRequest(
                            LegacyProductGroupApiFixtures.productGroupDetails(),
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            List.of(),
                            LegacyProductGroupApiFixtures.updateStatus(
                                    true, false, false, false, false, false, false));

            // when
            var bundle = mapper.toUpdateBundle(request);

            // then
            assertThat(bundle.descriptionCommand()).isNull();
        }

        @Test
        @DisplayName("updateStatus.noticeStatus가 false이면 noticeCommand는 null이다")
        void toUpdateBundle_NoticeStatusFalse_NoticeCommandIsNull() {
            // given - noticeStatus=false인 updateRequest
            LegacyUpdateProductGroupRequest request =
                    new LegacyUpdateProductGroupRequest(
                            LegacyProductGroupApiFixtures.productGroupDetails(),
                            null,
                            null,
                            null,
                            List.of(),
                            null,
                            List.of(),
                            LegacyProductGroupApiFixtures.updateStatus(
                                    true, false, false, false, false, false, false));

            // when
            var bundle = mapper.toUpdateBundle(request);

            // then
            assertThat(bundle.noticeCommand()).isNull();
        }
    }
}
