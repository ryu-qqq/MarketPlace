package com.ryuqq.marketplace.application.category.dto.response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CategoryTreeNode {

    private final CategoryResponse category;
    private final List<CategoryTreeNode> children;

    public CategoryTreeNode(CategoryResponse category) {
        this.category = category;
        this.children = new ArrayList<>();
    }

    public void addChild(CategoryTreeNode child) {
        children.add(child);
    }

    public CategoryResponse category() {
        return category;
    }

    public List<CategoryTreeNode> children() {
        return Collections.unmodifiableList(children);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Long id() {
        return category.id();
    }
}
