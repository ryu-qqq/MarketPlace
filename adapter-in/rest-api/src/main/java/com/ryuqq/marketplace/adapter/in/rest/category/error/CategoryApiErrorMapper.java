package com.ryuqq.marketplace.adapter.in.rest.category.error;

import com.ryuqq.marketplace.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.marketplace.domain.category.exception.CategoryErrorCode;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Category API Error Mapper
 *
 * <p>Category 도메인 예외를 HTTP 응답으로 매핑합니다.</p>
 *
 * <p><strong>HTTP 상태 코드 매핑</strong>:</p>
 * <ul>
 *   <li>CATEGORY_NOT_FOUND → 404 NOT_FOUND</li>
 *   <li>CATEGORY_CODE_DUPLICATE → 409 CONFLICT</li>
 *   <li>CATEGORY_HAS_CHILDREN → 400 BAD_REQUEST</li>
 *   <li>CATEGORY_MAX_DEPTH_EXCEEDED → 400 BAD_REQUEST</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
@Component
public class CategoryApiErrorMapper implements ErrorMapper {

    private static final Set<String> SUPPORTED_CODES = Set.of(
        CategoryErrorCode.CATEGORY_NOT_FOUND.getCode(),
        CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getCode(),
        CategoryErrorCode.CATEGORY_HAS_CHILDREN.getCode(),
        CategoryErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED.getCode()
    );

    private static final Map<String, HttpStatus> STATUS_MAP = Map.of(
        CategoryErrorCode.CATEGORY_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND,
        CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getCode(), HttpStatus.CONFLICT,
        CategoryErrorCode.CATEGORY_HAS_CHILDREN.getCode(), HttpStatus.BAD_REQUEST,
        CategoryErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED.getCode(), HttpStatus.BAD_REQUEST
    );

    private static final Map<String, String> TITLE_MAP = Map.of(
        CategoryErrorCode.CATEGORY_NOT_FOUND.getCode(), "카테고리를 찾을 수 없습니다",
        CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getCode(), "카테고리 코드가 중복됩니다",
        CategoryErrorCode.CATEGORY_HAS_CHILDREN.getCode(), "하위 카테고리가 있어 삭제할 수 없습니다",
        CategoryErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED.getCode(), "카테고리 최대 깊이를 초과했습니다"
    );

    @Override
    public boolean supports(String code) {
        return code != null && SUPPORTED_CODES.contains(code);
    }

    @Override
    public MappedError map(DomainException ex, Locale locale) {
        String code = ex.code();

        HttpStatus status = STATUS_MAP.getOrDefault(code, HttpStatus.BAD_REQUEST);
        String title = TITLE_MAP.getOrDefault(code, "카테고리 처리 오류");
        String detail = buildDetailMessage(ex);
        URI type = URI.create("about:blank");

        return new MappedError(status, title, detail, type);
    }

    private String buildDetailMessage(DomainException ex) {
        String message = ex.getMessage();
        if (message != null && !message.isBlank()) {
            return message;
        }

        Map<String, Object> args = ex.args();
        String code = ex.code();

        if (CategoryErrorCode.CATEGORY_NOT_FOUND.getCode().equals(code)) {
            Object categoryId = args.get("categoryId");
            return categoryId != null
                ? "Category not found: " + categoryId
                : "카테고리를 찾을 수 없습니다";
        }

        if (CategoryErrorCode.CATEGORY_CODE_DUPLICATE.getCode().equals(code)) {
            Object categoryCode = args.get("code");
            return categoryCode != null
                ? "Category code already exists: " + categoryCode
                : "카테고리 코드가 이미 존재합니다";
        }

        if (CategoryErrorCode.CATEGORY_HAS_CHILDREN.getCode().equals(code)) {
            Object categoryId = args.get("categoryId");
            return categoryId != null
                ? "Category has children and cannot be deleted: " + categoryId
                : "하위 카테고리가 있어 삭제할 수 없습니다";
        }

        if (CategoryErrorCode.CATEGORY_MAX_DEPTH_EXCEEDED.getCode().equals(code)) {
            Object maxDepth = args.get("maxDepth");
            return maxDepth != null
                ? "Category max depth exceeded. Max depth: " + maxDepth
                : "카테고리 최대 깊이를 초과했습니다";
        }

        return "카테고리 처리 중 오류가 발생했습니다";
    }
}
