package org.davidmoten.kool.json;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;

public final class LazyTreeNode implements Supplier<TreeNode> {

    private final JsonParser p;

    LazyTreeNode(JsonParser p) {
        this.p = p;
    }

    @Override
    public TreeNode get() {
        try {
            return (TreeNode) Util.MAPPER.readTree(p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
