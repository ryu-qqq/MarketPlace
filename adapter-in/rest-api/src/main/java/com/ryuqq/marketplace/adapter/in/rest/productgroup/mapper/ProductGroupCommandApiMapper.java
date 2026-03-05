package com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupExcelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroupCommandApiMapper - 상품 그룹 Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductGroupCommandApiMapper {
    private static final long UNRESOLVED_POLICY_ID = 0L;

    /**
     * RegisterProductGroupApiRequest -> RegisterProductGroupCommand 변환.
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     * @deprecated sellerId 서버 검증을 위해 {@link #toCommand(long, RegisterProductGroupApiRequest)} 사용
     */
    @Deprecated
    public RegisterProductGroupCommand toCommand(RegisterProductGroupApiRequest request) {
        return toCommand(request.sellerId(), request);
    }

    /**
     * RegisterProductGroupApiRequest -> RegisterProductGroupCommand 변환 (sellerId 서버 주입).
     *
     * <p>SUPER_ADMIN은 요청된 sellerId를 사용하고, 일반 사용자는 서버에서 해석된 sellerId를 사용합니다.
     *
     * @param sellerId 서버에서 해석된 셀러 ID
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterProductGroupCommand toCommand(
            long sellerId, RegisterProductGroupApiRequest request) {
        return new RegisterProductGroupCommand(
                sellerId,
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId(),
                request.productGroupName(),
                resolveOptionType(request.optionType(), request.optionGroups()),
                request.images().stream()
                        .map(
                                img ->
                                        new RegisterProductGroupCommand.ImageCommand(
                                                img.imageType(), img.originUrl(), img.sortOrder()))
                        .toList(),
                request.optionGroups() != null
                        ? request.optionGroups().stream()
                                .map(
                                        group ->
                                                new RegisterProductGroupCommand.OptionGroupCommand(
                                                        group.optionGroupName(),
                                                        group.canonicalOptionGroupId(),
                                                        group.inputType(),
                                                        group.optionValues().stream()
                                                                .map(
                                                                        value ->
                                                                                new RegisterProductGroupCommand
                                                                                        .OptionValueCommand(
                                                                                        value
                                                                                                .optionValueName(),
                                                                                        value
                                                                                                .canonicalOptionValueId(),
                                                                                        value
                                                                                                .sortOrder()))
                                                                .toList()))
                                .toList()
                        : null,
                request.products().stream()
                        .map(
                                product ->
                                        new RegisterProductGroupCommand.ProductCommand(
                                                product.skuCode(),
                                                product.regularPrice(),
                                                product.currentPrice(),
                                                product.stockQuantity(),
                                                product.sortOrder(),
                                                product.selectedOptions().stream()
                                                        .map(
                                                                so ->
                                                                        new SelectedOption(
                                                                                so
                                                                                        .optionGroupName(),
                                                                                so
                                                                                        .optionValueName()))
                                                        .toList()))
                        .toList(),
                new RegisterProductGroupCommand.DescriptionCommand(
                        request.description().content(), List.of()),
                new RegisterProductGroupCommand.NoticeCommand(
                        request.notice().noticeCategoryId(),
                        request.notice().entries().stream()
                                .map(
                                        entry ->
                                                new RegisterProductGroupCommand.NoticeEntryCommand(
                                                        entry.noticeFieldId(), entry.fieldValue()))
                                .toList()));
    }

    /**
     * RegisterProductGroupExcelApiRequest -> RegisterProductGroupCommand 변환.
     *
     * <p>배치 엑셀 등록 요청은 sellerId를 컨텍스트에서 주입하고, 정책 ID는 서비스에서 기본 정책으로 보정합니다.
     *
     * @param sellerId 인증 컨텍스트에서 해석된 셀러 ID
     * @param request 엑셀 등록 요청 DTO
     * @return Application Command DTO
     */
    public RegisterProductGroupCommand toCommand(
            long sellerId, RegisterProductGroupExcelApiRequest request) {
        return new RegisterProductGroupCommand(
                sellerId,
                request.brandId(),
                request.categoryId(),
                UNRESOLVED_POLICY_ID,
                UNRESOLVED_POLICY_ID,
                request.productGroupName(),
                resolveOptionType(request.optionType(), request.optionGroups()),
                request.images().stream()
                        .map(
                                img ->
                                        new RegisterProductGroupCommand.ImageCommand(
                                                img.imageType(), img.originUrl(), img.sortOrder()))
                        .toList(),
                request.optionGroups() != null
                        ? request.optionGroups().stream()
                                .map(
                                        group ->
                                                new RegisterProductGroupCommand.OptionGroupCommand(
                                                        group.optionGroupName(),
                                                        group.canonicalOptionGroupId(),
                                                        group.inputType(),
                                                        group.optionValues().stream()
                                                                .map(
                                                                        value ->
                                                                                new RegisterProductGroupCommand
                                                                                        .OptionValueCommand(
                                                                                        value
                                                                                                .optionValueName(),
                                                                                        value
                                                                                                .canonicalOptionValueId(),
                                                                                        value
                                                                                                .sortOrder()))
                                                                .toList()))
                                .toList()
                        : null,
                request.products().stream()
                        .map(
                                product ->
                                        new RegisterProductGroupCommand.ProductCommand(
                                                product.skuCode(),
                                                product.regularPrice(),
                                                product.currentPrice(),
                                                product.stockQuantity(),
                                                product.sortOrder(),
                                                product.selectedOptions().stream()
                                                        .map(
                                                                so ->
                                                                        new SelectedOption(
                                                                                so
                                                                                        .optionGroupName(),
                                                                                so
                                                                                        .optionValueName()))
                                                        .toList()))
                        .toList(),
                new RegisterProductGroupCommand.DescriptionCommand(
                        request.description().content(), List.of()),
                new RegisterProductGroupCommand.NoticeCommand(
                        request.notice().noticeCategoryId(),
                        request.notice().entries().stream()
                                .map(
                                        entry ->
                                                new RegisterProductGroupCommand.NoticeEntryCommand(
                                                        entry.noticeFieldId(), entry.fieldValue()))
                                .toList()));
    }

    /**
     * UpdateProductGroupFullApiRequest -> UpdateProductGroupFullCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupFullCommand toCommand(
            Long productGroupId, UpdateProductGroupFullApiRequest request) {
        return new UpdateProductGroupFullCommand(
                productGroupId,
                request.productGroupName(),
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId(),
                resolveOptionType(request.optionType(), request.optionGroups()),
                request.images().stream()
                        .map(
                                img ->
                                        new UpdateProductGroupFullCommand.ImageCommand(
                                                img.imageType(), img.originUrl(), img.sortOrder()))
                        .toList(),
                request.optionGroups() != null
                        ? request.optionGroups().stream()
                                .map(
                                        group ->
                                                new UpdateProductGroupFullCommand
                                                        .OptionGroupCommand(
                                                        group.sellerOptionGroupId(),
                                                        group.optionGroupName(),
                                                        group.canonicalOptionGroupId(),
                                                        group.inputType(),
                                                        group.optionValues().stream()
                                                                .map(
                                                                        value ->
                                                                                new UpdateProductGroupFullCommand
                                                                                        .OptionValueCommand(
                                                                                        value
                                                                                                .sellerOptionValueId(),
                                                                                        value
                                                                                                .optionValueName(),
                                                                                        value
                                                                                                .canonicalOptionValueId(),
                                                                                        value
                                                                                                .sortOrder()))
                                                                .toList()))
                                .toList()
                        : null,
                request.products().stream()
                        .map(
                                product ->
                                        new UpdateProductGroupFullCommand.ProductCommand(
                                                product.productId(),
                                                product.skuCode(),
                                                product.regularPrice(),
                                                product.currentPrice(),
                                                product.stockQuantity(),
                                                product.sortOrder(),
                                                product.selectedOptions().stream()
                                                        .map(
                                                                so ->
                                                                        new SelectedOption(
                                                                                so
                                                                                        .optionGroupName(),
                                                                                so
                                                                                        .optionValueName()))
                                                        .toList()))
                        .toList(),
                new UpdateProductGroupFullCommand.DescriptionCommand(
                        request.description().content(), List.of()),
                new UpdateProductGroupFullCommand.NoticeCommand(
                        request.notice().noticeCategoryId(),
                        request.notice().entries().stream()
                                .map(
                                        entry ->
                                                new UpdateProductGroupFullCommand
                                                        .NoticeEntryCommand(
                                                        entry.noticeFieldId(), entry.fieldValue()))
                                .toList()));
    }

    /**
     * UpdateProductGroupBasicInfoApiRequest -> UpdateProductGroupBasicInfoCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductGroupBasicInfoCommand toCommand(
            Long productGroupId, UpdateProductGroupBasicInfoApiRequest request) {
        return new UpdateProductGroupBasicInfoCommand(
                productGroupId,
                request.productGroupName(),
                request.brandId(),
                request.categoryId(),
                request.shippingPolicyId(),
                request.refundPolicyId());
    }

    /**
     * BatchChangeProductGroupStatusApiRequest -> BatchChangeProductGroupStatusCommand 변환.
     *
     * @param sellerId 인증 컨텍스트에서 해석된 셀러 ID
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public BatchChangeProductGroupStatusCommand toCommand(
            Long sellerId, BatchChangeProductGroupStatusApiRequest request) {
        return new BatchChangeProductGroupStatusCommand(
                sellerId, request.productGroupIds(), request.targetStatus());
    }

    /**
     * optionType을 결정합니다. 명시적으로 전달된 값이 있으면 그대로 사용하고, 없으면 optionGroups 수로 추론합니다.
     *
     * @param optionType 클라이언트 전달 값 (nullable)
     * @param optionGroups 옵션 그룹 목록 (nullable)
     * @return 결정된 optionType 문자열
     */
    private String resolveOptionType(String optionType, List<?> optionGroups) {
        if (optionType != null && !optionType.isBlank()) {
            return optionType;
        }
        if (optionGroups == null || optionGroups.isEmpty()) {
            return OptionType.NONE.name();
        }
        if (optionGroups.size() == 1) {
            return OptionType.SINGLE.name();
        }
        return OptionType.COMBINATION.name();
    }
}
