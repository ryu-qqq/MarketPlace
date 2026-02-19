package com.ryuqq.marketplace.application.productgroup;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.DescriptionCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.ImageCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.NoticeCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.NoticeEntryCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.OptionGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.OptionValueCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand.ProductCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import java.util.List;

/**
 * ProductGroup Application Command 테스트 Fixtures.
 *
 * <p>ProductGroup 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupCommandFixtures {

    private ProductGroupCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final long DEFAULT_BRAND_ID = 100L;
    public static final long DEFAULT_CATEGORY_ID = 200L;
    public static final long DEFAULT_SHIPPING_POLICY_ID = 1L;
    public static final long DEFAULT_REFUND_POLICY_ID = 1L;
    public static final long DEFAULT_NOTICE_CATEGORY_ID = 10L;
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품 그룹";
    public static final String DEFAULT_OPTION_TYPE = "SINGLE";
    public static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";
    public static final String DEFAULT_DESCRIPTION_CONTENT = "<p>상품 상세설명</p>";
    public static final String DEFAULT_SKU_CODE = "SKU-001";

    // ===== RegisterProductGroupCommand =====

    public static RegisterProductGroupCommand registerCommand() {
        return new RegisterProductGroupCommand(
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                defaultImageCommands(),
                defaultOptionGroupCommands(),
                defaultProductCommands(),
                defaultDescriptionCommand(),
                defaultNoticeCommand());
    }

    public static RegisterProductGroupCommand registerCommandWithNoOption() {
        return new RegisterProductGroupCommand(
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID,
                DEFAULT_PRODUCT_GROUP_NAME,
                "NONE",
                defaultImageCommands(),
                List.of(),
                defaultProductCommandsNoOption(),
                defaultDescriptionCommand(),
                defaultNoticeCommand());
    }

    public static RegisterProductGroupCommand registerCommand(long sellerId, long brandId) {
        return new RegisterProductGroupCommand(
                sellerId,
                brandId,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                defaultImageCommands(),
                defaultOptionGroupCommands(),
                defaultProductCommands(),
                defaultDescriptionCommand(),
                defaultNoticeCommand());
    }

    // ===== RegisterProductGroupCommand 내부 Command Fixtures =====

    public static List<ImageCommand> defaultImageCommands() {
        return List.of(
                new ImageCommand("THUMBNAIL", DEFAULT_IMAGE_URL, 0),
                new ImageCommand("DETAIL", "https://example.com/detail.jpg", 1));
    }

    public static List<OptionGroupCommand> defaultOptionGroupCommands() {
        return List.of(
                new OptionGroupCommand(
                        "색상",
                        null,
                        null,
                        List.of(
                                new OptionValueCommand("검정", null, 0),
                                new OptionValueCommand("흰색", null, 1))));
    }

    public static List<OptionGroupCommand> optionGroupCommandsWithCanonical() {
        return List.of(
                new OptionGroupCommand(
                        "색상",
                        1L,
                        null,
                        List.of(
                                new OptionValueCommand("검정", 1L, 0),
                                new OptionValueCommand("흰색", 2L, 1))));
    }

    public static List<ProductCommand> defaultProductCommands() {
        return List.of(
                new ProductCommand(
                        DEFAULT_SKU_CODE,
                        10000,
                        9000,
                        100,
                        0,
                        List.of(new SelectedOption("색상", "검정"))),
                new ProductCommand(
                        "SKU-002", 10000, 9000, 50, 1, List.of(new SelectedOption("색상", "흰색"))));
    }

    public static List<ProductCommand> defaultProductCommandsNoOption() {
        return List.of(new ProductCommand(DEFAULT_SKU_CODE, 10000, 9000, 100, 0, List.of()));
    }

    public static DescriptionCommand defaultDescriptionCommand() {
        return new DescriptionCommand(DEFAULT_DESCRIPTION_CONTENT, List.of());
    }

    public static NoticeCommand defaultNoticeCommand() {
        return new NoticeCommand(
                DEFAULT_NOTICE_CATEGORY_ID,
                List.of(
                        new NoticeEntryCommand(1L, "100% 면"),
                        new NoticeEntryCommand(2L, "국내 제조"),
                        new NoticeEntryCommand(3L, "세탁 가능")));
    }

    // ===== UpdateProductGroupBasicInfoCommand =====

    public static UpdateProductGroupBasicInfoCommand updateBasicInfoCommand(long productGroupId) {
        return new UpdateProductGroupBasicInfoCommand(
                productGroupId,
                "수정된 상품 그룹명",
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID);
    }

    public static UpdateProductGroupBasicInfoCommand updateBasicInfoCommand(
            long productGroupId, String productGroupName) {
        return new UpdateProductGroupBasicInfoCommand(
                productGroupId,
                productGroupName,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID);
    }

    // ===== UpdateProductGroupFullCommand =====

    public static UpdateProductGroupFullCommand updateFullCommand(long productGroupId) {
        return new UpdateProductGroupFullCommand(
                productGroupId,
                "수정된 상품 그룹명",
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_SHIPPING_POLICY_ID,
                DEFAULT_REFUND_POLICY_ID,
                defaultUpdateImageCommands(),
                defaultUpdateOptionGroupCommands(),
                defaultUpdateProductCommands(),
                defaultUpdateDescriptionCommand(),
                defaultUpdateNoticeCommand());
    }

    public static List<UpdateProductGroupFullCommand.ImageCommand> defaultUpdateImageCommands() {
        return List.of(
                new UpdateProductGroupFullCommand.ImageCommand("THUMBNAIL", DEFAULT_IMAGE_URL, 0),
                new UpdateProductGroupFullCommand.ImageCommand(
                        "DETAIL", "https://example.com/detail-updated.jpg", 1));
    }

    public static List<UpdateProductGroupFullCommand.OptionGroupCommand>
            defaultUpdateOptionGroupCommands() {
        return List.of(
                new UpdateProductGroupFullCommand.OptionGroupCommand(
                        1L,
                        "색상",
                        null,
                        null,
                        List.of(
                                new UpdateProductGroupFullCommand.OptionValueCommand(
                                        1L, "검정", null, 0),
                                new UpdateProductGroupFullCommand.OptionValueCommand(
                                        2L, "흰색", null, 1))));
    }

    public static List<UpdateProductGroupFullCommand.ProductCommand>
            defaultUpdateProductCommands() {
        return List.of(
                new UpdateProductGroupFullCommand.ProductCommand(
                        1L,
                        DEFAULT_SKU_CODE,
                        12000,
                        10000,
                        80,
                        0,
                        List.of(new SelectedOption("색상", "검정"))),
                new UpdateProductGroupFullCommand.ProductCommand(
                        null,
                        "SKU-003-NEW",
                        12000,
                        10000,
                        30,
                        1,
                        List.of(new SelectedOption("색상", "흰색"))));
    }

    public static UpdateProductGroupFullCommand.DescriptionCommand
            defaultUpdateDescriptionCommand() {
        return new UpdateProductGroupFullCommand.DescriptionCommand("<p>수정된 상세설명</p>", List.of());
    }

    public static UpdateProductGroupFullCommand.NoticeCommand defaultUpdateNoticeCommand() {
        return new UpdateProductGroupFullCommand.NoticeCommand(
                DEFAULT_NOTICE_CATEGORY_ID,
                List.of(
                        new UpdateProductGroupFullCommand.NoticeEntryCommand(1L, "95% 면"),
                        new UpdateProductGroupFullCommand.NoticeEntryCommand(2L, "국내 제조"),
                        new UpdateProductGroupFullCommand.NoticeEntryCommand(3L, "드라이 클리닝")));
    }

    // ===== BatchChangeProductGroupStatusCommand =====

    public static BatchChangeProductGroupStatusCommand batchChangeStatusCommand(
            long sellerId, String targetStatus) {
        return new BatchChangeProductGroupStatusCommand(
                sellerId, List.of(1L, 2L, 3L), targetStatus);
    }

    public static BatchChangeProductGroupStatusCommand batchChangeToActiveCommand(long sellerId) {
        return batchChangeStatusCommand(sellerId, "ACTIVE");
    }

    public static BatchChangeProductGroupStatusCommand batchChangeToInactiveCommand(long sellerId) {
        return batchChangeStatusCommand(sellerId, "INACTIVE");
    }

    public static BatchChangeProductGroupStatusCommand batchChangeStatusCommand(
            long sellerId, List<Long> productGroupIds, String targetStatus) {
        return new BatchChangeProductGroupStatusCommand(sellerId, productGroupIds, targetStatus);
    }

    // ===== BatchRegisterProductGroupFullUseCase용 Fixtures =====

    public static List<RegisterProductGroupCommand> batchRegisterCommands(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(
                        i ->
                                new RegisterProductGroupCommand(
                                        DEFAULT_SELLER_ID,
                                        DEFAULT_BRAND_ID + i,
                                        DEFAULT_CATEGORY_ID,
                                        DEFAULT_SHIPPING_POLICY_ID,
                                        DEFAULT_REFUND_POLICY_ID,
                                        DEFAULT_PRODUCT_GROUP_NAME + "-" + i,
                                        DEFAULT_OPTION_TYPE,
                                        defaultImageCommands(),
                                        defaultOptionGroupCommands(),
                                        defaultProductCommands(),
                                        defaultDescriptionCommand(),
                                        defaultNoticeCommand()))
                .toList();
    }

    public static List<RegisterProductGroupCommand> batchRegisterCommandsWithNoOption(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(
                        i ->
                                new RegisterProductGroupCommand(
                                        DEFAULT_SELLER_ID,
                                        DEFAULT_BRAND_ID + i,
                                        DEFAULT_CATEGORY_ID,
                                        DEFAULT_SHIPPING_POLICY_ID,
                                        DEFAULT_REFUND_POLICY_ID,
                                        DEFAULT_PRODUCT_GROUP_NAME + "-NONE-" + i,
                                        "NONE",
                                        defaultImageCommands(),
                                        List.of(),
                                        defaultProductCommandsNoOption(),
                                        defaultDescriptionCommand(),
                                        defaultNoticeCommand()))
                .toList();
    }
}
