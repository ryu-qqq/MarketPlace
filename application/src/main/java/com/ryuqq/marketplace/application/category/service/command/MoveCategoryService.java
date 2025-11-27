package com.ryuqq.marketplace.application.category.service.command;

import com.ryuqq.marketplace.application.category.dto.command.MoveCategoryCommand;
import com.ryuqq.marketplace.application.category.port.in.command.MoveCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.out.command.CategoryPersistencePort;
import com.ryuqq.marketplace.application.category.port.out.query.CategoryQueryPort;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * MoveCategoryService - 카테고리 이동 Command Service
 *
 * <p>카테고리 이동 시 하위 카테고리의 path/depth 일괄 업데이트</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지</li>
 *   <li>@Transactional 경계: Command Service에만 적용</li>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Service
public class MoveCategoryService implements MoveCategoryUseCase {

    private final CategoryPersistencePort persistencePort;
    private final CategoryQueryPort queryPort;

    public MoveCategoryService(
            CategoryPersistencePort persistencePort,
            CategoryQueryPort queryPort) {
        this.persistencePort = persistencePort;
        this.queryPort = queryPort;
    }

    @Override
    @Transactional
    public void execute(MoveCategoryCommand command) {
        // 이동 대상 조회
        Category category = queryPort.findById(command.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        Long oldParentId = category.parentIdValue();

        // 새 부모 조회 (루트 이동이 아닌 경우)
        Category newParent = null;
        if (!command.isMovingToRoot()) {
            newParent = queryPort.findById(command.newParentId())
                .orElseThrow(() -> new CategoryNotFoundException(command.newParentId()));

            // Cycle 검증: 새 부모가 현재 카테고리의 하위가 아닌지 확인
            List<Category> descendants = queryPort.findDescendants(command.categoryId());
            for (Category descendant : descendants) {
                if (descendant.idValue().equals(command.newParentId())) {
                    throw new IllegalArgumentException("Cannot move category to its own descendant");
                }
            }
        }

        // 정렬 순서 업데이트
        if (command.newSortOrder() != null) {
            category.updateSortOrder(SortOrder.of(command.newSortOrder()));
        }

        // 카테고리 및 하위 노드 저장 (path/depth는 Persistence Layer에서 재계산)
        List<Category> toUpdate = new ArrayList<>();
        toUpdate.add(category);

        // 기존 부모가 자식이 없어지면 leaf로 변경
        if (oldParentId != null) {
            Category oldParent = queryPort.findById(oldParentId).orElse(null);
            if (oldParent != null) {
                List<Category> siblings = queryPort.findByParentId(oldParentId);
                // 현재 카테고리를 제외한 형제가 없으면 leaf로 변경
                if (siblings.size() <= 1) {
                    oldParent.markAsLeaf();
                    toUpdate.add(oldParent);
                }
            }
        }

        // 새 부모를 non-leaf로 변경
        if (newParent != null) {
            newParent.markAsNotLeaf();
            toUpdate.add(newParent);
        }

        persistencePort.persistAll(toUpdate);
    }
}
